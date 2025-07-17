package com.xiaoniu.mybatis.framwork.mapping;


public interface SqlSource {

  BoundSql getBoundSql(Object parameterObject);

}
