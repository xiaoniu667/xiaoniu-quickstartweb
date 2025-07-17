package com.xiaoniu.mybatis.spring;


import com.xiaoniu.mybatis.framwork.session.SqlSessionFactory;
import com.xiaoniu.spring.FactoryBean;

/**
 * 通过mapper获取代理对象
 *
 * @param <T>
 */
public class MapperFactoryBean<T> implements FactoryBean<T> {

    private Class<T> mapperInterface;

    //自动注入
    private SqlSessionFactory sqlSessionFactory;

    public MapperFactoryBean() {

    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
    }

    public MapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }


    @Override
    public T getObject() throws Exception {
        return sqlSessionFactory.openSession().getMapper(this.mapperInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.mapperInterface;
    }

}
