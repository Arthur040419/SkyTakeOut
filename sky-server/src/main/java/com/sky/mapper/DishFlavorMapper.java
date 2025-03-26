package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {


    /**
     * 批量添加口味信息
     *
     * @param flavors
     */
    void saveBatch(List<DishFlavor> flavors);
}
