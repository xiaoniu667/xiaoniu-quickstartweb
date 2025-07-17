package com.xiaoniu.spring;

import com.xiaoniu.exception.BeansException;

import java.util.Map;

/**
 * 注解方式创建bean
 */
public class AnnotationConfigApplicationContext extends AbstractApplicationContext {


    public AnnotationConfigApplicationContext(Class configClass) {
        super(configClass);
        refresh();
    }


}
