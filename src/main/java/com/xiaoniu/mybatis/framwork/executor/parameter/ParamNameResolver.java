package com.xiaoniu.mybatis.framwork.executor.parameter;

import com.xiaoniu.mybatis.framwork.Configuration;
import com.xiaoniu.mybatis.framwork.annotation.XiaoNiuParam;
import com.xiaoniu.mybatis.framwork.reflection.ParamNameUtil;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class ParamNameResolver {
    private Configuration configuration;
    private Method method;

    private boolean hasParamAnnotation;

    //保证有序性
    private SortedMap<Integer, String> names;

    public ParamNameResolver(Configuration configuration, Method method) {
        this.configuration = configuration;
        this.method = method;
        resolverAnnotation();
    }

    /**
     * 解析方法里面的参数
     *
     * @param args
     * @return
     */
    public Object getNamedParams(Object[] args) {
        final int paramCount = names.size();
        if (args == null || paramCount == 0) {
            return null;
        } else if (paramCount == 1 && !hasParamAnnotation) {
            return args[names.firstKey()];
        } else {
            final Map<String, Object> paramMap = new HashMap<>();
            //遍历每一个参数
            for (Map.Entry<Integer, String> entry : names.entrySet()) {
                //key=name value=参数的值
                //key= arg0 value = 参数值
                paramMap.put(entry.getValue(), args[entry.getKey()]);
            }
            return paramMap;
        }
    }

    //    解析注解
    private void resolverAnnotation() {
        names = new TreeMap<Integer, String>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            if (parameter.isAnnotationPresent(XiaoNiuParam.class)) {
                hasParamAnnotation = true;
                String value = parameter.getAnnotation(XiaoNiuParam.class).value();
                names.put(i, value);
            } else {
                //arg0
                String name = ParamNameUtil.getParamNames(method).get(i);
                names.put(i, name);
            }
        }
        //不允许用户修改
        names = Collections.unmodifiableSortedMap(names);
    }
}
