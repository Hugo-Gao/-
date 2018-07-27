package com.gyf.miaosha.dao;

import com.gyf.miaosha.domain.MiaoshaUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MiaoshaUserDao
{
    @Select("select * from miaosha_user where id = #{id}")
    public MiaoshaUser getById(@Param("id") long id);

    @Select("update miaosha_user set password=#{password} where id=#{id}")
    void update(MiaoshaUser toBeUpdate);

    @Insert("insert ignore into miaosha_user(id, nickname, password, salt, head, register_date) value (#{id},#{nickname},#{password},#{salt},#{head},#{registerDate})")
    int insertUser(MiaoshaUser user);
}
