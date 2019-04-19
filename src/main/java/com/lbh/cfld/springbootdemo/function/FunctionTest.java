package com.lbh.cfld.springbootdemo.function;

import java.util.Arrays;

public class FunctionTest {
    static IFuncationSum<Long> sumFunction = list -> {
        Long num =0l;
        for(Long a : list){
            num += a;
        }
        return num;
    };
    public static void main(String[] arg){
       Long s =  sumFunction.sum(Arrays.asList(1L,2L,3L));
    }
}
