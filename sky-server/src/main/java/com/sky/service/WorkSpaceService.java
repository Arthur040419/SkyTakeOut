package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

import java.time.LocalDateTime;

public interface WorkSpaceService {

    /**
     * 查询指定时间范围的运营数据
     * @return
     */
    BusinessDataVO businessData(LocalDateTime begin,LocalDateTime end);


    /**
     * 查询套餐总览
     * @return
     */
    SetmealOverViewVO countSetmeals();

    /**
     * 根据菜品状态查询菜品数量
     * @return
     */
    DishOverViewVO countByStatus();


    /**
     * 查询订单管理数据
     * @return
     */
    OrderOverViewVO countOrdersByStatus();

}
