package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@Slf4j
@Api(tags = "套餐相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {


    @Autowired
    SetmealService setmealService;

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增套餐")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐：{}",setmealDTO);
        setmealService.saveSetmeal(setmealDTO);
        return Result.success();
    }


    /**
     * 套餐分页查询
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询：{}",setmealPageQueryDTO);
        PageResult pageResult =  setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }


    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> selectById(@PathVariable Long id){
        log.info("根据id查询套餐：{}",id);
        SetmealVO setmealVO = setmealService.selectById(id);
        return Result.success(setmealVO);
    }


    /**
     * 更新套餐
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("更新套餐")
    public Result update(@RequestBody SetmealDTO setmealDTO){
        log.info("更新套餐");
        setmealService.update(setmealDTO);
        return Result.success();
    }

    /**
     * 套餐启用、禁用
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("套餐启用、禁用")
    public Result startOrStop(@PathVariable Integer status,Long id){
        log.info("套餐启用、禁用：{},{}",status,id);
        setmealService.startOrStop(status,id);
        return Result.success();

    }


    /**
     * 批量删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("批量删除套餐")
    public Result deleteBatch(@RequestParam List<Long> ids){
        log.info("批量删除套餐：{}",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }

}
