package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ShoppingCartMapper {

    /**
     * 加入购物车
     * @param shoppingCart
     */

    void add(ShoppingCart shoppingCart);


    /**
     * 更新菜品套餐数量
     * @param shoppingCart
     */
    @Update("update shopping_cart set number=#{number} where id=#{id}")
    void updateNumberById(ShoppingCart shoppingCart);


    /**
     * 动态条件查询
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 查看购物车
     * @param shoppingCart
     * @return
     */
    @Select("select * from shopping_cart where user_id=#{userId}")
    List<ShoppingCart> showShoopingCart(ShoppingCart shoppingCart);


    /**
     * 清空购物车
     * @param userId
     */
    @Delete("delete from shopping_cart where user_id=#{userId}")
    void clean(Long userId);

    /**
     * 删除购物车的一个菜品或套餐
     * @param shoppingCart
     */
    void sub(ShoppingCart shoppingCart);
}
