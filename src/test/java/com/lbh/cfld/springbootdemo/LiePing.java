package com.lbh.cfld.springbootdemo;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class LiePing {
    private String cookie;
    private ChromeDriver driver;
    private String filePath = "D:\\LiePingResume\\";
    @Test
    public void chrome() throws InterruptedException, IOException {
        cookie = FileUtils.readFileToString(new File("src/main/resources/LiePingCookie.txt"),"UTF-8");
        String[] arg = cookie.split("; ");
        System.setProperty("webdriver.chrome.driver","D:/chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://c.liepin.com/");
        for (int i = 0;i<arg.length;i++){
            String[] split = arg[i].split("=");
            driver.manage().addCookie(new Cookie(split[0],split[1],".liepin.com","/",null));
        }
        driver.get("https://lpt.liepin.com/cvsearch/showcondition/");
        Thread.sleep(1000);
        List<WebElement> elementsByClassName = driver.findElementsByClassName("educate-shade-btn0");
        if(elementsByClassName.size()>0){
            driver.findElementByClassName("educate-shade-btn0").click();
            Thread.sleep(500);
        }
        if(driver.findElements(By.xpath("//button[@class='btn btn-primary search-btn']")).size()<=0){
            driver.quit();//cookie失效，没登录
            return;
        }
        HashMap<String, String> map = new HashMap<>();
        map.put("abode","深圳|004000");
        map.put("company","腾讯");
        map.put("updateTime","4");
        map.put("position","销售总监");
        conditionInput(map);
        driver.findElement(By.xpath("//button[@class='btn btn-primary search-btn']")).click();
        getResume();
    }
    public void conditionInput(HashMap<String,String> condition) throws InterruptedException {
        Set<String> keys = condition.keySet();
        for(String key : keys){
            switch (key){
                case "company" :
                    driver.findElement(By.xpath("//input[@class='search-input']")).sendKeys(condition.get("company"));
                    break;
                case "updateTime" :
                    driver.executeScript("document.getElementsByName('updateDate')[0].value='0"+condition.get("updateTime")+"'");
                    break;
                case "position" :
                    driver.executeScript("document.getElementsByName('wantJobTitles')[0].setAttribute('value','"+condition.get("position")+"')");
                    break;
                case "abode" :
                    selectCity(condition.get("abode"));
                    break;
            }
            Thread.sleep(1000);
        }
    }
    public void selectCity(String city) throws InterruptedException {
        String[] split = city.split("\\|");
        driver.findElements(By.xpath("//span[@data-selector='city-btn']")).get(0).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[text()='"+split[0]+"']")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[text()='确定']")).click();
        driver.findElements(By.xpath("//span[@data-selector='city-btn']")).get(1).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[text()='"+split[0]+"']")).click();
        Thread.sleep(1000);
        driver.findElement(By.xpath("//a[text()='确定']")).click();
    }
    //获取简历
    public void getResume() throws InterruptedException, IOException {
        WebElement element = driver.findElementByClassName("resume-list-box");
        Thread.sleep(500);
        WebElement ulEl = element.findElement(By.className("resume-list"));
        Thread.sleep(1000);
        List<WebElement> liEl = ulEl.findElements(By.tagName("li"));//简历列表div
        Thread.sleep(1000);
        if(liEl.size() <= 0){ //没获取到当前页的简历列表，可能达到每日搜索次数限制 45次
            Thread.sleep(5000);
            driver.quit();
            return;
        }
        Iterator<WebElement> iterator = liEl.iterator();
        while (iterator.hasNext()){
            WebElement next = iterator.next();
            WebElement headUrl = next.findElement(By.className("head-img"));
            headUrl.click();
            Thread.sleep(1000);
            Set<String> winHandels = driver.getWindowHandles();
            List<String> it = new ArrayList<String>(winHandels);
            driver.switchTo().window(it.get(1));
            StringBuilder content = new StringBuilder("<meta charset=\"UTF-8\" />");
            String innerHtml = driver.findElementByClassName("board").getAttribute("innerHTML");
            content.append(innerHtml);
            String resumeId = driver.findElement(By.xpath("//h6[@class='float-left']")).findElements(By.tagName("small")).get(1).getText();
            File file = new File(filePath + resumeId + ".html");
            //String content = driver.getPageSource();
            downLoadHtml(file,content.toString());
            driver.close();
            driver.switchTo().window(it.get(0));
            Thread.sleep((2+(int)(Math. random()*(8-2)) )*1000);
        }
        List<WebElement> nextS = driver.findElements(By.xpath("//a[text()='下页']"));
        if(nextS.size() <=0){
            Thread.sleep(30000);
            driver.quit();//没有下一页
            return;
        }else {
            WebElement next = driver.findElement(By.xpath("//a[text()='下页']"));
            next.click();
            getResume();
        }
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
