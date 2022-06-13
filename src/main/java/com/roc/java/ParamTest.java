package com.roc.java;

/**
 * @author gang.xie
 */
public class ParamTest {

    public String name;

    public int age;


    public static void main(String[] args) {
        ParamTest paramTest = new ParamTest();
        String s1 = "abc";
        int a = 10;
        System.out.println(System.identityHashCode(s1));
        System.out.println(System.identityHashCode(a));
        paramTest.test(s1, a);
        System.out.println(System.identityHashCode(s1));
        System.out.println(System.identityHashCode(a));
        System.out.println(s1);
        System.out.println(a);

        paramTest.age = 10;
        paramTest.name = "ccc";
        System.out.println(paramTest);
        paramTest.test1(paramTest);
        System.out.println(paramTest);
        System.out.println(paramTest.name);
        System.out.println(paramTest.age);
    }

    private void test(String s1, int a) {
        System.out.println(System.identityHashCode(s1));
        System.out.println(System.identityHashCode(a));
        s1 = "cd";
        a = 9;
        System.out.println(System.identityHashCode(s1));
        System.out.println(System.identityHashCode(a));
    }

    private void test1(ParamTest test) {
        System.out.println(test);
        test.age = 12;
        test.name = "ddd";
        System.out.println(test);
    }
}
