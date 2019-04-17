package com.lbh.cfld.springbootdemo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
    @RequestMapping("/index")
    public String indexMethod(Model model){
        model.addAttribute("host","http://baidu.com");
        return "index";
    }

    /*分支分支分支分支分支 修复bug*/


    @RequestMapping("/index2")
    public String indexMethod2(Model model){
        model.addAttribute("host","http://baidu.com");
        return "index";
    }
    /*注释分支分支*/
}
