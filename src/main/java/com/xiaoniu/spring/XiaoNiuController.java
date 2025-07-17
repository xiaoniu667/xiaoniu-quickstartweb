package com.xiaoniu.spring;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XiaoNiuController {
    String value() default "";
}
