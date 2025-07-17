package com.xiaoniu.mybatis.framwork.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.xiaoniu.mybatis.framwork.reflection.MetaObject;
import com.xiaoniu.mybatis.framwork.reflection.SystemMetaObject;


import javax.sql.DataSource;
import java.util.Properties;


public class DruidDataSourceFactory implements DataSourceFactory {
    protected DataSource dataSource;

    public DruidDataSourceFactory() {
        this.dataSource = new DruidDataSource();
    }

    @Override
    public void setProperty(Properties props) {
        MetaObject metaObject = SystemMetaObject.forObject(dataSource);
        for (Object key : props.keySet()) {
            String propertyName = (String) key;
            if (metaObject.hasSetter(propertyName)) {
                String value = (String) props.get(propertyName);
                Object convertedValue = convertValue(metaObject, propertyName, value);
                metaObject.setValue(propertyName, convertedValue);
            }
        }
    }

    private Object convertValue(MetaObject metaDataSource, String propertyName, String value) {
        Object convertedValue = value;
        Class<?> targetType = metaDataSource.getSetterType(propertyName);
        if (targetType == Integer.class || targetType == int.class) {
            convertedValue = Integer.valueOf(value);
        } else if (targetType == Long.class || targetType == long.class) {
            convertedValue = Long.valueOf(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            convertedValue = Boolean.valueOf(value);
        }
        return convertedValue;
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }



}
