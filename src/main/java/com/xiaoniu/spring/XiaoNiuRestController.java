package com.xiaoniu.spring;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface XiaoNiuRestController {
    String value() default "";
}
