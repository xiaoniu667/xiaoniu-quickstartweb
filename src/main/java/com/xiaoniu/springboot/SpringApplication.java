package com.xiaoniu.springboot;

import com.xiaoniu.exception.ApplicationContextException;
import com.xiaoniu.springmvc.AnnotationConfigWebApplicationContext;
import com.xiaoniu.springmvc.WebApplicationContext;
import com.xiaoniu.springutils.ConfigurableEnvironment;

import java.util.Map;


public class SpringApplication {

    public static AnnotationConfigWebApplicationContext run(Class<?> clazz) {
        //读取配置文件
        ConfigurableEnvironment configurableEnvironment = ConfigurableEnvironment.getConfigurableEnvironment();
        configurableEnvironment.loadConfigByYaml();
        //创建spring容器
        AnnotationConfigWebApplicationContext application = new AnnotationConfigWebApplicationContext(clazz);
        //从容器中取到webserver
        WebServer webServer = getWebServerFactory(application);
        webServer.start(application);
        return application;
    }

    private static WebServer getWebServerFactory(AnnotationConfigWebApplicationContext application) {
        Map<String, WebServer> webServers = application.getBeansOfType(WebServer.class);
        if (webServers.isEmpty()) {
            throw new ApplicationContextException("Unable to start ServletWebServerApplicationContext due to missing "
                    + "ServletWebServerFactory bean.");
        }
        if (webServers.size() > 1) {
            throw new ApplicationContextException("Unable to start ServletWebServerApplicationContext due to multiple "
                    + "ServletWebServerFactory beans");
        }

        return webServers.values().stream().findFirst().get();
    }
}
