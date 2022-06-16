package com.decucin.spring;

import javax.xml.bind.Element;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 表示注解使用的时间
@Target(ElementType.TYPE)   // 表示作用在类上
public @interface ComponentScan {
    String value() default "";
}
