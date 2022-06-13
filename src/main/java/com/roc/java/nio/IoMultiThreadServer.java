package com.roc.java.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author xiegang
 */
public class IoMultiThreadServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("start");
                    ServerSocket serverSocket = new ServerSocket(9876);
                    while (true) {

                        //这个过程其实分两步1.连接 2.读取数据 传统scoket（BIO）在两步中都会阻塞
                        //等待accept时，线程处于阻塞状态。http://xiaorui.cc/archives/3256
                        Socket socket = serverSocket.accept();
                        new Thread(() -> doBusiness(socket)).start();

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        Thread.sleep(1000);
        System.out.println(thread.getState());
    }


    //传统服务端socket处理分为两步，
    // 第一步是等待连接serverSocket.accept()，线程阻塞
    //第二步是读取数据inputStream.read(bytes)，线程阻塞

    //当使用单线程接受请求时，后面的请求必须要等前面的请求读取完成之后，才能继续处理。相当于没有并发能力
    //为了提高并发，我们可以使用多线程读取数据，也就是inputStream.read(bytes)。
    //这个的问题：1.accept仍然是阻塞状态，那么是否可以理解，当并发连接数量大时，accept也可能是瓶颈。
    //2. 对于每个连接都创建一个线程进行处理的话，对于系统资源的要求非常高。这里可以使用固定大小的线程池来处理，
    // 可以减少对线程的创建，但是呢在面对高并发的时候处理因为线程池中线程数量的限制，仍然会出现瓶颈。

    //那么优化的方向应该是 1.能否使用一个进程管理多个连接。
    //那么最好的解决方式应该是serverSocket.accept()和inputStream.read(bytes)变成非阻塞的。


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
//            socket.close();
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
