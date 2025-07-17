package com.xiaoniu.spring;

@FunctionalInterface
public interface Condition {

    boolean matches(ConditionContext context);
}
