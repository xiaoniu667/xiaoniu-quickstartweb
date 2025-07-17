package com.xiaoniu.mybatis.spring.boot.autoconfigura;


import com.alibaba.druid.pool.DruidDataSource;
import com.xiaoniu.mybatis.framwork.session.SqlSession;
import com.xiaoniu.mybatis.framwork.session.SqlSessionFactory;
import com.xiaoniu.mybatis.framwork.session.defaults.DefaultSqlSessionFactory;
import com.xiaoniu.mybatis.spring.SqlSessionFactoryBean;
import com.xiaoniu.spring.XiaoNiuBean;
import com.xiaoniu.spring.XiaoNiuComponent;

import javax.sql.DataSource;
import java.lang.reflect.Method;

//mybatis的自动配置类
@XiaoNiuComponent
public class MybatisAutoConfiguration {


    public MybatisAutoConfiguration() {
    }

    @XiaoNiuBean
    //这里需要有参数的注入属性
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        return factory.getObject();
    }

//    public static void main(String[] args) {
//        try {
//            Class<?> clazz = Class.forName("com.xiaoniu.mybatis.spring.boot.autoconfigura.MybatisAutoConfiguration");
//            Object configObject = clazz.newInstance();
//            Method[] methods = clazz.getMethods();
//            for (Method method : methods) {
//                if (method.getName().equals("sqlSessionFactory")) {
//                    Object[] argsToUse = new Object[1];
//                    Class<?> parameterType = method.getParameterTypes()[0];
//                    String parameterName = parameterType.getSimpleName();
//                    parameterName = parameterName.substring(0, 1).toLowerCase() + parameterName.substring(1);
//                    Object awareBean = new DruidDataSource();
//                    argsToUse[0] = awareBean;
//                    Object object = method.invoke(configObject, argsToUse);
//                    System.out.println(object);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
