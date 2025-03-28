package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

//套餐mapper
@Mapper
public interface SetMealMapper {

    /**
     * 根据分类id，查询指定分类下的套餐种类数
     * @param categoryId
     * @return
     */
    @Select("select count(0) from setmeal where category_id=#{categoryId}")
    Integer countSetMeal(Long categoryId);


    /**
     * 添加套餐
     * @param setmeal
     */
    @Options(useGeneratedKeys = true,keyProperty = "id")
    @Insert("insert into setmeal(category_id, name, price, status, description, image, create_time, update_time, create_user, update_user)" +
            "values(#{categoryId},#{name},#{price},#{status},#{description},#{image},#{createTime},#{updateTime},#{createUser},#{updateUser})  ")
    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);


    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @Select("select * from setmeal where id = #{id}")
    Setmeal selectById(Long id);


    /**
     * 更新套餐
     * @param setmeal
     */
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    /**
     * 批量删除套餐
     * @param ids
     */
    void deleteBatch(List<Long> ids);

    /**
     * 根据分类id查询套餐
     * @param categoryId
     * @return
     */
    List<Setmeal> selectByCategoryId(Long categoryId);
}
