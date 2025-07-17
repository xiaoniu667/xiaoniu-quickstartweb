package com.xiaoniu.springmvc;

import com.google.gson.Gson;
import com.xiaoniu.spring.*;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

public class DispatcherServlet extends BaseHttpServlet {
    private final Map<String, HandlerMethod> handlerMethodMap = new HashMap<>();

    public DispatcherServlet(WebApplicationContext webApplicationContext) {
        super(webApplicationContext);
    }


    @Override
    protected void onRefresh(WebApplicationContext webApplicationContext) {
        //初始化路径对应的方法
        initHandlerMapping(webApplicationContext);
    }

    private void initHandlerMapping(WebApplicationContext webApplicationContext) {
        Map<String, Object> allBeans = webApplicationContext.getAllBeans();
        for (Map.Entry<String, Object> stringObjectEntry : allBeans.entrySet()) {
            Object bean = stringObjectEntry.getValue();
            Class<?> beanClass = bean.getClass();
            if (beanClass.isAnnotationPresent(XiaoNiuRestController.class)
                    || beanClass.isAnnotationPresent(XiaoNiuController.class)) {
                boolean beanIsRestController = (beanClass.isAnnotationPresent(XiaoNiuRestController.class) || beanClass.isAnnotationPresent(XiaoNiuResponseBody.class));
                //take the requestMapping
                String classUrl = "";
                if (beanClass.isAnnotationPresent(XiaoNiuRequestMapping.class)) {
                    XiaoNiuRequestMapping requestMapping = beanClass.getAnnotation(XiaoNiuRequestMapping.class);
                    String path = requestMapping.value();
                    //if user not add /
                    classUrl = addStartPath(path);
                }
                //take all methods
                Method[] methods = beanClass.getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(XiaoNiuRequestMapping.class)) {
                        XiaoNiuRequestMapping requestMappingMethod = method.getAnnotation(XiaoNiuRequestMapping.class);
                        //解析路径
                        String methodUrl = requestMappingMethod.value();
                        methodUrl = addStartPath(methodUrl);
                        //判断是否有responseBody注解
                        boolean returnJson;
                        if (beanIsRestController) {
                            returnJson = true;
                        } else {
                            //if class do not have responseBody check method
                            returnJson = method.isAnnotationPresent(XiaoNiuResponseBody.class);
                        }
                        HandlerMethod handlerMethod = new HandlerMethod(bean, method, classUrl + methodUrl, returnJson);
                        handlerMethodMap.put(classUrl + methodUrl, handlerMethod);
                    }
                }
            }
        }
    }

    private String addStartPath(String path) {
        String classUrl = "";
        if (!path.startsWith("/")) {
            classUrl = "/" + path;
        } else {
            classUrl = path;
        }
        return classUrl;
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //设置编码
        resp.setCharacterEncoding("gbk");
        String requestURI = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = requestURI.replace(contextPath, "");
        HandlerMethod handlerMethod = handlerMethodMap.get(path);
        if (handlerMethod != null) {
            Method method = handlerMethod.getMethod();
            Object instance = handlerMethod.getBean();
            Boolean isJson = handlerMethod.getReturnJson();
            //解析参数
            Object[] args = getMethodArgs(method, req, resp);
            try {
                //调用方法
                if (isJson) {
                    //返回json数据
                    Object object = method.invoke(instance, args);
                    Gson gson = new Gson();
                    String pJson = gson.toJson(object);
                    PrintWriter printWriter = resp.getWriter();
                    printWriter.write(pJson);
                } else {
                    //返回视图
                    method.invoke(instance, args);
                    PrintWriter printWriter = resp.getWriter();
                    printWriter.write("暂时不支持视图返回模式,请加上restcontroller注解");
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private Object[] getMethodArgs(Method method, HttpServletRequest req, HttpServletResponse resp) {
        Class<?>[] parameterClazz = method.getParameterTypes();
        Object[] ars = new Object[parameterClazz.length];
        int argIndex = 0;
        int index = 0;
        for (Class<?> clazz : parameterClazz) {
            if (clazz.isAssignableFrom(ServletRequest.class)) {
                ars[argIndex++] = req;
            }
            if (clazz.isAssignableFrom(ServletResponse.class)) {
                ars[argIndex++] = resp;
            }
            Annotation[] parameterAnnotations = method.getParameterAnnotations()[index];
//            Parameter[] parameters = method.getParameters();
            //解析注解
            if (parameterAnnotations.length > 0) {
                for (int i = 0; i < parameterAnnotations.length; i++) {
                    Annotation parameterAnnotation = parameterAnnotations[i];
                    if (XiaoNiuRequestParam.class.isAssignableFrom(parameterAnnotation.getClass())) {
                        XiaoNiuRequestParam pa = (XiaoNiuRequestParam) parameterAnnotation;
                        String value = pa.value();
                        if (!"".equals(value)) {
                            String parameter = req.getParameter(pa.value());
                            ars[argIndex++] = parameter;
                        } else {
//                            Parameter parameter = parameters[index];
//                            String name = parameter.getName(); //arg0 arg1
                            List<String> paramNameList = Collections.list(req.getParameterNames());
                            if (!paramNameList.isEmpty()) {
                                String paramName = paramNameList.get(index);
                                ars[argIndex++] = req.getParameter(paramName);
                            }
                        }
                    }
                }
            }
            index++;
        }
        return ars;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }
}
