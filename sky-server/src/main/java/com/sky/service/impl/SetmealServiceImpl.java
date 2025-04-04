package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetMealMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealOverViewVO;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
public class SetmealServiceImpl implements SetmealService {


    @Autowired
    SetMealMapper setMealMapper;

    @Autowired
    SetmealDishMapper setmealDishMapper;


    /**
     * 新增菜品
     *
     * @param setmealDTO
     */
    @Override
    @Transactional
    public void saveSetmeal(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //添加套餐信息
        setMealMapper.insert(setmeal);

        //主键返回
        Long setmealId = setmeal.getId();

        //添加套餐包含的菜品信息
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        //先判断套餐内有没有菜品
        if (dishes != null && dishes.size() > 0) {
            for (SetmealDish setmealDish : dishes) {
                setmealDish.setSetmealId(setmealId);
            }
            setmealDishMapper.insertBatch(dishes);
        }

    }

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setMealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 根据id查询套餐
     *
     * @param id
     * @return
     */
    @Override
    public SetmealVO selectById(Long id) {
        //查询套餐信息
        Setmeal setmeal = setMealMapper.selectById(id);

        //查询与套餐关联的菜品信息
        List<SetmealDish> setmealDishes = setmealDishMapper.selectBySetmealId(id);

        //封装查询结果
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;

    }

    /**
     * 更新套餐
     *
     * @param setmealDTO
     */
    @Transactional  //开启事务管理
    @Override
    public void update(SetmealDTO setmealDTO) {
        //更新套餐信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.update(setmeal);

        //删除原本与套餐关联的所有菜品信息
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());

        //添加新的套餐菜品信息
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        //先判断套餐内有没有菜品
        if (dishes != null && dishes.size() > 0) {
            for (SetmealDish setmealDish : dishes) {
                setmealDish.setSetmealId(setmealDTO.getId());
            }
            setmealDishMapper.insertBatch(dishes);
        }

    }

    /**
     * 套餐启用、禁用
     *
     * @param status
     * @param id
     */
    @Override
    public void startOrStop(Integer status, Long id) {
        //判断当前套餐中是否有未起售的菜品

        if (status==StatusConstant.ENABLE) {
            List<Dish> dishes = setmealDishMapper.getDishes(id);
            for (Dish dish : dishes) {
                if(dish.getStatus()==StatusConstant.DISABLE){
                    throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }

        Setmeal setmeal = Setmeal.builder()
                .id(id)
                .status(status)
                .build();
        setMealMapper.update(setmeal);
    }

    /**
     * 批量删除套餐
     *
     * @param ids
     */
    @Transactional
    @Override
    public void deleteBatch(List<Long> ids) {
        //判断套餐是否可以删除--套餐是否处于在售状态
        for (Long id : ids) {
            Setmeal setmeal = setMealMapper.selectById(id);
            if (setmeal.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //删除与套餐关联的菜品
        setmealDishMapper.deleteBySetmealIds(ids);

        //删除套餐
        setMealMapper.deleteBatch(ids);

    }


    /**
     * 动态条件查询
     * @param setmeal
     * @return
     */
    @Override
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> setmeals =setMealMapper.list(setmeal);
        return setmeals;
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
}
