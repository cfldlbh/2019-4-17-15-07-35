package com.lbh.cfld.springbootdemo.controller;

import com.lbh.cfld.springbootdemo.model.UserInfo;
import com.lbh.cfld.springbootdemo.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {
    @Autowired
    private UserInfoService userInfoService;

    @RequestMapping("/index")
    public String indexMethod(Model model){
        model.addAttribute("host","http://baidu.com");
        return "index";
    }
    @ResponseBody
    @RequestMapping("/userInfo/{name}")
    public UserInfo indexMethod2(@PathVariable String name){
        UserInfo userInfo = new UserInfo();
        userInfo.setUserName(name);
        return  userInfoService.selectUserInfoByName(userInfo);
    }
    /*注释分支分支*/

    @RequestMapping("/login")
    public String login(){
        return "login";
    }
}
