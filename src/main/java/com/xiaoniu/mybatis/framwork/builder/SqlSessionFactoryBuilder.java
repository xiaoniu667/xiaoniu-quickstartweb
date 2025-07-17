package com.xiaoniu.mybatis.framwork.builder;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.builder.xml.XMLConfigBuilder;
import com.xiaoniu.mybatis.framwork.session.SqlSessionFactory;
import com.xiaoniu.mybatis.framwork.session.defaults.DefaultSqlSessionFactory;

import java.io.Reader;

public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(Reader reader) {
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        Configuration configuration = xmlConfigBuilder.parse();
        return build(configuration);
    }

    public SqlSessionFactory build(Configuration config) {
        return new DefaultSqlSessionFactory(config);
    }

}
