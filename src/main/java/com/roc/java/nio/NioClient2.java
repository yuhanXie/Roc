package com.roc.java.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class NioClient2 {


    public static void main(String[] args) {
        System.out.println("start");
//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> {
                try {
                    new NioClient2().run();
                } catch (IOException e) {
                    e.printStackTrace();
                }
//            }).start();
//        }


    }

    private void run() throws IOException {
        System.out.println("client start :");
        //得到一个网络通道
        SocketChannel socketChannel = SocketChannel.open();

        //设置非阻塞
//        socketChannel.configureBlocking(false);
        //提供服务器端的ip 和 端口
        InetSocketAddress inetSocketAddress = new InetSocketAddress("127.0.0.1", 1234);

        //连接服务器
        socketChannel.connect(inetSocketAddress);
        ByteBuffer writeBuffer = ByteBuffer.allocate(32);
        ByteBuffer readBuffer = ByteBuffer.allocate(32);

        writeBuffer.put("hello nio2".getBytes());
        writeBuffer.flip();
        while (true) {
            writeBuffer.rewind();
            socketChannel.write(writeBuffer);
            readBuffer.clear();
            socketChannel.read(readBuffer);
        }
    }


//    public static void main(String[] args) throws IOException {
//        for (int i = 0; i < 10; i++) {
//            SocketChannel sc = SocketChannel.open();
//            sc.configureBlocking(false);
//            Selector selector = Selector.open();
//            sc.connect(new InetSocketAddress(1234));
//            sc.register(selector, SelectionKey.OP_CONNECT);
//
//            while (true) {
//                selector.select();
//                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
//                while (iterator.hasNext()) {
//                    SelectionKey next = iterator.next();
//                    iterator.remove();
//                    handle(next);
//                }
//            }
//        }
//    }
//
//    private static void handle(SelectionKey key) throws IOException {
//        if (key.isConnectable()) {
//            SocketChannel sc = (SocketChannel) key.channel();
//            if (sc.isConnectionPending()) {
//                sc.finishConnect();
//            }
//            sc.configureBlocking(false);
//            ByteBuffer send = ByteBuffer.wrap("Hello NIO server".getBytes());
//            sc.write(send);
//            sc.register(key.selector(), SelectionKey.OP_READ);
//        } else if (key.isReadable()) {
//            SocketChannel sc = (SocketChannel) key.channel();
//            ByteBuffer send = ByteBuffer.allocate(1024);
//            int len = sc.read(send);
//            if (len != -1) {
//                System.out.println(System.currentTimeMillis() + ":收到来自服务端数据：" + new String(send.array(), 0, len));
//            }
//        } else if (key.isWritable()) {
//
//        }
//    }
}

