package com.lbh.cfld.springbootdemo.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lbh.cfld.springbootdemo.model.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.mybatis.spring.annotation.MapperScan;

@MapperScan
public interface UserInfoMapper extends BaseMapper {
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
