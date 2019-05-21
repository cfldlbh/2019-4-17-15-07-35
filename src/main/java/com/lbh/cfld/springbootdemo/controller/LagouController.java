package com.lbh.cfld.springbootdemo.controller;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;

import com.lbh.cfld.springbootdemo.resp.RespResultGenerator;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.cookie.Cookie;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mysql.jdbc.StringUtils;

import com.lbh.cfld.springbootdemo.resp.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;




@Api("获取拉钩简历详情")
@Controller
public class LagouController {
	@RequestMapping(value = "/getDetails",method=RequestMethod.GET)
	public void getDetails(String userName, String passWord, String url, HttpServletResponse resp) throws Exception{
		if(StringUtils.isNullOrEmpty(userName) ||StringUtils.isNullOrEmpty(passWord) || StringUtils.isNullOrEmpty(url)){
		    message(resp,"用户名、密码、链接不能为空!");
			return;
		}
        LaGou2 laGou2 = new LaGou2(userName, passWord);
		try{
            String resumeDetails = laGou2.getResumeDetails(url,resp);
            if(resumeDetails.equals("success")){
                return ;
            }
            message(resp,resumeDetails);
        }catch (Exception e){
            message(resp,"获取失败");
		    return ;
        }
	}
	public void message(HttpServletResponse resp,String msg) throws IOException {
        File downFile = new File("C:\\message.txt");
        FileWriter fileWriter = new FileWriter(downFile);
        fileWriter.write(msg);
        fileWriter.flush();
        fileWriter.close();
        resp.setContentLength((int)downFile.length());
        resp.setHeader("Content-Type","application/octet-stream;charset=UTF-8");
        resp.setHeader("Content-Disposition","filename='message.txt'");
        FileInputStream inputStream = new FileInputStream(downFile);
        byte[] data = new byte[(int)downFile.length()];
        inputStream.read(data);
        inputStream.close();
        OutputStream stream = resp.getOutputStream();
        stream.write(data);
        stream.flush();
        stream.close();
    }
//
//    public BasicCookieStore readCookies(String userName) throws IOException, ClassNotFoundException {
//    	File file = new File(cookieFilePath+userName+".ser");
//    	if(!file.exists()){
//    		file.getParentFile().mkdirs();
//    		file.createNewFile();
//    	}
//    	BasicCookieStore cookieStore = new BasicCookieStore();
//        FileInputStream fileInputStream = new FileInputStream(cookieFilePath+userName+".ser");
//        if(fileInputStream.available()!=0){
//            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
//            cookieStore = (BasicCookieStore)objectInputStream.readObject();
//        }
//        return cookieStore;
//    }
//    public void saveCookies(String userName,BasicCookieStore cookieStore) throws IOException {
//        FileOutputStream fileInputStream = new FileOutputStream(cookieFilePath+userName+".ser");
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileInputStream);
//        objectOutputStream.writeObject(cookieStore);
//        objectOutputStream.close();
//    }
//
//    @ApiOperation("验证码认证")
//    private void approve(CloseableHttpClient client) throws RuntimeException,IOException {
//        String referer = "https://passport.lagou.com";
//        String wtype = "geetest";
//        String yuekuai = "http://api.ddocr.com/api/gateway.jsonp?gt=66442f2f720bfc86799932d8ad2eb6c7&referer="+referer+"&wtype="+wtype+"&secretkey=112cfa4dbdb6408d96a07fc3f7075844"; //打码平台
//        HttpGet httpGet = new HttpGet(yuekuai);
//        String s = responseToStr(httpGet,client);
//        JSONObject jsonObject = JSON.parseObject(s);
//        int status =(int) jsonObject.get("status");
//        if(status == -1){
//            //log.info("验证码平台失败====code:"+code);
//            int reasonCode = (int)jsonObject.get("reasonCode");
//            throw new RuntimeException("验证码识别失败,错误码:"+reasonCode);
//        }
//        challenge = (String)jsonObject.getJSONObject("data").get("challenge");
//        //log.info("验证码成功==={msg:"+msg+",validate="+validate+",challenge="+challenge+"}");
//    }
//
//    @ApiOperation("发起请求将响应内容转为String")
//    private String responseToStr(@ApiParam("http请求对象")HttpRequestBase request,CloseableHttpClient client) throws IOException {
//        //TODO
//        CloseableHttpResponse execute = client.execute(request);
//        HttpEntity entity = execute.getEntity();
//        return EntityUtils.toString(entity);
//    }
//
//    @ApiOperation("登录操作")
//    private String login(CloseableHttpClient client,String loginName,String password,BasicCookieStore cookieStore) throws RuntimeException, IOException {
//    	password = encryptionPw(password);
//        HttpPost getToken = new HttpPost("https://passport.lagou.com/login/login.html");
//        searchToken(responseToStr(getToken,client));//页面上寻找x_Anti_Forge_Token
//        approve(client);///验证码验证方法
//        HttpPost loginRequest = new HttpPost("https://passport.lagou.com/login/login.json");
//        LinkedList<NameValuePair> nameValuePairs = new LinkedList<NameValuePair>();
//        nameValuePairs.add(new BasicNameValuePair("isValidate", "true"));
//        nameValuePairs.add(new BasicNameValuePair("username", loginName));
//        nameValuePairs.add(new BasicNameValuePair("password", password));
//        nameValuePairs.add(new BasicNameValuePair("request_form_verifyCode", ""));
//        nameValuePairs.add(new BasicNameValuePair("submit", ""));
//        nameValuePairs.add(new BasicNameValuePair("challenge",challenge));
//        loginRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
//        loginRequest.setHeader("X-Anit-Forge-Code", x_Anti_Forge_Code);//没有这2个参数拉钩会提示请勿重复提交页面
//        loginRequest.setHeader("X-Anit-Forge-Token", x_Anti_Forge_Token);
//        String resp = responseToStr(loginRequest,client);
//        String message = (String)JSONObject.parseObject(resp).get("message");
//        if( ! message.equals("操作成功")){//操作失败返回拉钩的错误提示。验证码错误抛异常
//            return message;
//        }
//        HttpGet grantGet = new HttpGet("https://passport.lagou.com/grantServiceTicket/grant.html");
//        String resutl = responseToStr(grantGet, client);
//        this.saveCookies(loginName,cookieStore);
//        return resutl;//返回登录之前的页面
//    }
//
//    @ApiOperation("从html找到x_Anti_Forge_Token的值")
//    private void searchToken(String htmlText){
//        String token = "";
//        String code = "";
//        Pattern compile2 = Pattern.compile("window.X_Anti_Forge_Code = '[\\d]{8}'");
//        Pattern compile = Pattern.compile("window.X_Anti_Forge_Token = '[\\s\\S]{36}'");
//        Matcher m = compile.matcher(htmlText);
//        m.find();
//        token = m.group(0);
//        Matcher m2 = compile2.matcher(htmlText);
//        m2.find();
//        code = m2.group(0);
//        x_Anti_Forge_Token = token.substring(token.indexOf("'") + 1, token.lastIndexOf("'"));
//        x_Anti_Forge_Code = code.substring(code.indexOf("'") + 1, code.lastIndexOf("'"));
//    }
//
//    @ApiOperation("获取简历id并对重定向做出处理")
//    public String redirect(String url,CloseableHttpClient client) throws IOException {
//        HttpGet get = new HttpGet(url);
//        HttpParams params = new BasicHttpParams();
//        params.setParameter("http.protocol.handle-redirects", false); // 默认不让重定向
//        get.setParams(params);
//        CloseableHttpResponse response = client.execute(get);
//        if(response.getStatusLine().getStatusCode() == 302 || response.getStatusLine().getStatusCode() == 301){
//            String location = response.getFirstHeader("Location").getValue();
//            if(location.indexOf("http://easy.lagou.com/can/index.htm") != -1){
//                String directRid = location.substring(location.lastIndexOf("=")+1);
//                EntityUtils.consume(response.getEntity());
//                return directRid;
//            }else {
//                EntityUtils.consume(response.getEntity());
//                return redirect(location,client);
//            }
//        }else{
//            String entity = EntityUtils.toString(response.getEntity());
//            EntityUtils.consume(response.getEntity());
//            return entity;
//        }
//    }
//
//    @ApiOperation("获取简历详情")
//    private ResponseEntity<ResponseResult<JSONObject>> getResumeDetails(String url,CloseableHttpClient client,String userName,String passWord,BasicCookieStore cookieStore) throws Exception {
//        String detailsHtml;
//        String resumeId;
//        Document document;
//        if(url.indexOf("http") == -1){
//            return RespResultGenerator.genError(null,"错误！");
//        }
//        detailsHtml = redirect(url,client);
//        if(detailsHtml.length()<50){//小于50获取到的是简历ID，大于50要从页面截取到简历ID
//            resumeId = detailsHtml;
//        }else{
//            document = Jsoup.parse(detailsHtml);
//            if(document.getElementById("isVisiable_request_form_verifyCode")!=null ){//true表示被拉钩重定向到登录页面了需要登录
//                detailsHtml = this.login(client,userName,passWord,cookieStore); //登录操作 成功是返回页面，失败返回拉钩的登录错误提示。错误提示应该不会操作50个字符
//                if(detailsHtml.length()<50){
//                    throw new NullPointerException("登录失败！"+detailsHtml);
//                }
//            }
//            if(detailsHtml.indexOf("{\"id\":\"")==-1){
//                return RespResultGenerator.genError(null,"错误！");
//            }
//            String sub = detailsHtml.substring(detailsHtml.indexOf("{\"id\":\""));//从页面截取到简历ID
//            String jsonString = sub.substring(0, sub.indexOf("};") + 1);
//            resumeId = (String)JSONObject.parseObject(jsonString).get("id");//转为json对象获取重定向到简历的id
//        }
//        HttpGet getInfo = new HttpGet("https://easy.lagou.com/resume/order/" + resumeId + ".json");
//        String resumeInfo = responseToStr(getInfo,client);
//        JSONObject resumeJson = JSON.parseObject(resumeInfo);
//        String name = (String)resumeJson.getJSONObject("content").getJSONObject("data").getJSONObject("resumeVo").get("name");
//        String highestEducation = (String)resumeJson.getJSONObject("content").getJSONObject("data").getJSONObject("resumeVo").get("highestEducation");
//        if(url.indexOf("nearBy/preview")!=-1){//true为非拉勾模板简历，有pdf，word，doc,txt格式
//            //Thread.sleep((2+(int)(Math. random()*(15-2)) )*1000);
//            HttpGet downLoadFile = new HttpGet("https://easy.lagou.com/resume/download.htm?resumeId="+resumeId);
//            String fileName = resposeDownload(downLoadFile, client, cookieStore);
//            return RespResultGenerator.genOK(null,"简历非拉钩模板！文件下载到："+downLoadDirectory);
//        }else{
//        	this.saveCookies(userName,cookieStore);
//        	return RespResultGenerator.genOK(resumeJson, "获取简历详情成功");
//        }
//    }
//
//    //简历下载
//    private String resposeDownload(@ApiParam("http请求对象") HttpRequestBase request,CloseableHttpClient client,BasicCookieStore cookieStore) throws IOException, ScriptException, NoSuchMethodException {
//        String headValue;
//        request.setHeader("Accept-Language","zh-CN,zh;q=0.8");
//        CloseableHttpResponse execute = client.execute(request);
//        Header lastHeader = execute.getLastHeader("Content-Disposition");
//        if(lastHeader==null){
//            return resposeDownload2(request,client,cookieStore);
//        }
//        headValue = lastHeader.getValue();
//        headValue = new String(headValue.getBytes("ISO8859-1"), "utf-8");
//        String fileName = headValue.substring(headValue.indexOf("\"") + 1, headValue.length() - 1);
//        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//        File file = new File(downLoadDirectory+suffix+"\\"+fileName);
//        HttpEntity entity = execute.getEntity();
//        if(!file.exists()){
//            file.getParentFile().mkdirs();
//            file.createNewFile();
//        }
//        FileOutputStream outputStream = new FileOutputStream(file);
//        entity.writeTo(outputStream);
//        outputStream.close();
//        return downLoadDirectory+suffix+"\\"+fileName;
//    }
//
//    //简历下载,需要执行js生成X_HTTP_TOKEN的cookie到服务器验证的下载
//    private String resposeDownload2(@ApiParam("http请求对象") HttpRequestBase request,CloseableHttpClient client,BasicCookieStore cookieStore) throws IOException, ScriptException, NoSuchMethodException {
//        HttpGet checkRequest = new HttpGet("https://easy.lagou.com/wafcheck.json");
//        String headValue;
//        request.setHeader("Accept-Language","zh-CN,zh;q=0.8");
//        request.setHeader("Accept","*/*");
//        request.setHeader("Accept-Encoding","gzip, deflate, sdch, br");
//        request.setHeader("Host","easy.lagou.com");
//        request.setHeader("Connection","keep-alive");
//        BasicClientCookie basicClientCookie = new BasicClientCookie("X_HTTP_TOKEN", "fa9d04ce1525cb6a7955417551decee67af8fb53d7");
//        basicClientCookie.setDomain("easy.lagou.com");
//        basicClientCookie.setPath("/");
//        cookieStore.addCookie(basicClientCookie);
////        BasicClientCookie basicClientCookie1 = new BasicClientCookie("X_MIDDLE_TOKEN", "b44d9d3ea6905433ba85338e2ab68f96");
////        basicClientCookie1.setDomain("easy.lagou.com");
////        basicClientCookie1.setPath("/");
////        cookieStore.addCookie(basicClientCookie1);
//        CloseableHttpResponse checkResponse = client.execute(checkRequest);
//        String date = checkResponse.getLastHeader("Date").getValue();
//        List<Cookie> cookies = cookieStore.getCookies();
//        String httpToken = "";
//        for(Cookie c : cookies){
//            if(c.getName().equals("user_trace_token")){
//                String traceToken  = c.getValue();
//                httpToken = executeJs(date,traceToken);//执行js拿到httpToken参数
//            }
//        }
//        for(Cookie c : cookies){
//            if(c.getName().equals("X_HTTP_TOKEN")){
//                BasicClientCookie bcCookie = new BasicClientCookie("X_HTTP_TOKEN", httpToken);
//                bcCookie.setDomain("easy.lagou.com");
//                bcCookie.setPath("/");
//                cookieStore.addCookie(bcCookie);
//            }
//        }
//        CloseableHttpResponse execute = client.execute(request);
//        Header lastHeader = execute.getLastHeader("Content-Disposition");
//        if(lastHeader==null){
//            return "fail";
//        }
//        headValue = lastHeader.getValue();
//        headValue = new String(headValue.getBytes("ISO8859-1"), "utf-8");
//        String fileName = headValue.substring(headValue.indexOf("\"") + 1, headValue.length() - 1);
//        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
//        File file = new File(downLoadDirectory+suffix+"\\"+fileName);
//        HttpEntity entity = execute.getEntity();
//        if(!file.exists()){
//            file.getParentFile().mkdirs();
//            file.createNewFile();
//        }
//        FileOutputStream outputStream = new FileOutputStream(file);
//        entity.writeTo(outputStream);
//        outputStream.close();
//        return file.getName();
//    }
//
//    @ApiOperation("执行js拿到X_HTTP_TOKEN参数")
//    public String executeJs(String responseTime,String trace) throws IOException, ScriptException, NoSuchMethodException {
//        ScriptEngineManager manager = new ScriptEngineManager();
//        ScriptEngine engine = manager.getEngineByName("javascript");
//        FileReader reader = new FileReader(jsFileName);   // 执行指定脚本
//        engine.eval(reader);
//        String guid = "";
//        if(engine instanceof Invocable) {
//            Invocable invoke = (Invocable)engine;    // 调用merge方法，并传入两个参数
//            guid = (String) invoke.invokeFunction("getlagou",responseTime,trace);//服务器响应时间
//        }
//        reader.close();
//        return guid;
//    }
//
//    //获取加密的密码
//    public String encryptionPw(String password){
//        String encodeStr= DigestUtils.md5Hex(password);
//        return DigestUtils.md5Hex("veenike"+encodeStr+"veenike");
//    }
//
//    public String getResumeDetails2(BasicCookieStore cookie,String url,String userName,String passWord,CloseableHttpClient client) throws IOException, InterruptedException, ScriptException, NoSuchMethodException {
//    	 System.setProperty("webdriver.chrome.driver","D:/chromedriver.exe");
//         ChromeDriver driver = new ChromeDriver();
//         driver.get("https://passport.lagou.com");
//         List<Cookie> cookies = cookie.getCookies();
//         for (Cookie c : cookies){
//             if(!c.getDomain().equals("api.ddocr.com") && !c.getDomain().equals("passport.zhaopin.com") && !c.getDomain().equals(".lagou.com") && !c.getDomain().equals(".easy.lagou.com") && !c.getDomain().equals("easy.lagou.com")&& !c.getDomain().equals("www.lagou.com")){
//                 driver.manage().addCookie(new org.openqa.selenium.Cookie(c.getName(),c.getValue(),c.getDomain(),c.getPath(),null));
//             }
//         }
//         Thread.sleep(10000);
//         driver.get(url);
//         if(driver.findElementsById("isVisiable_request_form_verifyCode").size() > 0){
//             String detailsHtml = this.login(client,userName,passWord,cookie); //登录操作 成功是返回页面，失败返回拉钩的登录错误提示。错误提示应该不会操作50个字符
//             if(detailsHtml.length()<50){
//                 throw new NullPointerException("登录失败！"+detailsHtml);
//             }
//             List<Cookie> cookies2 = cookie.getCookies();
//             for (Cookie c : cookies2){
//                 if(!c.getDomain().equals("api.ddocr.com") && !c.getDomain().equals("passport.zhaopin.com") && !c.getDomain().equals(".lagou.com") && !c.getDomain().equals(".easy.lagou.com") && !c.getDomain().equals("easy.lagou.com")&& !c.getDomain().equals("www.lagou.com")){
//                     driver.manage().addCookie(new org.openqa.selenium.Cookie(c.getName(),c.getValue(),c.getDomain(),c.getPath(),null));
//                 }
//             }
//             driver.get(url);
//         }
//        if(url.indexOf("nearBy/preview")!=-1){//true为非拉勾模板简历，有pdf，word，doc,txt格式
//            String elementId = driver.findElementById("resumePreviewContainer").findElements(By.tagName("div")).get(0).getAttribute("data-resume-id");
//            HttpGet downLoadFile = new HttpGet("https://easy.lagou.com/resume/download.htm?resumeId="+elementId);
//            String fileName = resposeDownload(downLoadFile, client, cookie);
//            FileInputStream in = new FileInputStream(new File(fileName));
//            int size=in.available();
//            byte[] buffer=new byte[size];
//            in.read(buffer);
//            in.close();
//            String str=new String(buffer,"UTF-8");
//            return str;
//        }else{
//            String pageSource = driver.getPageSource();
//            return pageSource;
//        }
//
//    }

}
