package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;

    @Autowired
    DishFlavorMapper dishFlavorMapper;

    /**
     * 保存菜品
     *
     * @param dishDTO
     */
    @Override
    @Transactional      //开启事务
    public void saveDishAndFlavor(DishDTO dishDTO) {
        //添加菜品需要两个接口，一个添加菜品信息，另一个添加菜品的口味信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        //1.添加菜品信息
        dishMapper.save(dish);


        // TODO 记得把这个异常删掉
        int i = 10/0;       //异常

        //添加菜品口味信息需要先获取刚插入的菜品的id，主键返回
        Long id = dish.getId();

        //2.添加菜品口味信息
        //先判断有没有口味信息，没有的话就不要添加了
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() != 0) {
            //将菜品的主键id放入口味信息中
            flavors.forEach(flavor ->{
                flavor.setDishId(id);
            });
            //批量添加口味信息
            dishFlavorMapper.saveBatch(dishDTO.getFlavors());

        }

    }
}
