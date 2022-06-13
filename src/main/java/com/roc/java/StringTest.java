package com.roc.java;

/**
 * @author gang.xie
 */
public class StringTest {

    public static void main(String[] args) {
        StringTest test = new StringTest();
        test.test1();
    }

    private void test1() {
        String s1 = "abc";
        String s2 = "abc";
        //字符串常量池中生成“abc”字符串，栈上生成s1,s2同时指向字符串常量池中的"abc"
        System.out.println(s1 == s2);
        s1 = "hello";
        //在常量池中创建新的hello字符串，将s1的指针指向hello
        System.out.println(s1 == s2);
    }

    private void test2() {
        String s1 = "abc";
        String s2 = "abc";
        //在常量池中创建新的字符串“abcdef”
        s2 += "def";
        System.out.println(s1);
        System.out.println(s2);
    }

    private void test3() {
        String s1 = "abc";
        String s2 = s1.replace("a", "m");
        System.out.println(s1);
        System.out.println(s2);
    }
}
