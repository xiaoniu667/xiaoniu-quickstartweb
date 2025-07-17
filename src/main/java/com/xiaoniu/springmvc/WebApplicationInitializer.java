package com.xiaoniu.springmvc;

import javax.servlet.ServletContext;

public interface WebApplicationInitializer  {
    void onStartup(ServletContext servletContext);
}
