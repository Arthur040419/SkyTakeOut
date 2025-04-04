package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.Map;

@Mapper
public interface UserMapper {

    /**
     * 根据openId查询用户
     * @param openId
     * @return
     */
    @Select("select * from user where openid=#{openId}")
    public User selectByOpenId(String openId);


    /**
     * 插入用户信息
     * @param user
     */
    void insert(User user);


    /**
     * 根据id查询用户
     * @param userId
     * @return
     */
    @Select("select * from user where id = #{userId}")
    User getById(Long userId);

    /**
     * 动态统计用户数量
     * @param map
     * @return
     */
    Integer countUser(Map<String, Object> map);

    /**
     * 统计每天的新用户
     * @param map
     * @return
     */
    //Integer countNewUser(Map<String, Object> map);

    /**
     * 统计截至目前为止的所有用户数量
     * @param time
     * @return
     */
//    @Select("select count(0) from user where create_time < #{time}")
//    Integer countAllUser(LocalDateTime time);
}
