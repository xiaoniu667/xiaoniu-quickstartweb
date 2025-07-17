package com.xiaoniu.mybatis.framwork.executor;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.mapping.MappedStatement;
import com.xiaoniu.mybatis.framwork.statement.StatementHandler;
import com.xiaoniu.mybatis.framwork.transaction.Transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class SimpleExecutor extends BaseExecutor {

    public SimpleExecutor(Configuration configuration, Transaction transaction) {
        super(configuration, transaction);
    }

    @Override
    protected int doUpdate(MappedStatement ms, Object parameter) throws SQLException {
        Statement stmt = null;
        try {
            Configuration configuration = ms.getConfiguration();
            // 新建一个 StatementHandler
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter);
            Connection connection = transaction.getConnection();
            // 准备语句
            stmt = handler.prepare(connection);

            //参数化
            handler.parameterize(stmt);
            // StatementHandler.update
            return handler.update(stmt);
        } finally {
            closeStatement(stmt);
        }
    }

    @Override
    protected <E> List<E> doQuery(MappedStatement ms, Object parameter) throws Exception {
        Statement stmt = null;
        try {
            Configuration configuration = ms.getConfiguration();
            StatementHandler handler = configuration.newStatementHandler(this, ms, parameter);
            Connection connection = transaction.getConnection();
            //准备语句
            stmt = handler.prepare(connection);
            //参数化
            handler.parameterize(stmt);
            //执行 返回结果
            return handler.query(stmt);
        } finally {
            closeStatement(stmt);
        }
    }
}
