package com.lbh.cfld.springbootdemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import java.io.*;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Api(value="拉钩获取邮箱内简历详情")
public class Lagou {
    @ApiParam("登录时需要的参数")
    private String x_Anti_Forge_Token;
    @ApiParam("登录时需要的参数")
    private String x_Anti_Forge_Code;
    @ApiParam("客户端")
    public CloseableHttpClient client;
    @ApiParam("登录时需要的参数,拉钩验证是否通过")
    private String challenge;
//    private String loginName = "15879586926";
//    private String password = "d3381a54b9e1f145cb46fac679395948";
    private String loginName = "15989316116";//李永英
    private String password = "68b83baaef5a1d213e35f95bbdd8989f";//lyy0830

    private Logger log = Logger.getLogger(Lagou.class);
    @Test
    @ApiOperation("拉钩登录方法")
    public void login() throws Exception {
        RequestConfig requestConfig = RequestConfig.custom().setCircularRedirectsAllowed(true).build();
        client = HttpClients.custom().setDefaultRequestConfig(requestConfig).build();
        HttpPost getToken = new HttpPost("https://passport.lagou.com/login/login.html");
        getToken.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        CloseableHttpResponse tokenResponse = client.execute(getToken);
        HttpEntity tokenEntity = tokenResponse.getEntity();
        findToken(EntityUtils.toString(tokenEntity),false); //寻找x_Anti_Forge_Token 、x_Anti_Forge_Code
        loginRequest();
    }
    @ApiOperation("从html找到x_Anti_Forge_Token的值")
    public void findToken(@ApiParam("登录页面的html") String entityStr,Boolean flag) {
        String token = "";
        String code = "";
        Pattern compile2;
        if(flag){
            compile2 = Pattern.compile("window.X_Anti_Forge_Code = '[\\s\\S]{36}'");
        }else{
            compile2 = Pattern.compile("window.X_Anti_Forge_Code = '[\\d]{8}'");
        }
        Pattern compile = Pattern.compile("window.X_Anti_Forge_Token = '[\\s\\S]{36}'");
        Matcher m = compile.matcher(entityStr);
        m.find();
        token = m.group(0);
        Matcher m2 = compile2.matcher(entityStr);
        m2.find();
        code = m2.group(0);
        x_Anti_Forge_Token = token.substring(token.indexOf("'") + 1, token.lastIndexOf("'"));
        x_Anti_Forge_Code = code.substring(code.indexOf("'") + 1, code.lastIndexOf("'"));
    }

    @ApiOperation("验证码识别，成功后获得challenge参数")
    public void yzm() throws Exception {
        String yuekuai = "https://api.yuekuai.tech/api/geetest-sense?token=7xg5ATWau3&gt=66442f2f720bfc86799932d8ad2eb6c7&developer=5cc449b44bfdb38a8c96e513"; //打码平台
        HttpGet httpGet = new HttpGet(yuekuai);
        CloseableHttpResponse execute = client.execute(httpGet);
        HttpEntity entity = execute.getEntity();
        String entityStr = EntityUtils.toString(entity);
        JSONObject jsonObject = JSON.parseObject(entityStr);
        challenge = (String)jsonObject.getJSONObject("data").get("challenge");
        String validate = (String)jsonObject.getJSONObject("data").get("validate");
        String msg = (String)jsonObject.get("msg");
        int code =(int) jsonObject.get("code");
        if(code!=0){
            log.info("验证码平台失败====code:"+code);
            throw new Exception();
        }
        log.info("验证码成功==={msg:"+msg+",validate="+validate+",challenge="+challenge+"}");
        return;
    }

    @ApiOperation("填充表单参数进行登录认证")
    public void loginRequest() throws Exception {
        yzm();
        HttpPost loginRequest = new HttpPost("https://passport.lagou.com/login/login.json");
        loginRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        LinkedList<NameValuePair> nameValuePairs = new LinkedList<>();
        nameValuePairs.add(new BasicNameValuePair("isValidate", "true"));
        nameValuePairs.add(new BasicNameValuePair("username", loginName));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        nameValuePairs.add(new BasicNameValuePair("request_form_verifyCode", ""));
        nameValuePairs.add(new BasicNameValuePair("submit", ""));
        nameValuePairs.add(new BasicNameValuePair("challenge",challenge));
        loginRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
//        loginRequest.setHeader("X-Anit-Forge-Code", x_Anti_Forge_Code);
//        loginRequest.setHeader("X-Anit-Forge-Token", x_Anti_Forge_Token);
        CloseableHttpResponse response = client.execute(loginRequest);
        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        System.out.println(result);
        HttpGet grantGet = new HttpGet("https://passport.lagou.com/grantServiceTicket/grant.html");
        grantGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        grantGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        grantGet.setHeader("Accept-Encoding","gzip, deflate, br");
        grantGet.setHeader("Accept-Language","zh-CN,zh;q=0.9");
        client.execute(grantGet);
        HttpPost indexRequest = new HttpPost("https://www.lagou.com/");
        indexRequest.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        CloseableHttpResponse indexResponse = client.execute(indexRequest);
        HttpEntity indexResponseEntity = indexResponse.getEntity();
        String indexHtml = EntityUtils.toString(indexResponseEntity);
        System.out.println(indexHtml);
        FileWriter fileWriter = new FileWriter("d:\\lagou1.html");
        fileWriter.write(indexHtml);
        fileWriter.flush();
        fileWriter.close();
        return;
    }

    @ApiOperation("获取简历详情")
    public void getResumeDetails(String url) throws Exception {
        login();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        httpGet.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding","gzip, deflate, br");
        httpGet.setHeader("Accept-Language","zh-CN,zh;q=0.9");
        CloseableHttpResponse execute = client.execute(httpGet);
        HttpEntity entity = execute.getEntity();
        String s = EntityUtils.toString(entity);
        System.out.println(s);
        String substring = s.substring(s.indexOf("{\"id\":\""));
        String substring1 = substring.substring(0, substring.indexOf("};") + 1);
        JSONObject jsonObject = JSONObject.parseObject(substring1);
        String id = (String)jsonObject.get("id");
        analysis(url,s,id);
    }

    @Test
    public void test01() throws Exception {
        getResumeDetails("http://www.lagou.com/nearBy/preview.html?deliverId=1122732771400994816");
    }
    public void analysis(String url,String html,String directRid) throws IOException {
        HttpGet httpGet = new HttpGet("https://easy.lagou.com/resume/order/" + directRid + ".json");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
        httpGet.setHeader("Referer","https://easy.lagou.com/can/index.htm?from=gray&directRid="+directRid);
        String entity = this.responseToString(httpGet);
        JSONObject jsonObject = JSON.parseObject(entity);
        String name = (String)jsonObject.getJSONObject("content").getJSONObject("data").getJSONObject("resumeVo").get("name");
        String highestEducation = (String)jsonObject.getJSONObject("content").getJSONObject("data").getJSONObject("resumeVo").get("highestEducation");
        if(url.indexOf("nearBy/preview")!=-1){//附件形式的简历 pdf格式

//            HttpGet httpGet2 = new HttpGet("https://easy.lagou.com/resume/preview_info.json?resumeId=" + directRid);
//            httpGet2.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
//            httpGet2.setHeader("Referer","https://easy.lagou.com/can/index.htm?from=gray&directRid="+directRid);
//            client.execute(httpGet2);

//            HttpGet httpGet1 = new HttpGet("https://easy.lagou.com/resume/" + directRid + ".pdfa");
            HttpGet httpGet1 = new HttpGet("https://easy.lagou.com/resume/download.htm?resumeId="+directRid+"&challenge="+challenge);
            httpGet1.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36");
            httpGet1.setHeader("Accept-Encoding","gzip, deflate, sdch, br");
            httpGet1.setHeader("Accept-Language","zh-CN,zh;q=0.8");
            httpGet1.setHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            File file = new File("E:\\LagouPdf\\" + name + "-" + highestEducation + ".pdf");
            resposeDownload(file,httpGet1);
        }else{
            File file = new File("E:\\LagouHtml\\" + name +"-"+highestEducation+ ".html");
            downLoadHtml(file,JSONObject.toJSONString(jsonObject));
        }
    }
    //发起请求将响应内容转换String
    public String responseToString(HttpRequestBase request) throws IOException {
        CloseableHttpResponse execute = client.execute(request);
        HttpEntity entity = execute.getEntity();
        return EntityUtils.toString(entity);
    }
    //响应内容保存本地
    public void resposeDownload(File file,HttpRequestBase request) throws IOException {
        CloseableHttpResponse execute = client.execute(request);
        HttpEntity entity = execute.getEntity();
        if(!file.exists()){
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        entity.writeTo(outputStream);
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
}
