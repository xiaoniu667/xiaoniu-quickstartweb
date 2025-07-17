package com.xiaoniu.mybatis.framwork.mapping;

import com.xiaoniu.mybatis.framwork.Configuration;

import java.util.Map;

/**
 * 封装解析xml文件中的sql语句参数信息
 */
public class MappedStatement {
    private Configuration configuration;
    private String id;
    private SqlCommandType sqlCommandType;

    private String parameterType;
    private String resultType;
    private String sql;
    private Map<Integer, String> parameter;

    private SqlSource sqlSource;


    public SqlSource getSqlSource() {
        return sqlSource;
    }


    public MappedStatement() {
    }

    public static class Builder {
        private MappedStatement mappedStatement = new MappedStatement();

        public Builder(Configuration configuration, String id, SqlCommandType sqlCommandType, String parameterType, String resultType, String sql, Map<Integer, String> parameter) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = id;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.parameterType = parameterType;
            mappedStatement.resultType = resultType;
            mappedStatement.sql = sql;
            mappedStatement.parameter = parameter;
        }

        public Builder(Configuration configuration, String msId, SqlCommandType sqlCommandType, String resultType, SqlSource sqlSource) {
            mappedStatement.configuration = configuration;
            mappedStatement.id = msId;
            mappedStatement.sqlCommandType = sqlCommandType;
            mappedStatement.resultType = resultType;
            mappedStatement.sqlSource = sqlSource;
        }

        public MappedStatement build() {
            return mappedStatement;
        }

        public Builder configuration(Configuration configuration) {
            mappedStatement.configuration = configuration;
            return this;
        }

        public Builder id(String id) {
            mappedStatement.id = id;
            return this;
        }
        public Builder sqlSource(SqlSource sqlSource) {
            mappedStatement.sqlSource = sqlSource;
            return this;
        }

        public Builder sqlCommandType(SqlCommandType sqlCommandType) {
            mappedStatement.sqlCommandType = sqlCommandType;
            return this;
        }

        public Builder parameterType(String parameterType) {
            mappedStatement.parameterType = parameterType;
            return this;
        }

        public Builder resultType(String resultType) {
            mappedStatement.resultType = resultType;
            return this;
        }

        public Builder sql(String sql) {
            mappedStatement.sql = sql;
            return this;
        }

        public Builder parameter(Map<Integer, String> parameter) {
            mappedStatement.parameter = parameter;
            return this;
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public String getId() {
        return id;
    }

    public SqlCommandType getSqlCommandType() {
        return sqlCommandType;
    }

    public String getParameterType() {
        return parameterType;
    }

    public String getResultType() {
        return resultType;
    }

    public String getSql() {
        return sql;
    }

    public Map<Integer, String> getParameter() {
        return parameter;
    }
}
