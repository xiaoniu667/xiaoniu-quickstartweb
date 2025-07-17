package com.xiaoniu.mybatis.spring;


import com.xiaoniu.exception.BeanDefinitionParsingException;
import com.xiaoniu.spring.BeanDefinition;
import com.xiaoniu.spring.BeanDefinitionRegistry;

import java.io.File;
import java.net.URL;
import java.util.LinkedHashSet;
import java.util.Set;

public class ClassPathMapperScanner extends AbstractMapperScanner {

    public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    @Override
    protected Set<BeanDefinition> doScan(String basePackage) {
        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
        basePackage = basePackage.replace(".", "/");
        ClassLoader classLoader = ClassPathMapperScanner.class.getClassLoader();
        //资源文件，里面有file属性
        URL resource = classLoader.getResource(basePackage);
        if (resource == null) {
            throw new BeanDefinitionParsingException("扫描路径不存在");
        }
        File file = new File(resource.getFile());
        getFilePath(file);
        for (String fileClassPath : beanClassList) {
            try {
                Class<?> loadClass = classLoader.loadClass(fileClassPath); //com.xiaoniu.mybatis.framwork.spring.UserService
                //为每一个mapper接口创建一个代理工厂MapperProxyFactory
                //说明他是一个bean对象，需要注入到ioc容器之中。
                BeanDefinition beanDefinition = new BeanDefinition();
                String beanName = loadClass.getSimpleName(); //UserService
                //字符串将第一个字符变为小写
                beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
                beanDefinition.setId(beanName);//userService
                beanDefinition.setClassName(fileClassPath);//com.xiaoiu.bean.userMapper
                //注册到注册中心，放到map之中。
                beanDefinitions.add(beanDefinition);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return beanDefinitions;
    }

//    private final String OSName = System.getProperty("os.name");
//    private BeanDefinitionRegistry beanDefinitionRegistry;
//    protected ArrayList<String> beanClassList = new ArrayList<>();
//
//    private Class<? extends MapperFactoryBean> mapperFactoryBeanClass = MapperFactoryBean.class;
//
//    public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
//        this.beanDefinitionRegistry = registry;
//    }
//
//
//    public void scan(BeanDefinitionRegistry registry, String annotationScanPath, String basePackage) {
//        //DefaultListableBeanFactory
//        Set<BeanDefinition> beanDefinitions;
//        if (annotationScanPath == null || StringUtils.isEmpty(annotationScanPath)) {
//            beanDefinitions = doScanWithPath(basePackage);
//        } else if (basePackage == null || StringUtils.isEmpty(basePackage)) {
//            beanDefinitions = doScanWithAnnotation(annotationScanPath);
//        } else {
//            throw new RuntimeException("扫描路径有错误!请检查您的启动类设置和扫描路径设置");
//        }
//        //处理一下bean定义
//        processBeanDefinitions(beanDefinitions, registry);
//        //注册到容器之中
//        registerBeanDefinitions(beanDefinitions, registry);
//    }
//
//    private Set<BeanDefinition> doScanWithAnnotation(String annotationScanPath) {
//        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
//        annotationScanPath = annotationScanPath.replace(".", "/");
//        ClassLoader classLoader = ClassPathMapperScanner.class.getClassLoader();
//        //资源文件，里面有file属性
//        URL resource = classLoader.getResource(annotationScanPath);
//        if (resource == null) {
//            throw new BeanDefinitionParsingException("扫描路径不存在");
//        }
//        File file = new File(resource.getFile());
//        getFilePath(file);
//        for (String fileClassPath : beanClassList) {
//            try {
//                Class<?> loadClass = classLoader.loadClass(fileClassPath); //com.xiaoniu.mybatis.framwork.spring.UserService
//                if (loadClass.isAnnotationPresent(XiaoNiuMapper.class)) {
//                    //为每一个mapper接口创建一个代理工厂MapperProxyFactory
//                    //说明他是一个bean对象，需要注入到ioc容器之中。
//                    BeanDefinition beanDefinition = new BeanDefinition();
//                    String beanName = loadClass.getSimpleName(); //UserService
//                    //字符串将第一个字符变为小写
//                    beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
//                    beanDefinition.setId(beanName);//userService
//                    beanDefinition.setClassName(fileClassPath);//com.xiaoiu.bean.userMapper
//                    //注册到注册中心，放到map之中。
//                    beanDefinitions.add(beanDefinition);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//        return beanDefinitions;
//    }
//
//    private Set<BeanDefinition> doScanWithPath(String basePackage) {
//        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
//        basePackage = basePackage.replace(".", "/");
//        ClassLoader classLoader = ClassPathMapperScanner.class.getClassLoader();
//        //资源文件，里面有file属性
//        URL resource = classLoader.getResource(basePackage);
//        if (resource == null) {
//            throw new BeanDefinitionParsingException("扫描路径不存在");
//        }
//        File file = new File(resource.getFile());
//        getFilePath(file);
//        for (String fileClassPath : beanClassList) {
//            try {
//                Class<?> loadClass = classLoader.loadClass(fileClassPath); //com.xiaoniu.mybatis.framwork.spring.UserService
//                //为每一个mapper接口创建一个代理工厂MapperProxyFactory
//                //说明他是一个bean对象，需要注入到ioc容器之中。
//                BeanDefinition beanDefinition = new BeanDefinition();
//                String beanName = loadClass.getSimpleName(); //UserService
//                //字符串将第一个字符变为小写
//                beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
//                beanDefinition.setId(beanName);//userService
//                beanDefinition.setClassName(fileClassPath);//com.xiaoiu.bean.userMapper
//                //注册到注册中心，放到map之中。
//                beanDefinitions.add(beanDefinition);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//        return beanDefinitions;
//    }
//
//    private void registerBeanDefinitions(Set<BeanDefinition> beanDefinitions, BeanDefinitionRegistry registry) {
//        for (BeanDefinition beanDefinition : beanDefinitions) {
//            registry.registerBeanDefinition(beanDefinition.getId(), beanDefinition);
//        }
//    }
//
////    private Set<BeanDefinition> doScan(String... basePackages) {
////        Set<BeanDefinition> beanDefinitions = new LinkedHashSet<>();
////        for (String basePackage : basePackages) {
////            basePackage = basePackage.replace(".", "/");
////            ClassLoader classLoader = ClassPathMapperScanner.class.getClassLoader();
////            //资源文件，里面有file属性
////            URL resource = classLoader.getResource(basePackage);
////            if (resource == null) {
////                throw new BeanDefinitionParsingException("扫描路径不存在");
////            }
////            File file = new File(resource.getFile());
////            getFilePath(file);
////            for (String fileClassPath : beanClassList) {
////                try {
////                    Class<?> loadClass = classLoader.loadClass(fileClassPath); //com.xiaoniu.mybatis.framwork.spring.UserService
////                    //为每一个mapper接口创建一个代理工厂MapperProxyFactory
////                    //说明他是一个bean对象，需要注入到ioc容器之中。
////                    BeanDefinition beanDefinition = new BeanDefinition();
////                    String beanName = loadClass.getSimpleName(); //UserService
////                    //字符串将第一个字符变为小写
////                    beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
////                    beanDefinition.setId(beanName);//userService
////                    beanDefinition.setClassName(fileClassPath);//com.xiaoiu.bean.userMapper
////                    //注册到注册中心，放到map之中。
////                    beanDefinitions.add(beanDefinition);
////                } catch (Exception e) {
////                    e.printStackTrace();
////                }
////            }
////        }
////        return beanDefinitions;
////    }
//
//    //处理一下mapper文件的定义 同时设置sqlSessionFactory 用于属性注入
//    private void processBeanDefinitions(Set<BeanDefinition> beanDefinitions, BeanDefinitionRegistry registry) {
//        for (BeanDefinition beanDefinition : beanDefinitions) {
//            //将mapper中的属性设置为原来的class
//            beanDefinition.setOldClassName(beanDefinition.getClassName());
//            //新的class为mapperFactoryBeanClass
//            beanDefinition.setClassName(this.mapperFactoryBeanClass.getName());
//            MutablePropertyValues mutablePropertyValues = beanDefinition.getMutablePropertyValues();
//            if (mutablePropertyValues == null) {
//                MutablePropertyValues propertyValues = new MutablePropertyValues();
//                PropertyValue propertyValue = new PropertyValue("sqlSessionFactory", "sqlSessionFactory", null);
//                propertyValues.addPropertyValue(propertyValue);
//                beanDefinition.setMutablePropertyValues(propertyValues);
//            }
//        }
//    }
//
//
//    /**
//     * 注解的方式扫描bean 这个是在springboot中存在
//     */
////    private void doScan(Class<?> clazz, BeanDefinitionRegistry registry, BeanDefinitionRegistry beanFactory) {
////        String packageName = clazz.getPackage().getName();
////        packageName = packageName.replace(".", "/");
////        ClassLoader classLoader = ClassPathMapperScanner.class.getClassLoader();
////        //资源文件，里面有file属性
////        URL resource = classLoader.getResource(packageName);
////        if (resource == null) {
////            throw new BeanDefinitionParsingException("扫描路径不存在");
////        }
////        File file = new File(resource.getFile());
////        getFilePath(file);
////        for (String fileClassPath : beanClassList) {
////            try {
////                Class<?> loadClass = classLoader.loadClass(fileClassPath); //com.xiaoniu.mybatis.framwork.spring.UserService
////                if (loadClass.isAnnotationPresent(XiaoNiuMapper.class)) {
////                    //为每一个mapper接口创建一个代理工厂MapperProxyFactory
////                    //说明他是一个bean对象，需要注入到ioc容器之中。
////                    BeanDefinition beanDefinition = new BeanDefinition();
////                    String beanName = loadClass.getSimpleName(); //UserService
////                    //字符串将第一个字符变为小写
////                    beanName = beanName.substring(0, 1).toLowerCase() + beanName.substring(1);
////                    beanDefinition.setId(beanName);//userService
////                    beanDefinition.setClassName(fileClassPath);//com.xiaoiu.bean.userMapper
////                    //注册到注册中心，放到map之中。
////                    registry.registerBeanDefinition(beanName, beanDefinition);
////                    beanFactory.registerBeanDefinition(beanName, beanDefinition);
////                }
////            } catch (Exception e) {
////                e.printStackTrace();
////            }
////        }
////    }
////    private void getFilePath(File file) {
////        if (file.isDirectory()) {
////            for (File listFile : Objects.requireNonNull(file.listFiles())) {
////                getFilePath(listFile);
////                if (listFile.isFile()) {
////                    String absolutePath = listFile.getAbsolutePath();
////                    //判断是否是class结尾的。
////                    if (absolutePath.endsWith(".class")) {
////                        absolutePath = absolutePath.substring(absolutePath.indexOf("classes") + 8, absolutePath.indexOf(".class"));
////                        if (OSName.startsWith("Windows")) {
////                            absolutePath = absolutePath.replace("\\", ".");
////                        } else {
////                            absolutePath = absolutePath.replace("/", ".");
////                        }
////                        beanClassList.add(absolutePath);
////                    }
////                }
////            }
////        }
////    }
//

}
