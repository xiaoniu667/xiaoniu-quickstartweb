package com.xiaoniu.exception;


public class BeanDefinitionStoreException extends BeansException {

    private final String resourceDescription;

    private final String beanName;

    public BeanDefinitionStoreException(String message) {
        super(message);
        this.resourceDescription = null;
        this.beanName = null;
    }

    public BeanDefinitionStoreException(
            String resourceDescription, String beanName, String msg, Throwable cause) {
        super("无效的bean定义，bean:'" + beanName + "' 定义在 " + resourceDescription + ": " + msg,
                cause);
        this.resourceDescription = resourceDescription;
        this.beanName = beanName;
    }

}
