package com.xiaoniu.spring;


import com.xiaoniu.exception.BeanDefinitionParsingException;
import com.xiaoniu.exception.BeanDefinitionStoreException;
import com.xiaoniu.exception.NoSuchBeanDefinitionException;
import com.xiaoniu.springboot.XiaoNiuSpringBootApplication;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleBeanDefinitionRegistry implements BeanDefinitionRegistry {

    private final String OSName = System.getProperty("os.name");
    //将注解扫描的bean存入map容器
    protected ConcurrentHashMap<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(64);
    protected ArrayList<String> beanClassList = new ArrayList<>();


    public SimpleBeanDefinitionRegistry(Class<?> config, BeanDefinitionRegistry beanFactory, ConditionEvaluator conditionEvaluator) {
        //将扫描注解，将bean信息封装为BeanDefinition对象，放入map容器中，之后进行bean的初始化操作
        this.currentRegistryProcessor(config, beanFactory, conditionEvaluator);
    }

    protected void currentRegistryProcessor(Class<?> configClass, BeanDefinitionRegistry beanFactory, ConditionEvaluator conditionEvaluator) {
        //获取注解  //获取字节码文件  将bean信息封装为BeanDefinition对象，放入map容器中
        String oldScanPath = "";
        if (configClass.isAnnotationPresent(XiaoNiuSpringBootApplication.class)) {
            oldScanPath = configClass.getPackage().getName();
        } else if (configClass.isAnnotationPresent(XiaoNiuComponentScan.class)) {
            XiaoNiuComponentScan annotation = (XiaoNiuComponentScan) configClass.getAnnotation(XiaoNiuComponentScan.class);
            oldScanPath = annotation.value();
        }
        oldScanPath = oldScanPath.replace(".", "/");
        ClassLoader classLoader = SimpleBeanDefinitionRegistry.class.getClassLoader();
        //资源文件，里面有file属性
        URL resource = classLoader.getResource(oldScanPath);
        if (resource == null) {
            throw new BeanDefinitionParsingException("扫描路径不存在");
        }
        File file = new File(resource.getFile());
        getFilePath(file);
        for (String fileClassPath : beanClassList) {
            try {
                Class<?> loadClass = classLoader.loadClass(fileClassPath); //com.xiaoniu.spring.UserService
                //如果是springboot的注解，需要扫描上面的import注解中的自动配置类
                if (loadClass.isAnnotationPresent(XiaoNiuSpringBootApplication.class)) {
                    XiaoNiuSpringBootApplication xiaoNiuSpringBootApplicationAnnotation = loadClass.getAnnotation(XiaoNiuSpringBootApplication.class);
                    Annotation[] annotations = xiaoNiuSpringBootApplicationAnnotation.annotationType().getAnnotations();
                    for (Annotation annotation : annotations) {
                        if (annotation instanceof XiaoNiuImport) {
                            XiaoNiuImport xiaoNiuImportAnnotation = (XiaoNiuImport) annotation;
                            Class<?>[] value = xiaoNiuImportAnnotation.value();
                            for (Class<?> aClass : value) {
                                String className = aClass.getName(); //com.xiaoniu.springboot.WebServerChooser
                                //判断是否是bean
                                verifyBean(beanFactory, conditionEvaluator, className, aClass);
                            }
                        }
                    }
                }
                verifyBean(beanFactory, conditionEvaluator, fileClassPath, loadClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //通过是否有XiaoNiuComponent等注解判断是否是bean
    private void verifyBean(BeanDefinitionRegistry beanFactory, ConditionEvaluator conditionEvaluator, String fileClassPath, Class<?> loadClass) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        if (loadClass.isAnnotationPresent(XiaoNiuComponent.class)
                || loadClass.isAnnotationPresent(XiaoNiuController.class)
                || loadClass.isAnnotationPresent(XiaoNiuService.class)
                || loadClass.isAnnotationPresent(XiaoNiuRestController.class)) {
            if (loadClass.isAnnotationPresent(XiaoNiuComponent.class)) {
                Method[] methods = loadClass.getMethods();
                for (Method method : methods) {
                    //判断该method的返回值能否成为成为一个bean对象
                    if (method.isAnnotationPresent(XiaoNiuBean.class)) {
                        if (method.isAnnotationPresent(XiaoNiuConditional.class)) {
                            XiaoNiuConditional xiaoNiuConditional = method.getAnnotation(XiaoNiuConditional.class);
                            Class<?> conditionClazz = xiaoNiuConditional.value();
                            //判断这个类是否实现了Condition
                            if (Condition.class.isAssignableFrom(conditionClazz)) {
                                //调用Condition中的matches方法
                                Condition condition = (Condition) conditionClazz.getDeclaredConstructor().newInstance();
                                // 调用 Condition 类的 matches 方法
                                boolean matchesResult = condition.matches(conditionEvaluator);
                                if (matchesResult) {
                                    registerBeanDefinitionForFactoryMethod(beanFactory, fileClassPath, loadClass, method);
                                }
                            }
                        } else {
                            registerBeanDefinitionForFactoryMethod(beanFactory, fileClassPath, loadClass, method);
                        }
                    }
                }
            }
            registerBeanDefinition(beanFactory, fileClassPath, loadClass);
        }
    }

    private void registerBeanDefinition(BeanDefinitionRegistry beanFactory, String fileClassPath, Class<?> loadClass) {
        //说明他是一个bean对象，需要注入到ioc容器之中。
        BeanDefinition beanDefinition = new BeanDefinition();
        String beanName = loadClass.getSimpleName(); //UserService
        //字符串将第一个字符变为小写
        beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
        beanDefinition.setId(beanName);//userService
        beanDefinition.setClassName(fileClassPath);
        //注册到注册中心，放到map之中。
        this.registerBeanDefinition(beanName, beanDefinition);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }


    private void registerBeanDefinitionForFactoryMethod(BeanDefinitionRegistry beanFactory, String fileClassPath, Class<?> loadClass, Method method) {
        //说明他是一个bean对象，需要注入到ioc容器之中。
        BeanDefinition beanDefinition = new BeanDefinition();
        String methodName = method.getName();
        String beanName = methodName;  //bean的名字默认为方法名字
        //字符串将第一个字符变为小写
        beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
        beanDefinition.setId(beanName);//userService
        beanDefinition.setClassName(fileClassPath);
        beanDefinition.setFactoryMethodName(methodName);
        //注册到注册中心，放到map之中。
        this.registerBeanDefinition(beanName, beanDefinition);
        beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }


    private void getFilePath(File file) {
        if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                getFilePath(listFile);
                if (listFile.isFile()) {
                    String absolutePath = listFile.getAbsolutePath();
                    //判断是否是class结尾的。
                    if (absolutePath.endsWith(".class")) {
                        absolutePath = absolutePath.substring(absolutePath.indexOf("classes") + 8, absolutePath.indexOf(".class"));
//                        absolutePath = absolutePath.substring(absolutePath.indexOf("com"), absolutePath.indexOf(".class"));
                        if (OSName.startsWith("Windows")) {
                            absolutePath = absolutePath.replace("\\", ".");
                        } else {
                            absolutePath = absolutePath.replace("/", ".");
                        }
                        beanClassList.add(absolutePath);
                    }
                }
            }
        }
    }


    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
        this.beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        this.beanDefinitionMap.remove(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
        return this.beanDefinitionMap.get(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.contains(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        if (this.beanDefinitionMap.size() > 0) {
            return this.beanDefinitionMap.keySet().toArray(new String[0]);
        }
        return new String[0];
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }


}


