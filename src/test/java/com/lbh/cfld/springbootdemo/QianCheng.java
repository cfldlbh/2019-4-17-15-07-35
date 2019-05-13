package com.lbh.cfld.springbootdemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.UUID;

public class QianCheng {
    private CloseableHttpClient client;
    private String downLoadPath = "D:\\Qiancheng\\validate";
    public QianCheng(){
        ArrayList<Header> headers = new ArrayList<>();
        headers.add(new BasicHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36"));
        client = HttpClients.custom().setDefaultHeaders(headers).build();
    }
    @Test
    public void test() throws IOException {
        String resumeString = login("15879586926", "19961023qianchen");
    }

    private String approve() throws IOException {
        HttpGet getIndex = new HttpGet("https://www.51job.com");
        String indexHtml = responseToStr(getIndex);
        Document doc = Jsoup.parse(indexHtml);
        String verifyPic_img = doc.getElementById("verifyPic_img").attr("src");//验证码图片url
        HttpGet imgGet = new HttpGet(verifyPic_img);
        String filePtah = "D:\\QianCheng\\validate\\" + UUID.randomUUID() + ".png";
        CloseableHttpResponse execute = client.execute(imgGet);
        HttpEntity entity = execute.getEntity();
        entity.writeTo(new FileOutputStream(new File(filePtah)));
        String result = ZlDama.imgDama(1, filePtah, null);
        JSONObject resultObject = JSONObject.parseObject(result);
        if((boolean) resultObject.get("result") == false){
            return "";
        }
        JSONObject data = resultObject.getJSONObject("data");
        String number = (String)data.get("val");
        return number;
    }

    public String login(String name,String password) throws IOException {
        String validate = approve();//验证码结果
        String loginUrl = "https://login.51job.com/ajax/login.php";
        HttpPost loginPost = new HttpPost(loginUrl);
        LinkedList<BasicNameValuePair> basicNameValuePairs = new LinkedList<>();
        basicNameValuePairs.add(new BasicNameValuePair("action","save"));
        basicNameValuePairs.add(new BasicNameValuePair("from_domain","i"));
        basicNameValuePairs.add(new BasicNameValuePair("lang","c"));
        basicNameValuePairs.add(new BasicNameValuePair("loginname",name));
        basicNameValuePairs.add(new BasicNameValuePair("password",password));
        basicNameValuePairs.add(new BasicNameValuePair("verifycode",validate));
        basicNameValuePairs.add(new BasicNameValuePair("isread","on"));
        loginPost.setEntity(new UrlEncodedFormEntity(basicNameValuePairs,"UTF-8"));
        String s = responseToStr(loginPost);
        String substring = s.substring(s.indexOf("{"));
        JSONObject jsonObject = JSONObject.parseObject(substring);
        String result = (String)jsonObject.get("result");
        if(result.equals("0")){
            if(jsonObject.get("error_code").equals("020100")){
                login(name,password);
            }
            return "";//登录失败
        }
        String re = responseToStr(new HttpGet("https://i.51job.com/userset/ajax/index_cv.php?lang=c&_=1557471108415"));
        String resultJson = re.substring(1, re.length()-1);
        int resumeid = (int)JSONObject.parseObject(resultJson).get("resumeid");
        HttpGet httpGet = new HttpGet("https://i.51job.com/resume/standard_resume.php?lang=c&resumeid=" + resumeid);
        String resumeHtml = responseToStr(httpGet,"GBK");
        return  resumeHtml;
    }
    @ApiOperation("发起请求将响应内容转为String")
    private String responseToStr(@ApiParam("http请求对象") HttpRequestBase request,String... coding) throws IOException {
        CloseableHttpResponse execute = client.execute(request);
        HttpEntity entity = execute.getEntity();
        if(coding.length>0){
            return EntityUtils.toString(entity,coding[0]);
        }
        return EntityUtils.toString(entity);
    }
    private String getResumeDetails() throws IOException {
        JSONObject resumeObject = new JSONObject();
        String s = responseToStr(new HttpGet("https://i.51job.com/userset/ajax/index_cv.php?lang=c&_=1557471108415"));
        String resultJson = s.substring(1, s.length()-1);
        int resumeid = (int)JSONObject.parseObject(resultJson).get("resumeid");
        HttpGet httpGet = new HttpGet("https://i.51job.com/resume/standard_resume.php?lang=c&resumeid=" + resumeid);
        String resumeHtml = responseToStr(httpGet,"GBK");
        Document parse = Jsoup.parse(resumeHtml);
        Element root = parse.getElementById("maincontent");
        Element basic = root.getElementById("Basic");
        Element name_ = basic.getElementsByClass("name ").last();
        Element at = basic.getElementsByTag("p").first();
        Element email = basic.getElementsByClass("tab").last().getElementsByClass("email icons at").last();
        Element phone = basic.getElementsByClass("tab").last().getElementsByClass("tel icons").last();
        Element intention = root.getElementById("intention");
        Element childDiv = intention.child(1).child(0).child(0);
        Element money = childDiv.child(0).getElementsByTag("div").last();//期望工资
        Element site = childDiv.child(1).getElementsByTag("div").last().getElementsByClass("ong").first();//期望工资地点
        Element position = childDiv.child(2).getElementsByTag("div").last().getElementsByClass("ong").first();//简历职位
        Elements hy = childDiv.child(3).getElementsByTag("div").last().getElementsByClass("ong");//简历行业
        Elements pj = childDiv.child(4).getElementsByTag("div");//自我评价
        Elements time = childDiv.child(5).getElementsByTag("div");//到岗时间
        Element work = root.getElementById("work");
        Elements bd = work.getElementsByClass("bd");//工作经验集合
        Iterator<Element> iterator = bd.iterator();
        ArrayList<JSONObject> workList = new ArrayList<>();
        while(iterator.hasNext()){
            JSONObject jsonObject = new JSONObject();
            Element next = iterator.next();
            Element worktime = next.getElementsByClass("con edit ebox").last().getElementsByClass("sp").last().getElementsByTag("span").first();//工作时间
            Element companyName = next.getElementsByClass("con edit ebox").last().getElementsByClass("sp").last().getElementsByClass("w280 at").last();//工作公司名
            Element workName = next.getElementsByClass("con edit ebox").last().getElementsByClass("sp").last().getElementsByClass("fbox").last().getElementsByClass("zhi at").first();//公司职位
            Element description = next.getElementsByClass("con edit ebox").first().getElementsByClass("e").first().getElementsByTag("div").first();//工作描述
            jsonObject.put("workTime",worktime.html());
            jsonObject.put("companyName",companyName.html());
            jsonObject.put("workName",workName.html());
            jsonObject.put("description",description.html());
            workList.add(jsonObject);
        }
        ArrayList<JSONObject> projectList = new ArrayList<>();
        Element projects = root.getElementById("project");
        Elements bd1 = projects.getElementsByClass("bd");//项目经验集合
        Iterator<Element> iterator1 = bd1.iterator();
        while (iterator1.hasNext()){
            JSONObject jsonObject = new JSONObject();
            Element next = iterator1.next();
            Element projectTime = next.child(0).child(0).getElementsByTag("span").first();//项目周期时间
            Element projectName = next.child(0).child(0).getElementsByClass("fbox guan").first().getElementsByClass("at").first();//项目名称
            Element projectCompany = next.child(0).child(1).getElementsByClass("div").first();//项目所属公司
            Element description = next.child(0).child(2).getElementsByTag("div").first();//项目描述
            Element duty = next.child(0).child(3).getElementsByTag("div").first();//责任描述
            jsonObject.put("projectTime",projectTime.html());
            jsonObject.put("projectName",projectName.html());
            jsonObject.put("projectCompany",projectCompany.html());
            jsonObject.put("description",description.html());
            jsonObject.put("duty",duty.html());
            projectList.add(jsonObject);
        }
        Element education = root.getElementById("education");
        Elements bd2 = education.getElementsByClass("bd");//学历经历集合
        ArrayList<JSONObject> educationList = new ArrayList<>();
        Iterator<Element> iterator2 = bd2.iterator();
        while (iterator2.hasNext()){
            JSONObject jsonObject = new JSONObject();
            Element next = iterator2.next();
            Element educationTime = next.child(0).getElementsByClass("sp").first().getElementsByTag("span").first();//学历经历时间
            Elements fbox = next.child(0).getElementsByClass("sp").first().getElementsByClass("fbox");
            Element schoolName = null;
            if(fbox == null){
                schoolName =next.child(0).getElementsByClass("sp").first().getElementsByClass("fbox hai").first().getElementsByClass("at").first();//学校名称
                jsonObject.put("overseas",true);
            }else{
                schoolName =next.child(0).getElementsByClass("sp").first().getElementsByClass("fbox").first().getElementsByClass("at").first();//学校名称
                jsonObject.put("overseas",false);
            }
            Element specialtyName = next.child(0).getElementsByClass("sp").first().getElementsByClass("w140 at").first();//专业名称
            Element rank = next.child(0).getElementsByClass("sp").first().getElementsByTag("cl3 w140 at").first();//学历等级
            jsonObject.put("educationTime",educationTime.html());
            jsonObject.put("schoolName",schoolName.html());
            jsonObject.put("specialtyName",specialtyName.html());
            jsonObject.put("rank",rank.html());
            educationList.add(jsonObject);
        }
        return resumeHtml;
    }
    //验证码图片转Base64
    public static String ImageToBase64ByOnline(String imgURL) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            // 创建URL
            URL url = new URL(imgURL);
            byte[] by = new byte[1024];
            // 创建链接
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            InputStream is = conn.getInputStream();
            // 将内容读取内存中
            int len = -1;
            while ((len = is.read(by)) != -1) {
                data.write(by, 0, len);
            }
            // 关闭流
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data.toByteArray());
    }
}
