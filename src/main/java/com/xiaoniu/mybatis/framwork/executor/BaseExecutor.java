package com.xiaoniu.mybatis.framwork.executor;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.mapping.MappedStatement;
import com.xiaoniu.mybatis.framwork.transaction.Transaction;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

//在抽象基类中封装了执行器的全部接口，这样具体的子类继承抽象类后，就不用在处理这些共性的方法。
public abstract class BaseExecutor implements Executor {
    protected Configuration configuration;
    protected Transaction transaction;
    protected Executor wrapper;
    private boolean closed;

    protected BaseExecutor(Configuration configuration, Transaction transaction) {
        this.configuration = configuration;
        this.transaction = transaction;
        this.wrapper = this;
    }

    @Override
    public int update(MappedStatement ms, Object parameter) throws SQLException {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        return doUpdate(ms, parameter);
    }

    protected abstract int doUpdate(MappedStatement ms, Object parameter) throws SQLException;

    @Override
    public <E> List<E> query(MappedStatement ms, Object parameter) throws Exception {
        if (closed) {
            throw new RuntimeException("Executor was closed.");
        }
        return doQuery(ms, parameter);
    }

    //交给子类去完成
    protected abstract <E> List<E> doQuery(MappedStatement ms, Object parameter) throws Exception;

    @Override
    public void commit(boolean required) throws SQLException {
        if (closed) {
            throw new RuntimeException("Cannot commit, transaction is already closed");
        }
        if (required) {
            transaction.commit();
        }
    }

    @Override
    public void rollback(boolean required) throws SQLException {
        if (closed) {
            throw new RuntimeException("Cannot rollback, transaction is already closed");
        }
        if (required) {
            transaction.rollback();
        }
    }

    @Override
    public void close(boolean forceRollback) {
        try {
            try {
                rollback(forceRollback);
            } finally {
                if (transaction != null) {
                    transaction.close();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Unexpected exception on closing transaction.  Cause: " + e);
        } finally {
            transaction = null;
            closed = true;
        }
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public Transaction getTransaction() {
        return transaction;
    }

    protected void closeStatement(Statement statement) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
