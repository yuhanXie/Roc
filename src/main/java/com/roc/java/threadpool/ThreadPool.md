## 一. 构造函数
```java
    /**
     * Creates a new {@code ThreadPoolExecutor} with the given initial
     * parameters.
     *
     * @param corePoolSize the number of threads to keep in the pool, even
     *        if they are idle, unless {@code allowCoreThreadTimeOut} is set
     * @param maximumPoolSize the maximum number of threads to allow in the
     *        pool
     * @param keepAliveTime when the number of threads is greater than
     *        the core, this is the maximum time that excess idle threads
     *        will wait for new tasks before terminating.
     * @param unit the time unit for the {@code keepAliveTime} argument
     * @param workQueue the queue to use for holding tasks before they are
     *        executed.  This queue will hold only the {@code Runnable}
     *        tasks submitted by the {@code execute} method.
     * @param threadFactory the factory to use when the executor
     *        creates a new thread
     * @param handler the handler to use when execution is blocked
     *        because the thread bounds and queue capacities are reached
     * @throws IllegalArgumentException if one of the following holds:<br>
     *         {@code corePoolSize < 0}<br>
     *         {@code keepAliveTime < 0}<br>
     *         {@code maximumPoolSize <= 0}<br>
     *         {@code maximumPoolSize < corePoolSize}
     * @throws NullPointerException if {@code workQueue}
     *         or {@code threadFactory} or {@code handler} is null
     */
    public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory,
                              RejectedExecutionHandler handler) {
    }
```

| 字段                     | 定义                                                         | 解释                                                         |
| ------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| maximumPoolSize          | the maximum number of threads to allow in the pool           | 最大线程数，线程池中允许的最大线程数                         |
| corePoolSize             | the number of threads to keep in the pool, even if they are idle, unless {@code allowCoreThreadTimeOut} is set | 核心线程数，线程池中最少的线程数                             |
| keepAliveTime            | when the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating. | 保持存活时间，当线程数大于核心线程数时，多余的线程空闲等待的最大线程，超过后便销毁 |
| unit                     | the time unit for the {@code keepAliveTime} argument         | keepAliveTime的单位                                          |
| workQueue                | the queue to use for holding tasks before they are executed.  This queue will hold only the {@code Runnable} tasks submitted by the {@code execute} method. | 用来保存未执行任务的队列，必须是通过execute执行的            |
| threadFactory            | the factory to use when the executor creates a new thread    | 创建线程的工厂，设置线程的前缀，设置线程的线程组             |
| RejectedExecutionHandler | the handler to use when execution is blocked because the thread bounds and queue capacities are reached | 当线程池和队列都满了之后，添加新任务后的策略                 |

## 二. 任务执行流程

![](/home/yuhan/Documents/workspace/Roc/src/main/java/com/roc/java/threadpool/线程池.png)

## 三. 线程池的状态

| 状态       | 定义                                                         | 解释                                                         |
| ---------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| RUNNING    | Accept new tasks and process queued tasks                    | 接收新任务，处理队列中的任务                                 |
| SHUTDOWN   | Don't accept new tasks, but process queued tasks             | 不接受新任务，处理队列中的任务                               |
| STOP       | Don't accept new tasks, don't process queued tasks,and interrupt in-progress tasks | 不接受新任务，不处理队列中的任务，且中断正在执行的任务       |
| TIDYING    | All tasks have terminated, workerCount is zero,the thread transitioning to state TIDYING will run the terminated() hook method | 所有任务终止，工作数量置为0，转为TIDYING状态时，会执行terminated方法 |
| TERMINATED | terminated() has completed                                   | terminated执行结束（可重写的方法，用于自定义处理）           |

状态转移图

![](/home/yuhan/Documents/workspace/Roc/src/main/java/com/roc/java/threadpool/线程池状态转移.png)

线程池状态主要是线程池内部执行任务时定义的状态，以便更好的执行任务，记下来的意义不大。

## 四. ctl

```java
private final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
```

线程池用了一个原子类来记录当前任务数量和线程池状态，这个思想我们可以参考，在一些场景中，可以使用这个方式。

ctl的前3位用来表示线程的状态，后29位表示任务的数量

```java
    private static final int COUNT_BITS = Integer.SIZE - 3;
    //前3位位0, 后29位1
    private static final int CAPACITY   = (1 << COUNT_BITS) - 1;
    // runState is stored in the high-order bits
    //前3位:111 后面29位都是0
    private static final int RUNNING    = -1 << COUNT_BITS;
    //前3位：000
    private static final int SHUTDOWN   =  0 << COUNT_BITS;
    //前3位：001
    private static final int STOP       =  1 << COUNT_BITS;
    //前3位：010
    private static final int TIDYING    =  2 << COUNT_BITS;
    //前3位：011
    private static final int TERMINATED =  3 << COUNT_BITS;

    // Packing and unpacking ctl
    //线程的状态 和~capacity与运算后，和前面定义的状态进行比较，得到相应状态
    private static int runStateOf(int c)     { return c & ~CAPACITY; }
    //任务的数量, 和capacity与运算，获得数量
    private static int workerCountOf(int c)  { return c & CAPACITY; }
```

## 五. 常用线程池

Java提供了四种常用的线程池创建，都在Executors.java中，但是阿里开发规范中明确提到禁止使用Executors创建线程池，我们先来看下定义的线程池，然后再看看为何开发规范不推荐使用。

```java
    public static ExecutorService newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE,
                                      60L, TimeUnit.SECONDS,
                                      new SynchronousQueue<Runnable>());
    }

    public static ExecutorService newSingleThreadExecutor() {
        return new FinalizableDelegatedExecutorService
            (new ThreadPoolExecutor(1, 1,
                                    0L, TimeUnit.MILLISECONDS,
                                    new LinkedBlockingQueue<Runnable>()));
    }
    
    public static ExecutorService newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads,
                                      0L, TimeUnit.MILLISECONDS,
                                      new LinkedBlockingQueue<Runnable>());
    }

    public static ScheduledExecutorService newScheduledThreadPool(int corePoolSize) {
        return new ScheduledThreadPoolExecutor(corePoolSize);
    }

    public ScheduledThreadPoolExecutor(int corePoolSize) {
        super(corePoolSize, Integer.MAX_VALUE, 0, NANOSECONDS,
              new DelayedWorkQueue());
    }

```

| 线程池         | coreSize<br>maxSize<br>queue                          | 备注                                                         | 优缺点                                                       |
| -------------- | ----------------------------------------------------- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| 缓存线程池     | 0<br>Integer.MAX_VALUE<br>SynchronousQueue            | SynchronousQueue其实不是一个队列，当put一个元素后，就必须take该元素之后，才能继续put。也就是说当前队列不会存储任务。缓存线程池会无限增加线程来执行任务，当线程空闲超过60s，就会销毁 | 适用于执行大量短生命周期异步任务。可能出现创建大量线程以及销毁的重复过程 |
| 单线程线程池   | 1<br>1<br>LinkedBlockingQueue                         | LinkedBlockingQueue默认长度为Integer.MAX_VALUE，所有任务都进到队列中，一个线程按序执行所有任务。当单线程因为异常终止了，也会创建新的线程来取代它 | 保证任务有序的执行，执行速度会慢。可能出现大量任务堆积在队列中 |
| 固定线程池     | nThreads<br>nThread<br>LinkedBlockingQueue            | 核心线程数和最大线程数可自定义，但必须相同。相对比较常规使用的线程池 | 通过设置线程数量，可有效利用多核机器。                       |
| 定时任务线程池 | corePoolSize<br>Integer.MAX_VALUE<br>DelayedWorkQueue | DelayedWorkQueue用来完成延迟或周期性任务                     | 完成延迟或周期性任务                                         |

1. 当FixedThreadPool的nThreads=1时，和SingleThreadExecutor的区别

FixedThreadPool在创建后是可以修改配置，比如修改核心线程数等。但SingleThreadExecutor是无法修改的。

2. 为何开发规范不推荐使用Executors创建线程池

首先直接使用ThreadPoolExecutor，可以加强开发者对线程池的理解；其次使用Executors创建的线程池如果不恰当的话，很容易出现OOM的情况，例如无限创建线程，或者队列添加大量数据就会导致OOM。

## 六. 参数配置原则

CPU密集型：核心线程数设为cpu数+1（《Java并发编程实战》一书中给出的原因是：即使当计算（CPU）密集型的线程偶尔由于页缺失故障或者其他原因而暂停时，这个“额外”的线程也能确保 CPU 的时钟周期不会被浪费。)

IO密集型：核心线程数设置成cpu数*2

美团曾发布过一个文章，可以实时调整线程池的参数

## 七. RejectedExecutionHandler

| 策略名称            | 策略方式                                   |      |
| ------------------- | ------------------------------------------ | ---- |
| AbortPolicy         | 丢弃任务，并抛出RejectedExecutionException |      |
| CallerRunsPolicy    | 尝试用调用线程执行任务                     |      |
| DiscardOldestPolicy | 丢弃队列最前面的任务，再尝试执行当前任务   |      |
| DiscardPolicy       | 丢弃当前任务，不抛出异常                   |      |

