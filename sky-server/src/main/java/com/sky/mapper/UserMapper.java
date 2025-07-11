package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 根据OPENID查询用户
     * */
    @Select("select * from user where openid = #{openid}")
    User getByOpenId(String openid);

    /**
     * 插入数据
     * */
    void insert(User user);
}
