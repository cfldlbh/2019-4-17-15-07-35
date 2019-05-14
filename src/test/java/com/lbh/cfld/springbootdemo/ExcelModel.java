package com.lbh.cfld.springbootdemo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;

public class ExcelModel extends BaseRowModel {
    @ExcelProperty(index=0,value = "第一格")
    private String index;
    @ExcelProperty(index = 1,value = "dierge")
    private String description;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
