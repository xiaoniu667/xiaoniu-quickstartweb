package com.xiaoniu.springboot;

import com.xiaoniu.datasource.spring.boot.autoconfigura.DruidDataSourceAutoConfiguration;
import com.xiaoniu.mybatis.spring.MapperScannerConfigurer;
import com.xiaoniu.mybatis.spring.boot.autoconfigura.MybatisAutoConfiguration;
import com.xiaoniu.spring.XiaoNiuComponentScan;
import com.xiaoniu.spring.XiaoNiuImport;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@XiaoNiuComponentScan
@XiaoNiuImport({WebServerAutoConfiguration.class,
                MapperScannerConfigurer.class,
                DruidDataSourceAutoConfiguration.class,
                MybatisAutoConfiguration.class})
public @interface XiaoNiuSpringBootApplication {
}
