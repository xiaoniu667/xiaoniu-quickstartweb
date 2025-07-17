package com.xiaoniu.springboot;

import com.alibaba.druid.util.StringUtils;
import com.xiaoniu.springmvc.DispatcherServlet;
import com.xiaoniu.springmvc.WebApplicationContext;
import com.xiaoniu.springutils.ConfigurableEnvironment;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.core.StandardEngine;
import org.apache.catalina.core.StandardHost;
import org.apache.catalina.startup.Tomcat;

import java.util.Map;

public class TomcatServer implements WebServer {

//    private static final ServerConfig serverConfig;
//
//    static {
//        //读取配置文件中的内容
//        serverConfig = new YamlReader().readServerConfig("application.yml");
//    }

    private int SERVERPORT = 8080;
    private String DEFAULTHOST = "localhost";
    private String CONTEXTPATH = "";


    @Override
    public void start(WebApplicationContext applicationContext) {
        //修改配置
        loadApplicationYmlConfig();

        Tomcat tomcat = new Tomcat();
        Server server = tomcat.getServer();
        Service service = server.findService("Tomcat");
        Connector connector = new Connector();
        connector.setPort(SERVERPORT);
        StandardEngine engine = new StandardEngine();
        engine.setDefaultHost(DEFAULTHOST);
        StandardHost host = new StandardHost();
        host.setName(DEFAULTHOST);
        StandardContext standardContext = new StandardContext();
        standardContext.setPath(CONTEXTPATH);
        standardContext.addLifecycleListener(new Tomcat.FixContextListener());
        host.addChild(standardContext);
        engine.addChild(host);

        service.addConnector(connector);
        service.setContainer(engine);
        tomcat.addServlet(CONTEXTPATH, "dispatcherServlet", new DispatcherServlet(applicationContext));
        standardContext.addServletMappingDecoded("/*", "dispatcherServlet");

        try {
            tomcat.start();
        } catch (LifecycleException e) {
            e.printStackTrace();
        }
    }

    private void loadApplicationYmlConfig() {
        ConfigurableEnvironment configurableEnvironment = ConfigurableEnvironment.getConfigurableEnvironment();
        Map<String, Object> yamlMap = configurableEnvironment.getYamlMap();
        if (yamlMap != null) {
            Map<String, Object> serverMap = (Map<String, Object>) yamlMap.get("server");
            Integer port = (Integer) serverMap.get("port");
            if (port != null) {
                SERVERPORT = port;
            }
        }
    }
}
