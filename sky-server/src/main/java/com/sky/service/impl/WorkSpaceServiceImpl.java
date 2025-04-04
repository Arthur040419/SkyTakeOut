package com.sky.service.impl;

import com.sky.constant.StatusConstant;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    SetMealMapper setMealMapper;
    @Autowired
    DishMapper dishMapper;


    /**
     * 查询指定时间范围的运营数据
     *
     * @return
     */
    @Override
    public BusinessDataVO businessData(LocalDateTime begin,LocalDateTime end) {
        //查询新增用户数
        Map<String, Object> map = new HashMap<>();
//        map.put("begin", LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
//        map.put("end", LocalDateTime.of(LocalDate.now(), LocalTime.MAX));

        map.put("begin", begin);
        map.put("end", end);
        Integer newUsers = userMapper.countUser(map);

        //统计订单完成率
        //总订单数
        Integer totalOrders = orderMapper.countByMap(map);
        //有效订单数
        map.put("status", Orders.COMPLETED);
        Integer validOrders = orderMapper.countByMap(map);
        Double orderCompletionRate;
        if (totalOrders != 0 && validOrders != 0) {
            //订单完成率
            orderCompletionRate = validOrders.doubleValue() / totalOrders.doubleValue();
        }else{
            orderCompletionRate = Double.MIN_NORMAL;
        }

        //统计营业额
        Double turnover = orderMapper.getTurnoverByMap(map);

        //统计平均客单价
        Double unitPrice;
        if(validOrders==0){
            unitPrice=0.0;
        }else{
            unitPrice = turnover.doubleValue() / validOrders.doubleValue();
        }
        //保留两位小数
        DecimalFormat df = new DecimalFormat("#.00");
        String formatResult = df.format(unitPrice);
        unitPrice=Double.valueOf(formatResult);

        return BusinessDataVO
                .builder()
                .newUsers(newUsers)
                .orderCompletionRate(orderCompletionRate)
                .turnover(turnover)
                .unitPrice(unitPrice)
                .validOrderCount(validOrders)
                .build();
    }

    /**
     * 查询套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO countSetmeals() {
        //已停售套餐数量
        Integer discontinued = setMealMapper.countSetMealByStatus(StatusConstant.DISABLE);
        //已起售套餐数量
        Integer sold = setMealMapper.countSetMealByStatus(StatusConstant.ENABLE);
        return SetmealOverViewVO
                .builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }

    /**
     * 根据菜品状态查询菜品数量
     * @return
     */
    @Override
    public DishOverViewVO countByStatus() {
        //查询停售菜品数量
        Integer discontinued = dishMapper.countByStatus(StatusConstant.DISABLE);
        //查询起售菜品数量
        Integer sold = dishMapper.countByStatus(StatusConstant.ENABLE);
        return DishOverViewVO
                .builder()
                .discontinued(discontinued)
                .sold(sold)
                .build();
    }


    /**
     * 查询订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO countOrdersByStatus() {
        Map<String,Object> map = new HashMap<>();
        map.put("begin",LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        map.put("end",LocalDateTime.of(LocalDate.now(),LocalTime.MAX));
        //查询全部订单
        Integer allOrders = orderMapper.countByMap(map);
        map.put("status",Orders.CANCELLED);
        //查询取消订单数量
        Integer cancelledOrders = orderMapper.countByMap(map);
        //查询已完成数量
        map.put("status",Orders.COMPLETED);
        Integer completedOrders = orderMapper.countByMap(map);
        //查询待派送数量
        map.put("status",Orders.CONFIRMED);
        Integer deliveredOrders = orderMapper.countByMap(map);
        //查询待接单数量
        map.put("status",Orders.TO_BE_CONFIRMED);
        Integer waitingOrders = orderMapper.countByMap(map);

        return OrderOverViewVO
                .builder()
                .allOrders(allOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .deliveredOrders(deliveredOrders)
                .waitingOrders(waitingOrders)
                .build();
    }
}
