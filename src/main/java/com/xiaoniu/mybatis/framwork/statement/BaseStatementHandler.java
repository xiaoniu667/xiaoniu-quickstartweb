package com.xiaoniu.mybatis.framwork.statement;



import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.executor.Executor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class BaseStatementHandler implements com.xiaoniu.mybatis.framwork.statement.StatementHandler {
    protected final Configuration configuration;
    protected final Executor executor;
    protected final com.xiaoniu.mybatis.framwork.mapping.MappedStatement mappedStatement;
    protected final com.xiaoniu.mybatis.framwork.executor.parameter.ParameterHandler parameterHandler;
    protected final Object parameterObject;
    protected com.xiaoniu.mybatis.framwork.mapping.SqlSource sqlSource;
    protected com.xiaoniu.mybatis.framwork.executor.resultset.ResultSetHandler resultSetHandler;


    public BaseStatementHandler(Executor executor, com.xiaoniu.mybatis.framwork.mapping.MappedStatement mappedStatement, Object parameterObject) {
        this.configuration = mappedStatement.getConfiguration();
        this.executor = executor;
        this.mappedStatement = mappedStatement;
        this.sqlSource = mappedStatement.getSqlSource();
        // 参数和结果集
        this.parameterObject = parameterObject;
        this.resultSetHandler = configuration.newResultSetHandler(executor, mappedStatement);
        this.parameterHandler = configuration.newParameterHandler(mappedStatement, parameterObject);
    }

    @Override
    public Statement prepare(Connection connection) throws SQLException {
        Statement statement;
        try {
            // 实例化 Statement
            statement = instantiateStatement(connection);
            // 参数设置，可以被抽取，提供配置
            statement.setQueryTimeout(350);
            statement.setFetchSize(10000);
            return statement;
        } catch (Exception e) {
            throw new RuntimeException("Error preparing statement.  Cause: " + e, e);
        }
    }

    protected abstract Statement instantiateStatement(Connection connection) throws SQLException;
}
