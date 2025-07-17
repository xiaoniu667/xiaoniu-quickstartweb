package com.xiaoniu.mybatis.framwork.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class UnknownTypeHandler extends com.xiaoniu.mybatis.framwork.type.BaseTypeHandler<Object> {

    private Map<Class<?>, com.xiaoniu.mybatis.framwork.type.TypeHandler> typeHandlerMap;

    public UnknownTypeHandler(Map<Class<?>, TypeHandler> typeHandlerMap) {
        this.typeHandlerMap = typeHandlerMap;
    }


    private TypeHandler resolveTypeHandler(Object parameter) {
        return typeHandlerMap.get(parameter.getClass());
    }

    @Override
    protected void setTypeParameter(PreparedStatement ps, int i, Object value) throws SQLException {
        TypeHandler handler = resolveTypeHandler(value);
        handler.setParameter(ps, i, value);
    }

    @Override
    public Object getResult(ResultSet resultSet, String columnName) throws SQLException {
        return null;
    }
}
