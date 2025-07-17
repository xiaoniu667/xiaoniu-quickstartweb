package com.xiaoniu.spring;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XiaoNiuResponseBody {
    String value() default "";
}
