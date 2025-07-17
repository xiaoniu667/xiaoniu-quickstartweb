package com.xiaoniu.spring;



import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

class CglibProxyCreator implements ProxyCreator {
    @Override
    public Object createProxy(Object bean, String beanName) {
        Class<?> targetClass = bean.getClass();
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(targetClass);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
                //调用原来的方法
                method.invoke(bean, objects);
                return null;
            }
        });
        Object proxy = enhancer.create();
        return proxy;
    }
}
