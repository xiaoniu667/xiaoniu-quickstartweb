package com.xiaoniu.mybatis.framwork.binding;

//提供包路径的扫描和映射器代理类注册机服务，完成接口对象的代理类注册处理。


import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.session.SqlSession;
import com.xiaoniu.mybatis.framwork.utils.ClassScanner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MapperRegistry {
    private Configuration configuration;

    public MapperRegistry() {
    }

    public MapperRegistry(Configuration configuration) {
        this.configuration = configuration;
    }

    //键值对  键为接口类型，值为代理工厂
    private final Map<Class<?>, MapperProxyFactory<?>> knownMappers = new HashMap<Class<?>, com.xiaoniu.mybatis.framwork.binding.MapperProxyFactory<?>>();

    //根据接口的class获取对应的代理工厂，然后创建代理对象
    @SuppressWarnings("unchecked")
    public <T> T getMapper(Class<T> mapperClass, SqlSession sqlSession) {
        MapperProxyFactory<T> mapperProxyFactory = (MapperProxyFactory<T>) knownMappers.get(mapperClass);
        if (mapperProxyFactory == null) {
            throw new RuntimeException("Type " + mapperClass + " is not known to the MapperRegistry.");
        }
        try {
            return  mapperProxyFactory.newInstance(sqlSession);
        } catch (Exception e) {
            throw new RuntimeException("Error getting mapper instance. Cause: " + e, e);
        }
    }
    //扫包注册对应的代理工厂
    public void addMapper(Class<?> type) {
        if (type.isInterface()) {
            if (hasMapper(type)) {
                throw new RuntimeException("Type " + type + " is already known to the MapperRegistry.");
            }
            knownMappers.put(type, new MapperProxyFactory<>(type));
        }
    }
    public <T> boolean hasMapper(Class<T> type) {
        return knownMappers.containsKey(type);
    }

    public void addMappers(String packageName) {
        Set<Class<?>> mapperSet = ClassScanner.scanPackage(packageName);
        for (Class<?> mapperClass : mapperSet) {
            addMapper(mapperClass);
        }
    }

}
