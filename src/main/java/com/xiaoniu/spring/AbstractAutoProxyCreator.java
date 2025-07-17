package com.xiaoniu.spring;


import com.xiaoniu.exception.AopException;

abstract class AbstractAutoProxyCreator implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        //我们自己平时创建的 AOP 对象基本上都是在 applyBeanPostProcessorsAfterInitialization 中进行处理的
        //spring底层通过两种方法实现动态代理，一种是CGLIB，一种是JDK动态代理，如果目标对象是接口，则使用JDK动态代理，否则使用CGLIB动态代理
        //查看当前bean是否需要创建代理对象
        String[] specificInterceptors = getAdvicesAndAdvisorsForBean(bean.getClass(), beanName);
        if (specificInterceptors.length > 0) {
            Object proxy = createAopProxy(bean, beanName);
            return proxy != null ? proxy : bean;
        }
        return bean;

    }

    protected abstract String[] getAdvicesAndAdvisorsForBean(Class<?> aClass, String beanName);

    protected Object createAopProxy(Object bean, String beanName) {
        Class<?> targetClass = bean.getClass();
        if (targetClass == null) {
            throw new AopException("TargetSource无法确定目标类:需要创建代理需要接口或目标");
        }
        //如果是实现接口，则使用JDK动态代理
        Class<?>[] interfaces = targetClass.getInterfaces();
        if (interfaces.length > 0) {
            return new JdkProxyCreator().createProxy(bean, beanName);
        }
        //否则使用cglib动态代理
        return new CglibProxyCreator().createProxy(bean, beanName);
    }

}

