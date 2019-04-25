package com.lbh.cfld.springbootdemo;

import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.Sheet;
import com.alibaba.excel.support.ExcelTypeEnum;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import static java.lang.System.out;

public class ExcelListener extends AnalysisEventListener<ExcelModel> {
    List list = new ArrayList();
    @Override
    public void invoke(ExcelModel o, AnalysisContext analysisContext) {
        out.println(o.getIndex());
        out.println(o.getDescription());
        out.println(analysisContext.getCurrentSheet());
        list.add(o);

    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        try {
            FileOutputStream outputStream = new FileOutputStream("c:/7878.xls");
            ExcelWriter excelWriter = new ExcelWriter(outputStream, ExcelTypeEnum.XLS);
            Sheet sheet = new Sheet(1, 0,ExcelModel.class);
            sheet.setSheetName("第一个sheet");
            excelWriter.write(list,sheet);
            excelWriter.finish();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
