package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.OrderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface OrderMapper {
    /**
     * 插入订单数据
     * @param orders
     */
    void insert(Orders orders);


    /**
     * 根据订单号查询订单
     * @param orderNumber
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);

    /**
     * 根据订单id查询订单信息
     * @param id
     * @return
     */
    @Select("select * from orders where id=#{id}")
    Orders getById(Long id);


    /**
     * 查询用户所有订单信息
     *
     * @param order
     * @return
     */
    Page<OrderVO> getOrders(Orders order);

    /**
     * 条件分页查询订单信息
     * @param ordersPageQueryDTO
     * @return
     */
    Page<Orders> conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO);


    /**
     * 根据订单状态查询订单数量
     * @param status
     * @return
     */
    @Select("select count(0) from orders where status=#{status}")
    Integer countByStatus(Integer status);

    /**
     * 查询超时订单
     * @param status
     * @param time
     * @return
     */
    @Select("select * from orders where status=#{status} and order_time < #{time}")
    List<Orders> getByStatusAndOrderTime(Integer status, LocalDateTime time);

    /**
     * 查询营业额
     * @param map
     * @return
     */
    Double getTurnoverByMap(Map<String, Object> map);


    /**
     * 统计订单情况
     * @param map
     * @return
     */
    Integer countByMap(Map<String, Object> map);
}
