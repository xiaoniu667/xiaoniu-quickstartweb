package com.xiaoniu.spring;

//配置文件的方式创建bean对象

public class ClassPathXmlApplicationContext extends AbstractApplicationContext{


    public ClassPathXmlApplicationContext(String configLocation) {
        super(configLocation);
        refresh();
    }
}
