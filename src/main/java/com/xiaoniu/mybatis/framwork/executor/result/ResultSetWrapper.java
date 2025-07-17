package com.xiaoniu.mybatis.framwork.executor.result;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.type.TypeHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResultSetWrapper {
    private final Map<Class<?>, TypeHandler> typeHandlerMap;
    private ResultSet resultSet;
    private Configuration configuration;
    private final List<String> columnNames = new ArrayList<>();

    public ResultSetWrapper(ResultSet resultSet, Configuration configuration) throws SQLException {
        this.resultSet = resultSet;
        this.configuration = configuration;
        this.typeHandlerMap = configuration.getTypeHandlerMap();
        final ResultSetMetaData metaData = resultSet.getMetaData();
        final int columnCount = metaData.getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            columnNames.add(metaData.getColumnName(i));
        }

    }

    public Map<Class<?>, TypeHandler> getTypeHandlerMap() {
        return typeHandlerMap;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }
}
