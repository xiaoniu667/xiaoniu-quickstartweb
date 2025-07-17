package com.xiaoniu.mybatis.framwork.type;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class BaseTypeHandler<T> implements com.xiaoniu.mybatis.framwork.type.TypeHandler<T> {

    @Override
    public void setParameter(PreparedStatement ps, int i, T value) throws SQLException {
        if (value == null) {
            throw new RuntimeException("请检查您的参数值是否为空 或者 存在多个参数没有设置param注解");
        } else {
            setTypeParameter(ps,i,value);
        }
    }


    protected abstract void setTypeParameter(PreparedStatement ps, int i, T value) throws SQLException;

}
