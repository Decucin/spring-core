package com.decucin.service;

import com.decucin.spring.ApplicationContext;

public class Test {
    public static void main(String[] args) {
        ApplicationContext applicationContext = new ApplicationContext(AppConfig.class);
        UserInterface userService = (UserInterface)applicationContext.getBean("userService");
//        System.out.println(userService.getBeanName());
        userService.test();
    }
}
