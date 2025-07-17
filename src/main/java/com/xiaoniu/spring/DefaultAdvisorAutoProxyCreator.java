package com.xiaoniu.spring;

import java.util.ArrayList;

/**
 * ，默认创建代理类
 */
class DefaultAdvisorAutoProxyCreator extends AbstractAutoProxyCreator implements AspectRegistry {

    public DefaultAdvisorAutoProxyCreator() {

    }

    /**
     * 寻找切面是否存在当前bean，存在放回数组
     */
    @Override
    protected String[] getAdvicesAndAdvisorsForBean(Class<?> aClass, String beanName) {
        ArrayList<String> existCurrentBeanExpression = new ArrayList<>();
        beanName = beanName.substring(0, 1).toUpperCase() + beanName.substring(1);
        for (String expression : aopInfoList) {
            //这里判断只是演示一下，实际上切面的判断是很复杂的
            //正常的情况应该是识别到这个bean，而不是粗略的判断
            if (expression.contains(beanName)) {
                existCurrentBeanExpression.add(expression);
            }
        }
        return existCurrentBeanExpression.toArray(new String[0]);
    }


}
