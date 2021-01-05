package com.lagou.edu.mvcframework.annotations;

import java.lang.annotation.*;

/**
 * @Documented:文本编译器加载到
 * @Target：指定加载的对象 ElementType.TYPE,ElementType.METHOD 类和方法都行
 * @Retention：什么时候起效 RetentionPolicy.RUNTIME 运行时
 */
@Documented
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface LagouRequestMapping {
    String value() default "";
}
