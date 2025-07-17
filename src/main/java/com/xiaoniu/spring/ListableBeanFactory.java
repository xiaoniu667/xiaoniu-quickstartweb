package com.xiaoniu.spring;

import com.xiaoniu.exception.BeansException;

import java.util.Map;

public interface ListableBeanFactory extends BeanFactory {
    Map<String, Object> getAllBeans();

    <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;
}
