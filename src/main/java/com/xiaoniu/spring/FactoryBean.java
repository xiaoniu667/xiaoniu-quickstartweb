

package com.xiaoniu.spring;

//FactoryBean是一个工厂Bean，可以生成某一类型Bean实例，它最大的一个作用是：可以让我们自定义Bean的创建过程。
public interface FactoryBean<T> {

    T getObject() throws Exception;

    Class<?> getObjectType();

    default boolean isSingleton() {
        return true;
    }

}
