package com.roc.spring.anonation;

import java.lang.reflect.Field;

/**
 * @author xiegang
 */
public class TestController {


    public static void main(String[] args) {
        User user = new User();
        user.setAge(-5);
        check(user);
        user.setAge(15);
        check(user);
    }


    @MethodLog
    public String getUser(String userId) {
        return userId + ":你好";
    }

    private static void check(User user)  {
        Class c = user.getClass();
        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(Validate.class)) {
                Validate validateVal = field.getAnnotation(Validate.class);
                field.setAccessible(true);
                int age = 0;
                try {
                    age = field.getInt(user);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (age < validateVal.min() || age > validateVal.max()) {
                    System.out.println("error: age " + age + " is not correct");
                } else {
                    System.out.println("success: age is correct");
                }
            }
        }
    }
}
