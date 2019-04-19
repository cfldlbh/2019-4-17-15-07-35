package com.lbh.cfld.springbootdemo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lbh.cfld.springbootdemo.dao.UserInfoMapper;
import com.lbh.cfld.springbootdemo.model.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

@Service
public class UserInfoService {
    @Autowired
    private UserInfoMapper userInfoMapper;

    public UserInfo selectUserInfoByName(UserInfo userInfo){
        UserInfo byName = userInfoMapper.findByName(userInfo.getUserName());
        QueryWrapper<Object> qw = new QueryWrapper<>();
        qw.gt("id",333);
        qw.between("id",555,888);

        return byName;
    }
}
