package com.decucin.spring;

import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class ApplicationContext {

    private Class configClass;

    private ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Object> singletonObjets = new ConcurrentHashMap<>();

    private ArrayList<BeanPostProcessor> beanPostProcessorList = new ArrayList<>();

    public ApplicationContext(Class configClass) {
        this.configClass = configClass;

        // 开始扫描
        if(configClass.isAnnotationPresent(ComponentScan.class)){
            ComponentScan componentScanAnnotation = (ComponentScan) configClass.getAnnotation(ComponentScan.class);
            // 现在找到了扫描路径
            String path = componentScanAnnotation.value();
            // 但其实我们需要的文件是.class文件而不是.java
            path = path.replace(".", "/");
            // 先找到ClassLoader里的resouce（classPath）
            ClassLoader classLoader = ApplicationContext.class.getClassLoader();
            URL resource = classLoader.getResource(path);
            // 需要的是要把
            File file = new File(resource.getFile());
            if(file.isDirectory()){
                File[] files = file.listFiles();
                // 要的只是.class文件
                for (File f : files) {
                    String fileName = f.getAbsolutePath();
                    if(fileName.endsWith(".class")){
                        // 判断是否是bean就是看有没有注解
                        // 获取注解的方式： 反射
                        // 从类加载器中加载进去
                        String className = fileName.substring(fileName.indexOf("com"), fileName.indexOf(".class"));
                        className = className.replace("/", ".");
                        Class<?> clazz = null;
                        try {
                            clazz = classLoader.loadClass(className);
                            // 有这个注解说明是bean
                            if(clazz.isAnnotationPresent(Component.class)){
                                String beanName = clazz.getAnnotation(Component.class).value();

                                // 先判断是否实现了某些接口
                                if(BeanPostProcessor.class.isAssignableFrom(clazz)){
                                    BeanPostProcessor instance = (BeanPostProcessor) clazz.newInstance();
                                    beanPostProcessorList.add(instance);
                                }

                                // 要是空的话初始化
                                if("".equals(beanName)){
                                    beanName = Introspector.decapitalize(clazz.getSimpleName());
                                }
                                // 生成一个BeanDefinition对象
                                BeanDefinition beanDefinition = new BeanDefinition();
                                beanDefinition.setType(clazz);
                                // 从scope注解判断其是单例还是多例
                                if(clazz.isAnnotationPresent(Scope.class)){
                                    String scope = clazz.getAnnotation(Scope.class).value();
                                    beanDefinition.setScope(scope);
                                }else{
                                    beanDefinition.setScope("singleton");
                                }
                                beanDefinitionMap.put(beanName, beanDefinition);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }

        // 创建单例bean对象
        for (String beanName : beanDefinitionMap.keySet()) {
            BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
            if(beanDefinition.getScope().equals("singleton")){
                Object bean = creatBean(beanName, beanDefinition);
                singletonObjets.put(beanName, bean);
            }
        }
    }

    private Object creatBean(String beanName, BeanDefinition beanDefinition){
        Class clazz = beanDefinition.getType();
        // 具有无参构造方法
        try {
            Object instance = clazz.getConstructor().newInstance();

            // 这里依赖注入
            for (Field f : clazz.getDeclaredFields()) {
                if(f.isAnnotationPresent(Autowired.class)){
                    // 通过反射赋值
                    f.setAccessible(true);
                    f.set(instance, getBean(f.getName()));
                }
            }

            // 回调
            if(instance instanceof BeanNameAware){
                ((BeanNameAware) instance).setBeanName(beanName);
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessorBeforeInitialization(beanName, beanDefinition);
            }

            // 初始化
            if(instance instanceof InitializingBean){
                ((InitializingBean) instance).afterPropertiesSet();
            }

            for (BeanPostProcessor beanPostProcessor : beanPostProcessorList) {
                instance = beanPostProcessor.postProcessorAfterInitialization(beanName, beanDefinition);
            }

            return instance;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object getBean(String beanName){
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if(beanDefinition == null){
            throw new NullPointerException();
        }else{
            String scope = beanDefinition.getScope();
            // 单例bean直接拿
            // 多例bean创建
            if(scope.equals("singleton")){
                Object bean = singletonObjets.get(beanName);
                if(bean == null){
                    Object o = creatBean(beanName, beanDefinition);
                    singletonObjets.put(beanName, o);
                    return o;
                }
                return bean;
            }else{
                return creatBean(beanName, beanDefinition);
            }
        }
    }
}
