package com.xiaoniu.spring;

public class ConditionEvaluator implements ConditionContext {

    private final ClassLoader classLoader;

    public ConditionEvaluator() {
        classLoader = ClassLoader.getSystemClassLoader();
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
}
