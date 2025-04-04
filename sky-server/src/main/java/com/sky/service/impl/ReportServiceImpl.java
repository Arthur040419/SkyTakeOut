package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    OrderDetailMapper orderDetailMapper;

    /**
     * 营业额统计
     *
     * @param start
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO turnoverReport(LocalDate start, LocalDate end) {
        //封装dateList
        List<LocalDate> time = new ArrayList<>();
        time.add(start);
        LocalDate begin = start;
        while (!begin.equals(end)) {
            begin = begin.plusDays(1);
            time.add(begin);
        }


        List<Double> amountList = new ArrayList<>();
        //统计每一天的营业额
        //数据库中的时间用的是LocalDateTime，因此这里要把LocalDate转成LocalDateTime
        for (LocalDate date : time) {
            LocalDateTime startTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map<String, Object> map = new HashMap<>();
            map.put("start", startTime);
            map.put("end", endTime);
            map.put("status", Orders.COMPLETED);
            Double amount = orderMapper.getTurnoverByMap(map);
            //小细节，如果返回的amount为空就要将其转为0，否则后面转为字符串时空也会被转为字符串
            amount = amount == null ? 0.0 : amount;
            amountList.add(amount);
        }


        return TurnoverReportVO
                .builder().
                dateList(StringUtils.join(time, ','))
                .turnoverList(StringUtils.join(amountList, ','))
                .build();
    }


    /**
     * 用户统计
     *
     * @param start
     * @param end
     * @return
     */
    @Override
    public UserReportVO userReport(LocalDate start, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(start);
        while (!start.equals(end)) {
            start = start.plusDays(1);
            dateList.add(start);
        }

        //存放每天的新增用户数量
        List<Integer> newUserCount = new ArrayList<>();
        //存放用户总数量
        List<Integer> allUserCount = new ArrayList<>();

        for (LocalDate date : dateList) {
            Map<String, Object> map = new HashMap<>();
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            //统计截至当前的用户总数
            Integer allUsers = userMapper.countUser(map);
            allUserCount.add(allUsers);

            //统计新用户
            map.put("start", LocalDateTime.of(date, LocalTime.MIN));
            Integer newUsers = userMapper.countUser(map);
            newUserCount.add(newUsers);

        }

        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ','))
                .newUserList(StringUtils.join(newUserCount, ','))
                .totalUserList(StringUtils.join(allUserCount, ','))
                .build();
    }


    /**
     * 订单统计
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO ordersReport(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate tmpDate = begin;
        dateList.add(begin);
        while (!tmpDate.equals(end)) {
            tmpDate = tmpDate.plusDays(1);
            dateList.add(tmpDate);
        }
        //统计总的订单数
        Map<String, Object> map = new HashMap<>();
        map.put("end", LocalDateTime.of(end, LocalTime.MAX));
        Integer totalOrderCount = orderMapper.countByMap(map);

        //统计总的有效订单数
        map.put("status", Orders.COMPLETED);
        Integer validOrderCount = orderMapper.countByMap(map);

        //计算有效订单率
        Double orderCompletionRate = validOrderCount.doubleValue() / totalOrderCount.doubleValue();

        //统计每天有效订单数
        //统计每天总的订单数
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        for (LocalDate date : dateList) {
            map.clear();
            map.put("begin", LocalDateTime.of(date, LocalTime.MIN));
            map.put("end", LocalDateTime.of(date, LocalTime.MAX));
            orderCountList.add(orderMapper.countByMap(map));

            map.put("status", Orders.COMPLETED);
            validOrderCountList.add(orderMapper.countByMap(map));
        }

        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList, ','))
                .orderCountList(StringUtils.join(orderCountList, ','))
                .validOrderCountList(StringUtils.join(validOrderCountList, ','))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    /**
     * 统计销量排名前10的菜品
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public SalesTop10ReportVO salesTop10(LocalDate begin, LocalDate end) {
        List<GoodsSalesDTO> sales = orderDetailMapper.getTop10(LocalDateTime.of(begin,LocalTime.MIN), LocalDateTime.of(end,LocalTime.MAX));
        //封装查询结果
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();


//         不使用stream流
//        for (GoodsSalesDTO sale : sales) {
//            nameList.add(sale.getName());
//            numberList.add(sale.getNumber());
//        }
        //使用stream流
        nameList = sales.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        numberList = sales.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        return SalesTop10ReportVO
                .builder()
                .nameList(StringUtils.join(nameList,','))
                .numberList(StringUtils.join(numberList,','))
                .build();
    }
}
