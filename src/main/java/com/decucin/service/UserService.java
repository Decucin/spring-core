package com.decucin.service;

import com.decucin.spring.*;

@Component("userService")
@Scope("singleton")
public class UserService implements BeanNameAware, InitializingBean, UserInterface{

    @Autowired
    private  OrderService orderService;

    private String name;

//    public void setOrderService(OrderService orderService) {
//        this.orderService = orderService;
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

//    public OrderService getOrderService() {
//        return orderService;
//    }

//    public String getBeanName() {
//        return beanName;
//    }

//    private String beanName;

    public void test(){
        System.out.println(orderService);
        System.out.println(name);
    }

    @Override
    public void setBeanName(String beanName) {
        this.name = beanName;
    }

    @Override
    public void afterPropertiesSet() {
        System.out.println("执行了初始化方法");
    }
}
