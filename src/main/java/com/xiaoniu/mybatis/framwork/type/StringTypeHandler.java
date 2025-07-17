package com.xiaoniu.mybatis.framwork.type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StringTypeHandler extends com.xiaoniu.mybatis.framwork.type.BaseTypeHandler<String> {



    @Override
    protected void setTypeParameter(PreparedStatement ps, int i, String value) throws SQLException {
        ps.setString(i, value);
    }

    @Override
    public String getResult(ResultSet resultSet, String columnName) throws SQLException {
        return resultSet.getString(columnName);
    }
}
