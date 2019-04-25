package com.lbh.cfld.springbootdemo;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.support.ExcelTypeEnum;
import org.apache.poi.ss.formula.functions.T;

import java.io.*;
import java.util.List;

public class ExcelUtil {

    public static void write(File fileWriter, List<T> resources){
        ExcelTypeEnum suffix = ExcelTypeEnum.XLSX;
        FileOutputStream outputStream =null;
        try {
            suffix = suffix(fileWriter);
            outputStream = new FileOutputStream(fileWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ExcelWriter excelWriter = new ExcelWriter(outputStream, suffix);
    }
    public static ExcelTypeEnum suffix(File fileWriter) throws FileNotFoundException {
        if(fileWriter==null){
            throw new FileNotFoundException();
        }
        String name = fileWriter.getName();
        String suf = name.substring(name.lastIndexOf(".") + 1).toUpperCase();
        switch (suf){
            case "XLS":
                return ExcelTypeEnum.XLS;
            case "XLSX":
                return ExcelTypeEnum.XLSX;
            default:
                throw new FileNotFoundException();
        }
    }
}
