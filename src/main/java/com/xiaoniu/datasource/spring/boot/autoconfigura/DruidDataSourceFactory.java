package com.xiaoniu.datasource.spring.boot.autoconfigura;

import com.alibaba.druid.pool.DruidDataSource;
import com.xiaoniu.mybatis.framwork.reflection.MetaObject;
import com.xiaoniu.mybatis.framwork.reflection.SystemMetaObject;
import com.xiaoniu.springutils.ConfigurableEnvironment;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

public class DruidDataSourceFactory {
    protected DataSource dataSource;

    public DruidDataSourceFactory() {
        this.dataSource = new DruidDataSource();
    }

    public DataSource getDruidDataSource() {
        loadApplicationYmlConfig();
        return dataSource;
    }

    private void loadApplicationYmlConfig() {
        ConfigurableEnvironment configurableEnvironment = ConfigurableEnvironment.getConfigurableEnvironment();
        Map<String, Object> yamlMap = configurableEnvironment.getYamlMap();
        Properties properties = new Properties();
        if (yamlMap != null) {
            Map<String, Object> databaseMap = (Map<String, Object>) yamlMap.get("datasource");
            for (Map.Entry<String, Object> entry : databaseMap.entrySet()) {
                properties.setProperty(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        //设置数据源的值
        setDataSourceProperty(properties);
    }


    public void setDataSourceProperty(Properties props) {
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
}
