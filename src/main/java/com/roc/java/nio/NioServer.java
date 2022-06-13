package com.roc.java.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class NioServer {

    public static void main(String[] args) throws IOException {
        System.out.println("server start");
        new NioServer().run();
        //NIO学习系列（二）-从NIO server到epoll源码解析
    }

    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuff = ByteBuffer.allocate(1024);

    private ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);
    private ByteBuffer heapBuffer = ByteBuffer.allocate(1024);

    private Selector selector;

    public NioServer() throws IOException {
        //创建ServerSocketChannelImpl，创建了fd
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        //设置当前通道为非阻塞
        serverSocketChannel.configureBlocking(false);
        ServerSocket socket = serverSocketChannel.socket();
        //当前通道绑定监听xx端口号的socket
        socket.bind(new InetSocketAddress(5612));
        //创建EPollSelectorImpl，创建了fd
        selector = Selector.open();
        //将当前通道注册到selector，并监听ACCEPT事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    private void run() throws IOException {

        do {
            System.out.println("等待请求进来");
            //selector.select()，系统阻塞，当有感兴趣的事件触发，才会唤醒selector
            while (selector.select() > 0) {
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    //需移除此selectionKey
                    iterator.remove();
                    if (selectionKey.isAcceptable()) {
                        //当有连接进来，类似于serverSocket.accept()
                        accept(selectionKey);
                    } else if (selectionKey.isReadable()) {
                        //当有连接 数据准备好了
                        read(selectionKey);
                    } else if (selectionKey.isWritable()) {
                        //当通道可写
                        write(selectionKey);
                    }
                }
            }
        } while (true);
    }


    private void accept(SelectionKey selectionKey) throws IOException {

        System.out.println(selectionKey.hashCode() + ":isAcceptable");
        ServerSocketChannel socketChannel = (ServerSocketChannel) selectionKey.channel();
        //获取请求连接的通道，accept()时会创建新的socketChannel用于后续的读写。
        // ServerSocketChannel只负责监听连接事件，相当于总机，接入请求后，根据其事件类型，转给分机处理（是创建新的）
        SocketChannel channel = socketChannel.accept();
        //设置为非阻塞
        channel.configureBlocking(false);
        //注册读事件
        channel.register(selector, SelectionKey.OP_READ);
    }

    private void read(SelectionKey selectionKey) throws IOException {
        System.out.println(selectionKey.hashCode() + ":isReadable");
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        //完成IO的第二步：从内核空间到复制到用户空间,线程处于阻塞状态
        //当传入的数据大于buffer大小时，当buffer满，会立刻返回；然后会重新触发读事件，进行继续读取
        int length = socketChannel.read(buffer);
        System.out.println("length :" + length);
        if (length > 0) {
            //flip的作用是将当前指针指到0，这样就可以从头读取
            buffer.flip();
            String text = new String(buffer.array(), StandardCharsets.UTF_8).trim();
            System.out.println(selectionKey.hashCode() + ":from client data:" + text);
            socketChannel.shutdownInput();
            socketChannel.register(selector, SelectionKey.OP_WRITE);
        }
    }


    private void write(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        System.out.println(selectionKey.hashCode() + ":isWritable");
        writeBuff.put("hello client, i am nio server, i receive your request".getBytes(StandardCharsets.UTF_8));
        //flip和rewind的区别：都是把position置为0,但是flip会修改limit的值为当前position，而rewind默认limit就是capacity
        writeBuff.flip();
//        writeBuff.rewind();
        while (writeBuff.hasRemaining()) {
            socketChannel.write(writeBuff);
        }
        socketChannel.shutdownOutput();
        //当写完之后，取消注册写事件，否则会一直触发，导致报错
        selectionKey.interestOps(selectionKey.interestOps() & ~SelectionKey.OP_WRITE);
    }





}
