package com.xiaoniu.datasource.spring.boot.autoconfigura;

import com.xiaoniu.spring.XiaoNiuBean;
import com.xiaoniu.spring.XiaoNiuComponent;
import com.xiaoniu.spring.XiaoNiuConditional;

import javax.sql.DataSource;

//自动配置类 配置德鲁伊数据源
@XiaoNiuComponent
public class DruidDataSourceAutoConfiguration {

    @XiaoNiuBean
    @XiaoNiuConditional(DruidCondition.class)
    public DataSource dataSource() {
        return new DruidDataSourceFactory().getDruidDataSource();
    }
}
