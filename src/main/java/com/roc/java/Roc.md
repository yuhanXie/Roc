#### spring的特性
1. IOC(Inverse of Control:依赖倒置)
把底层类作为参数传入上层类，实现上层类对下层类的“控制”，更容易维护，有利于不同组件的协同合作和单元测试。
**IOC Container**：创建对象时，首先一层层找依赖对象，找到最底层，从最底层开始创建对象，一层层往上找，直到创建出需要的对象。
优点：实例化复杂对象时，无需关心其创建细节和依赖
2. AOP（aspect oriented program：面向切面编程）
AOP是典型的代理模式
作用：无需干扰业务代码，可以在业务前后做一些通用处理，如安全控制，日志等
静态AOP： aspectJ
动态AOP： JDK proxy, CGlib
- JDK proxy: 对实现了接口的类做代理，利用Proxy和InvocationHandler，在运行时，生成接口的代理类，织入切面
- CGlib：在运行时，生成目标类的子类，切面切入到子类中（final修饰的类是无法使用CGlib的）
- aspectJ：在编译成字节码时，切面逻辑直接以字节码添加到目标类的字节码文件中（性能更好）
#### volatile
只对实例变量（无static修饰）和类变量（以static修饰）有用，对方法内的变量无用（方法内的不会出现多线程问题）
作用：

1. 每次获取变量都是从主存中实时拉去，每次赋值也会同步到主存，保证变量的可见性

2. 禁止指令重排（在不影响运行结果的情况下，为提高性能，对字节码指令进行重新排序）使用内存屏障达到此目的，必须写完之后才能读，否则在多线程情况下会出现问题

  问题：不能保证原子性
#### Java的三大特性
- 封装：对对象内部进行隐藏（private），只暴露对外的出口（public）
- 继承：子类可以继承父类的属性和方法
- 多态：继承和重写（override）

#### 反射
作用：动态创建实例对象，获取类的域信息和方法
动态加载，灵活；性能差
原理：类信息都存储在方法区中，反射可拿到方法区中反射类的相关信息，从而创建实例对象
用处：spring创建bean对象等，反射是框架设计的灵魂

#### 分库分表

- 垂直分库：按照业务的不同，将不同的表分到不同的数据库，不同的库放在不同的服务器
- 垂直分表：将一张大表拆分成几张表，将不常用的字段/文本等大字段拆分到附表中，热点数据放到主表
- 水平分库：将一张表的数据按照一定的规则拆分到不同的库，查询时根据规则决定查对应的库。（id为奇数的放一个库，id为偶数的放一个库，根据id的奇偶决定查哪个库）
- 水平分表：将一张表的数据按照一定规则拆分到不同的表，以提高单表查询速度（id为奇数的在一个库中，再次进行查分，尾数是1，3，5的放在一张表，尾数为7，9的在另一张表）

#### ThreadPoolExecutor

corePoolSize，maximumPoolSize，keepAliveTime，unit，workQueue，threadFactory，RejectedExecutionHandler

提交任务时，当前任务数小于核心线程数，创建新的线程执行任务；反之，判断当前队列是否已满，未满的话，进入队列排队。反之，是否小于最大线程数，小于的话，则创建新的线程执行任务；反之，交由rejectedHandler处理，有4种处理方式。

丢弃当前任务，不抛出异常；丢弃当前任务，抛出异常；尝试使用调用线程执行任务；丢弃队列最前面的任务，再尝试执行当前任务

keepAliveTime：当前空闲线程数量大于核心线程的话，当空闲时间超过keepAliveTime的话，线程自动销毁。

ctl：用来保存线程池的状态（前3位）和当前任务的数量（后29位）

#### redis

高性能高可用

原因：

- 内存数据库；
- 数据结构简单（对各类数据结构都做了封装，以提高性能，例如SDS（simple dynamic string）)
- 基于Reactor模式实现网络事件处理；
- 单线程（redis 6.0支持多线程，不过是在网络连接方面，操作上仍是单线程）

支持的数据类型：string，hash，list，set，zset,bitmaps

1. String：计数器/session/分布式系统的全局序列号/分布式锁（SETNX：不存在才赋值；SETEX：原子操作，设置值，并设置过期时间）
2. Hash：有一个field的概念，userId为key，productId为field，商品数量为value，添加商品或数量，可以很好的获取购物车商品总数
3. List：栈，队列，公众号信息流
4. Set：点赞，关注，收藏等，共同关注，互相关注等
5. ZSet：排行榜
6. Bitmaps：分布式的bloom filter

持久化

- rdb：定时将内存的数据以快照的形式保存到硬盘
- aof：将redis的每次写记录都写到日志文件

redis重启时，优先从aof中读取数据，rdb体积更小，恢复速度快，但是aof数据更安全

1. 主从架构：避免单点故障，redis提供replication（复制）的功能，当主数据库中数据更新后，自动同步到从数据库上。避免了单点故障，读写分离，降低了主节点的压力，但是单点故障后，需要手动切换
2. 哨兵：哨兵集群，不提供redis服务，主要用来监控redis的实例节点，client第一次从哨兵找到redis的主节点，后续直接访问redis的主节点，当redis主节点故障后，哨兵第一时间感知到，并选出新的主节点通知客户端（订阅方式）。单点故障后，可自动切换；配置复杂，需要多个哨兵节点，主从切换时，会有瞬间断开；主节点突然宕机，可能会导致部分数据无法同步，扩容困难
3. 高可用集群：去中心化，数据按照槽slot分布在多个节点，每个节点负责一部分槽位。有槽位定位算法crc16，纠正机制实现槽位信息的校验调整

- 缓存穿透：大量请求的key不存在缓存中，全部落到数据库查询，大概率是攻击导致。

​       接口校验，排除无效参数；bloom filter

- 缓存击穿：某个热点的key突然失效，导致查询全部落到数据库

​      设置热点key永不过期，分布式锁

- 缓存雪崩：大量热点的key同时失效

​      对同一批缓存数据，设置不同的过期时间；如果是redis挂了的话，可以使用redis集群

redis分布式锁的框架：redisson，实现了重入锁，非公平锁，信号量等

过期删除策略：惰性+定期删除

内存淘汰机制：lru，随机等策略

#### 布隆过滤器

可以在海量数据中判断某个数据是否存在

使用一个很大的bit字符串或数组，若干个hash算法。给到一个数据时，通过几个hash算法，生成若干个整数值index，将bit数组对应index的值设为1。判断某个数据是否存在，使用相同hash算法，查看对应index上的值是否为1，若有不为1的话，那么一定不存在；若都为1，那么可能存在。

需要保证bit字符串足够大，否则容易出现hash冲突

#### 一次完整的http请求

1. 域名解析：将域名转为ip。主机向本地域名服务器请求，找到对应ip则返回，没找到，则本地域名服务器向根域名服务器查询，查到ip则返回，没查到则向顶级域名服务器查询，最后由本地域名服务器返回给主机
2. ip寻址：找到对应主机。每个路由器都维护了路由表，通过最长前缀匹配，选择出口，最后找到对应主机
3. 与对应主机进行TCP连接。三次握手，client ：syn = 1 seq = X； server：syn = 1，ack = x + 1, seq = y；client ：syn = 0,seq = x + 1, ack = y + 1/
4. 建立连接后，发起http请求
5. 服务端受到http请求，返回报文

#### kafka

高吞吐量的分布式发布订阅系统

- producer：生产者
- topic：消息主题
- partition：分区（同一个主题下的消息分成多个区）
- replication：分区的副本
- broker： kafka实例
- consumer：消费者
- consumer group：多个消费者组成一个消费者组，同一个分区的数据只能被消费者组中的某一个消费者消费

发送消息可以指定分区，若未指定但设置了数据的key，则会根据key，hash出一个分区，都没有的话，则会轮询出分区。

kafka使用segment文件存储消息，segment中包含.log, .index, .timeIndex。通过分段+索引的方式，提高查询效率。

ack机制：producer发送消息，设置ack=0，表示发送之后不等待发送结果；ack=1表示leader收到之后，就返回ack；ack=all/-1，表示leader和follower全部收到之后返回成功

丢消息

1. 设置ack=0/1，都可能会丢消息
2. 消费者消费消息时，设置为自动提交offset，当消费者消费消息时，offset更新后，消费失败了。（解决办法是：设置为手动提交，当业务代码执行完成后，手动提交）

重复消费

1. producer发送消息后，因为网络或其他原因未收到ack或失败消息的话，producer可能会再次发送消息，导致重复（解决办法：启动kafka的幂等性，同时ack=all，producer发送消息时，会带seq，每发一条seq+1。发生重试时，判断当前seq，如果比当前seq大，则存储，否则丢弃）
2. 手动提交offset时，挂掉，其实已经消费完成，但offset未更新，导致重复（业务上设置幂等，用redis缓存offset或唯一键）

ZooKeeper在kafka中的作用：1. broker注册  2. topic和partition注册 3. 负载均衡

kafka如何保证消息的顺序消费

kafka在单个分区中通过offset，尾部追加保证顺序性，设置topic只对应一个partition，即可保证顺序性

#### mysql

ACID（atomicity，consistency，isolation，durability）

- read uncommited：事务未committed，也可以读到
- read committed：只有事务committed后，才可以读到
- repeatable read： A事务开始查询的值，在B事务修改后，A事务再次查询，仍和开始查询的值一样，不受其他事务修改影响
- serializable：A事务开始后，B事务无法执行任何更新语句，必须等到A事务完成后

mysql的锁：表级锁和行级锁

- 共享锁（S锁，读锁）

事务A对对象1加上S锁，则事务A可以读数据，但不可以修改。其他事务也可以加S锁，但不能加X锁

- 排他锁（X锁，写锁）

事务A对对象1加上X锁，事务A可以读写对象1，其他事务不能对对象1加任何锁

意向锁是表级锁，有意向共享锁，意向排他锁

当事务A准备对表中的几条数据加X锁，会先对表加意向排他锁，而事务B向加上S锁时，发现表上有意向排他锁，就会阻塞。假设表中数据过多的话，就不需要逐行查询锁标记，提高性能

死锁：两个或以上的事务在执行过程中因争抢资源而造成互相等待的现象

1. 等待事务超时，主动回滚
2. 进行死锁检查，主动回滚某条事务，让别的事务能继续执行

#### hystrix

防止级联异常

1. 构建hystrixCommand或者hystrixObservableCommand，会执行到toObservable方法中
2. 查询缓存中是否存在结果，存在的话直接返回
3. 检查断路器是否打开，打开的话直接走降级策略
4. 查询线程池或信号量资源是否还有，否的话走降级策略
5. 执行请求，请求失败或超时，也会走降级fallback

断路器会计算一段时间内请求的失败数，达到一定阈值，就会打开断路器

#### feign

- 通过JDK proxy生成对应的feign调用接口的代理类
- 根据声明的注解，解析出底层的methodHandler，基于requestBean动态生成request 
- Encoder将bean封装成请求
- 拦截器负责对请求和返回进行装饰处理
- logger日志记录
- client基于重试器发起http请求，默认是httpUrlConnection，可以切换为httpClient或OkHttp

#### ribbon

根据请求，获取eureka上对应服务的实例。根据对应算法选择对应实例

- 轮询算法：每次id+1
- 随机算法
- 加权响应时间负载均衡。根据响应时间，设置weight。响应时间越长，weight越小，被选中的概率就小
- 自定义算法

#### http1.0和http2.0

- 2.0采用二进制帧和多路复用
- 支持对header进行压缩
- 支持服务器端推送消息

#### http和https

https主要增加了ssl层，用来加密，分为handshake和record，handshake的作用就是获得证书，生成record通信时的对称密钥

#### elastic



elastic

#### elastic

https://www.zhihu.com/question/323811022

#### HashMap

jdk1.8时数据结构是数组+链表+红黑树，自定义Node数组，默认长度是16，核心方法是get，putVal，resize；当链表长度大于8时，会转为红黑树；低于6时，会降为链表。key，value均为null

线程不安全：jdk 1.8时，采用尾插法，A线程拿到链表尾节点，准备添加元素时，B线程抢到时间片，拿到链表尾节点，将元素添加进去，B线程执行完成后，A线程再执行，那么B线程的操作就会被覆盖掉

ConcurrentHashMap：原来的实现是segment数组，现在也是Node节点的数组，逻辑几乎和HashMap一致，区别在于关键代码块上加了synchronized修饰。key，value均不能为null

HashTable：直接在方法上加了synchronized修饰，数据结构是数组+链表，key，value均不能为null

#### synchronized

本身是重量级锁，使用monitor完成锁，底层实现是依靠操作系统的mutex lock实现的，而这就涉及到用户态和内核态之间的切换，导致性能降低。

JDK 1.6以后，优化为锁升级策略。

- 无锁：JVM关闭偏向锁状态，或JVM延迟开启偏向锁的时间段创建的对象，且没有synchronized
- 偏向锁：引入原因是实际只有一个线程执行此代码块。实现方式是将当前线程的线程id通过CAS贴到对象的markword，表明该线程占有了该对象的偏向锁
- 轻量级锁：多个线程轮流执行同步代码块时使用。当线程B请求对象的锁时，发现线程A的线程id已经贴到mark word上时，就会升级成轻量级锁。实现方式是在当前线程的栈帧中插入lock record，赋值为mark word，并尝试将mark word的指针指向lock record，赋值成功的话，则获得轻量级锁。重入时，创建一个空的lock record，解锁即弹出此lock record
- 自旋锁：就是轻量级锁，当未获得轻量级锁的线程会进入自旋操作
- 重量级锁：当线程自旋超过一定次数，或其他线程也来竞争轻量级锁时，就会膨胀为重量级锁

#### CAS&ABA

compare and swap：为防止多线程修改同一数据，在每次写入数据时，都会拿原来的值和内存中的值对比一下，如果相同，则认为没改过，就可以把计算后的数据写入到内存中；如果不同，那就认为修改过，就重新读取，在此重复上述操作。

ABA：当拿原来的值和内存的值比较时，可能是在多个线程都修改过此数据后，变回了原来的值，在比较时发现是一致的，但实际已经修改过了。解决方案是增加版本号：每次修改都修改一次版本号。比较时同时比较值和版本号

#### 红黑树

二叉搜索树：左子树的值小于根节点，根节点小于右子树，所有子树也都是二叉搜索树。缺点是在插入数据的时候容易退化成链表

平衡二叉树（AVL）：特殊的二叉搜索树，左右子树的高度差的绝对值不超过1，子树也是平衡二叉树。不会退化成链表，查询，插入，删除的时间复杂度都是O(logN)。缺点是在插入或删除时，需要通过旋转（左旋，右旋）来达到平衡，影响性能

红黑树：大致的平衡二叉树，根节点到叶子的最长路径不超过最短路径的两倍。

1. 节点是红色或黑色，根节点是黑色，叶子节点是黑色空节点
2. 每个红色节点的两个子节点 都是黑色
3. 从任一节点到其每个叶子的所有路径都包含相同数目的黑色节点

优点是减少了旋转的次数

#### CAP

- Consistency：一致性，所有节点在同一时间数据一致
- Availability：可用性，服务可用
- Partition tolerance：分区容错性，某节点或分区出现故障，仍可提供满足一致性或可用性服务

- Zookeeper：CP；
- Eureka：AP
- Consul ：AP
- Nacos：支持AP和CP，可根据服务情况选择

#### CPU过高

1. top，shift+p（按照cpu使用率排序），定位到cpu使用过高的进程id
2. top -H p pid 或者ps -mp -o THREAD,tid,time 查看cpu使用过高的线程id
3. jstack pid | grep tid 查看线程信息，定位到代码，做具体分析

可能原因是无限循环或者频繁young gc

#### 内存过高

1. top，shift+m（按内存使用情况排序），找到对应进程pid
2. dump内存快照 ：jmap -dump:format=b,file=memory.bin pid
3. 使用jprofiler/jvisualVM等工具，打开内存快照，分析对象情况。推荐使用jprofiler，直接打开Biggest Objects，就很清楚

#### Throwable

子类有Exception和Error，Error有OutOfMemoryError，StackOverflowError，都属于unchecked；Exception分为RuntimeException（unchecked）和其他（checked），RuntimeException下有IndexOutOfBoundsException，NullPointerException等；其他比如IO Exception

#### mysql索引

索引的数据结构是B+树，其原因是首先使用平衡二叉树可以提高查询速度，但是呢为了利用磁盘预读功能，减少IO读写次数，使用B树是比二叉树更好的。

B树：平衡多叉树，每个节点存储多个索引和数据，可设置为磁盘页的大小的，每次读取都会读取一整个节点数据。

B+树：平衡多叉树，数据都存在叶子节点，非叶子节点存储索引，同时叶子节点中都有指针指向下一个叶子节点。这是为了提高范围查询的速度。数据库查询存在大量范围查询，如果使用B树，在查到某个节点后，可能需要再从头去查询符合范围的节点。而B+树不用

失效的场景

- 类型转换；类型是string，查询时未加引号
- 使用like，通配符在前；失效：like '%aa'，生效：like 'aa%'
- 使用or；要想索引生效，必须or的前后每个列都加上索引
- 对索引进行函数运算；
- 联合索引；假设联合索引是(A,B,C)，那么可支持的索引是A,(A,B),(A,B,C)；(B),(B,C)是失效的

#### AQS&ReentrantLock

reentrantLock是可重入锁，支持公平锁和非公平锁。区别在于非公平锁在第一次获取锁，就会尝试获取，而不排队。其他流程一致。

加锁：

1. 判断当前锁状态，state=0表示还没有锁，CAS尝试获取锁
2. 若state!=0，判断当前线程是否是占有锁的线程，是的话，state++，表示重入次数
3. 将当前线程封装成Node，先尝试CAS快速添加到队尾，如果失败的话，自旋插入到队尾
4. 判断当前Node是否为head，是的话，再次尝试获取锁，否则就等待了

解锁：

1. 非当前线程，则报错。state-1，如果state > 0的话，则直接结束
2. state = 0的话，把独占线程设为null，唤醒头结点

#### Tomcat

Tomcat可以运行多个Web应用程序，不同应用程序下可能会有相同类，但版本不同，如何保证隔离?
使用WebAppClassLoader优先加载当前应用目录下的类

也可能存在Web应用程序有完全相同的类，可以共享，无需每个独自加载
WebAppClassLoader上加了SharedClassLoader，若WebAppClassLoader没有加载到，就委托到SharedClassloader

为了隔绝Tomcat本身的依赖类和应用程序的依赖类
使用CatlinaClassLoader加载Tomcat本身的依赖

如果Tomcat的依赖类和应用程序的依赖类也需要共享
使用CommonClassLoader加载

各个类加载器的加载目录可以到在tomcat的catalina.properties配置文件

