package com.decucin.spring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 表示运行时使用
@Target(ElementType.FIELD)   // 表示作用在字段上
public @interface Autowired {

}


