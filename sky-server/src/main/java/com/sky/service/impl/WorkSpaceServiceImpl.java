package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
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


    /**
     * 查询今日运营数据
     *
     * @return
     */
    @Override
    public BusinessDataVO businessData() {
        //查询新增用户数
        Map<String, Object> map = new HashMap<>();
        map.put("begin", LocalDateTime.of(LocalDate.now(), LocalTime.MIN));
        map.put("end", LocalDateTime.of(LocalDate.now(), LocalTime.MAX));
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
}
