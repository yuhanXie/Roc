## 一. Java NIO Demo

前面学习了socket，io模型及io多路复用的系统实现，这次想要学习的是Java NIO是如何利用epoll完成高并发网络框架的。（这里nio不能单纯理解为nonblocking io，而是多路复用的架构理念。linux使用epoll实现io多路复用，Java使用selector实现io多路复用）

### NIO server demo

```java
public class NioServer {

    public static void main(String[] args) throws IOException {
        System.out.println("server start");
        new NioServer().run();
    }

    private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
    private ByteBuffer writeBuff = ByteBuffer.allocate(1024);

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
            socketChannel.register(selector, SelectionKselector = Selector.open();ey.OP_WRITE);
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
```

### client demo

```java
public class IoClient {

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5612);
        OutputStream outputStream = socket.getOutputStream();
        Scanner scanner = new Scanner(System.in);
        while (!scanner.hasNext("exit")) {
            String nextLine = scanner.nextLine();
            // 向服务端发送数据
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
```

## 二. 源码探索

对源码不感兴趣的同学可以跳到下一个标题。

epoll的三个核心方法在上篇文章做了介绍，分别是epoll_create，epoll_ctl，epoll_wait。这次就结合代码探索下Java是如何使用epoll的。

### selector -> epoll_create

首先看Selector，selector的创建是通过Selector.open()，通过源码不断进入，最后会进到创建EPollSelectorImpl实例中

```java
    EPollSelectorImpl(SelectorProvider sp) throws IOException {
        super(sp);
        //为一个管道创建两个fd，读取端为高32位：fd0，写入端为低32位:fd1
        long pipeFds = IOUtil.makePipe(false);
        fd0 = (int) (pipeFds >>> 32);
        fd1 = (int) pipeFds;
        try {
            //初始化epoll_event
            pollWrapper = new EPollArrayWrapper();
            //将读取端注册到epoll_event中
            pollWrapper.initInterrupt(fd0, fd1);
            fdToKey = new HashMap<>();
        } catch (Throwable t) {
          //ignore
        }
    }
```

EPollSelectorImpl构造函数中，会创建EPollArrayWrapper，EPollArrayWrapper从JavaDoc的注释就可以眼前一亮：**操控linux上epoll_event的native数组**。epoll_event那不就是epoll保存监控事件的数据结构嘛。再看构造函数，epfd = epollCreate(); --> 这不就是调用native方法创建epollfd的嘛。这就很轻松的把selector和epoll创建结合起来了。这也就可以总结：**EPollArrayWrapper是Selector的成员变量，也是epoll在Java中的代表**

```java
/**
 * Manipulates a native array of epoll_event structs on Linux:
 * 操控linux上epoll_event的native数组
 */ 
   EPollArrayWrapper() throws IOException {
        // creates the epoll file descriptor
        epfd = epollCreate();

        // the epoll_event array passed to epoll_wait
        int allocationSize = NUM_EPOLLEVENTS * SIZE_EPOLLEVENT;
        pollArray = new AllocatedNativeObject(allocationSize, true);
        pollArrayAddress = pollArray.address();

        // eventHigh needed when using file descriptors > 64k
        if (OPEN_MAX > MAX_UPDATE_ARRAY_SIZE)
            eventsHigh = new HashMap<>();
    }

    void initInterrupt(int fd0, int fd1) {
        outgoingInterruptFD = fd1;
        incomingInterruptFD = fd0;
        epollCtl(epfd, EPOLL_CTL_ADD, fd0, EPOLLIN);
    }
    
    //epoll的三个方法都定义在这里了。
    private native int epollCreate();
    private native void epollCtl(int epfd, int opcode, int fd, int events);
    private native int epollWait(long pollAddress, int numfds, long timeout,
                                 int epfd) throws IOException;
```

### ServerSocketChannel

Java中单独定义了ServerSocketChannel，用来监听连接事件。那就是需要把ServerSocketChannel的fd及感兴趣的accept事件注册到epoll_event中。也就是此行代码serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT)。

1. 先找到AbstractSelectableChannel.register方法，定位到((AbstractSelector)sel).register(this, ops, att)
2. 找到SelectorImpl.register方法，定位到k.interestOps(ops)
3. 找到SelectionKeyImpl.interestOps() -> nioInterestOps()，定位到channel.translateAndSetInterestOps(ops, this);
4. 找到ServerSocketChannelImpl.translateAndSetInterestOps()，定位到 sk.selector.putEventOps(sk, newOps);
5. 找到EpollSelectorImpl.putEventOps()， 定位到pollWrapper.setInterest(ch.getFDVal(), ops)
6. 找到EPollArrayWrapper.setInterest()，在此只需要记住updateDescriptors[updateCount++] = fd;

> updateDescriptors数组用来保存注册事件的fd

ServerSocketChannel.register没有直接调用epoll_ctl，但请先记住updateDescriptors数组
```java
// 1. AbstractSelectableChannel.java
public final SelectionKey register(Selector sel, int ops,
                                       Object att)
        throws ClosedChannelException
    {
        synchronized (regLock) {
            if (!isOpen())
                throw new ClosedChannelException();
            if ((ops & ~validOps()) != 0)
                throw new IllegalArgumentException();
            if (isBlocking())
                throw new IllegalBlockingModeException();
            SelectionKey k = findKey(sel);
            if (k != null) {
                k.interestOps(ops);
                k.attach(att);
            }
            if (k == null) {
                // New registration
                synchronized (keyLock) {
                    if (!isOpen())
                        throw new ClosedChannelException();
                    //register方法
                    k = ((AbstractSelector)sel).register(this, ops, att);
                    addKey(k);
                }
            }
            return k;
        }
    }
```

```java
   
//2. SelectorImpl.java
protected final SelectionKey register(AbstractSelectableChannel ch,
                                          int ops,
                                          Object attachment)
    {
        if (!(ch instanceof SelChImpl))
            throw new IllegalSelectorException();
        SelectionKeyImpl k = new SelectionKeyImpl((SelChImpl)ch, this);
        k.attach(attachment);
        synchronized (publicKeys) {
            implRegister(k);
        }
        k.interestOps(ops);
        return k;
    }
```

```java
   //3. SelectionKeyImpl.java 
   public SelectionKey interestOps(int ops) {
        ensureValid();
        return nioInterestOps(ops);
    }
    
    public SelectionKey nioInterestOps(int ops) {
        if ((ops & ~channel().validOps()) != 0)
            throw new IllegalArgumentException();
        channel.translateAndSetInterestOps(ops, this);
        interestOps = ops;
        return this;
    }
```



```java
    //4. ServerSocketChannelImpl.java
  public void translateAndSetInterestOps(int ops, SelectionKeyImpl sk) {
        int newOps = 0;

        // Translate ops
        if ((ops & SelectionKey.OP_ACCEPT) != 0)
            newOps |= Net.POLLIN;
        // Place ops into pollfd array
        sk.selector.putEventOps(sk, newOps);
    }
```



```java
   // 5. EPollSelectorImpl.java
   public void putEventOps(SelectionKeyImpl ski, int ops) {
        if (closed)
            throw new ClosedSelectorException();
        SelChImpl ch = ski.channel;
        pollWrapper.setInterest(ch.getFDVal(), ops);
    }
```

```java
    //6. EPollArrayWrapper.java
    void setInterest(int fd, int mask) {
        synchronized (updateLock) {
            // record the file descriptor and events
            int oldCapacity = updateDescriptors.length;
            //扩容
            if (updateCount == oldCapacity) {
                int newCapacity = oldCapacity + INITIAL_PENDING_UPDATE_SIZE;
                int[] newDescriptors = new int[newCapacity];
                System.arraycopy(updateDescriptors, 0, newDescriptors, 0, oldCapacity);
                updateDescriptors = newDescriptors;
            }
            updateDescriptors[updateCount++] = fd;

            // events are stored as bytes for efficiency reasons
            byte b = (byte)mask;
            assert (b == mask) && (b != KILLED);
            setUpdateEvents(fd, b, false);
        }
    }
```

### selector.select() -> epoll_wait

从demo可看出，selector.select()方法是核心方法，等待就绪事件的到来。这个的源码还是比较好找的。一步步跟进代码就可以到EPollSelectorImpl.doSelect()方法，定位到pollWrapper.poll(timeout);接着就是EPollArrayWrapper.poll()，这里有两个方法值得注意，1是updateRegistrations；2是epollWait。

我们先看updateRegistrations：这里就能看到从updateDescriptors数组中取出之前注册的fd，然后调用epollCtl，将事件注册到epoll_event上。注册完成之后，调用epollWait等待就绪事件返回。

```java
//EpollSelectorImpl.java    
protected int doSelect(long timeout) throws IOException {
        if (closed)
            throw new ClosedSelectorException();
        processDeregisterQueue();
        try {
            begin();
            //poll
            pollWrapper.poll(timeout);
        } finally {
            end();
        }
        processDeregisterQueue();
        int numKeysUpdated = updateSelectedKeys();
        if (pollWrapper.interrupted()) {
            // Clear the wakeup pipe
            pollWrapper.putEventOps(pollWrapper.interruptedIndex(), 0);
            synchronized (interruptLock) {
                pollWrapper.clearInterrupted();
                IOUtil.drain(fd0);
                interruptTriggered = false;
            }
        }
        return numKeysUpdated;
    }
```

```java
//EPollArrayWrapper.java
int poll(long timeout) throws IOException {
    //将之前添加到updateDescriptors数组的fd添加到epoll_event中
    updateRegistrations();
    //调用epollwait，等待有就绪连接返回
    updated = epollWait(pollArrayAddress, NUM_EPOLLEVENTS, timeout, epfd);
    for (int i=0; i<updated; i++) {
        if (getDescriptor(i) == incomingInterruptFD) {
            interruptedIndex = i;
            interrupted = true;
            break;
        }
    }
    return updated;
}

/**
 * Update the pending registrations.
 */
private void updateRegistrations() {
    synchronized (updateLock) {
        int j = 0;
        while (j < updateCount) {
            int fd = updateDescriptors[j];
            short events = getUpdateEvents(fd);
            boolean isRegistered = registered.get(fd);
            int opcode = 0;

            if (events != KILLED) {
                if (isRegistered) {
                    opcode = (events != 0) ? EPOLL_CTL_MOD : EPOLL_CTL_DEL;
                } else {
                    opcode = (events != 0) ? EPOLL_CTL_ADD : 0;
                }
                if (opcode != 0) {
                    //将fd感兴趣事件opcode 注册到epfd
                    epollCtl(epfd, opcode, fd, events);
                    if (opcode == EPOLL_CTL_ADD) {
                        registered.set(fd);
                    } else if (opcode == EPOLL_CTL_DEL) {
                        registered.clear(fd);
                    }
                }
            }
            j++;
        }
        updateCount = 0;
    }
}
```

附一下EPollArrayWrapper.c代码

```c
Java_sun_nio_ch_EPollArrayWrapper_epollCreate(JNIEnv *env, jobject this)
{
    /*
     * epoll_create expects a size as a hint to the kernel about how to
     * dimension internal structures. We can't predict the size in advance.
     */
    int epfd = epoll_create(256);
    if (epfd < 0) {
       JNU_ThrowIOExceptionWithLastError(env, "epoll_create failed");
    }
    return epfd;
}

Java_sun_nio_ch_EPollArrayWrapper_epollCtl(JNIEnv *env, jobject this, jint epfd,
                                           jint opcode, jint fd, jint events)
{
    struct epoll_event event;
    int res;

    event.events = events;
    event.data.fd = fd;

    RESTARTABLE(epoll_ctl(epfd, (int)opcode, (int)fd, &event), res);

    /*
     * A channel may be registered with several Selectors. When each Selector
     * is polled a EPOLL_CTL_DEL op will be inserted into its pending update
     * list to remove the file descriptor from epoll. The "last" Selector will
     * close the file descriptor which automatically unregisters it from each
     * epoll descriptor. To avoid costly synchronization between Selectors we
     * allow pending updates to be processed, ignoring errors. The errors are
     * harmless as the last update for the file descriptor is guaranteed to
     * be EPOLL_CTL_DEL.
     */
    if (res < 0 && errno != EBADF && errno != ENOENT && errno != EPERM) {
        JNU_ThrowIOExceptionWithLastError(env, "epoll_ctl failed");
    }
}

Java_sun_nio_ch_EPollArrayWrapper_epollWait(JNIEnv *env, jobject this,
                                            jlong address, jint numfds,
                                            jlong timeout, jint epfd)
{
    struct epoll_event *events = jlong_to_ptr(address);
    int res;

    if (timeout <= 0) {           /* Indefinite or no wait */
        RESTARTABLE(epoll_wait(epfd, events, numfds, timeout), res);
    } else {                      /* Bounded wait; bounded restarts */
        res = iepoll(epfd, events, numfds, timeout);
    }

    if (res < 0) {
        JNU_ThrowIOExceptionWithLastError(env, "epoll_wait failed");
    }
    return res;
}
```

至此，Java nio利用epoll的三个核心指令完成socket io 多路复用的源码探索完成。

## 三. 原理简述

1. EpollArrayWrapper是操控epoll_event数据结构的封装类，包含了epoll_create,epoll_ctl,epoll_wait指令
2. Selector.open()调用epoll_create创建epoll_event，返回epfd，用来管理和监控连接
3. SeverSocketChannel.open()本质就是创建了一个fd，通过socket绑定端口号，相当于建立了监听网络的通道
4. SeverSocketChannel.register()将fd，OP_ACCEPT（感兴趣事件）注册到selector，这里并没有调用epoll_ctl，只是将fd添加到EPollArrayWrapper.updateDescriptors数组中
5. Selector.select()会将updateDescriptors数组中的fd通过epoll_ctl添加到epoll_event中，然后调用epoll_wait方法阻塞，等待有连接事件发生后，才会返回
6. 当建立连接之后，通过socketChannel.accept创建新的socketChannel用于此连接后续的数据读写，ServerSocketChannel只负责监听连接事件

## 四. 总结

这次主要学习了Java NIO和epoll是如何结合使用的，源码追索都比较枯燥，当搞清楚调用逻辑之后，还是挺兴奋的。











epoll的唤醒机制