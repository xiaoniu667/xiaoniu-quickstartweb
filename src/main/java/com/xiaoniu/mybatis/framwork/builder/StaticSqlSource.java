package com.xiaoniu.mybatis.framwork.builder;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.mapping.BoundSql;
import com.xiaoniu.mybatis.framwork.mapping.ParameterMapping;
import com.xiaoniu.mybatis.framwork.mapping.SqlSource;

import java.util.List;


public class StaticSqlSource implements SqlSource {

  private final String sql;
  private final List<ParameterMapping> parameterMappings;
  private final Configuration configuration;

  public StaticSqlSource(Configuration configuration, String sql) {
    this(configuration, sql, null);
  }

  public StaticSqlSource(Configuration configuration, String sql, List<ParameterMapping> parameterMappings) {
    this.sql = sql;
    this.parameterMappings = parameterMappings;
    this.configuration = configuration;
  }

  @Override
  public BoundSql getBoundSql(Object parameterObject) {
    return new BoundSql(configuration, sql, parameterMappings, parameterObject);
  }

}
