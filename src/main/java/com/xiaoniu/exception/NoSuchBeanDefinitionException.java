package com.xiaoniu.exception;

public class NoSuchBeanDefinitionException extends BeansException{
    public NoSuchBeanDefinitionException(String name) {
        super("没有bean的名字叫做 '" + name + "' 可以获得");
    }

    public NoSuchBeanDefinitionException(String name, String message) {
        super("没有bean的名字叫做 '" + name + "' 可以获得");
    }
}
