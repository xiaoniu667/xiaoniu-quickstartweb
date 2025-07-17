package com.xiaoniu.springmvc;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HandlerMethod {
    private Object bean;
    private Method method;
    private String path;
    private Boolean returnJson;
}
