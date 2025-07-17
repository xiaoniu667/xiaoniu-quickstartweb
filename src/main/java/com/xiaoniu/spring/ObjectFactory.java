package com.xiaoniu.spring;


import com.xiaoniu.exception.BeansException;

@FunctionalInterface
public interface ObjectFactory<T> {
	T getObject() throws BeansException;

}
