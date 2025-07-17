package com.xiaoniu.spring;


import com.xiaoniu.exception.BeansException;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractApplicationContext extends AbstractBeanFactory implements ApplicationContext {
    public static BeanDefinitionRegistry beanDefinitionRegistry;
    private final Object startupShutdownMonitor = new Object();
    private DefaultListableBeanFactory beanFactory;
    public final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    private ConditionEvaluator conditionEvaluator;
    private Class<?> configClass;


    public AbstractApplicationContext() {
    }


    public AbstractApplicationContext(Class<?> configClass) {
        this.conditionEvaluator = new ConditionEvaluator();
        this.beanFactory = new DefaultListableBeanFactory();
        this.configClass = configClass;
        //通过注解的方式注册bean
        beanDefinitionRegistry = new SimpleBeanDefinitionRegistry(configClass, beanFactory, conditionEvaluator);
    }

    public AbstractApplicationContext(String configLocation) {
        this.beanFactory = new DefaultListableBeanFactory();
        //通过xml的方式注册bean
        beanDefinitionRegistry = new XmlBeanDefinitionRegistry(configLocation, beanFactory);

    }

    protected void refresh() {
        synchronized (this.startupShutdownMonitor) {
            //注册BeanFactoryPostProcessor
            postProcessBeanFactory(beanFactory);
            //实现BeanFactoryPostProcessor的增强
            invokeBeanFactoryPostProcessors(beanFactory);
            //注册beanPostProcessor
            registerBeanPostProcessors();
            //创建所有的bean
            finishBeanFactoryInitialization(beanFactory);
        }
    }


    private void registerBeanPostProcessors() {
        //获取注册中心
        String[] beanDefinitionNames = beanDefinitionRegistry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);
            String className = beanDefinition.getClassName();
            //完成AOP注册和用户实现BeanPostProcessor的注册
            try {
                Class<?> loadClass = Class.forName(className);
                if (loadClass.isAnnotationPresent(XiaoNiuAspect.class)) {
                    //进行切面注解的注册
                    aspectAnnotationRegistry.registerAspect(loadClass);
                    addBeanPostProcessor(new DefaultAdvisorAutoProxyCreator());
                }
                //如果这个bean实现了BeanPostProcessor 注册到beanPostProcessorList
                if (BeanPostProcessor.class.isAssignableFrom(loadClass)) {
                    BeanPostProcessor beanPostProcessor = (BeanPostProcessor) loadClass.getDeclaredConstructor().newInstance();
                    addBeanPostProcessor(beanPostProcessor);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public Map<String, Object> getAllBeans() {
        return singletonObjects;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        return beanFactory.getBeansOfType(type);
    }


    protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
        String[] beanDefinitionNames = beanDefinitionRegistry.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = beanDefinitionRegistry.getBeanDefinition(beanDefinitionName);
            String className = beanDefinition.getClassName();
            try {
                Class<?> clazz = Class.forName(className);
                if (BeanDefinitionRegistryPostProcessor.class.isAssignableFrom(clazz)) {
                    //通过容器的方法创建bean
                    BeanDefinitionRegistryPostProcessor beanDefinitionRegistryPostProcessor = (BeanDefinitionRegistryPostProcessor) beanFactory.getBean(beanDefinition.getId());
                    beanDefinitionRegistryPostProcessor.postProcessBeanDefinitionRegistry(beanFactory,configClass);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {

    }

    protected void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
        this.beanFactoryPostProcessors.add(postProcessor);
    }

    protected List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return this.beanFactoryPostProcessors;
    }


    /**
     * bean的创建->实例化->属性注入->初始化
     *
     * @param beanFactory
     */
    protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
        beanFactory.preInstantiateSingletons();
    }

    /**
     * 给用户提供获取bean的方法
     *
     * @param name
     * @return
     * @throws BeansException
     */
    public Object getBean(String name) throws BeansException {
        return beanFactory.getBean(name);
    }


}
