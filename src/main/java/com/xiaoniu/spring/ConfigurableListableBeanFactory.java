package com.xiaoniu.spring;


import com.xiaoniu.exception.BeansException;

import java.util.List;

/**
 * 暂时没什么扩展，结合spring写的
 */
public interface ConfigurableListableBeanFactory extends ListableBeanFactory , BeanDefinitionRegistry{

    void preInstantiateSingletons() throws BeansException;

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

}
