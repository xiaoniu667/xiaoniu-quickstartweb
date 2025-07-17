# xiaoniu-quickstartweb

## 项目简介

一个轻量级的Java Web快速启动框架，整合了Spring核心功能（IoC、MVC）、MyBatis和Druid数据库连接池，实现一键启动即可部署Web应用。

## 作者信息

- 昵称：小牛
- QQ：578812414
- 个人源码分享网站：[https://bs.mh007.cc](https://bs.mh007.cc/)

> 我的网站提供丰富的毕业设计源码项目资源，欢迎下载使用！

## 框架特性

### 核心功能

✅ 实现了Spring核心IoC容器功能，支持依赖注入
✅ 整合Spring MVC，自动序列化返回JSON数据
✅ 内置MyBatis ORM框架支持
✅ 集成Druid数据库连接池

### 自动配置

🔄 Spring Boot风格的自动配置：

- Tomcat嵌入式服务器
- Druid数据源
- MyBatis mapper扫描

### 待实现功能

⏳ Spring AOP切面编程支持

## 快速开始

### 安装依赖

在项目的`pom.xml`中添加依赖：

xml

```
<dependency>
    <groupId>your.group.id</groupId>
    <artifactId>xiaoniu-quickstartweb</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 配置说明

1. 在`resources`目录下创建`application.yml`配置文件
2. 配置Druid数据源信息
3. 指定MyBatis mapper.xml的扫描路径

#### 配置示例

yaml

```
server:
  port: 8080
  
datasource:
  type: com.alibaba.druid.pool.DruidDataSource
  driverClassName: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&characterEncoding=utf-	8&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=GMT%2B8
  username: root
  password: root
  
mybatis:
  mapperLocations: classpath:mapper/*.xml
```

## 使用指南

1. 启动应用后，内置Tomcat服务器将自动运行
2. 访问`http://localhost:8080`即可测试
3. 按照Spring MVC标准编写Controller即可自动处理JSON序列化

## 贡献与支持

如有任何问题或建议，欢迎通过QQ(578812414)联系作者。
