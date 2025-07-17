package com.xiaoniu.mybatis.framwork.executor;

import com.xiaoniu.mybatis.framwork.mapping.MappedStatement;
import com.xiaoniu.mybatis.framwork.transaction.Transaction;

import java.sql.SQLException;
import java.util.List;

public interface Executor {

    <E> List<E> query(MappedStatement ms, Object parameter) throws Exception;

    int update(MappedStatement ms, Object parameter) throws SQLException;


    Transaction getTransaction();

    void commit(boolean required) throws SQLException;

    void rollback(boolean required) throws SQLException;

    void close(boolean forceRollback);

    boolean isClosed();
}
