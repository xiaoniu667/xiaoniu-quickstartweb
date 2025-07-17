package com.xiaoniu.mybatis.framwork.utils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ClassScanner {
    private static final String OSName = System.getProperty("os.name");
    protected static ArrayList<String> beanClassList = new ArrayList<>();
    public static Set<Class<?>> scanPackage(String packageName) {
        HashSet<Class<?>> hashSet = new HashSet<>();
        packageName = packageName.replace(".", "/");
        ClassLoader classLoader = ClassScanner.class.getClassLoader();
        //资源文件，里面有file属性
        URL resource = classLoader.getResource(packageName);
        if (resource == null) {
            throw new RuntimeException("扫描路径不存在");
        }
        File file = new File(resource.getFile());
        getFilePath(file);
        for (String fileClassPath : beanClassList) {
            try {
                Class<?> loadClass = classLoader.loadClass(fileClassPath); //com.xiaoniu.mybatis.framwork.spring.UserService
                hashSet.add(loadClass);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hashSet;
    }
    private static void getFilePath(File file) {
        if (file.isDirectory()) {
            for (File listFile : Objects.requireNonNull(file.listFiles())) {
                getFilePath(listFile);
                if (listFile.isFile()) {
                    String absolutePath = listFile.getAbsolutePath();
                    //判断是否是class结尾的。
                    if (absolutePath.endsWith(".class")) {
                        absolutePath = absolutePath.substring(absolutePath.indexOf("classes") + 8, absolutePath.indexOf(".class"));
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
}
