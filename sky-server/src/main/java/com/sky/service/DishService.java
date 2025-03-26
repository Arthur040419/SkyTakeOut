package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {


    /**
     * 保存菜品
     * @param dishDTO
     */
    void saveDishAndFlavor(DishDTO dishDTO);
}
