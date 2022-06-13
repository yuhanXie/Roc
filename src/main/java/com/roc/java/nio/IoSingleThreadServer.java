package com.roc.java.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xiegang
 */
public class IoSingleThreadServer {

    //这个过程其实分两步1.连接 2.读取数据 传统scoket（BIO）在两步中都会阻塞

    //若没有数据到内核缓冲区（TCP RecvBuffer）,会阻塞在此，直到有连接进来
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9876);
        while (true) {
            try {
                //监听9876端口，操作系统使用先进先出的队列存储请求9876端口的连接
                //执行serverSocket.accept()，就是从队列中取出连接请求。
                // 当serverSocket.accept()时，对于client，就会从Socket构造函数中返回
                Socket socket = serverSocket.accept();
                doBusiness(socket);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void doBusiness(Socket socket) {
        try {
            System.out.println("request started");
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[1024];
            //2.读取数据,阻塞状态。
            inputStream.read(bytes);
            System.out.println("receive:" + new String(bytes));

            PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
            writer.println("HTTP/1.1 200 OK");
            writer.println("Content-Type:text/html;charset=utf-8");
            writer.println();
            writer.println("hello nio");
            writer.close();
            socket.close();
            System.out.println("request end");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建epoll实例
     *
     * @return epoll实例的文件描述符id
     */
    private native int epollCreate();

    /**
     * @param epfd   epoll 实例fd
     * @param opcode 操作符
     * @param fd     注册的fd
     * @param events 注册fd感兴趣的事件
     */
    private native void epollCtl(int epfd, int opcode, int fd, int events);


    /**
     * private static final int EPOLL_CTL_ADD      = 1;
     * private static final int EPOLL_CTL_DEL      = 2;
     * private static final int EPOLL_CTL_MOD      = 3;
     */

    private native int epollWait(long pollAddress, int numfds, long timeout,
                                 int epfd) throws IOException;

}
