package com.xiaoniu.mybatis.framwork.datasource;

import javax.sql.DataSource;
import java.util.Properties;

public interface DataSourceFactory {
    void setProperty(Properties property);
    DataSource getDataSource();
}
