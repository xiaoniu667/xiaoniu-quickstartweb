
package com.xiaoniu.mybatis.spring.transactionfactory;

import com.xiaoniu.mybatis.framwork.transaction.Transaction;
import com.xiaoniu.mybatis.framwork.transaction.TransactionFactory;
import com.xiaoniu.mybatis.framwork.transaction.TransactionIsolationLevel;

import java.sql.Connection;
import java.util.Properties;

import javax.sql.DataSource;


public class SpringManagedTransactionFactory implements TransactionFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  public Transaction newTransaction(DataSource dataSource, TransactionIsolationLevel level, boolean autoCommit) {
    return new SpringManagedTransaction(dataSource,level,autoCommit);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Transaction newTransaction(Connection conn) {
    throw new UnsupportedOperationException("新建spring事务处理器需要数据源");
  }

}
