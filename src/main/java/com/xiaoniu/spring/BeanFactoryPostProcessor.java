package com.xiaoniu.spring;

import com.xiaoniu.exception.BeansException;

@FunctionalInterface
public interface BeanFactoryPostProcessor {

	/**
	 所有的 BeanDefinition 已经加载到容器中，但还没有实例化任何 Bean 之前的扩展方法
	 */
	void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;

}
