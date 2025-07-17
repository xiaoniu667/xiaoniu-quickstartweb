package com.xiaoniu.springutils;

public class StringArrayCombination {
    public static void main(String[] args) {
        // 假设 annotationScanPath 和 this.basePackage 是两个字符串
        String annotationScanPath = "com.example.annotations";
        String basePackage = "com.example.package1";

        // 将 annotationScanPath 和 this.basePackage 拆分为字符串数组
        String[] annotationScanPathArray = annotationScanPath.split(",");
        String[] basePackageArray = basePackage.split(",");

        // 合并 annotationScanPathArray 和 basePackageArray 到一个数组 backPackages
        String[] backPackages = new String[annotationScanPathArray.length + basePackageArray.length];

        // 将 annotationScanPathArray 的元素复制到 backPackages 中
        System.arraycopy(annotationScanPathArray, 0, backPackages, 0, annotationScanPathArray.length);

        // 将 basePackageArray 的元素复制到 backPackages 中
        System.arraycopy(basePackageArray, 0, backPackages, annotationScanPathArray.length, basePackageArray.length);

        // 打印合并后的数组内容
        System.out.println("合并后的数组内容:");
        for (String pkg : backPackages) {
            System.out.println(pkg);
        }
    }
}
