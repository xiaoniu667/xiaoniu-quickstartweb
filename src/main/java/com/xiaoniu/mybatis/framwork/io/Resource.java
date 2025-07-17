package com.xiaoniu.mybatis.framwork.io;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * 根据文件名称得到一个io流
 */
public class Resource {
    public static Reader getResourceAsReader(String resource) {
        return new InputStreamReader(getResourceAsStream(resource));
    }


    public static InputStream getResourceAsStream(String resource) {
        ClassLoader[] classLoaders = getDefaultClassLoader();
        for (ClassLoader classLoader : classLoaders) {
            InputStream inputStream = classLoader.getResourceAsStream(resource);
            if (inputStream != null) {
                return inputStream;
            }
        }
        return null;
    }


    private static ClassLoader[] getDefaultClassLoader() {
        ClassLoader[] classLoaders = {
                Thread.currentThread().getContextClassLoader(),
                ClassLoader.getSystemClassLoader()

        };
        return classLoaders;
    }


}
