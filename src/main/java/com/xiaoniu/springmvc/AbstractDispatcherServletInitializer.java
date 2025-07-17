package com.xiaoniu.springmvc;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import java.util.Objects;

/**
 * 让用户继承这一个类 实现0配置 配置servlet
 */
public abstract class AbstractDispatcherServletInitializer implements WebApplicationInitializer {

    private static final String  DEFAULT_SERVLET_NAME = "dispatcherServlet";
    private static final String  DEFAULT_FILTER_NAME = "filter";
    private static final Integer  DEFAULT_FILE_SIZE_MB = 1024*1024;


    @Override
    public void onStartup(ServletContext servletContext) {
        AnnotationConfigWebApplicationContext webApplicationContext =
                new AnnotationConfigWebApplicationContext(getScanClass());
        DispatcherServlet dispatcherServlet = new DispatcherServlet(webApplicationContext);
        ServletRegistration.Dynamic dynamic = servletContext.addServlet(DEFAULT_SERVLET_NAME, dispatcherServlet);
        //设置信息
        dynamic.setLoadOnStartup(1);
        //设置文件上传大小 文件最大大小 临时文件存储位置
        dynamic.setMultipartConfig(getMultipartConfigElement());
        //设置映射器
        dynamic.addMapping(getMappings());
        String[] filters = getFilters();
        if(!Objects.isNull(filters)){
            for (String filter : filters) {
                servletContext.addFilter(DEFAULT_FILTER_NAME,filter);
            }
        }
    }

    protected abstract Class<?> getScanClass();

    protected MultipartConfigElement getMultipartConfigElement(){
        return new MultipartConfigElement(null,DEFAULT_FILE_SIZE_MB*10,DEFAULT_FILE_SIZE_MB*10,DEFAULT_FILE_SIZE_MB*10);
    }
    //映射器
    protected  String[] getMappings(){
        return new String[]{"/"};
    }

    //过滤器
    protected abstract String[] getFilters();

}
