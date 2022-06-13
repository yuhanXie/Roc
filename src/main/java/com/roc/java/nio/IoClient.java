package com.roc.java.nio;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Scanner;

public class IoClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5612);
        OutputStream outputStream = socket.getOutputStream();
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNext("exit")) {
            String nextLine = scanner.nextLine();
            // 像服务端发送数据
            outputStream.write(nextLine.getBytes());
        }
        outputStream.flush();
        socket.shutdownOutput();
        byte[] response = new byte[1024];
        int length = socket.getInputStream().read(response);
        if (length > 0) {
            System.out.println(new String(response).trim());
        }
        socket.shutdownInput();
        socket.close();
        System.out.println("end");
    }



}
