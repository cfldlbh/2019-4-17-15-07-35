package com.lbh.cfld.springbootdemo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
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
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Api(value="邮箱链接获取拉钩简历详情")
public class LaGou2 {
    private String loginName;
    private String password;
    private String cookieFile = "src/main/resources/LagouCookie/";
    private String downLoadDirectory = "E:\\LagouDownTest\\";//本地保存简历地址
    private BasicCookieStore cookieStore = new BasicCookieStore();
    private CloseableHttpClient client;
    @ApiParam("登录时需要的参数")
    private String x_Anti_Forge_Token;
    @ApiParam("登录时需要的参数")
    private String x_Anti_Forge_Code;
    @ApiParam("登录时需要的参数,拉钩验证是否通过")
    private String challenge;

    public LaGou2(String userName,String password){
        try {
            this.loginName = userName;
            this.password = this.encryptionPw(password);
            //设置请求头带浏览器标识
            List<Header> defaultHeaders = Arrays.asList(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36"));
            RequestConfig requestConfig = RequestConfig.custom().setCircularRedirectsAllowed(true).setConnectTimeout(30000).setSocketTimeout(30000).build();
            cookieStore = readCookies(this.loginName);//文件中读取cookies转换为cookie对象
            client = HttpClients.custom().setDefaultHeaders(defaultHeaders).setDefaultRequestConfig(requestConfig).setMaxConnPerRoute(100)
                    .setMaxConnTotal(300).setDefaultCookieStore(cookieStore).build();//创建httpclient对象设置默认请求头，自动重定向，最大路由数100和最大连接数300，添加默认cookie
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    @ApiOperation("登录操作")
    private String login() throws Exception {
        HttpPost getToken = new HttpPost("https://passport.lagou.com/login/login.html");
        searchToken(responseToStr(getToken));//页面上寻找x_Anti_Forge_Token
        try{
            approve();///验证码验证方法
        }catch(Exception e){
            return "验证码失败";
        }
        HttpPost loginRequest = new HttpPost("https://passport.lagou.com/login/login.json");
        LinkedList<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("isValidate", "true"));
        nameValuePairs.add(new BasicNameValuePair("username", loginName));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("request_form_verifyCode", ""));
        nameValuePairs.add(new BasicNameValuePair("submit", ""));
        nameValuePairs.add(new BasicNameValuePair("challenge",challenge));
        loginRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
        loginRequest.setHeader("X-Anit-Forge-Code", x_Anti_Forge_Code);//没有这2个参数拉钩会提示请勿重复提交页面
        loginRequest.setHeader("X-Anit-Forge-Token", x_Anti_Forge_Token);
        String resp = responseToStr(loginRequest);
        String message = (String)JSONObject.parseObject(resp).get("message");
        if( ! message.equals("操作成功")){//返回拉钩的错误提示。如果验证码错误会抛异常
            return message;
        }
        HttpGet grantGet = new HttpGet("https://passport.lagou.com/grantServiceTicket/grant.html");
        return responseToStr(grantGet);//返回登录之前的页面
    }
    @ApiOperation("验证码认证")
    private void approve() throws Exception {
        String referer = "https://passport.lagou.com";
        String wtype = "geetest";
        String yuekuai = "http://api.ddocr.com/api/gateway.jsonp?gt=66442f2f720bfc86799932d8ad2eb6c7&referer="+referer+"&wtype="+wtype+"&secretkey=112cfa4dbdb6408d96a07fc3f7075844"; //打码平台
        HttpGet httpGet = new HttpGet(yuekuai);
        String s = responseToStr(httpGet);
        JSONObject jsonObject = JSON.parseObject(s);
        int status =(int) jsonObject.get("status");
        if(status == -1){
            //log.info("验证码平台失败====code:"+code);
            int reasonCode = (int)jsonObject.get("reasonCode");
            throw new Exception("验证码识别失败,请重试！错误码:"+reasonCode);
        }
        challenge = (String)jsonObject.getJSONObject("data").get("challenge");
        //log.info("验证码成功==={msg:"+msg+",validate="+validate+",challenge="+challenge+"}");
    }

    @ApiOperation("从html找到x_Anti_Forge_Token的值")
    private void searchToken(String htmlText){
        String token = "";
        String code = "";
        Pattern compile2 = Pattern.compile("window.X_Anti_Forge_Code = '[\\d]{8}'");
        Pattern compile = Pattern.compile("window.X_Anti_Forge_Token = '[\\s\\S]{36}'");
        Matcher m = compile.matcher(htmlText);
        m.find();
        token = m.group(0);
        Matcher m2 = compile2.matcher(htmlText);
        m2.find();
        code = m2.group(0);
        x_Anti_Forge_Token = token.substring(token.indexOf("'") + 1, token.lastIndexOf("'"));
        x_Anti_Forge_Code = code.substring(code.indexOf("'") + 1, code.lastIndexOf("'"));
    }

    @ApiOperation("发起请求将响应内容转为String")
    private String responseToStr(@ApiParam("http请求对象")HttpRequestBase request) throws IOException {
        //TODO
        CloseableHttpResponse execute = client.execute(request);
        HttpEntity entity = execute.getEntity();
        return EntityUtils.toString(entity);
    }
    public void downLoadHtml(File file,String html) throws IOException {
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(html);
        fileWriter.flush();
        fileWriter.close();
    }
    //简历下载,需要执行js生成X_HTTP_TOKEN的cookie到服务器验证的下载
    private String resposeDownload2(@ApiParam("http请求对象") HttpRequestBase request) throws IOException, ScriptException, NoSuchMethodException {
        HttpGet checkRequest = new HttpGet("https://easy.lagou.com/wafcheck.json");
        String headValue;
        request.setHeader("Accept-Language","zh-CN,zh;q=0.8");
        request.setHeader("Accept","*/*");
        request.setHeader("Accept-Encoding","gzip, deflate, sdch, br");
        request.setHeader("Host","easy.lagou.com");
        request.setHeader("Connection","keep-alive");
        BasicClientCookie basicClientCookie = new BasicClientCookie("X_HTTP_TOKEN", "fa9d04ce1525cb6a7955417551decee67af8fb53d7");
        basicClientCookie.setDomain("easy.lagou.com");
        basicClientCookie.setPath("/");
        cookieStore.addCookie(basicClientCookie);
//        BasicClientCookie basicClientCookie1 = new BasicClientCookie("X_MIDDLE_TOKEN", "b44d9d3ea6905433ba85338e2ab68f96");
//        basicClientCookie1.setDomain("easy.lagou.com");
//        basicClientCookie1.setPath("/");
//        cookieStore.addCookie(basicClientCookie1);
        CloseableHttpResponse checkResponse = client.execute(checkRequest);
        String date = checkResponse.getLastHeader("Date").getValue();
        List<Cookie> cookies = cookieStore.getCookies();
        String httpToken = "";
        for(Cookie c : cookies){
            if(c.getName().equals("user_trace_token")){
                String traceToken  = c.getValue();
                httpToken = executeJs(date,traceToken);//执行js拿到httpToken参数
            }
        }
        for(Cookie c : cookies){
            if(c.getName().equals("X_HTTP_TOKEN")){
                BasicClientCookie bcCookie = new BasicClientCookie("X_HTTP_TOKEN", httpToken);
                bcCookie.setDomain("easy.lagou.com");
                bcCookie.setPath("/");
                cookieStore.addCookie(bcCookie);
            }
        }
        CloseableHttpResponse execute = client.execute(request);
        Header lastHeader = execute.getLastHeader("Content-Disposition");
        if(lastHeader==null){
            return "fail";
        }
        headValue = lastHeader.getValue();
        headValue = new String(headValue.getBytes("ISO8859-1"), "utf-8");
        String fileName = headValue.substring(headValue.indexOf("\"") + 1, headValue.length() - 1);
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        File file = new File(downLoadDirectory+suffix+"\\"+fileName);
        HttpEntity entity = execute.getEntity();
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        entity.writeTo(outputStream);
        outputStream.close();
        return downLoadDirectory+suffix+"\\"+fileName;
    }
    //简历下载
    private String resposeDownload(@ApiParam("http请求对象") HttpRequestBase request) throws IOException, ScriptException, NoSuchMethodException {
        String headValue;
        request.setHeader("Accept-Language","zh-CN,zh;q=0.8");
        CloseableHttpResponse execute = client.execute(request);
        Header lastHeader = execute.getLastHeader("Content-Disposition");
        if(lastHeader==null){
            return resposeDownload2(request);
        }
        headValue = lastHeader.getValue();
        headValue = new String(headValue.getBytes("ISO8859-1"), "utf-8");
        String fileName = headValue.substring(headValue.indexOf("\"") + 1, headValue.length() - 1);
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        File file = new File(downLoadDirectory+suffix+"\\"+fileName);
        HttpEntity entity = execute.getEntity();
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        entity.writeTo(outputStream);
        outputStream.close();
        return downLoadDirectory+suffix+"\\"+fileName;
    }

    @ApiOperation("获取简历详情")
    public String getResumeDetails(String url, HttpServletResponse resp) throws Exception {
        String detailsHtml;
        String resumeId;
        Document document;
        if(url.indexOf("http") == -1){
            return "检查url是否正确！";
        }
        detailsHtml = redirect(url);
        if(detailsHtml.length()<50){//小于50获取到的是简历ID，大于50要从页面截取到简历ID
            resumeId = detailsHtml;
        }else{
            document = Jsoup.parse(detailsHtml);
            if(document.getElementById("isVisiable_request_form_verifyCode")!=null ){//true表示被拉钩重定向到登录页面了需要登录
                detailsHtml = this.login(); //登录操作 成功是返回页面，失败返回拉钩的登录错误提示。错误提示应该不会操作50个字符
                if(detailsHtml.length()<50){
                    return "登录失败！"+detailsHtml;
                }
            }
            if(detailsHtml.indexOf("{\"id\":\"")==-1){
                return "简历id未找到！";
            }
            this.saveCookies(this.loginName);
            String sub = detailsHtml.substring(detailsHtml.indexOf("{\"id\":\""));//从页面截取到简历ID
            String jsonString = sub.substring(0, sub.indexOf("};") + 1);
            resumeId = (String)JSONObject.parseObject(jsonString).get("id");//转为json对象获取重定向到简历的id
        }
        HttpGet getInfo = new HttpGet("https://easy.lagou.com/resume/order/" + resumeId + ".json");
        String resumeInfo = responseToStr(getInfo);
        JSONObject resumeJson = JSON.parseObject(resumeInfo);
        String name = (String)resumeJson.getJSONObject("content").getJSONObject("data").getJSONObject("resumeVo").get("name");
        String highestEducation = (String)resumeJson.getJSONObject("content").getJSONObject("data").getJSONObject("resumeVo").get("highestEducation");
        HttpGet downLoadFile = new HttpGet("https://easy.lagou.com/resume/download.htm?resumeId="+resumeId);
        String fileName = resposeDownload(downLoadFile);
        if(fileName.equals("fail")){
            return "文件下载失败";
        }else{
            File downFile = new File(fileName);
            resp.setContentLength((int)downFile.length());
            resp.setHeader("Content-Type","application/octet-stream;charset=UTF-8");
            String decode = URLEncoder.encode(downFile.getName(),"UTF-8");
            resp.setHeader("Content-Disposition","filename='"+decode+"'");
            FileInputStream inputStream = new FileInputStream(downFile);
            byte[] data = new byte[(int)downFile.length()];
            inputStream.read(data);
            inputStream.close();
            OutputStream stream = resp.getOutputStream();
            stream.write(data);
            stream.flush();
            stream.close();
        }
        this.saveCookies(this.loginName);
        client.close();
        return "success";
    }

    @ApiOperation("获取简历id并对重定向做出处理")
    public String redirect(String url) throws IOException {
        HttpGet get = new HttpGet(url);
        HttpParams params = new BasicHttpParams();
        params.setParameter("http.protocol.handle-redirects", false); // 默认不让重定向
        get.setParams(params);
        CloseableHttpResponse response = client.execute(get);
        if(response.getStatusLine().getStatusCode() == 302 || response.getStatusLine().getStatusCode() == 301){
            String location = response.getFirstHeader("Location").getValue();
            if(location.indexOf("http://easy.lagou.com/can/index.htm") != -1){
                String directRid = location.substring(location.lastIndexOf("=")+1);
                EntityUtils.consume(response.getEntity());
                return directRid;
            }else {
                EntityUtils.consume(response.getEntity());
                return redirect(location);
            }
        }else{
            String entity = EntityUtils.toString(response.getEntity());
            EntityUtils.consume(response.getEntity());
            return entity;
        }
    }
    @Test
    public void testMd5(){
        String lyy0830 = encryptionPw("lyy0830");
    }
    //获取加密的密码
    public String encryptionPw(String password){
        String encodeStr= DigestUtils.md5Hex(password);
        return DigestUtils.md5Hex("veenike"+encodeStr+"veenike");
    }
    public void saveCookies(String userName) throws IOException {
        FileOutputStream fileInputStream = new FileOutputStream(cookieFile+userName+".ser");
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileInputStream);
        objectOutputStream.writeObject(cookieStore);
        objectOutputStream.close();
    }

    public BasicCookieStore readCookies(String userName) throws IOException, ClassNotFoundException {
        File file = new File(cookieFile+userName+".ser");
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        BasicCookieStore cookieStore = new BasicCookieStore();
        FileInputStream fileInputStream = new FileInputStream(cookieFile+userName+".ser");
        if(fileInputStream.available()!=0){
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            cookieStore = (BasicCookieStore)objectInputStream.readObject();
        }
        return cookieStore;
    }
    @ApiOperation("执行js拿到X_HTTP_TOKEN参数")
    public String executeJs(String responseTime,String trace) throws IOException, ScriptException, NoSuchMethodException {
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
    private String openChrome (String url) throws InterruptedException, IOException {
        System.setProperty("webdriver.chrome.driver","D:/chromedriver.exe");
        ChromeDriver driver = new ChromeDriver();
        List<Cookie> cookies = cookieStore.getCookies();
        HashMap<String, ArrayList> map = new HashMap<>();
        for(Cookie c: cookies){
            if(c.getDomain().equals("/")){
                driver.manage().addCookie(new org.openqa.selenium.Cookie(c.getName(),c.getValue(),c.getDomain(),c.getPath(),null));
            }else{
                if(!(map.get(c.getDomain())!=null && map.get(c.getDomain()).size() >0 )){
                    ArrayList<Cookie> objects = new ArrayList<Cookie>();
                    objects.add(c);
                    map.put(c.getDomain(),objects);
                }else{
                    ArrayList list = map.get(c.getDomain());
                    list.add(c);
                }
            }
        }
        for(String key : map.keySet()){
            driver.get("https://"+key);
            Thread.sleep(3000);
            ArrayList<Cookie> arrayList = map.get(key);
            for(Cookie c : arrayList){
                driver.manage().addCookie(new org.openqa.selenium.Cookie(c.getName(),c.getValue(),c.getDomain(),c.getPath(),null));
            }
        }
        driver.get(url);
        String pageSource = driver.getPageSource();
//        Set<org.openqa.selenium.Cookie> webCookie = driver.manage().getCookies();
//        BasicCookieStore basicCookieStore = new BasicCookieStore();
//        for(org.openqa.selenium.Cookie c: webCookie ){
//            BasicClientCookie clientCookie = new BasicClientCookie(c.getName(), c.getValue());
//            clientCookie.setPath(c.getPath());
//            clientCookie.setDomain(c.getDomain());
//            clientCookie.setExpiryDate(c.getExpiry());
//            basicCookieStore.addCookie(clientCookie);
//
//        }
//        cookieStore = basicCookieStore;
//        saveCookies(this.loginName);
        return pageSource;
    }
}
