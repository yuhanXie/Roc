## 一. 理解Socket

一个简单的服务器

```java
    public static void main(String[] args) throws IOException {
         //监听9876端口
        ServerSocket serverSocket = new ServerSocket(9876);
        while (true) {
            try {
                //等待客户端连接，此时处于阻塞状态
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
```

一个简单的客户端

```java
    public static void main(String[] args) throws IOException {
        //连接9876端口号
        Socket socket = new Socket("localhost", 9876);
        OutputStream outputStream = socket.getOutputStream();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String nextLine = scanner.nextLine();
            // 向服务端发送数据
            outputStream.write(nextLine.getBytes());
        }
    }
```

平常开发过程中，我们其实不太会接触到socket，都是直接发起http请求。但是呢，java中http请求底层也是使用socket进行网络通信，因为我们都会利用现成的工具如Tomcat，Tomcat的底层也是socket编程，也就是说和网络打交道，就离不开socket。

socket是什么呢？socket是在应用层和传输层之间的一个抽象层，它把TCP复杂的操作抽象为几个简单的接口供应用层调用，而开发者就无需关心TCP连接相关细节，就可以进行网络编程。简而言之，Socket就是解决服务器和客户端之前进程通信连接的。看下它的核心方法：

#### accept流程

1. 创建ServerSocket：创建一个用于监听某端口号的socket，同时在内核中创建syn队列和accept队列
2. serverSocket.accept()：线程阻塞在此，等待请求进来
3. client发起connect请求：这是TCP三次握手的第一次syn请求，会将此请求添加到syn队列
4. 当TCP三次握手完成，将此请求从syn队列移到accept队列，此时会唤醒监控socket所在线程
5. serverSocket.accept()会将请求从accept队列取出，创建新的socket和client进行读写操作

参考[理解socket connect和accept的实现细节](http://xiaorui.cc/archives/3256)，可见accept就把TCP三次握手的操作封装了起来，而用户进程只需要关心请求来的数据。

#### inputStream.read()

也就是大名鼎鼎的IO，上面例子实现的是传统IO，也叫Blocking IO（BIO）。先简述通用的IO过程，主要分为两步：

1. 等待数据从物理设备复制到内核缓冲区（ 等待数据准备好）
2. 将内核缓冲区的数据复制到用户缓冲区

>为什么没有直接将数据复制到用户缓冲区？
>
>为保证系统安全，禁止应用程序直接访问或操作内核数据，只能通过操作系统指令进行操作。

我们调用read的时候，线程会阻塞在此，等待上面两步执行完成之后，才能继续执行业务代码。而我们都知道IO操作是比CPU慢很多的，如果每个连接进来都需要等待IO操作，那么应用可处理的连接也就太差了。

> 这里有一个小知识点：read的过程中，如果你打印线程状态的话，其实是RUNNABLE，accept的时候也是RUNNABLE状态，众所周知，传统IO是阻塞IO，应该是BLOCKED或者WAITING都比较好理解。这是为什么呢？我们先看下RUNNABLE的javaDoc：处在runnable状态的线程是在jvm中执行，但是可能是在等待操作系统中如cpu的资源。
>
> 所以Java中定义线程的状态和操作系统的状态是有一定区别的，现代操作系统一般都是时分处理器，时间片极短，很难区分ready和running状态，所以Java将其合并为runnable。
>
> IO操作比CPU慢，那当CPU执行到IO操作时，会立马切换，执行调度队列中的其他线程任务，而当前线程会被放到等待队列中，所以此时线程是阻塞状态，不占用CPU。当IO操作完成后，会发出中断信号，CPU会进入中断处理流程，此时之前等待IO的线程会被重新放入调度队列，等待CPU调度。以上是一个简单描述，详细可参考[Java线程状态RUNNABLE详解](https://my.oschina.net/goldenshaw/blog/705397)
>
> 

```java

        /**
         * Thread state for a runnable thread.  A thread in the runnable
         * state is executing in the Java virtual machine but it may
         * be waiting for other resources from the operating system
         * such as processor.
         */
        RUNNABLE,
```

对于一个高并发的应用来说，不能够在等待网络IO的过程中浪费时间。那么我们是不是能对这个read方法进行一些优化呢。我们先来分析一下，read方法是同步的，所以每次请求进来都需要等待之前的请求执行结束，也就是并发数是1了。

初步优化方式就是在io读写时**开启多线程**，这样可以保证多个连接同时处理。这个代码我就不举例了，一般来说都是两种方式，1是每个请求进来直接创建一个新的线程处理，2是使用固定线程池处理。1的问题在于线程的资源相对是昂贵的，在高并发的场景下，会创建大量的线程，在并发下降后又会有线程的销毁，线程创建和销毁都是消耗系统资源的。2的问题是在面对并发非常高的情况下，仍然没有办法应对。那有更进一步的优化方式嘛？

仔细思考一下，问题其实是在io读写的时候是个同步过程，线程必须等待。我们结合前面提到IO过程的两步 如果能够：**当内核将这两步都执行结束之后，再通知用户进程，进行读取（这就是AIO）**，那应用处理效率就是最理想化了。不过我们不能一口气吃成个胖子，我们先看看能否把**第一步变成异步，第二步不变（这就是NIO）**；若第一步是异步的话，那我们就需**要一个线程去管理所有的连接，若有连接第一步完成之后，再通知用户进程，进行读取（这就是IO多路复用）**。这样我们就是把IO模型都引入进来了。

## 二. I/O模型

一般来说IO模型分为两个维度，同步和阻塞。

阻塞：第一步调用recv/recvfrom时，当数据未准备好时是否返回，返回则为非阻塞，反之则为阻塞

同步：第二步等待数据从内核空间复制到用户空间，若未复制完成就返回为异步，反之则是同步

BIO（blocking io）：同步阻塞IO

![](/home/yuhan/Documents/workspace/git/Note/pic/同步阻塞.png)

其缺点显而易见：当数据未复制到用户空间时，调用read方法的线程就会一直阻塞。

NIO（non blocking io）同步非阻塞IO

![](/home/yuhan/Documents/workspace/git/Note/pic/同步非阻塞.png)

相对于阻塞IO，当用户线程发起read请求时，当内核数据未准备好时，系统会直接返回error，此时用户线程可以先去做些其他事情，只要定时轮询数据准备好即可。其缺点当然就是会产生大量的系统调用，因为不确定数据何时准备好，所以只有不停的轮询。

IO multiplexing：IO多路复用

![](/home/yuhan/Documents/workspace/git/Note/pic/IO多路复用.png)

非阻塞IO每次只能查询一个连接的数据准备情况，导致会产生大量的轮询。那么IO多路复用就是用户线程一次可查询多个连接的数据准备情况，并且返回数据准备好的socket（此处使用select/poll/epoll有些微差别，后续再说），然后用户线程进行读取即可，可大大减少轮询次数。

AIO：异步非阻塞IO

![](/home/yuhan/Documents/workspace/git/Note/pic/异步IO.png)

当用户线程调用read之后，就会立刻返回。等待内核数据准备好，将数据从内核空间复制到用户空间后，通知用户线程进行数据读取。这其实就是我们前面说的理想状态了。使用AIO是处理高并发的最优解决方案，但实际上AIO应用的并不多，这个后续再说。

## 三. select/poll/epoll

目前网络框架中应用最广的就是IO多路复用，而IO多路复用离不开系统底层提供的几个指令，那就是select/poll/epoll，这里就再学习一下这几个指令。

#### select

关键代码

```c
int select (int n, fd_set *readfds, fd_set *writefds, fd_set *exceptfds, struct timeval *timeout);

// fd_set定义
#include <sys/select.h>

//fd_setsize 定义为了1024
#define FD_SETSIZE 1024
#define NFDBITS (8 * sizeof(unsigned long))
#define __FDSET_LONGS (FD_SETSIZE/NFDBITS)

typedef struct {
        unsigned long fds_bits[__FDSET_LONGS];
} fd_set;

void FD_SET(int fd, fd_set *fdset)   //将fd添加到fdset
void FD_CLR(int fd, fd_set *fdset)   //从fdset中删除fd
void FD_ISSET(int fd, fd_set *fdset) //判断fd是否已存在fdset
void FD_ZERO(fd_set *fdset)          //初始化fdset内容全为0   
```

一般来说，网络请求只关心n 和 fd_set *readfds，n是最大的文件描述符值+1，readfds是个数组，保存对读事件感兴趣的socket的fd（文件描述符：当程序打开一个现有文件（socket）或者创建一个新文件（socket）时，内核向进程返回一个文件描述符，在UNIX、Linux的系统调用中，大量的系统调用都是依赖于文件描述符）。使用select的demo如下：

```c
  while(1){ 
  //将文件描述符数组每一位全都置为0
	FD_ZERO(&rset);
	//每次while循环都要重新设置要监控的socket
  	for (i = 0; i< 5; i++ ) {
  		FD_SET(fds[i],&rset);
  	}
	//一直阻塞直到有读事件已ready
	select(max+1, &rset, NULL, NULL, NULL);

	for(i=0;i<5;i++) {
	//循环判断是哪个socket可读
		if (FD_ISSET(fds[i], &rset)){
			memset(buffer,0,MAXBUF);
			read(fds[i], buffer, MAXBUF);
			puts(buffer);
		}
	}
```

1. 把感兴趣的socket fd添加到rset中，调用select方法，阻塞在此
2. 当有socket数据准备好之后，select方法就会返回
3. 用户线程循环遍历所有fd，找到准备好数据的socket，进行读取

缺点

1. 每次调用select方法，都需要将所有fd都复制到内核空间
2. 每次可添加的fd是有上限，为1024
3. 当select返回时，不知道是哪些socket数据准备好了，需要遍历fd_set
4. 当select返回后，未准备好的fd需要重新添加到rset，并复制到内核空间

#### poll

poll方法与select方法基本一致，唯一的区别是将fd_set换成了pollfd，fd_set定义好的数组，且设置了数组长度为1024，而pollfd相当于是链表。

```c
struct pollfd
  {
    int fd;			/* File descriptor to poll.  */
    short int events;		/* Types of events poller cares about.  */
    short int revents;		/* Types of events that actually occurred.  */
  };

```

demo：

```c
  for (i=0;i<5;i++) 
  {
    memset(&client, 0, sizeof (client));
    addrlen = sizeof(client);
    pollfds[i].fd = accept(sockfd,(struct sockaddr*)&client, &addrlen);
    pollfds[i].events = POLLIN;
  }
  sleep(1);
  while(1){
  	puts("round again");
	poll(pollfds, 5, 50000);

	for(i=0;i<5;i++) {
		if (pollfds[i].revents & POLLIN){
			pollfds[i].revents = 0;
			memset(buffer,0,MAXBUF);
			read(pollfds[i].fd, buffer, MAXBUF);
			puts(buffer);
		}
	}
  }
```

poll 相对于select的优点就是：没有了1024的数量限制，但是呢内存复制，遍历仍然和select都是一样的。

#### epoll

关键代码

```c
#include <sys/epoll.h>

// 数据结构
// 每一个epoll对象都有一个独立的eventpoll结构体
// 红黑树用于存放通过epoll_ctl方法向epoll对象中添加进来的事件
// epoll_wait检查是否有事件发生时，只需要检查eventpoll对象中的rdlist双链表中是否有epitem元素即可
struct eventpoll {
    ...
    /*红黑树的根节点，这颗树存储着所有添加到epoll中的需要监控的事件*/
    struct rb_root  rbr;
    /*双链表存储所有就绪的文件描述符*/
    struct list_head rdlist;
    ...
};

// API
int epoll_create(int size); // 内核中创建一个 eventpoll 对象，把所有需要监听的 socket 都放到 eventpoll 对象中
int epoll_ctl(int epfd, int op, int fd, struct epoll_event *event); // epoll_ctl 负责把 socket 增加、删除到内核红黑树
int epoll_wait(int epfd, struct epoll_event * events, int maxevents, int timeout);// epoll_wait 检测双链表中是否有就绪的文件描述符，如果有，则返回


```

demo

```c
//创建epollfd，eventpoll对象只需创建一次
int epfd = epoll_create(10);
  ...
  ...
  for (i=0;i<5;i++) 
  {
    static struct epoll_event ev;
    memset(&client, 0, sizeof (client));
    addrlen = sizeof(client);
    ev.data.fd = accept(sockfd,(struct sockaddr*)&client, &addrlen);
    ev.events = EPOLLIN;
    //添加epollfd到eventpoll的rb_root（红黑树结构，插入，删除，查询速度均衡）
    epoll_ctl(epfd, EPOLL_CTL_ADD, ev.data.fd, &ev); 
  }
  
  while(1){
  	puts("round again");
    //等待，返回的是已就绪的fd
  	nfds = epoll_wait(epfd, events, 5, 10000);
	//遍历已就绪的fd，存储在rdlist中
	for(i=0;i<nfds;i++) {
			memset(buffer,0,MAXBUF);
			read(events[i].data.fd, buffer, MAXBUF);
			puts(buffer);
	}
  }
 
```

我们来看看epoll解决了哪些问题，

1. eventpoll是存储在内核中，fd只需要复制一次到内核，无需像select/poll，每次调用都需要复制到内核
2. 取消了1024的数据限制，并且使用了红黑树的结构，可更快的添加，删除及查询（时间复杂度是O(logN)，原先是O(n)
3. 使用rdlist保存已就绪的fd，无需for循环检查

## 四. 总结

此次学习内容：socket编程及大概流程，IO模型，IO多路复用的指令的select/poll/epoll的相关知识，学习NIO之前的理论知识基本就学到这，这个内容还是相当复杂和有深度的。当然还有一些知识点是没有学习到的，像epoll的触发模式，如果后续学习过程中有遇到的话在进行进一步的学习吧

参考文章：

https://juejin.cn/post/6931543528971436046

https://www.6aiq.com/article/1548222475606

https://my.oschina.net/alchemystar/blog/3008840
