package com.sky.controller.user;

import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userSetmealController")
@Slf4j
@Api(tags = "C端-套餐相关接口")
@RequestMapping("/user/setmeal")
public class SetmealController {


    @Autowired
    SetmealService setmealService;
    @Autowired
    DishService dishService;


    /**
     * 根据分类id查询起售中的套餐
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @Cacheable(cacheNames = "setmealCache",key = "#categoryId")
    @ApiOperation("根据分类id查询套餐")
    public Result<List<Setmeal>> list(Long categoryId){
        log.info("根据分类id查询套餐");
        Setmeal setmeal = Setmeal.builder()
                .status(StatusConstant.ENABLE)
                .categoryId(categoryId)
                .build();
        List<Setmeal> setmeals = setmealService.list(setmeal);
        return Result.success(setmeals);

    }


    /**
     * 根据套餐id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
//    @Cacheable(cacheNames = "setmealDishCache",key = "#id")
    @ApiOperation("根据套餐id查询菜品")
    public Result<List<DishItemVO>> dish(@PathVariable Long id){
        log.info("根据套餐id查询菜品");
        List<DishItemVO> dishes = dishService.selectBySetmealId(id);
        return Result.success(dishes);
    }

}
