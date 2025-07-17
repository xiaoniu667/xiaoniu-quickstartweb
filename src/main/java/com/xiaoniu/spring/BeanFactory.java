package com.xiaoniu.spring;

interface BeanFactory {
    String FACTORY_BEAN_PREFIX = "&";
    Object getBean(String beanName);
}
