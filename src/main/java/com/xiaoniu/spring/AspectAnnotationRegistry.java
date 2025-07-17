package com.xiaoniu.spring;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * 注册切面
 */
public class AspectAnnotationRegistry implements AspectRegistry {

    public void registerAspect(Class<?> loadClass) {
        //如果是切面，判断是否存在before,after,around等等
        Method[] declaredMethods = loadClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            if (declaredMethod.isAnnotationPresent(XiaoNiuBefore.class)) {
                //将注解上的信息注册到注解集合
                XiaoNiuBefore beforeAnnotation = declaredMethod.getAnnotation(XiaoNiuBefore.class);
                String expression = beforeAnnotation.value();
                if (checkExpression(expression)) {
                    aopInfoList.add(beforeAnnotation.value());
                }
            }
            if (declaredMethod.isAnnotationPresent(XiaoNiuAround.class)) {
                //将注解上的信息注册到注解集合
                XiaoNiuAround beforeAnnotation = declaredMethod.getAnnotation(XiaoNiuAround.class);
                String expression = beforeAnnotation.value();
                if (checkExpression(expression)) {
                    aopInfoList.add(beforeAnnotation.value());
                }
            }
            if (declaredMethod.isAnnotationPresent(XiaoNiuAfter.class)) {
                //将注解上的信息注册到注解集合
                XiaoNiuAfter beforeAnnotation = declaredMethod.getAnnotation(XiaoNiuAfter.class);
                String expression = beforeAnnotation.value();
                if (checkExpression(expression)) {
                    aopInfoList.add(beforeAnnotation.value());
                }
            }
        }
    }

    public boolean checkExpression(String express) {
        return true;
    }

    public ArrayList<String> getAopInfoList() {
        return aopInfoList;
    }

}
