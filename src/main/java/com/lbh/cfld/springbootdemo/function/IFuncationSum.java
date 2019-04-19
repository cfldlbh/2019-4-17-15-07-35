package com.lbh.cfld.springbootdemo.function;

import java.util.List;

@FunctionalInterface
public interface IFuncationSum<T extends Number>{
     T sum(List<T> numbers);
}
