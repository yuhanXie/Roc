package com.roc.java;

/**
 * @author gang.xie
 */
public class Common {


    public static void main(String[] args) {
//        Integer a = 3;
//        Integer b = 3;
//        Integer c = 237;
//        Integer d = 237;
//
//        System.out.println(a == b);
//        System.out.println(c == d);
//
//        ArrayList<String> list  = new ArrayList<>();
//        list.add("1");
//        list.add("2");
//        list.add("3");
//
//        List<String> sub =  list.subList(1, 3);

//        int temp = 0;
//        switch (temp) {
//            default:
//                System.out.println("defualt");
//                break;
//            case 0:
//                System.out.println("0");
//            case 1:
//                System.out.println("1");
//                break;
//        }
//
//        test();

        String str = "abc";
        String str2 = new String("abc");
        System.out.println(str == str2);
    }

    public static void test() {
        StringBuffer a = new StringBuffer("a");
        StringBuffer b = new StringBuffer("b");
        test2(a, b);
        System.out.println(a);
        System.out.println(b);
    }

    private static void test2(StringBuffer a, StringBuffer b) {
        a.append(b);
        b = a;
    }
}
