package com.xiaoniu.mybatis.framwork.transaction.jdbc;


import com.xiaoniu.mybatis.framwork.transaction.Transaction;
import com.xiaoniu.mybatis.framwork.transaction.TransactionFactory;
import com.xiaoniu.mybatis.framwork.transaction.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;

public class JdbcTransactionFactory implements TransactionFactory {

    public JdbcTransactionFactory() {
    }

    @Override
    public Transaction newTransaction(Connection conn) {
        return new JdbcTransaction(conn);
    }

    @Override
    public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
        return new JdbcTransaction(dataSource, level, autoCommit);
    }
}
