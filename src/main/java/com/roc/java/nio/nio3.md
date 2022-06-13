
## 简介
在前面的文章学习了NIO的理论知识 [socket&io模型](https://juejin.cn/post/7046635944493088805) ，Java NIO与epoll的关联 [NIO server到epoll源码解析](https://juejin.cn/post/7047770233251037192)。
这次要来学习Java NIO了。众所周知，Java NIO的三大组件分别是Selector，Channel，Buffer。Selector是多路复用器，用来管理多个连接；Channel负责数据传输，类似火车轨道；Buffer负责数据读写，相当于火车。这次先学习Buffer，在网络中，我们一般都是用ByteBuffer，其他buffer也类似。

## 详解

### 1. 创建&分类
创建bytebuffer，我们有以下两种方式。创建出来的byteBuffer分为HeapByteBuffer和DirectByteBuffer。buffer的创建相对都是比较复杂的，尤其是directBuffer，所以**尽量复用，减少重复创建和回收**
> HeapByteBuffer：占用堆内内存，通过malloc申请，属于native memory，属于用户空间
> DirectByteBuffer：占用堆外内存
```java
     ByteBuffer heapBuffer = ByteBuffer.allocate(1024);
     ByteBuffer directBuffer = ByteBuffer.allocateDirect(1024);
```

### 2. directBuffer的优势
我们可以先看下使用buffer读取网络io数据的源码
```java
    static int read(FileDescriptor fd, ByteBuffer dst, long position,
                    NativeDispatcher nd)
        throws IOException
    {
        if (dst.isReadOnly())
            throw new IllegalArgumentException("Read-only buffer");
        //如果是DirectBuffer，直接读取
        if (dst instanceof DirectBuffer)
            return readIntoNativeBuffer(fd, dst, position, nd);

        // Substitute a native buffer
        //申请directBuffer，先将数据写到directBuffer，再写到hepBuffer
        ByteBuffer bb = Util.getTemporaryDirectBuffer(dst.remaining());
        try {
            int n = readIntoNativeBuffer(fd, bb, position, nd);
            bb.flip();
            if (n > 0)
                dst.put(bb);
            return n;
        } finally {
            Util.offerFirstTemporaryDirectBuffer(bb);
        }
    }
```
看源码，我们就能很清楚了解directBuffer的优势，当我们读取数据时，会先判断当前buffer是否是directBuffer，是的话就直接写入当前buffer，反之则先申请directBuffer，写到directBuffer，然后再复制到heapBuffer。
简而言之：**使用directBuffer可以减少一次数据的复制**。

### 3. 为何使用heapBuffer需要先复制到directBuffer
1. 当我们需要和操作系统/内核打交道，调用read,write等方法时需要传入buffer的起始地址和count。 若我们使用heapBuffer，当JVM发生gc时，buffer在堆中的地址可能会发生变化，这样的话内核读取数据的地址就会出错
2. Java中存储对象都是逻辑连续的，而系统调用都是需要物理地址连续的
这也就是为什么要用directBuffer去和内核交互的原因。哪怕上面两个问题可以通过某种方式解决，也是很复杂，并且存在隐患。
参考[Java NIO direct buffer的优势在哪儿？](https://www.zhihu.com/question/60892134)

### 4. directBuffer的内存管理
directBuffer虽然是堆外内存，但是也是通过Java代码创建的，所以JVM还是很负责任的把清理堆外内存的任务承担下来了。 我们来看下JVM是如何清理directBuffer的。这时候就得上源码了，
1. 我们先看看DirectByteBuffer的构造函数，关注到cleaner = Cleaner.create(this, new Deallocator(base, size, cap))。

```java
    DirectByteBuffer(int cap) {                   // package-private

        super(-1, 0, cap, cap);
        boolean pa = VM.isDirectMemoryPageAligned();
        int ps = Bits.pageSize();
        long size = Math.max(1L, (long)cap + (pa ? ps : 0));
        Bits.reserveMemory(size, cap);

        long base = 0;
        try {
            // 申请堆外内存空间
            base = unsafe.allocateMemory(size);
        } catch (OutOfMemoryError x) {
            Bits.unreserveMemory(size, cap);
            throw x;
        }
        unsafe.setMemory(base, size, (byte) 0);
        if (pa && (base % ps != 0)) {
            // Round up to page boundary
            //堆外内存的地址
            address = base + ps - (base & (ps - 1));
        } else {
            address = base;
        }
        //创建了Cleaner
        cleaner = Cleaner.create(this, new Deallocator(base, size, cap));
        att = null;
        
    }
```
2. 我们来看下Cleaner,可以发现它就是一个虚引用。
```java
public class Cleaner
    extends PhantomReference<Object>
{
    private Cleaner(Object referent, Runnable thunk) {
        super(referent, dummyQueue);
        this.thunk = thunk;
    }
}
```
3. PhantomReference的父类是Reference，reference有个static构造方法，启动了ReferenceHandler线程，并设置为守护线程
4. ReferenceHandler在while(true)中执行tryHandlePending方法，在此方法中若pending不为null，且instance of cleaner的话，就会赋值给c。
5. 在后面就会调用c.clean()。
> 如果仔细看代码的话，会发现pending没有地方显示赋值，是因为pending是gc赋值的
```java
public abstract class Reference<T> {

    /* List of References waiting to be enqueued.  The collector adds
     * References to this list, while the Reference-handler thread removes
     * them.  This list is protected by the above lock object. The
     * list uses the discovered field to link its elements.
     */
    private static Reference<Object> pending = null;

    static {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        for (ThreadGroup tgn = tg;
             tgn != null;
             tg = tgn, tgn = tg.getParent());
        //创建referenceHandler，并设置守护线程
        Thread handler = new ReferenceHandler(tg, "Reference Handler");
        handler.setPriority(Thread.MAX_PRIORITY);
        handler.setDaemon(true);
        handler.start();

        // provide access in SharedSecrets
        SharedSecrets.setJavaLangRefAccess(new JavaLangRefAccess() {
            @Override
            public boolean tryHandlePendingReference() {
                return tryHandlePending(false);
            }
        });
    }

    private static class ReferenceHandler extends Thread {

        private static void ensureClassInitialized(Class<?> clazz) {
            try {
                Class.forName(clazz.getName(), true, clazz.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw (Error) new NoClassDefFoundError(e.getMessage()).initCause(e);
            }
        }

        static {
            ensureClassInitialized(InterruptedException.class);
            ensureClassInitialized(Cleaner.class);
        }

        ReferenceHandler(ThreadGroup g, String name) {
            super(g, name);
        }

        public void run() {
            while (true) {
                tryHandlePending(true);
            }
        }
    }
    
    static boolean tryHandlePending(boolean waitForNotify) {
        Reference<Object> r;
        Cleaner c;
        try {
            synchronized (lock) {
                if (pending != null) {
                    //pending是引用
                    r = pending;
                    //如果pending 是cleaner的话，就赋值给c
                    c = r instanceof Cleaner ? (Cleaner) r : null;
                    // unlink 'r' from 'pending' chain
                    pending = r.discovered;
                    r.discovered = null;
                } else {
                    if (waitForNotify) {
                        lock.wait();
                    }
                    // retry if waited
                    return waitForNotify;
                }
            }
        } catch (OutOfMemoryError x) {
            Thread.yield();
            // retry
            return true;
        } catch (InterruptedException x) {
            // retry
            return true;
        }

        // Fast path for cleaners
        if (c != null) {
            //如果cleaner不为空，则调用cleaner.clean()方法
            c.clean();
            return true;
        }

        ReferenceQueue<? super Object> q = r.queue;
        if (q != ReferenceQueue.NULL) q.enqueue(r);
        return true;
    }
    
}
```
6. 那我们再回头来看创建directBuffer时cleaner.clean()，会调用thunk.run(),此thunk线程就是Deallocator，查看此线程实现，就能看到unsafe.freeMemory(address);这就释放了堆外内存


```java
       public void clean() {
        if (!remove(this))
            return;
        try {
            //调用Deallocator.run()
            thunk.run();
        } catch (final Throwable x) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                        if (System.err != null)
                            new Error("Cleaner terminated abnormally", x)
                                .printStackTrace();
                        System.exit(1);
                        return null;
                    }});
        }
    }

    
    private static class Deallocator implements Runnable
    {

        private static Unsafe unsafe = Unsafe.getUnsafe();

        private long address;
        private long size;
        private int capacity;

        private Deallocator(long address, long size, int capacity) {
            assert (address != 0);
            this.address = address;
            this.size = size;
            this.capacity = capacity;
        }

        public void run() {
            if (address == 0) {
                // Paranoia
                return;
            }
            //释放堆外内存
            unsafe.freeMemory(address);
            address = 0;
            Bits.unreserveMemory(size, capacity);
        }

    }
```

**总结**：jvm负责堆外内存的回收，创建directBuffer时会同步创建虚引用(PhantomReferenceh)cleaner，当directBuffer对象没有引用时，利用Reference的ReferenceHandler，会调用cleaner.clean方法，在clean方法中回收堆外内存

### 5. byteBuffer常用api

相关api我画了个简图，加强记忆

![buffer api图解](/home/yuhan/Documents/workspace/git/Note/pic/buffer.png)
```java
public abstract class Buffer {
    // Invariants: mark <= position <= limit <= capacity
    private int mark = -1;
    private int position = 0;
    private int limit;
    private int capacity;

    Buffer(int mark, int pos, int lim, int cap) {       // package-private
        if (cap < 0)
            throw new IllegalArgumentException("Negative capacity: " + cap);
        this.capacity = cap;
        limit(lim);
        position(pos);
        if (mark >= 0) {
            if (mark > pos)
                throw new IllegalArgumentException("mark > position: ("
                        + mark + " > " + pos + ")");
            this.mark = mark;
        }
    }

    //切换到读模式
    public final Buffer flip() {
        limit = position;
        position = 0;
        mark = -1;
        return this;
    }

    //与flip的区别在于limit没变，根据场景可能需要访问没有添加元素的位置
    public final Buffer rewind() {
        position = 0;
        mark = -1;
        return this;
    }

    public final Buffer mark() {
        mark = position;
        return this;
    }

    public final Buffer reset() {
        int m = mark;
        if (m < 0)
            throw new InvalidMarkException();
        position = m;
        return this;
    }
    public final Buffer clear() {
        position = 0;
        limit = capacity;
        mark = -1;
        return this;
    }
}

public abstract class ByteBuffer extends Buffer implements Comparable<ByteBuffer> {
    public abstract byte get();
    public abstract ByteBuffer put(byte b);
}

```

byteBuffer有所谓的读和写模式，其实只是方便理解，底层实现就是array，利用mark，limit，position方便读写数据。当写数据之后，想要读取数据，可以直接调用flip/rewind（有所区别，根据具体需求）来切换为读模式，position会置为0。

> limit : 写模式下limit=capacity，读模式下limit=position
>
> mark：手动调用，标记位置，用于之后回到该位置
>
> position：读/写模式下的当前位置

## 总结

主要学习了directBuffer的相关原理，由于gc，在堆内存创建的heapBuffer的物理地址可能发生变化，所以在调用系统指令时，都采用directBuffer。哪怕创建的是heapBuffer，也会先传到directBuffer，再复制到heapBuffer。jvm也负责堆外内存的回收，在创建heapBuffer时，会创建虚引用Cleaner，利用Reference的ReferenceHandler调用Cleaner.clean方法，进行回收堆外内存。Buffer的相关api只要理解了，还是比较清晰的

参考文章

https://blog.csdn.net/gdutxiaoxu/article/details/80738581
https://juejin.cn/post/6844904029663789070
http://www.disheng.tech/blog/java-%E5%A0%86%E5%A4%96%E5%86%85%E5%AD%98%E5%9B%9E%E6%94%B6%E5%8E%9F%E7%90%86/