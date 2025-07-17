package com.xiaoniu.springboot;

import com.xiaoniu.spring.Condition;
import com.xiaoniu.spring.ConditionContext;

public class JettyCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context) {
        ClassLoader systemClassLoader = context.getClassLoader();
        try {
            systemClassLoader.loadClass("org.eclipse.jetty.server.Server");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
