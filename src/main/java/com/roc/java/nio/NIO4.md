netty redis nginx
reference
mongo
docker + k8s 


reactor&proactor
reactor：非阻塞同步网络模型
单线程reactor模式: 使用一个reactor线程管理所有的连接和io读写处理

多线程reactor模式：使用一个reactor线程管理所有连接，使用线程池负责io读写

reactor主从：主reactor线程负责监听连接，使用子reactor线程监听io读写就绪事件，使用线程池进行io读写

proactor：异步网络模型
reactor在io读写时仍是同步，而proactor是异步的，等待io读写完成后，再通知proactor线程。
目前操作系统对异步io支持仍不完善。