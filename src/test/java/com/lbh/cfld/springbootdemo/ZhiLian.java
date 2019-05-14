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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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

    private ResponseResult getResume(String loginName,String passWord) throws Exception {
        try {
            approve();
        }catch (NullPointerException e){
            ResponseResult<Object> objectResponseResult = new ResponseResult<>();
            objectResponseResult.setSuccess(false);
            objectResponseResult.setMessage("验证码验证步骤失败！");
            return objectResponseResult;
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
            ResponseResult<Object> objectResponseResult = new ResponseResult<>();
            objectResponseResult.setSuccess(false);
            objectResponseResult.setMessage((String) jsonObject.get("msg"));
            return objectResponseResult;
        }
        //获得简历ID和简历ResumeNumber
        new 
//        HttpGet getDetail = new HttpGet("https://fe-api.zhaopin.com/c/i/user/detail");
//        String detail = responseToStr(getDetail);
//        JSONObject detailObject = JSONObject.parseObject(detail);
//        if((int)detailObject.get("code")!=200){
//            ResponseResult<Object> objectResponseResult = new ResponseResult<>();//获取简历必要参数失败
//            objectResponseResult.setSuccess(false);
//            objectResponseResult.setMessage("登录成功，但获取必要参数失败！");
//            return objectResponseResult;
//        }
//        String resumeId = (String)detailObject.getJSONObject("data").getJSONObject("Resume").get("Id");//简历ID
//        String resumeNumber = (String)detailObject.getJSONObject("data").getJSONObject("Resume").get("ResumeNumber");//简历ResumeNumber
//        String at="";//获取简历详情必要的参数
//        String rt="";//获取简历详情必要的参数
//        List<Cookie> cookies = cookieStore.getCookies();
//        for(Cookie c : cookies){
//            if(c.getName().equals("Token")){
//                at = c.getValue();
//            }
//            if(c.getName().equals("rt")){
//                rt = c.getValue();
//            }
//        }
//        if(at.length()==0 || rt.length()==0){
//            //获取必要参数失败
//            ResponseResult<Object> objectResponseResult = new ResponseResult<>();//获取简历必要参数失败
//            objectResponseResult.setSuccess(false);
//            objectResponseResult.setMessage("登录成功，但获取必要参数失败！");
//            return objectResponseResult;
//        }
//        StringBuilder stringBuilder = new StringBuilder("https://fe-api.zhaopin.com/c/i/resume?");
//        LinkedList<BasicNameValuePair> objects = new LinkedList<>();
//        objects.add(new BasicNameValuePair("resumeId",resumeId));
//        objects.add(new BasicNameValuePair("resumeNumber",resumeNumber));
//        objects.add(new BasicNameValuePair("lang","1"));
//        objects.add(new BasicNameValuePair("at",at));
//        objects.add(new BasicNameValuePair("rt",rt));
//        for(BasicNameValuePair b : objects){
//            stringBuilder.append(b.getName()).append("=").append(b.getValue()).append("&");
//        }
//        HttpGet getResumeRequest = new HttpGet(String.valueOf(stringBuilder));
//        String resumeJson = responseToStr(getResumeRequest);
//        JSONObject objectDetail = null;
//        try {
//            objectDetail  = JSONObject.parseObject(resumeJson);
//        }catch (Exception e){
//            ResponseResult<Object> objectResponseResult = new ResponseResult<>();
//            objectResponseResult.setSuccess(false);
//            objectResponseResult.setMessage("获取简历详情失败！");
//            return objectResponseResult;
//        }
//        if(objectDetail==null){
//            ResponseResult<Object> objectResponseResult = new ResponseResult<>();
//            objectResponseResult.setSuccess(false);
//            objectResponseResult.setMessage("获取简历详情失败！");
//            return objectResponseResult;
//        }
//        ResponseResult<JSONObject> objectResponseResult = new ResponseResult<>();
//        objectResponseResult.setSuccess(true);
//        objectResponseResult.setMessage("成功！");
//        objectResponseResult.setData(objectDetail.getJSONObject("data"));
        return objectResponseResult;
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
}
