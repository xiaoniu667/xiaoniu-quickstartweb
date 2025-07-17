package com.xiaoniu.spring;

interface ProxyCreator {
    Object createProxy(Object bean, String beanName);
}
