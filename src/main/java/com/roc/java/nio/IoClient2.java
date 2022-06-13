package com.roc.java.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class IoClient2 {

    public static void main(String[] args) throws IOException {
//        for (int i = 0; i < 100; i++) {
//            int finalI = i;
            new Thread(() -> {
//                System.out.println("请求" + finalI);
                Socket socket = null;
                try {
                    socket = new Socket("localhost", 5612);
                    OutputStream outputStream = socket.getOutputStream();
                    Scanner scanner = new Scanner(System.in);
                    while (scanner.hasNext()) {
                        String nextLine = scanner.nextLine();
                        // 像服务端发送数据
                        outputStream.write(nextLine.getBytes());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

//        }


    }
}
