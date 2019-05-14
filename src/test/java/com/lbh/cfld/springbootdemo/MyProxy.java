package com.lbh.cfld.springbootdemo;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class MyProxy implements InvocationHandler {
    private IORCtest object;
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("before writes document");
        Object result = method.invoke(object, args);
        return result;
    }

    public Object bind(IORCtest o){
        this.object = o;
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),object.getClass().getInterfaces(),this);
    }
}
