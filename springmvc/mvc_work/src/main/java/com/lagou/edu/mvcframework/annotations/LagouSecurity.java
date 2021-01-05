package com.lagou.edu.mvcframework.annotations;

import java.lang.annotation.*;

@Documented//说明该注解被包含在javadoc中
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)//注解在字节码中，运行时反射获得
public @interface LagouSecurity {
    String[] value() default "";
}

