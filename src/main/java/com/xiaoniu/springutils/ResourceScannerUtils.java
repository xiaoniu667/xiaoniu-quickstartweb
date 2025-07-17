package com.xiaoniu.springutils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class ResourceScannerUtils {

    public static void main(String[] args) {
        String xmlPath = "mapper/SchoolMapper.xml"; // 示例路径，根据需要修改为实际路径

        ResourceScannerUtils example = new ResourceScannerUtils();
        List<String> xmlFiles = example.scanResources(xmlPath);

        // 输出扫描到的所有 XML 文件路径
        System.out.println("Scanned XML Files:");
        for (String xmlFile : xmlFiles) {
            System.out.println(xmlFile);
        }
    }

    public static List<String> scanResources(String xmlPath) {
        List<String> xmlFiles = new ArrayList<>();

        // 获取类加载器
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            // 将包路径转换为类路径格式
            String packagePath = xmlPath.replace(".", "/");

            // 使用类加载器获取所有资源
            Enumeration<URL> resources = classLoader.getResources(packagePath);

            // 遍历资源
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();

                // 获取资源路径
                String resourcePath = resource.getFile();

                // 如果是文件夹，则获取文件夹下的所有 .xml 文件
                // 如果是文件系统中的目录
                File directory = new File(resourcePath);
                if (directory.exists() && directory.isDirectory()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.isFile() && file.getName().toLowerCase().endsWith(".xml")) {
                                // 截取掉 classes 后面的路径部分
                                String filePath = file.getAbsolutePath();
                                int classesIndex = filePath.indexOf("classes");
                                if (classesIndex != -1) {
                                    String relativePath = filePath.substring(classesIndex + "classes".length() + 1); // 加1是为了去掉路径分隔符
                                    xmlFiles.add(relativePath);
                                }
                            }
                        }
                    }
                }
            }

        } catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return xmlFiles;
    }
}
