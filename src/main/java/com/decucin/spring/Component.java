package com.decucin.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 表示运行时使用
@Target(ElementType.TYPE)   // 表示作用在类上
public @interface Component {

    String value() default "";  // 指定bean名称
}


