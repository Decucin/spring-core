package com.decucin.service;

import com.decucin.spring.BeanPostProcessor;
import com.decucin.spring.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    @Override
    public Object postProcessorBeforeInitialization(String beanName, Object bean) {
        if("userService".equals(beanName)){
            ((UserService)bean).setName("decucin的userService");
            System.out.println("这里userService执行了初始化之前的方法");
        }
        return bean;
    }

    @Override
    public Object postProcessorAfterInitialization(String beanName, Object bean) {
        if("userService".equals(beanName)){
            Object proxyInstance = Proxy.newProxyInstance(MyBeanPostProcessor.class.getClassLoader(),
                    bean.getClass().getInterfaces(),
                    (proxy, method, args) -> {
                // 这里是先执行切面逻辑，再执行原始的bean的方法
                System.out.println("执行了代理逻辑！");
                return method.invoke(bean, args);
            });
            return proxyInstance;
        }
        return bean;
    }
}
