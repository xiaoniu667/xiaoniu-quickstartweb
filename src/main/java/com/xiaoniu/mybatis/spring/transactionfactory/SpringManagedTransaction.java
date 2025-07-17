
package com.xiaoniu.mybatis.spring.transactionfactory;


import com.xiaoniu.mybatis.framwork.transaction.Transaction;
import com.xiaoniu.mybatis.framwork.transaction.TransactionIsolationLevel;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;



public class SpringManagedTransaction implements Transaction {

  protected Connection connection;
  protected DataSource dataSource;
  protected TransactionIsolationLevel level = TransactionIsolationLevel.NONE;
  protected boolean autoCommit;

  public SpringManagedTransaction(Connection connection) {
    this.connection = connection;
  }
  public SpringManagedTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
    this.dataSource = dataSource;
    this.level = level;
    this.autoCommit = autoCommit;
  }

  @Override
  public Connection getConnection() throws SQLException {
    connection = dataSource.getConnection();
    if (level != null) {
      connection.setTransactionIsolation(level.getLevel());
    }
    connection.setAutoCommit(autoCommit);
    return connection;
  }

  @Override
  public void commit() throws SQLException {
    if (connection != null && !connection.getAutoCommit()) {
      connection.commit();
    }
  }

  @Override
  public void rollback() throws SQLException {
    if (connection != null && !connection.getAutoCommit()) {
      connection.rollback();
    }
  }

  @Override
  public void close() throws SQLException {
    if (connection != null) {
      connection.close();
    }
  }
}
