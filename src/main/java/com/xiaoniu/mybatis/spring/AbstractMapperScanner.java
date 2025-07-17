package com.xiaoniu.mybatis.spring;

import com.alibaba.druid.util.StringUtils;
import com.xiaoniu.mybatis.spring.MapperFactoryBean;
import com.xiaoniu.spring.BeanDefinition;
import com.xiaoniu.spring.BeanDefinitionRegistry;
import com.xiaoniu.spring.MutablePropertyValues;
import com.xiaoniu.spring.PropertyValue;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractMapperScanner {

    private final String OSName = System.getProperty("os.name");
    private BeanDefinitionRegistry beanDefinitionRegistry;
    protected ArrayList<String> beanClassList = new ArrayList<>();

    private Class<? extends MapperFactoryBean> mapperFactoryBeanClass = MapperFactoryBean.class;

    public AbstractMapperScanner(BeanDefinitionRegistry registry) {
        this.beanDefinitionRegistry = registry;
    }

    public void scan(BeanDefinitionRegistry registry, String basePackage) {
        Set<BeanDefinition> beanDefinitions = doScan(basePackage);
        //处理一下bean定义
        processBeanDefinitions(beanDefinitions, registry);
        //注册到容器之中
        registerBeanDefinitions(beanDefinitions, registry);
    }

    protected abstract Set<BeanDefinition> doScan(String basePackage);

    //处理一下mapper文件的定义 同时设置sqlSessionFactory 用于属性注入
    private void processBeanDefinitions(Set<BeanDefinition> beanDefinitions, BeanDefinitionRegistry registry) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            //将mapper中的属性设置为原来的class
            beanDefinition.setOldClassName(beanDefinition.getClassName());
            //新的class为mapperFactoryBeanClass
            beanDefinition.setClassName(this.mapperFactoryBeanClass.getName());
            MutablePropertyValues mutablePropertyValues = beanDefinition.getMutablePropertyValues();
            if (mutablePropertyValues == null) {
                MutablePropertyValues propertyValues = new MutablePropertyValues();
                PropertyValue propertyValue = new PropertyValue("sqlSessionFactory", "sqlSessionFactory", null);
                propertyValues.addPropertyValue(propertyValue);
                beanDefinition.setMutablePropertyValues(propertyValues);
            }
        }
    }

    private void registerBeanDefinitions(Set<BeanDefinition> beanDefinitions, BeanDefinitionRegistry registry) {
        for (BeanDefinition beanDefinition : beanDefinitions) {
            registry.registerBeanDefinition(beanDefinition.getId(), beanDefinition);
        }
    }

    protected void getFilePath(File file) {
        if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                getFilePath(listFile);
                if (listFile.isFile()) {
                    String absolutePath = listFile.getAbsolutePath();
                    //判断是否是class结尾的。
                    if (absolutePath.endsWith(".class")) {
                        absolutePath = absolutePath.substring(absolutePath.indexOf("classes") + 8, absolutePath.indexOf(".class"));
                        if (OSName.startsWith("Windows")) {
                            absolutePath = absolutePath.replace("\\", ".");
                        } else {
                            absolutePath = absolutePath.replace("/", ".");
                        }
                        beanClassList.add(absolutePath);
                    }
                }
            }
        }
    }
}
