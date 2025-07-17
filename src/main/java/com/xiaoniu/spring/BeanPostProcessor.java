package com.xiaoniu.spring;

public interface BeanPostProcessor {
    //前置方法
   default Object postProcessBeforeInitialization(Object bean, String beanName){
       return bean;
   }

    //后置方法
    default Object postProcessAfterInitialization(Object bean, String beanName){
       return bean;
    }
}
