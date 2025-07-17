package com.xiaoniu.spring;

import lombok.Data;

@Data
public class BeanDefinition {
    private String id;
    private String className;
    private String oldClassName;
    //@Bean注解 配置类中需要调用的方法名字
    private String factoryMethodName;

    private MutablePropertyValues mutablePropertyValues;

    public boolean hasPropertyValues() {
        return (this.mutablePropertyValues != null && !this.mutablePropertyValues.isEmpty());
    }


}
