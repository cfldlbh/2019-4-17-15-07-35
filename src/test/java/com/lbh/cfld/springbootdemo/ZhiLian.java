package com.lbh.cfld.springbootdemo;

import com.alibaba.fastjson.JSONObject;
import com.lbh.cfld.springbootdemo.resp.ResponseResult;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ZhiLian {
    private String gt = "c28ada23e78b932c287d12991a41f1e0";
    private String challenge = "";
    private String validate="";
    private CloseableHttpClient client;
    private BasicCookieStore cookieStore = new BasicCookieStore();

    public ZhiLian(){
        ArrayList<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36"));
        client = HttpClients.custom().setDefaultHeaders(headers).setDefaultCookieStore(cookieStore).build();
    }

    @Test
    public void testGetResume() throws Exception {
        getResume("15879586926","19961023zhilian");
    }

    private String getResume(String loginName,String passWord) throws Exception {
        try {
            approve();
        }catch (NullPointerException e){
//            ResponseResult<Object> objectResponseResult = new ResponseResult<>();
//            objectResponseResult.setSuccess(false);
//            objectResponseResult.setMessage("验证码验证步骤失败！");
            return "验证码验证步骤失败！";
        }

        HttpPost loginReuqest = new HttpPost("https://passport.zhaopin.com/login");
        LinkedList<NameValuePair> from = new LinkedList<>();
        from.add(new BasicNameValuePair("loginname",loginName));
        from.add(new BasicNameValuePair("password",passWord));
        from.add(new BasicNameValuePair("rememberMe","true"));
        from.add(new BasicNameValuePair("from","c"));
        from.add(new BasicNameValuePair("t","4"));
        from.add(new BasicNameValuePair("geetest_challenge",challenge));
        from.add(new BasicNameValuePair("geetest_validate",validate));
        from.add(new BasicNameValuePair("geetest_seccode",validate+"|jordan"));//87a07f53d5a5a6c16242ab080cda5799|jordan
        loginReuqest.setEntity(new UrlEncodedFormEntity(from, HTTP.UTF_8));
        loginReuqest.setHeader("X-Requested-With","XMLHttpRequest");
        String result = responseToStr(loginReuqest);
        JSONObject jsonObject = JSONObject.parseObject(result);
        int code = (int)jsonObject.get("code");
        if(code != 1 && code != -1){
            //登录失败提示
//            ResponseResult<Object> objectResponseResult = new ResponseResult<>();
//            objectResponseResult.setSuccess(false);
//            objectResponseResult.setMessage((String) jsonObject.get("msg"));
            return (String)jsonObject.get("msg");
        }
        //获得简历ID和简历ResumeNumber
        System.setProperty("webdriver.chrome.driver","D:/chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.get("https://www.zhaopin.com/");
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie c : cookies){
            if(!c.getDomain().equals("api.ddocr.com") && !c.getDomain().equals("passport.zhaopin.com")){
                driver.manage().addCookie(new org.openqa.selenium.Cookie(c.getName(),c.getValue(),c.getDomain(),c.getPath(),null));
            }
        }
        driver.get("https://i.zhaopin.com/resume");
        String pageSource = driver.getPageSource();
        return pageSource;
    }

    private void approve() throws Exception {
        String referer = "https://passport.zhaopin.com";
        String wtype = "geetest";
        HttpGet httpGet = new HttpGet("https://passport.zhaopin.com/gt/register-slide?from=c");
        String challengeKey = responseToStr(httpGet);
        JSONObject jsonObject = JSONObject.parseObject(challengeKey);
        challenge = (String)jsonObject.get("challenge");
        HttpGet check = new HttpGet("http://api.ddocr.com/api/gateway.jsonp?gt=" + gt + "&referer=" + referer + "&wtype=" + wtype + "&secretkey=112cfa4dbdb6408d96a07fc3f7075844&challenge="+challenge);//打码平台
        if(challenge.length()<5){
            throw new NullPointerException();
        }
        String result = responseToStr(check);
        JSONObject resultObject = JSONObject.parseObject(result);
        int status =(int) resultObject.get("status");
        if(status == -1){
            //log.info("验证码平台失败====code:"+code);
            int reasonCode = (int)resultObject.get("reasonCode");
            throw new NullPointerException("验证码识别失败,错误码:"+reasonCode);
        }
        validate = (String)resultObject.getJSONObject("data").get("validate");
        //log.info("验证码成功==={msg:"+msg+",validate="+validate+",challenge="+challenge+"}");
    }

    @ApiOperation("发起请求将响应内容转为String")
    private String responseToStr(@ApiParam("http请求对象") HttpRequestBase request) throws IOException {
        CloseableHttpResponse execute = client.execute(request);
        HttpEntity entity = execute.getEntity();
        return EntityUtils.toString(entity);
    }
    private void login(String userName,String passWord) throws InterruptedException {
        System.setProperty("webdriver.chrome.driver","D:/chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        driver.manage().window().setSize(new Dimension(1024, 768));
        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.get("https://passport.zhaopin.com/login");
        driver.findElementById("loginName").sendKeys(userName);
        driver.findElementById("password").sendKeys(passWord);
        Thread.sleep(1000);
        driver.findElementById("submit").click();
        Thread.sleep(2000);
        GeetestCrawlerV2.geetestExecuter(driver);
    }
    @Test
    public void testGeetest() throws InterruptedException {
        login("1587955812215","19961023zhilian");
    }
}
