package com.xiaoniu.mybatis.framwork.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XiaoNiuSelect {
    String value();
}
