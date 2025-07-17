package com.xiaoniu.mybatis.framwork.statement;


import com.xiaoniu.mybatis.framwork.executor.Executor;
import com.xiaoniu.mybatis.framwork.mapping.MappedStatement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PreparedStatementHandler extends BaseStatementHandler {

    public PreparedStatementHandler(Executor executor, MappedStatement mappedStatement, Object parameterObject) {
        super(executor, mappedStatement, parameterObject);
    }

    //准备sql
    @Override
    protected Statement instantiateStatement(Connection connection) throws SQLException {
        return connection.prepareStatement(mappedStatement.getSqlSource().getBoundSql(parameterObject).getSql());
    }


    //设置参数
    @Override
    public void parameterize(Statement statement) throws SQLException {
        parameterHandler.setParameters((PreparedStatement) statement);
    }

    //执行处理结果
    @Override
    public <E> List<E> query(Statement statement) throws Exception {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return resultSetHandler.handleResultSets(ps);
    }

    @Override
    public int update(Statement statement) throws SQLException {
        PreparedStatement ps = (PreparedStatement) statement;
        ps.execute();
        return ps.getUpdateCount();
    }
}
