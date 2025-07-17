package com.xiaoniu.spring;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JdkProxyCreator implements ProxyCreator {
    @Override
    public Object createProxy(Object bean, String beanName) {
        Class<?> beanClass = bean.getClass();
        //根据beanClass,创建jdk动态代理对象
        Object proxy = Proxy.newProxyInstance(beanClass.getClassLoader(), beanClass.getInterfaces(), new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                method.invoke(bean, args);
                return null;
            }
        });
        return proxy;
    }
}
