package com.xiaoniu.springboot;

import com.xiaoniu.spring.Condition;
import com.xiaoniu.spring.ConditionContext;

public class TomcatCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context) {
        ClassLoader systemClassLoader = context.getClassLoader();
        try {
            systemClassLoader.loadClass("org.apache.catalina.startup.Tomcat");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
