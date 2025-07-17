package com.xiaoniu.mybatis.spring;


import com.xiaoniu.exception.BeansException;
import com.xiaoniu.spring.BeanDefinitionRegistry;
import com.xiaoniu.spring.BeanDefinitionRegistryPostProcessor;
import com.xiaoniu.spring.ConfigurableListableBeanFactory;
import com.xiaoniu.spring.XiaoNiuComponent;

/**
 * 自动配置类 这个自动配置主要是为了给basePackage赋值
 */
@XiaoNiuComponent
public class MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor {

    private String basePackage;

    public MapperScannerConfigurer(String basePackage) {
        this.basePackage = basePackage;
    }

    public MapperScannerConfigurer() {
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void setBasePackage(String basePackage) {
        this.basePackage = basePackage;
    }

    //扫描是否存在
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry, Class<?> config) throws BeansException {
        if (this.basePackage != null) {
            ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
            scanner.scan(registry, this.basePackage);
        } else if (config != null) {
            AnnotationMapperScanner annotationMapperScanner = new AnnotationMapperScanner(registry);
            annotationMapperScanner.scan(registry, config.getPackage().getName());
        } else {
            throw new RuntimeException("mybatis-spring:扫描路径错误");
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
