package com.xiaoniu.springmvc;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;


public abstract class BaseHttpServlet extends HttpServlet {

    protected WebApplicationContext webApplicationContext;

    public BaseHttpServlet(WebApplicationContext webApplicationContext) {
        this.webApplicationContext = webApplicationContext;
    }

    @Override
    public void init() throws ServletException {
        onRefresh(webApplicationContext);
    }

    protected abstract void onRefresh(WebApplicationContext webApplicationContext);
}
