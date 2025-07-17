package com.xiaoniu.springmvc;

import lombok.SneakyThrows;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.HandlesTypes;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Set;

/**
 * SPI的全称是Service Provider Interface，中文名为服务提供者接口，是一种服务发现机制，
 * 基于JDK内置的动态加载实现扩展点的机制
 * （通过在 ClassPath 路径下的 META-INF/services 文件夹查找文件，自动加载文件里所定义的类），
 * 这一机制使得框架扩展和替换组件变得容易
 */
@HandlesTypes(WebApplicationInitializer.class)
public class WebServletContainer implements ServletContainerInitializer {

    //c代表将WebApplicationInitializer下的所有子类传进来
    @SneakyThrows
    @Override
    public void onStartup(Set<Class<?>> webApplications, ServletContext ctx) throws ServletException {
        //创建dispatcherServlet以及初始化IOC
        if (webApplications != null) {
            ArrayList<WebApplicationInitializer> initializers = new ArrayList<>(webApplications.size());
            for (Class<?> webApplication : webApplications) {
                if (!webApplication.isInterface() && !Modifier.isAbstract(webApplication.getModifiers())
                        && WebApplicationInitializer.class.isAssignableFrom(webApplication)) {
                    Constructor<?> ctor = webApplication.getDeclaredConstructor();
                    if ((!Modifier.isPublic(ctor.getModifiers()) || !Modifier.isPublic(ctor.getDeclaringClass().getModifiers())) && !ctor.isAccessible()) {
                        ctor.setAccessible(true);
                    }
                    initializers.add((WebApplicationInitializer) ctor.newInstance());
                }
            }
            if(initializers.size() > 0){
                for (WebApplicationInitializer initializer : initializers) {
                    initializer.onStartup(ctx);
                }
            }
        }
    }
}
