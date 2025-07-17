package com.xiaoniu.mybatis.spring;

import com.xiaoniu.exception.BeanDefinitionParsingException;
import com.xiaoniu.mybatis.spring.AbstractMapperScanner;
import com.xiaoniu.mybatis.spring.ClassPathMapperScanner;
import com.xiaoniu.mybatis.spring.XiaoNiuMapper;
import com.xiaoniu.spring.BeanDefinition;
import com.xiaoniu.spring.BeanDefinitionRegistry;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

public class AnnotationMapperScanner extends AbstractMapperScanner {

    public AnnotationMapperScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected Set<BeanDefinition> doScan(String basePackage) {
        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
        basePackage = basePackage.replace(".", "/");
        ClassLoader classLoader = ClassPathMapperScanner.class.getClassLoader();
        //资源文件，里面有file属性
        URL resource = classLoader.getResource(basePackage);
        if (resource == null) {
            throw new BeanDefinitionParsingException("扫描路径不存在");
        }
        File file = new File(resource.getFile());
        getFilePath(file);
        for (String fileClassPath : beanClassList) {
            try {
                Class<?> loadClass = classLoader.loadClass(fileClassPath); //com.xiaoniu.mybatis.framwork.spring.UserService
                if(loadClass.isAnnotationPresent(XiaoNiuMapper.class)){
                    //为每一个mapper接口创建一个代理工厂MapperProxyFactory
                    //说明他是一个bean对象，需要注入到ioc容器之中。
                    BeanDefinition beanDefinition = new BeanDefinition();
                    String beanName = loadClass.getSimpleName(); //UserService
                    //字符串将第一个字符变为小写
                    beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
                    beanDefinition.setId(beanName);//userService
                    beanDefinition.setClassName(fileClassPath);//com.xiaoiu.bean.userMapper
                    //注册到注册中心，放到map之中。
                    beanDefinitions.add(beanDefinition);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return beanDefinitions;
    }
}
