package com.roc.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author gang.xie
 */
public class TestIo {

    public static void main(String[] args) {
        File file = new File("/home/yuhan/Documents/workspace/Roc/src/main/resources/pic/test.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (true) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                for (int i = 0; i < 1000000; i++) {
                    fileOutputStream.write(i);
                }
                fileOutputStream.close();
                FileInputStream inputStream = new FileInputStream(file);
                while (inputStream.read() != -1) {
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
