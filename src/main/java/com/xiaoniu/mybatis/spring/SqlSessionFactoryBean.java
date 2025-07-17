package com.xiaoniu.mybatis.spring;

import com.alibaba.druid.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.Environment;
import com.xiaoniu.mybatis.framwork.builder.SqlSessionFactoryBuilder;
import com.xiaoniu.mybatis.framwork.builder.xml.XMLConfigBuilder;
import com.xiaoniu.mybatis.framwork.builder.xml.XMLMapperBuilder;
import com.xiaoniu.mybatis.framwork.io.Resource;
import com.xiaoniu.mybatis.framwork.session.SqlSessionFactory;
import com.xiaoniu.mybatis.framwork.transaction.TransactionFactory;
import com.xiaoniu.mybatis.spring.boot.autoconfigura.MybatisAutoConfiguration;
import com.xiaoniu.mybatis.spring.transactionfactory.SpringManagedTransactionFactory;
import com.xiaoniu.spring.FactoryBean;
import com.xiaoniu.spring.InitializingBean;
import com.xiaoniu.springutils.ConfigurableEnvironment;
import com.xiaoniu.springutils.ResourceScannerUtils;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.DocumentException;

import javax.sql.DataSource;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(MybatisAutoConfiguration.class);

    private String RESOURCEPREFIX = "classpath:";
    private String ALLPATH = "*";
    private DataSource dataSource;

    private String configLocation;
    private SqlSessionFactory sqlSessionFactory;
    private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();
    private String environment = SqlSessionFactoryBean.class.getSimpleName();
    private TransactionFactory transactionFactory;


    //获取SqlSessionFactory 将SqlSessionFactory注入容器
    @Override
    public SqlSessionFactory getObject() throws Exception {
        if (this.sqlSessionFactory == null) {
            afterPropertiesSet();
        }

        return this.sqlSessionFactory;
    }

    public TransactionFactory getTransactionFactory() {
        return transactionFactory;
    }

    public void setTransactionFactory(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    @Override
    public Class<?> getObjectType() {
        return this.sqlSessionFactory == null ? SqlSessionFactory.class : this.sqlSessionFactory.getClass();
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getConfigLocation() {
        return configLocation;
    }

    public void setConfigLocation(String configLocation) {
        this.configLocation = configLocation;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.sqlSessionFactory = buildSqlSessionFactory();
    }

    protected SqlSessionFactory buildSqlSessionFactory() {
        final Configuration targetConfiguration;
        if (StringUtils.isEmpty(configLocation)) {
            //读取配置文件中的信息
            loadApplicationYmlConfig();
            if (StringUtils.isEmpty(configLocation)) {
                //加载mapperLocations中的文件
                targetConfiguration = loadApplicationMapperLocations();
                targetConfiguration.setEnvironment(new Environment(this.environment, this.dataSource,
                        this.transactionFactory == null ? new SpringManagedTransactionFactory() : this.transactionFactory));
                return this.sqlSessionFactoryBuilder.build(targetConfiguration);
            }
        }

        Reader reader = Resource.getResourceAsReader(configLocation);
        XMLConfigBuilder xmlConfigBuilder = new XMLConfigBuilder(reader);
        targetConfiguration = xmlConfigBuilder.parse();
        //设置数据源 设置事务处理器
        targetConfiguration.setEnvironment(new Environment(this.environment, this.dataSource,
                this.transactionFactory == null ? new SpringManagedTransactionFactory() : this.transactionFactory));
        return this.sqlSessionFactoryBuilder.build(targetConfiguration);
    }

    public static void main(String[] args) {
        String mapper = "mapper/*.xml";
        System.out.println(mapper.substring(0, mapper.indexOf("*")));
    }

    private Configuration loadApplicationMapperLocations() {
        //如果没有就采用的默认解析xml文件
        Configuration configuration = new Configuration();
        ConfigurableEnvironment configurableEnvironment = ConfigurableEnvironment.getConfigurableEnvironment();
        Map<String, Object> yamlMap = configurableEnvironment.getYamlMap();
        if (yamlMap != null) {
            Map<String, Object> mybatisMap = (Map<String, Object>) yamlMap.get("mybatis");
            if (mybatisMap == null) {
                logger.error("请检查配置文件中mybatis下的mapperLocations文件路径或者路径是否正确");
            }
            String mapperLocation = (String) mybatisMap.get("mapperLocations");
            //classpath:UserMapper.xml  //后期优化 可能会有多个xml文件
            //如果制定classpath:*.xml    其实就是解析多个mapper文件
            if (mapperLocation.startsWith(RESOURCEPREFIX)) {
                mapperLocation = mapperLocation.substring(RESOURCEPREFIX.length());
                if (mapperLocation.contains("*")) {
                    //将这个包下的所有文件转为输入流 解析为xml
                    List<String> allResources = ResourceScannerUtils.scanResources(mapperLocation.substring(0, mapperLocation.indexOf("*")));
                    for (String resource : allResources) {
                        parseXmlMapper(configuration, resource);
                    }
                } else {
                    parseXmlMapper(configuration, mapperLocation);
                }
            }
        }
        return configuration;
    }

    private void parseXmlMapper(Configuration configuration, String mapperLocation) {
        try {
            InputStream inputStream = Resource.getResourceAsStream(mapperLocation);
            XMLMapperBuilder xmlMapperBuilder = null;
            xmlMapperBuilder = new XMLMapperBuilder(inputStream, configuration, mapperLocation);
            xmlMapperBuilder.parse();
        } catch (DocumentException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void loadApplicationYmlConfig() {
        ConfigurableEnvironment configurableEnvironment = ConfigurableEnvironment.getConfigurableEnvironment();
        Map<String, Object> yamlMap = configurableEnvironment.getYamlMap();
        if (yamlMap != null) {
            Map<String, Object> mybatisMap = (Map<String, Object>) yamlMap.get("mybatis");
            if (mybatisMap != null) {
                String configurationLocation = (String) mybatisMap.get("configurationLocation");
                this.configLocation = configurationLocation;
            }
        }
    }
}
