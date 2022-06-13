package com.roc.jvm;

import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.ArrayList;

/**
 * @author gang.xie
 */
public class TestJvmLoader {

    static int i = 10;

    public static void main(String[] args) {
        System.out.println();
        System.out.println(String.class.getClassLoader());
        System.out.println(sun.net.spi.nameservice.dns.DNSNameService.class.getClassLoader());
        System.out.println(TestJvmLoader.class.getClassLoader());
        System.out.println(TestJvmLoader.class.getClassLoader().getParent());

//        URL[] urls =  sun.misc.Launcher.getBootstrapClassPath().getURLs();
//        Arrays.stream(urls).forEach(System.out::println);
//
//        print("扩展类加载器", TestJvmLoader.class.getClassLoader().getParent());
//        print("应用类加载器", TestJvmLoader.class.getClassLoader());
    }

    private static void print(String name, ClassLoader classLoader) {
        System.out.println(name + "ClassLoader->" + classLoader.toString());
        printURL(classLoader);
    }

    public static void printURL(ClassLoader classLoader) {
        Object ucp = insightField(classLoader, "ucp");
        Object path = insightField(ucp, "path");
        ArrayList ps = (ArrayList) path;
        for (Object p : ps) {
            System.out.println("-->" + p.toString());
        }
    }

    private static Object insightField(Object object, String ucp) {
        try {
            Field field = null;
            if (object instanceof URLClassLoader) {
                field = URLClassLoader.class.getDeclaredField(ucp);
            } else {
                field = object.getClass().getDeclaredField(ucp);
            }
            field.setAccessible(true);
            return field.get(object);
        } catch (Exception e) {
        }
        return null;
    }
}
