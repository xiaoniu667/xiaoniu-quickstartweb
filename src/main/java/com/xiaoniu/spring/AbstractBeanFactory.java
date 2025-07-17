package com.xiaoniu.spring;

import com.xiaoniu.exception.NoSuchBeanDefinitionException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractBeanFactory extends DefaultSingletonBeanRegistry implements BeanFactory {
    private static final List<BeanPostProcessor> beanPostProcessors = new ArrayList<>();
    protected AspectAnnotationRegistry aspectAnnotationRegistry = new AspectAnnotationRegistry();

    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        beanPostProcessors.remove(beanPostProcessor);
        beanPostProcessors.add(beanPostProcessor);
    }

    public void addBeanPostProcessors(Collection<? extends BeanPostProcessor> beanPostProcessors) {
        AbstractBeanFactory.beanPostProcessors.removeAll(beanPostProcessors);
        AbstractBeanFactory.beanPostProcessors.addAll(beanPostProcessors);
    }

    public int getBeanPostProcessorCount() {
        return beanPostProcessors.size();
    }

    public List<BeanPostProcessor> getBeanPostProcessors() {
        return beanPostProcessors;
    }


//
//    private void populateBean(Object bean, Class<?> beanClass, BeanDefinition beanDefinition) {
//
//        MutablePropertyValues pvs = (beanDefinition.hasPropertyValues() ? beanDefinition.getMutablePropertyValues() : null);
//
//        if (pvs != null) {
//            //进行xml文件的属性注入
//            MutablePropertyValues mutablePropertyValues = beanDefinition.getMutablePropertyValues();
//            for (PropertyValue mutablePropertyValue : mutablePropertyValues) {
//                //获取propertyValue对象
//                String propertyName = mutablePropertyValue.getName();
//                String propertyValue = mutablePropertyValue.getValue();
//                String properRef = mutablePropertyValue.getRef();
//                String setterMethodByFieldName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
//                //1.如果ref属性不为空
//                try {
//                    if (properRef != null && !("").equals(properRef)) {
//                        //获取依赖注入的bean对象
//                        Object beanPopulation = getBean(properRef);
//                        //拼接方法名  user=>setUser
//                        Method[] methods = beanClass.getMethods();
//                        for (Method method : methods) {
//                            //找到set方法
//                            if (setterMethodByFieldName.equals(method.getName())) {
//                                //执行set方法
//                                method.invoke(bean, beanPopulation);
//                            }
//                        }
//                    }
//                    //2.如果value属性不为空
//                    if (propertyValue != null && !("").equals(propertyValue)) {
//                        //获取method对象
//                        Method method = beanClass.getMethod(setterMethodByFieldName, String.class);
//                        method.invoke(bean, propertyValue);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        } else {
//            //通过注解进行属性注入
//            Field[] declaredFields = beanClass.getDeclaredFields();
//            for (Field declaredField : declaredFields) {
//                if (declaredField.isAnnotationPresent(XiaoNiuAutowired.class)) {
//                    String name = declaredField.getName();
//                    //此处产生循环依赖的问题
//                    Object beanField = getBean(name); //new A().b.a 放入单例子池
//                    declaredField.setAccessible(true);
//                    try {
//                        declaredField.set(bean, beanField);
//                    } catch (IllegalAccessException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//
//    }
//
//
//    //bean的初始化
//    private Object initialBean(Object bean, String beanName) {
//        //bean的初始化前
//        List<BeanPostProcessor> beanPostProcessorList = getBeanPostProcessors();
//        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
//            beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
//        }
//
//        //bean的初始化
//        if (bean instanceof InitializingBean) {
//            try {
//                ((InitializingBean) bean).afterPropertiesSet();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        //bean的初始化后AOP
//        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
//            bean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
//        }
//        return bean;
//    }
//
//    private Object getObjectForBeanInstance(Object bean, String name, String beanName) {
//        //如果是&开头的bean 直接返回容器之中否则可能需要调用其对应的getObject方法返回一个新的对象
//        if (isFactoryDereference(name)) {
//            return bean;
//        }
//        //如果不是&开头但是又是FactoryBean
//        if (bean instanceof FactoryBean) {
//            FactoryBean factoryBean = (FactoryBean) bean;
//            try {
//                return factoryBean.getObject();
//            } catch (Exception e) {
//                throw new NoSuchBeanDefinitionException(beanName);
//            }
//        }
//        return bean;
//    }
//
//    private boolean isFactoryDereference(String name) {
//        return (name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
//    }
//
//
//    protected String transformedBeanName(String name) {
//        if (name.startsWith(FACTORY_BEAN_PREFIX)) {
//            name = name.substring(FACTORY_BEAN_PREFIX.length());
//        }
//        return name;
//    }

}

//获取bean，如果没有创建，如果有返回
//        String[] beanNames = getBeanDefinitionNames();
//                for (String beanName : beanNames) {
//                BeanDefinition beanDefinition = getBeanDefinition(beanName);
//                //如果是FactoryBean
//                if (isFactoryBean(beanDefinition)) {
//                //修改beanDefinition中的映射关系然后创建bean
//                getBean(FACTORY_BEAN_PREFIX + beanName);
//                } else {
//                //创建普通的bean
//                getBean(beanName);
//                }
//                }
