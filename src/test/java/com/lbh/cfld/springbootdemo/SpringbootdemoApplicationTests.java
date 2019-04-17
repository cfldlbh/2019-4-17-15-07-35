package com.lbh.cfld.springbootdemo;

import com.lbh.cfld.springbootdemo.dao.UserInfoMapper;
import com.lbh.cfld.springbootdemo.model.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringbootdemoApplication.class)
public class SpringbootdemoApplicationTests {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Test

    public void contextLoads() {
        UserInfo userInfo = userInfoMapper.findByName("小王5");
       // System.out.println(userInfo.getUserName()+"=="+userInfo.getUserId());
    }
    @Test
    public void insertTest(){
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("wwww");
        userInfo.setUserPassword("123");
        userInfo.setUserEmail("54545");
        Integer insert = userInfoMapper.insert(userInfo);
    }

}
