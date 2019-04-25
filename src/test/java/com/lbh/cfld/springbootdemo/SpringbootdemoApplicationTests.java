package com.lbh.cfld.springbootdemo;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.lbh.cfld.springbootdemo.dao.UserInfoMapper;
import com.lbh.cfld.springbootdemo.model.UserInfo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Proxy;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SpringbootdemoApplication.class)
public class SpringbootdemoApplicationTests {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Test

    public void contextLoads() {
        UserInfo userInfo = userInfoMapper.findByName("小王5");
        System.out.println(userInfo.getUserName()+"=="+userInfo.getUserId());
    }
    @Test
    public void insertTest(){
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName("wwww");
        userInfo.setUserPassword("123");
        userInfo.setUserEmail("54545");
        Integer insert = userInfoMapper.insert(userInfo);
    }
    @Test
    public void proxyTest(){
        ORCtest orCtest = new ORCtest();
        MyProxy myProxy = new MyProxy();
        ORCtest bind = (ORCtest)myProxy.bind(orCtest);
        bind.test();

    }
    @Test
    public void proxyCGlib(){
        ORCtest orCtest = new ORCtest();
        ORCtest bind =(ORCtest) new MyProxyCGLIB().bind(orCtest);
        bind.test();
    }
    @Test
    public void proxyTest2(){
        IORCtest orCtest = new ORCtest();
        MyProxy myProxy = new MyProxy();
        ORCtest o =(ORCtest) Proxy.newProxyInstance(orCtest.getClass().getClassLoader(), orCtest.getClass().getInterfaces(), myProxy);
        o.test();
    }
    @Test
    public void testExcelRead() throws FileNotFoundException {
        FileInputStream inputStream = new FileInputStream("D:\\QQfile\\问题汇总.xls");
        ExcelListener listener = new ExcelListener();
        ExcelReader excelReader = new ExcelReader(inputStream, ExcelTypeEnum.XLS,null, listener);
        excelReader.read(new Sheet(1,0,ExcelModel.class));
    }
}
