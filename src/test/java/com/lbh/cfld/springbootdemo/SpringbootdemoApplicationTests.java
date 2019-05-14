package com.lbh.cfld.springbootdemo;

import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lbh.cfld.springbootdemo.dao.UserInfoMapper;
import com.lbh.cfld.springbootdemo.model.UserInfo;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;


import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = SpringbootdemoApplication.class)
public class SpringbootdemoApplicationTests {

    static String X_Anti_Forge_Token = "";
    static String X_Anti_Forge_Code = "";
//    static String loginName = "15879586926";
//    static String password = "d3381a54b9e1f145cb46fac679395948";//19961023
    static String loginName = "15989316116";//李永英
    static String password = "68b83baaef5a1d213e35f95bbdd8989f";//lyy0830
    static String loginurl = "https://passport.lagou.com/login/login.json";


    public static void main(String[] arg) throws IOException {
        //HttpHost proxy = new HttpHost("127.0.0.1",8888);
        CloseableHttpClient client = HttpClients.custom().build();
        HttpPost httpPost = new HttpPost("https://passport.lagou.com/login/login.html");
        httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        CloseableHttpResponse execute = client.execute(httpPost);
        HttpEntity entity = execute.getEntity();
        String entityStr = EntityUtils.toString(entity);
        findToken(entityStr);
        //验证码验证
        String jiyan = "https://api.yuekuai.tech/api/geetest-sense?token=7xg5ATWau3&gt=66442f2f720bfc86799932d8ad2eb6c7&developer=5cc449b44bfdb38a8c96e513";
        HttpGet httpPost3 = new HttpGet(jiyan);
        CloseableHttpResponse execute6 = client.execute(httpPost3);
        HttpEntity entity6 = execute6.getEntity();
        String s4 = EntityUtils.toString(entity6);
        JSONObject jsonObject = JSON.parseObject(s4);
        String challenge = (String)jsonObject.getJSONObject("data").get("challenge");
        String validate = (String)jsonObject.getJSONObject("data").get("validate");
        String msg = (String)jsonObject.get("msg");
        System.out.println(challenge);
        System.out.println(validate);
        System.out.println(msg);
        HttpPost loginPost = new HttpPost(loginurl);
        loginPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        LinkedList<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("isValidate", "true"));
        nameValuePairs.add(new BasicNameValuePair("username", loginName));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("request_form_verifyCode", "vv"));
        nameValuePairs.add(new BasicNameValuePair("submit", ""));
        nameValuePairs.add(new BasicNameValuePair("challenge",challenge));
        loginPost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
        loginPost.setHeader("X-Anit-Forge-Code", X_Anti_Forge_Code);
        loginPost.setHeader("X-Anit-Forge-Token", X_Anti_Forge_Token);
        CloseableHttpResponse execute1 = client.execute(loginPost);
        HttpEntity entity1 = execute1.getEntity();
        String s = EntityUtils.toString(entity1);
        System.out.println(s);
        HttpGet httpGet = new HttpGet("https://passport.lagou.com/grantServiceTicket/grant.html");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding","gzip, deflate, br");
        httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.9");
        CloseableHttpResponse execute3 = client.execute(httpGet);
        HttpGet httpGet1 = new HttpGet("https://www.lagou.com/?action=grantST&ticket=ST-75894de4868444f0a76064b7e812f0f8");
        httpGet1.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        httpGet1.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet1.setHeader("Accept-Encoding","gzip, deflate, br");
        httpGet1.setHeader("Accept-Language","zh-CN,zh;q=0.9");
        HttpPost httpPost1 = new HttpPost("https://www.lagou.com/");
        CloseableHttpResponse execute2 = client.execute(httpPost1);
        HttpEntity entity2 = execute2.getEntity();
        String s1 = EntityUtils.toString(entity2);
        System.out.println(s1);
        FileWriter fileWriter = new FileWriter("d:\\lagou.html");
        fileWriter.write(s1);
        fileWriter.flush();
        fileWriter.close();
    }

    public static void findToken(String entityStr) {
        String token = "";
        String code = "";
        Pattern compile = Pattern.compile("window.X_Anti_Forge_Token = '[\\s\\S]{36}'");
        Matcher m = compile.matcher(entityStr);
        m.find();
        token = m.group(0);
        Pattern compile2 = Pattern.compile("window.X_Anti_Forge_Code = '[\\d]{8}'");
        Matcher m2 = compile2.matcher(entityStr);
        m2.find();
        code = m2.group(0);
        X_Anti_Forge_Token = token.substring(token.indexOf("'") + 1, token.lastIndexOf("'"));
        X_Anti_Forge_Code = code.substring(code.indexOf("'") + 1, code.lastIndexOf("'"));
    }
@Test
public void tes() throws NoSuchMethodException, ScriptException, IOException {
    String encod = encod("Tue, 07 May 2019 14:10:59 GMT","20190507115831-8b993096-6d5e-418c-85bd-a08971920ec3");
}

    public String encod(String responseTime,String trace) throws IOException, ScriptException, NoSuchMethodException {
        String re= "(^| )user_trace_token=([^;]*)(;|$)";
        //cookie中提取user_trace_token的值；拿到"20190507110150-8c03d930-7de6-474a-b1d4-1591e0ad2c3e"；
        //HTTP_JS_KEY+20190507110150-8c03d930-7de6-474a-b1d4-1591e0ad2c3e;
        //_0x10ffc6方法接收HTTP_JS_KEY20190507110150-8c03d930-7de6-474a-b1d4-1591e0ad2c3e

        //HTTP_JS_KEY20190507110150-8c03d930-7de6-474a-b1d4-1591e0ad2c3e>>2位减1位
        //for(int i = 0;i<前面2位减1位的数组的length;i++){  数组全变0}
        // httpjs字符串.length*8  等于496
        //for(int i=0;i<496;i+=8){ _0x116949[_0x43844c >> 0x5] |= (_0x2ece02['charCodeAt'](_0x43844c / 0x8) & 0xff) << _0x43844c % 0x20;}
        //
        //发起json请求拿到服务器响应时间date 转换成时间戳 (/-/g, '/') 13位时间戳
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("javascript");
        String jsFileName = "c:\\lagouJS.js";   // 读取js文件
        FileReader reader = new FileReader(jsFileName);   // 执行指定脚本
        engine.eval(reader);
        String guid = "";
        if(engine instanceof Invocable) {
            Invocable invoke = (Invocable)engine;    // 调用merge方法，并传入两个参数
            guid = (String) invoke.invokeFunction("getlagou",responseTime,trace);//服务器响应时间
        }
        reader.close();
        return guid;
    }


    public void createHtml(JSONObject jsonObject) throws IOException {
        Document doc = DocumentHelper.createDocument();
        Element div = doc.addElement("div");
        div.addAttribute("class","scrollarea left-content");
        Element div1 = div.addElement("div");
        div1.addAttribute("class","scrollarea-content ");
        div1.addAttribute("tabindex","1").addAttribute("style","margin-top: 0px; margin-left: 0px;");
        Element div2 = div1.addElement("div");
        div2.addAttribute("class","information-content clearfix");
        Element div3 = div2.addElement("div");
        div3.addAttribute("class","inoformation-portrait");
        String headPic = (String)jsonObject.get("headPic");
        if(headPic.length()<20){
            headPic = "https://www.lgstatic.com/mds-pipline-fed/common/static/img/default_photo6c52aed4.png";
        }
        Element img = div3.addElement("img");
        img.addAttribute("class","photo-detail ").addAttribute("src",headPic);
        div2.addElement("i").addAttribute("class","icon-lg-edit");
        Element div2div = div2.addElement("div");
        div2div.addAttribute("class","information-right").addAttribute("style","min-height: 120px;");
        div2div.addElement("div");

//        OutputFormat format = OutputFormat.createPrettyPrint();
//        FileWriter fileWriter = new FileWriter("c:\\myxml.xml");
//        XMLWriter xmlWriter = new XMLWriter(fileWriter, format);
//        xmlWriter.write(doc);
//        xmlWriter.close();
//        fileWriter.close();
    }

    @Test
    public void methodTest() throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
//        Class<?> c =  LaGou1.class;
//        Object o = c.newInstance();
//        Method[] method = c.getMethods();
//        Method[] declaredMethods = c.getDeclaredMethods();
//        System.out.println("getMethods获取的方法：");
//        for(Method m:method)
//            System.out.println(m);
//        System.out.println("getDeclaredMethods获取的方法：");
//        for(Method m:declaredMethods)
//            System.out.println(m);
//        Class<methodClass> methodClassClass = methodClass.class;
//        Method add = methodClassClass.getMethod("add", int.class, int.class);
//        Object invoke = add.invoke(new methodClass(),1,4);
//        System.out.println(invoke);
//智联前程
    }
}
