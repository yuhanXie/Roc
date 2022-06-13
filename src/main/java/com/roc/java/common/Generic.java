package com.roc.java.common;

/**
 * 泛型类
 *
 * @author gang.xie
 */
public class Generic<T> {

    private T object;

    public Generic(T object) {
        this.object = object;
    }

    private T getObject() {
        return this.object;
    }

    public static void main(String[] args) {
        Generic<String> generic = new Generic<>("generic paradigm");
        System.out.println(generic.getObject().toString());
    }
}
