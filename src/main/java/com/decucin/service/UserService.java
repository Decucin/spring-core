package com.decucin.service;

import com.decucin.spring.*;

@Component("userService")
@Scope("singleton")
public class UserService implements BeanNameAware, InitializingBean, UserInterface{

    @Autowired
    private  OrderService orderService;

    private String beanName;

    private String field;

    public void test(){
        System.out.println(orderService);
    }

    @Override
    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }


    @Override
    public void afterPropertiesSet() {
        System.out.println("执行了初始化方法");
    }
}
