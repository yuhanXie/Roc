package com.roc.java;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gang.xie
 */
public class MapTest {


    public static void main(String[] args) {
        HashMap<String, String> hashMap = new HashMap<>();
        Hashtable<String, String> hashtable = new Hashtable<>();
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
        //hashmap key,value都可以为null
        hashMap.put(null, null);
        try {
            //hashtable key,value都不可以为null
            hashtable.put(null, "111");
            hashtable.put(null, null);
        } catch (Exception e) {
        }
        //concurrentHashMap key,value也都可以为null
        concurrentHashMap.put(null, null);
    }
}
