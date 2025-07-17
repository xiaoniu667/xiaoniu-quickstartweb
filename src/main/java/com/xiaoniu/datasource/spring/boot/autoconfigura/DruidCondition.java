package com.xiaoniu.datasource.spring.boot.autoconfigura;

import com.xiaoniu.spring.Condition;
import com.xiaoniu.spring.ConditionContext;

public class DruidCondition implements Condition {
    @Override
    public boolean matches(ConditionContext context) {
        ClassLoader systemClassLoader = context.getClassLoader();
        try {
            systemClassLoader.loadClass("com.alibaba.druid.pool.DruidDataSource");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
