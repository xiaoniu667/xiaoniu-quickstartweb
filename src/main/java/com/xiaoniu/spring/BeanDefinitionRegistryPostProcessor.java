package com.xiaoniu.spring;

import com.xiaoniu.exception.BeansException;

public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {


    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry,Class<?> configClass) throws BeansException;

}
