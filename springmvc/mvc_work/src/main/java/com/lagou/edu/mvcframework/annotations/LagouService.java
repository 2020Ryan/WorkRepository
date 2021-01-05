package com.lagou.edu.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @Documented:文本编译器加载到
 * @Target：指定加载的对象 ElementType.TYPE类
 * @Retention：什么时候起效 RetentionPolicy.RUNTIME 运行时
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface LagouService {
    String value() default "";
}
