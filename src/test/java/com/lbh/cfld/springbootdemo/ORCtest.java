package com.lbh.cfld.springbootdemo;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.Test;

import java.io.File;

public class ORCtest implements IORCtest {
    public static void main(String[] arg){
        File file = new File("D:\\QQfile\\20190424101001.png");
        ITesseract instance = new Tesseract();
        //instance.setDatapath("E:\\traineddata");//设置你的Tess4J下的tessdata目录
        instance.setLanguage("osd");//指定需要识别的语种
        String result ="";
        try {
            result = instance.doOCR(file);
        } catch (TesseractException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println(result);
    }

    @Override
    public void test() {
        System.out.println("jdk动态代理");
    }
}
