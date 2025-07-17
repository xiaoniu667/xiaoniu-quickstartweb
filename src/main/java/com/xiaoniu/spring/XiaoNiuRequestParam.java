package com.xiaoniu.spring;


import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XiaoNiuRequestParam {

    String value() default "";

    String name() default "";

}
