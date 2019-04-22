package com.lbh.cfld.springbootdemo.config;

import com.lbh.cfld.springbootdemo.model.UserInfo;
import com.lbh.cfld.springbootdemo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
@Component
public class DBUserDetailsService implements UserDetailsService {
    @Autowired
    private  UserInfoService userInfoService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(username);
        UserInfo user = userInfoService.selectUserInfoByName(userInfo);
        if(user == null){
            throw new UsernameNotFoundException("用户不存在");
        }
        ArrayList<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("user"));
        return new User(userInfo.getUserName(),userInfo.getUserPassword(),authorities);
    }
}
