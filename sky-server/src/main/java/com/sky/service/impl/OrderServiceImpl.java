package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service

public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    OrderDetailMapper orderDetailMapper;
    @Autowired
    ShoppingCartMapper shoppingCartMapper;
    @Autowired
    AddressBookMapper addressBookMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    WeChatPayUtil weChatPayUtil;


    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     */
    @Override
    @Transactional
    public OrderSubmitVO submit(OrdersSubmitDTO ordersSubmitDTO) {
        //处理订单异常情况（地址为空，购物车为空）
        //地址为空
        AddressBook address = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if (address == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //购物车为空
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> shoppingCarts = shoppingCartMapper.list(shoppingCart);
        if (shoppingCarts == null || shoppingCarts.size() == 0) {
            throw new OrderBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //向订单表中插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(BaseContext.getCurrentId());
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(address.getPhone());
        orders.setAddress(address.getDetail());
        orders.setConsignee(address.getConsignee());
        orderMapper.insert(orders);

        //向订单明细表中插入多条数据
        List<OrderDetail> orderDetails = new ArrayList<>();
        for (ShoppingCart cart : shoppingCarts) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setId(null);
            orderDetail.setOrderId(orders.getId());
            orderDetails.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetails);

        //清空购物车
        shoppingCartMapper.clean(BaseContext.getCurrentId());

        //封装要返回的订单信息
        OrderSubmitVO orderSubmitVO = new OrderSubmitVO();
        orderSubmitVO.setId(orders.getId());
        orderSubmitVO.setOrderNumber(orders.getNumber());
        orderSubmitVO.setOrderAmount(orders.getAmount());
        orderSubmitVO.setOrderTime(orders.getOrderTime());

        //返回订单信息
        return orderSubmitVO;

    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }

        //返回的OrderPaymentVO类型数据中的属性是用来给小程序调用微信官方服务器时的参数，但是我已经在小程序那边跳过了微信支付的功能，所以这里随便返回一个值就行
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("code", "ORDERPAID");
        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        //直接调用paySuccess方法，来修改订单状态
        paySuccess(ordersPaymentDTO.getOrderNumber());

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }


    /**
     * 查询订单详细
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO orderDetail(Long id) {
        //查询订单信息
        Orders order = orderMapper.getById(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        //查询订单详细
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        orderVO.setOrderDetailList(orderDetails);

        return orderVO;
    }


    /**
     * 用户取消订单
     *
     * @param id
     */
    @Override
    public void orderCancel(Long id) throws Exception {
        Orders order = orderMapper.getById(id);
        if (order == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //如果订单状态处于待支付或待接单，则允许用户直接取消订单
        if (order.getStatus() <= 2) {
            order.setStatus(Orders.CANCELLED);
        } else {
            //如果订单状态处于其他状态，不允许用户直接取消订单，需要用户联系商家
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders newOrder = new Orders();
        newOrder.setId(order.getId());
        //如果用户取消订单时订单状态为待接单，需要进行退款
        if (order.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口，这里先跳过
//            weChatPayUtil.refund(
//                    order.getNumber(), //商户订单号
//                    order.getNumber(), //商户退款单号
//                    new BigDecimal(0.01),//退款金额，单位 元
//                    new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
            newOrder.setPayStatus(Orders.REFUND);
        }
        newOrder.setStatus(Orders.CANCELLED);
        newOrder.setCancelReason("用户取消");
        newOrder.setCancelTime(LocalDateTime.now());

        orderMapper.update(newOrder);

    }

    /**
     * 查询历史订单
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Orders order = Orders.builder()
                .status(ordersPageQueryDTO.getStatus())
                .userId(BaseContext.getCurrentId())
                .build();

        Page<OrderVO> orders = orderMapper.getOrders(order);

        if(orders!=null&&orders.size()>0){
            List<OrderVO> orderVOs = orders.getResult();
            for (OrderVO orderVO : orderVOs) {
                List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(orderVO.getId());
                orderVO.setOrderDetailList(orderDetailList);
            }

        }
        PageResult pageResult = new PageResult();
        pageResult.setTotal(orders.getTotal());
        pageResult.setRecords(orders.getResult());
        return pageResult;
    }


    /**
     * 再来一单
     * @param id
     */
    @Override
    @Transactional
    public void repetition(Long id) {
        //将本次订单的商品再次添加到购物车中
        //查询本次订单的所有商品
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(id);
        for (OrderDetail orderDetail : orderDetails) {
            ShoppingCart shoppingCart = new ShoppingCart();
            BeanUtils.copyProperties(orderDetail,shoppingCart);
            shoppingCart.setUserId(BaseContext.getCurrentId());
            shoppingCarts.add(shoppingCart);
        }
        shoppingCartMapper.addBatch(shoppingCarts);

    }
}
