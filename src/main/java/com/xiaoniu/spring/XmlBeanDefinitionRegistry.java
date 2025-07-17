package com.xiaoniu.spring;


import com.xiaoniu.exception.BeanDefinitionStoreException;
import com.xiaoniu.exception.NoSuchBeanDefinitionException;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class XmlBeanDefinitionRegistry implements BeanDefinitionRegistry,BeanDefinitionReader {

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(64);

    public XmlBeanDefinitionRegistry(String configLocation,BeanDefinitionRegistry beanFactory) {
        //读取配置文件注册bean
        this.loadBeanDefinitions(configLocation,beanFactory);
    }

    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        this.beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        this.beanDefinitionMap.remove(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return this.beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.contains(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        if (this.beanDefinitionMap.size() > 0) {
            return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
        }
        return new String[0];
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    @Override
    public BeanDefinitionRegistry getRegistry() {
        return this;
    }

    public int loadBeanDefinitions(String resource,BeanDefinitionRegistry beanFactory) throws BeanDefinitionStoreException {
        //使用dom4j解析xml文件
        SAXReader saxReader = new SAXReader();
        //加载XML文件成为文档对象Document对象。
        Document document = null;
        try {
            document = saxReader.read(XmlBeanDefinitionRegistry.class.getClassLoader().getResourceAsStream(resource));
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        //拿根元素
        Element rootElement = document.getRootElement();
        List<Element> beans = rootElement.elements("bean");
        for (Element bean : beans) {
            //获取属性
            String id = bean.attributeValue("id");
            String classname = bean.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition();
            beanDefinition.setId(id);
            beanDefinition.setClassName(classname);
            //创建MutablePropertyValues对象
            MutablePropertyValues mutablePropertyValues = new MutablePropertyValues();
            //获取bean下面的property属性
            List<Element> properties = bean.elements("property");
            for (Element property : properties) {
                String name = property.attributeValue("name");
                String value = property.attributeValue("value");
                String ref = property.attributeValue("ref");
                PropertyValue propertyValue = new PropertyValue(name, ref, value);
                mutablePropertyValues.addPropertyValue(propertyValue);
            }
            //将MutablePropertyValues对象设置到beanDefinition中
            beanDefinition.setMutablePropertyValues(mutablePropertyValues);
            //将beanDefinition注册到注册中心
            this.registerBeanDefinition(id, beanDefinition);
            beanFactory.registerBeanDefinition(id, beanDefinition);
        }

        return 0;
    }
}


