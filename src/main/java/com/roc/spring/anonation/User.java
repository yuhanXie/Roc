package com.roc.spring.anonation;

/**
 * @author xiegang
 */
public class User {


    @Validate(min = 1, max = 50)
    public int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
