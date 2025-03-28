package com.sky.controller.user;


import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品相关接口
 */
@RestController("userDishController")
@Slf4j
@Api(tags = "C端-菜品相关接口")
@RequestMapping("/user/dish")
public class DishController {

    @Autowired
    DishService dishService;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * 根据分类id查询起售中的菜品
     *
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId) {
        log.info("根据分类id查询菜品：{}", categoryId);
        //查询Redis数据库
        String key = "dish_" + categoryId;
        ValueOperations valueOperations = redisTemplate.opsForValue();
        List<DishVO> dishes = (List<DishVO>) valueOperations.get(key);
        if (dishes != null && dishes.size() > 0) {
            //如果存在缓存，直接返回缓存结果
            return Result.success(dishes);
        }

        //如果不存再缓存，就查询数据库
        Dish dish = Dish.builder()
                .status(StatusConstant.ENABLE)
                .categoryId(categoryId)
                .build();
        dishes = dishService.userGetDishes(dish);
        //然后将查询结果存入数据库
        valueOperations.set(key,dishes);
        return Result.success(dishes);
    }
}
