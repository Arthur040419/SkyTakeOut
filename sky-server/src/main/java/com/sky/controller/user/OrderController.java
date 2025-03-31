package com.sky.controller.user;


import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/order")
@Api(tags = "用户订单相关接口")
@Slf4j
public class OrderController {

    @Autowired
    OrderService orderService;

    /**
     * 用户提交订单
     * @param ordersSubmitDTO
     * @return
     */
    @PostMapping("/submit")
    @ApiOperation("用户提交订单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO){
        log.info("用户提交订单：{}",ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submit(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    @ApiOperation("订单支付")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        log.info("订单支付：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        log.info("生成预支付交易单：{}", orderPaymentVO);
        return Result.success(orderPaymentVO);
    }


    /**
     * 查询订单详细
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详细")
    public Result<OrderVO> orderDetail(@PathVariable Long id){
        log.info("查询订单详细：{}",id);
        OrderVO orderVO = orderService.orderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 取消订单
     * @param id
     * @return
     */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result orderCancel(@PathVariable Long id) throws Exception {
        log.info("用户取消订单");
        orderService.orderCancel(id);
        return Result.success();
    }


    /**
     * 查询历史订单
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/historyOrders")
    @ApiOperation("查询历史订单")
    public Result<PageResult> historyOrders(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("查询历史订单");
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 再来一单
     * @param id
     * @return
     */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单：{}",id);
        orderService.repetition(id);
        return Result.success();
    }


}
