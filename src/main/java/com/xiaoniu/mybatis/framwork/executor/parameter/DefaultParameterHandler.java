
package com.xiaoniu.mybatis.framwork.executor.parameter;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.mapping.BoundSql;
import com.xiaoniu.mybatis.framwork.mapping.MappedStatement;
import com.xiaoniu.mybatis.framwork.mapping.ParameterMapping;
import com.xiaoniu.mybatis.framwork.reflection.MetaObject;
import com.xiaoniu.mybatis.framwork.type.TypeHandler;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;


public class DefaultParameterHandler implements ParameterHandler {


    private final MappedStatement mappedStatement;
    private final Object parameterObject;
    private final BoundSql boundSql;
    private final Configuration configuration;
    Map<Class<?>, TypeHandler> typeHandlerMap;

    public DefaultParameterHandler(MappedStatement mappedStatement, Object parameterObject, BoundSql boundSql) {
        this.mappedStatement = mappedStatement;
        this.configuration = mappedStatement.getConfiguration();
        this.typeHandlerMap = configuration.getTypeHandlerMap();
        this.parameterObject = parameterObject;
        this.boundSql = boundSql;
    }

    @Override
    public Object getParameterObject() {
        return parameterObject;
    }

    @Override
    public void setParameters(PreparedStatement ps) throws SQLException {
        //设置参数
        List<ParameterMapping> parameterMappings = mappedStatement.getSqlSource().getBoundSql(parameterObject).getParameterMappings();
        //遍历sql中的参数 按顺序遍历
        for (int i = 0; i < parameterMappings.size(); i++) {
            ParameterMapping parameterMapping = parameterMappings.get(i);
            String propertyName = parameterMapping.getProperty();
            Object value;
            if (typeHandlerMap.containsKey(parameterObject.getClass())) {
                value = parameterObject;
            } else {
                //如果参数是对象类型
                //通过元对象反射的机制得到对象属性值
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                value = metaObject.getValue(propertyName);
            }
            Map<Class<?>, TypeHandler> typeHandlerMap = configuration.getTypeHandlerMap();
            TypeHandler typeHandler = typeHandlerMap.get(parameterMapping.getJavaType());
            typeHandler.setParameter(ps, i + 1, value);
        }
    }
}
