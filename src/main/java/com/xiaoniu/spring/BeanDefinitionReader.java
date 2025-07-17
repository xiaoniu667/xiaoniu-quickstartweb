package com.xiaoniu.spring;

import com.xiaoniu.exception.BeanDefinitionStoreException;

interface BeanDefinitionReader {
    //获取注册中心
    BeanDefinitionRegistry getRegistry();

//    //将资源解析成beanDefinition注册到注册中心
//    int loadBeanDefinitions(String resource) throws BeanDefinitionStoreException;
}
