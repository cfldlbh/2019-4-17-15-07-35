package com.lbh.cfld.springbootdemo;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class MyProxyCGLIB {
    private Object p;
    public Object bind(final Object object){
        this.p = object;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(object.getClass());
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                System.out.println("CGLIB 实现的动态代理");
                Object invoke = method.invoke(p, objects);
                return invoke;
            }
        });
        return  enhancer.create();
    }
}
