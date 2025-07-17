package com.xiaoniu.spring;

import com.xiaoniu.spring.XiaoNiuComponent;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XiaoNiuService {
    String value() default "";
}
