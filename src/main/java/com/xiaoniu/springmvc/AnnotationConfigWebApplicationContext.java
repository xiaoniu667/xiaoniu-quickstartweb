package com.xiaoniu.springmvc;

import com.xiaoniu.spring.AbstractApplicationContext;


public class AnnotationConfigWebApplicationContext extends AbstractApplicationContext implements WebApplicationContext {

    public AnnotationConfigWebApplicationContext(Class<?> configClass) {
        super(configClass);
        refresh();
    }
}
