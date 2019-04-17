package com.lbh.cfld.springbootdemo.dao;

import com.lbh.cfld.springbootdemo.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserInfoMapper {
    @Select("select * from user_info where user_name= #{name}")
    UserInfo findByName(@Param("name") String name);

    @Select("insert into user_info " +
            "value(" +
            "null," +
            "#{userInfo.userName}," +
            "#{userInfo.userPassword}," +
            "null,"+
            "#{userInfo.userEmail}" +
            ",null" +
            ",null)")
    Integer insert(@Param("userInfo") UserInfo userInfo );
}
