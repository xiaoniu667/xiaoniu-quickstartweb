package com.xiaoniu.mybatis.framwork.executor.resultset;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public interface ResultSetHandler {

  //处理结果
  <E> List<E> handleResultSets(Statement stmt) throws SQLException, ClassNotFoundException;

}
