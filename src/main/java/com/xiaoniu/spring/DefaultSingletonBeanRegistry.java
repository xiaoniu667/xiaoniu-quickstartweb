package com.xiaoniu.spring;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专门用于存储单例Bean，包含三级缓存
 */
public class DefaultSingletonBeanRegistry {

    //一级缓存  单例池
    protected static final ConcurrentHashMap<String, Object> singletonObjects = new ConcurrentHashMap<>(64);
    //二级缓存  为了解决性能问题，可以缩小锁的粒子度。
    protected  final ConcurrentHashMap<String, Object> ersingletonObjects = new ConcurrentHashMap<>(64);
    //三级缓存   遵循规范并且能够解决循环依赖bean的动态代理的问题。
    protected  final ConcurrentHashMap<String, ObjectFactory<?>> singletonFactories = new ConcurrentHashMap<>(64);

    //路径映射
//    protected final HashMap<String, Method> pathMethodMap = new HashMap<>();


    //三级缓存作为出口
    protected Object getSingleton(String beanName) {
        Object singletonObject = singletonObjects.get(beanName);
        if (singletonObject == null) {
            singletonObject = this.ersingletonObjects.get(beanName);
            if (singletonObject == null) {
                synchronized (singletonObjects) {
                    //在完整的单例锁中一致地创建早期引用 升级为了四级检测锁
                    singletonObject = singletonObjects.get(beanName);
                    if (singletonObject == null) {
                        singletonObject = this.ersingletonObjects.get(beanName);
                        if (singletonObject == null) {
                            ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
                            if (singletonFactory != null) {
                                //如果三级缓存有的话，说明产生了循环依赖，执行里面的代理方法，判断返回代理对象或者普通对象
                                singletonObject = singletonFactory.getObject();
                                //放入二级缓存的目的是 比如A被循环依赖的两次，不用重复创建两次动态代理对象。
                                this.ersingletonObjects.put(beanName, singletonObject);
                                this.singletonFactories.remove(beanName);
                            }
                        }
                    }

                }
            }
        }
        return singletonObject;
    }

    ///获取bean的方法 二级缓存作为出口
//    protected Object getSingleton(String beanName) {
//        Object singletonObject = this.singletonObjects.get(beanName);
//        if (singletonObject == null) {
//            //为什么要锁住，不然多线程情况下也会产生空指针的问题。
//            synchronized (singletonObjects) {
//                //二级缓存作为出口
//                singletonObject = this.ersingletonObjects.get(beanName);
//
//            }
//        }
//        return singletonObject;
//    }
}
