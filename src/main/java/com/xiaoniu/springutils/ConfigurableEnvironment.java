package com.xiaoniu.springutils;

import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.Map;

/**
 * 写一个单例模式吧 然后加载spring的配置文件
 */
public class ConfigurableEnvironment {

    private  Map<String, Object> yamlMap;

    public ConfigurableEnvironment() {
    }

    private static final String APPLICATION_CONFIG = "application.yml";


    private static volatile ConfigurableEnvironment configurableEnvironment;

    public static ConfigurableEnvironment getConfigurableEnvironment() {
        if (configurableEnvironment == null) {
            synchronized (ConfigurableEnvironment.class) {
                if (configurableEnvironment == null) {
                    configurableEnvironment = new ConfigurableEnvironment();
                }
            }
        }
        return configurableEnvironment;
    }

    public void loadConfigByYaml() {
        Yaml yaml = new Yaml();
        try (InputStream in = ConfigurableEnvironment.class.getClassLoader().getResourceAsStream(APPLICATION_CONFIG)) {
            if (in != null) {
                yamlMap = yaml.load(in);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Object> getYamlMap() {
        return yamlMap;
    }

//    public static void main(String[] args) {
//        ConfigurableEnvironment configurableEnvironment = ConfigurableEnvironment.getConfigurableEnvironment();
//        configurableEnvironment.loadConfigByYaml();
//        System.out.println(configurableEnvironment.getYamlMap());
//    }
}
