package com.roc.jvm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;

/**
 * @author gang.xie
 */
public class CustomFindClassLoader extends ClassLoader {


    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        File file = new File("/home/yuhan/Documents/workspace/" + replace(name) + ".class");
        if (!file.exists()) {
            return super.findClass(name);
        }
        FileInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = new FileInputStream(file);
            outputStream = new ByteArrayOutputStream();
            int b = 0;
            while ((b = inputStream.read()) != 0) {
                outputStream.write(b);
            }
            byte[] bytes = outputStream.toByteArray();
            return defineClass(name, bytes, 0, bytes.length);
        } catch (Exception e) {
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
            }
        }
        return super.findClass(name);
    }

    private String replace(String  className) {
        return  className.replaceAll("\\.", "/");
    }

    public static void main(String[] args) throws ClassNotFoundException {
        CustomFindClassLoader customFindClassLoader = new CustomFindClassLoader();
        Class first = customFindClassLoader.loadClass("com.roc.jvm.TestClass");
        System.out.println(first.getClassLoader());

        customFindClassLoader = new CustomFindClassLoader();
        Class second = customFindClassLoader.loadClass("com.roc.jvm.TestClass");
        System.out.println(second.getClassLoader());
        System.out.println(first == second);

    }
}
