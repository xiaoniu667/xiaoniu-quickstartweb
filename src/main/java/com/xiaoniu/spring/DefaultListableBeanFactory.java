package com.xiaoniu.spring;

import com.xiaoniu.exception.BeanDefinitionStoreException;
import com.xiaoniu.exception.BeansException;
import com.xiaoniu.exception.NoSuchBeanDefinitionException;
import net.sf.cglib.core.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 专门用于进行创建bean，初始化bean
 */
public class DefaultListableBeanFactory extends AbstractBeanFactory implements
        ConfigurableListableBeanFactory, BeanDefinitionRegistry {

    private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);


    @Override
    public void preInstantiateSingletons() throws BeansException {
        //1.获取bean，如果没有创建，如果有返回
        String[] beanNames = getBeanDefinitionNames();
        for (String beanName : beanNames) {
            BeanDefinition beanDefinition = getBeanDefinition(beanName);
            //如果是FactoryBean
            if (isFactoryBean(beanDefinition)) {
                //修改beanDefinition中的映射关系然后创建bean
                getBean(FACTORY_BEAN_PREFIX + beanName);
            } else {
                //创建普通的bean
                getBean(beanName);
            }
        }
    }


    //问题1：为什么要锁singletonObjects,原因是如果不锁，两个线程将beanA注入容器可能会注册两次，这当然不符合单例bean的特点
    //问题2：为什么要引入ersingletonObejcts,原因是：增加性能，减少锁的粒度，参考单例模式（可以用singletonFactories代替），同时可以保证存在两个bean的循环依赖创建代理对象的时候也遵循单例bean
    //问题3：为什么要引入singletonFactories，原因是：遵循规范，只有循环依赖的bean可以提前创建代理对象，其他bean需要在后置方法创建代理对象。
    @Override
    public Object getBean(String name) {
        String beanName = transformedBeanName(name);
        //多线程情况下，取的方法可以不锁起来，参考双重检测锁。
        //需要优化为先byType的查找，如果找不到，再byName的查找，否则用户端写的属性名字的问题可能会注入失败
        Object bean = getSingleton(beanName);
        if (bean != null) {
            bean = getObjectForBeanInstance(bean, name, beanName);
            return bean;
        }
        synchronized (singletonObjects) {
            //第二次判断 双重检测锁 有了可以直接返回
            if (singletonObjects.contains(beanName)) {
                return singletonObjects.get(beanName);
            }
            BeanDefinition beanDefinition = getBeanDefinition(beanName);
            try {
                //2.调用构造方法实例化
                Object beanNew = doCreateBeanInstance(beanName, beanDefinition);

                //放入三级缓存解决循环依赖
                singletonFactories.put(beanName, () ->
                        new DefaultAdvisorAutoProxyCreator().createAopProxy(beanNew, beanName));

                //3.属性注入
                populateBean(beanNew, beanDefinition);
                //4.bean的初始化
                bean = this.initialBean(beanNew, beanName);
                //如果是FactoryBean 需要判断是否获取getObject
                bean = getObjectForBeanInstance(bean, name, beanName);
            } catch (Exception e) {
                throw new NoSuchBeanDefinitionException(beanName);
            }
            //  5.将完整的bean放入单例池
            singletonObjects.put(beanName, bean);
            //清除临时bean，不然可能产生多线程情况下，拿到不完整的bean。
            ersingletonObjects.remove(beanName);
            //清除临时bean，不然可能产生多线程情况下，拿到不完整的bean。
            singletonFactories.remove(beanName);
            return bean;
        }
    }

    private Object doCreateBeanInstance(String beanName, BeanDefinition beanDefinition) {
        Object bean;
        try {
            Class<?> beanClass = Class.forName(beanDefinition.getClassName());
            if (beanDefinition.getFactoryMethodName() != null) {
                return instantiateUsingFactoryMethod(beanName, beanClass, beanDefinition);
            }
            //如果是正常的bean对象 通过无参构造方法创建bean
            bean = beanClass.newInstance();
            //判断是否存在oldclass，如果存在就需要设置其对应的属性 比如配合mapperinterface
            autowireConstructor(bean, beanClass, beanDefinition);
        } catch (Exception e) {
            throw new NoSuchBeanDefinitionException(beanName);
        }
        return bean;
    }

    //@bean注解的解析
    private Object instantiateUsingFactoryMethod(String beanName, Class<?> configClazz, BeanDefinition beanDefinition) throws Exception {
        //configClazz是配置类的class文件 beanName为方法名称 factoryMethodName为方法名称
        Object configObject = configClazz.newInstance();
        Method[] methods = configClazz.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(beanDefinition.getFactoryMethodName())) {
                //推断构造方法
                if (method.getParameterCount() == 0) {
                    //直接返回调用方法的对象
                    return method.invoke(configObject, null);
                } else if (method.getParameterCount() == 1) {
                    Object[] argsToUse = new Object[1];
                    Class<?> parameterType = method.getParameterTypes()[0];
                    String parameterName = parameterType.getSimpleName();
                    parameterName = parameterName.substring(0, 1).toLowerCase() + parameterName.substring(1);
                    Object awareBean = getBean(parameterName);
                    argsToUse[0] = awareBean;
                    return method.invoke(configObject, argsToUse);
                } else {
                    throw new RuntimeException("暂时不支持多个参数的属性注入");
                }
            }
        }
        return null;
    }

    private Object autowireConstructor(Object beanNew, Class<?> clazz, BeanDefinition beanDefinition) throws ClassNotFoundException, InvocationTargetException, IllegalAccessException {
        boolean autowireNecessary = false;
        String oldClassName = beanDefinition.getOldClassName();
        if (oldClassName != null) {
            autowireNecessary = true;
        }

        if (autowireNecessary) {
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                //获取对应的set方法
                if (method.getName().startsWith("set") && method.getParameterCount() == 1 && method.getParameterTypes()[0].isAssignableFrom(Class.class)) {
                    method.invoke(beanNew, Class.forName(oldClassName));
                }
            }
        }
        return beanNew;
    }

    private Object getObjectForBeanInstance(Object bean, String name, String beanName) {
        //如果是&开头的bean 直接返回容器之中否则可能需要调用其对应的getObject方法返回一个新的对象
        if (isFactoryDereference(name)) {
            return bean;
        }
        //如果不是&开头但是又是FactoryBean
        if (bean instanceof FactoryBean) {
            FactoryBean factoryBean = (FactoryBean) bean;
            //注入属性
            try {
                return factoryBean.getObject();
            } catch (Exception e) {
                throw new NoSuchBeanDefinitionException(beanName);
            }
        }
        return bean;
    }

    private boolean isFactoryBean(BeanDefinition beanDefinition) {
        String className = beanDefinition.getClassName();
        try {
            Class<?> clazz = Class.forName(className);
            if (FactoryBean.class.isAssignableFrom(clazz)) {
                return true;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isFactoryDereference(String name) {
        return (name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
    }

    private String transformedBeanName(String name) {
        if (name.startsWith(FACTORY_BEAN_PREFIX)) {
            name = name.substring(FACTORY_BEAN_PREFIX.length());
        }
        return name;
    }

    private void populateBean(Object bean, BeanDefinition beanDefinition) throws ClassNotFoundException {
        //得到注入bean的class进行属性注入
        Class<?> beanClass = Class.forName(beanDefinition.getClassName());

        MutablePropertyValues pvs = (beanDefinition.hasPropertyValues() ? beanDefinition.getMutablePropertyValues() : null);

        if (pvs != null) {
            //进行xml文件的属性注入
            MutablePropertyValues mutablePropertyValues = beanDefinition.getMutablePropertyValues();
            for (PropertyValue mutablePropertyValue : mutablePropertyValues) {
                //获取propertyValue对象
                String propertyName = mutablePropertyValue.getName();
                String propertyValue = mutablePropertyValue.getValue();
                String properRef = mutablePropertyValue.getRef();
                String setterMethodByFieldName = "set" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
                //1.如果ref属性不为空
                try {
                    if (properRef != null && !("").equals(properRef)) {
                        //获取依赖注入的bean对象
                        Object beanPopulation = getBean(properRef);
                        //拼接方法名  user=>setUser
                        Method[] methods = beanClass.getMethods();
                        for (Method method : methods) {
                            //找到set方法
                            if (setterMethodByFieldName.equals(method.getName())) {
                                //执行set方法
                                method.invoke(bean, beanPopulation);
                            }
                        }
                    }
                    //2.如果value属性不为空
                    if (propertyValue != null && !("").equals(propertyValue)) {
                        //获取method对象
                        Method method = beanClass.getMethod(setterMethodByFieldName, String.class);
                        method.invoke(bean, propertyValue);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            //通过注解进行属性注入
            Field[] declaredFields = beanClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                if (declaredField.isAnnotationPresent(XiaoNiuAutowired.class)) {
                    String name = declaredField.getName();
                    //此处产生循环依赖的问题
                    Object beanField = getBean(name); //new A().b.a 放入单例子池
                    declaredField.setAccessible(true);
                    try {
                        declaredField.set(bean, beanField);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    //bean的初始化
    private Object initialBean(Object bean, String beanName) {
        //bean的初始化前
//        List<BeanPostProcessor> beanPostProcessorList = beanDefinitionRegistry.getBeanPostProcessorList();
        List<BeanPostProcessor> beanPostProcessorList = getBeanPostProcessors();
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            beanPostProcessor.postProcessBeforeInitialization(bean, beanName);
        }

        //bean的初始化
        if (bean instanceof InitializingBean) {
            try {
                ((InitializingBean) bean).afterPropertiesSet();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //bean的初始化后AOP
        for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
            bean = beanPostProcessor.postProcessAfterInitialization(bean, beanName);
        }
        return bean;
    }

    @Override
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
        return this.beanDefinitionMap.containsKey(beanName);
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
    public Map<String, Object> getAllBeans() {
        return singletonObjects;
    }

    @Override
    public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
        String[] beanNames = getBeanNamesForType(type);
        Map<String, T> result = new LinkedHashMap((int) ((float) beanNames.length / 0.75F), 0.75F);
        for (String beanName : beanNames) {
            try {
                Object beanInstance = getBean(beanName);
                result.put(beanName, (T) beanInstance);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    private <T> String[] getBeanNamesForType(Class<T> type) {
        List<String> result = new ArrayList<>();

        String[] resolvedBeanNames;
        //去单例池中找出所有类型为type的bean
        for (Map.Entry<String, Object> stringObjectEntry : singletonObjects.entrySet()) {
            if (type.isAssignableFrom(stringObjectEntry.getValue().getClass())) {
                result.add(stringObjectEntry.getKey());
            }
        }
        if (result.size() > 0) {
            resolvedBeanNames = result.toArray(new String[0]);
        } else {
            resolvedBeanNames = new String[0];
        }
        return resolvedBeanNames;
    }


    public Map<String, BeanDefinition> getBeanDefinitionMap() {
        return beanDefinitionMap;
    }

}

//可能会产生循环依赖的问题，所以在执行完构造方法之后，将bean对象先放入单例池，
//但是在多线程的情况下，可能会产生空指针的问题
//如果线程A将Aservice放入了单例池，但是还没有进行完依赖注入，也就是注入Bservice，
// 线程B执行了getBean方法，Aservice中的bservice属性还没有值，因此报空指针异常。
//可以就是说将一级缓存的地方锁起来,但是性能不高，为了解决这个问题，我们只需要锁临街资源就行了。
//可以加入二级缓存。
//将没有属性的bean存入二级缓存
//                ersingletonObjects.put(beanName, bean);

//产生循环依赖问题的bean需要进行动态代理，否则可能引起循环依赖的bean不是动态代理对象
//将创建动态代理的方法放入到三级缓存
