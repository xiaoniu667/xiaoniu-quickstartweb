# xiaoniu-quickstartweb

#### 作者介绍
1. 小牛 
2. qq:578812414
3. 个人源码分享网站:https://bs.mh007.cc
4. 该网站有丰富好用的毕业设计的源码项目，欢迎大家下载使用


#### 框架介绍
1. 手写以及整合了springioc、springmvc、springboot、mybatis、springboot-mybatis、springboot-druid，实现一键启动即可部署web应用
2. spring的核心ioc的功能，并且实现依赖注入。
3. springboot的自动配置，如druid、mybatis、tomcat自动配置。
4. springmvc的核心dispatcherServlet功能，自动序列化返回json数据。
5. 尚未完成spring的切面逻辑




#### 安装教程

1.  在pom.xml中引入xiaoniu-quickstartweb依赖

#### 使用说明

1. 在resources中创建配置application.yml格式的文件
2. 配置druid数据源信息
3. 指定mapper.xml的扫描路径

#### application.yml格式如下
```yaml
server:
 port: 8080
datasource:
 type: com.alibaba.druid.pool.DruidDataSource
 driverClassName: com.mysql.cj.jdbc.Driver
 url: jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf-8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8
 username: root
 password: root
mybatis:
 mapperLocations: classpath:mapper/*.xml
```

