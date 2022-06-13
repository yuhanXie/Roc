## 如何判断一个对象是垃圾

### 引用计数法

原理：当一个对象被创建（一般在堆区）时，同时创建一个引用计数器，当这个对象被引用了，则计数器加1，当引用失效之后，则计数器减1。当计数器为0时，则表示此对象为垃圾

问题：存在循环引用问题。当A引用B，B引用C，C又引用A的话，那么此3个对象永远不会被判断为垃圾

### 可达性算法

确定某些对象为根对象（GC Roots），从这些根对象出发，遍历找到和这些根对象有引用关系的对象，形成引用链，而不在这些引用链上的对象就被判定为垃圾。

如下图所示，两个浅蓝色的对象即是垃圾。

![](/home/yuhan/Documents/workspace/Roc/src/main/java/com/roc/jvm/可达性分析.png)

问题来了，哪些对象可以作为GC Roots？

1. JVM stack：在虚拟机栈中的对象
2. native method stack：本地方法栈中的对象
3. runtime constant pool：运行时常量池中的对象
4. static reference in method area：在方法区的静态变量

## 如何回收垃圾

### 标记清除算法

1. 根据可达性算法，将不可达对象标记为垃圾
2. 统一清除所有垃圾

优点：当需要回收的对象较少时，效率较高

缺点：产生大量内存碎片

![](/home/yuhan/Documents/workspace/Roc/src/main/java/com/roc/jvm/标记清除.png)

### 标记复制算法

1. 将内存区域分为两部分，对象只分配在其中一半区域
2. 根据可达性算法，将不可达对象标记为垃圾
3. 将所有存活对象复制到按序复制到另一半区域，当前区域全部清除

优点：解决出现大量内存碎片问题

缺点：一半的内存区域不可用，浪费内存

![](/home/yuhan/Documents/workspace/Roc/src/main/java/com/roc/jvm/复制.png)

### 标记整理算法

1. 根据可达性算法，将不可达对象标记为垃圾
2. 将所有存活对象整理复制到内存的一端

优点：无内存碎片问题，不造成内存浪费

缺点：在复制过程中效率较低

![](/home/yuhan/Documents/workspace/Roc/src/main/java/com/roc/jvm/标记整理.png)

## JVM GC分代算法

众所周知，JVM堆内存是分代管理的，如下。GC在针对不同区域则会采用不同的GC算法

![](/home/yuhan/Documents/workspace/Roc/src/main/java/com/roc/jvm/堆内存分代.png)

年轻代：存放大量朝生夕死的对象，采用标记复制算法，将eden区和survivor from区的垃圾进行标记，然后将存活对象全部复制到survivor to区。

老年代：存放生命周期长的对象，采用标记整理算法，每次GC将存活对象复制到内存的一端。

## GC的时机

简而言之：就是当内存不够时，就会发生GC

**Minor GC**

时机：Eden无法存放对象时

算法：标记复制算法

**Major GC（Full GC）**

时机：old区无法存放对象时

算法：标记整理算法

![](/home/yuhan/Documents/workspace/Roc/src/main/java/com/roc/jvm/对象在堆区创建过程.png)

## 垃圾收集器

垃圾收集器是区分年轻代和老年代的

![](/home/yuhan/Documents/workspace/Roc/src/main/java/com/roc/jvm/垃圾收集器.png)

### Serial

采用复制算法的年轻代收集器，单线程进行垃圾回收，回收过程中需要STW（stop the world）

配合使用的老年代收集器：CMS，Serial Old

优点：简单，高效，适用于单核环境下使用

缺点：无法利用现在机器都是多核的优势

### ParNew

Serial的多线程版本，除多线程进行垃圾回收以外，其余均和Serial一致

配合使用的老年代收集器：CMS，Serial Old

优点：利用了多核的功能（在单核环境下，Serial是比ParNew快的）

缺点：回收过程中需要STW，当需要回收的过多时，停顿时间会较长

### Parallel Scavenge

采用复制算法的年轻代收集器，多线程进行垃圾回收，与ParNew的区别在于它关注了系统的**吞吐量**

> 吞吐量 = 运行用户代码的时间 / （运行用户代码的时间 + GC的时间） 高吞吐量的应用适合后台执行的任务

此收集器有两个参数：

**-XX:MaxGCPauseMillis**：设置垃圾回收的最大停顿时间

**-XX:GCTimeRatio**：设置垃圾回收时间的占总时间的比率

设置-XX:MaxGCPauseMillis可以提高响应速度，设置-XX:GCTimeRatio可以提高吞吐量，提高CPU的利用效率

另外此收集器还有另一个参数：**-XX:UseAdaptiveSizePolicy**，开启的话，JVM会根据应用的运行状态，动态设置eden区，survivor区，old区之间的比例，以提供最合适的停顿时间或最大的吞吐量。

### Serial Old

Serial收集器的老年代版本，单线程，采用标记整理算法

### Parallel Old

Parallel Scavenge收集器的老年代版本，多线程，采用标记整理算法

### CMS（concurrent mark sweep）

以最短停顿时间为目的的收集器

1. 初始标记：需要STW，标记老年代所有根对象。GC roots直接引用的对象及年轻代中存活对象引用的对象
2. 并发标记：无需STW，标记所有存活对象。从上一阶段找到的根对象开始，进行可达性分析。（JVM会提前把老年代逻辑划分为大小相等的区域Card，因为应用程序并发执行，一些引用关系可能发生变化，，若引用关系发生改变，则将通过card marking将所在区域标记为脏区（dirty card））
3. 最终标记：STW，重新扫描对象，进行重新标记
4. 并发清除和重置：无需STW，清除所有垃圾，回收内存，重置CMS算法的内部数据，为下一次GC做准备

在java 9中开始被启用，在java 14中被删除

优点：降低了STW的时间，提高更好的响应速度

缺点：

1. 最大的问题是对老年代采用的是标记清除算法，会产生大量内存碎片
2. 对CPU资源敏感，因并发步骤较多，占用一部分线程，降低吞吐量
3. 无法处理浮动垃圾（并发清除时，可能会产生新的垃圾，而必须要等到下一次GC处理）

### G1

G1逻辑上还保存着年轻代，老年代的概念。实现上，会把内存区域按照固定大小分成若干块，叫做**region**。每一个region可以属于eden区，也可以属于old区，可动态变化。每次GC都会以region为单位进行回收。通过**-XX:MaxGCPauseMills**此参数，可设置每次STW最大停顿时间。eg. 当发生young gc时，G1根据最大停顿时间，尽量计算出可清理region的数量，可以不清除所有young区的region，进行GC，做到有效控制GC的停顿时间。

gc算法从整体上来说是标记整理，从单个region上来说是标记复制，会将单个region的存活对象复制到一个空的region上去。

### ZGC

1. 停顿时间不会超过10ms
2. 支持超大内存，达到4TB

故ZGC适用于大内存低延迟的服务

### Shennandoah

CMS有个很大的问题就是采用的是标记-清除算法，导致碎片化严重，思考一下为何CMS不采用标记整理算法呢？其原因是CMS希望降低STW的时间，那么如果用标记整理，那么在整理时，对象的引用地址会发生变化，此时用户线程是不能访问的，那么就得STW，把引用地址切换之后才能用。这样就会提高STW，所以采用的标记清除。

同理G1在标记复制的时候，也会有STW的过程。而Shennandoah在复制时，采用了读屏障和被称为“Brooks Pointers”的转发指针，避免掉STW过程。

## 垃圾收集器总结

关于垃圾收集器，我基本都是简述，如果对某个收集器感兴趣的话，可以做深入研究

| 收集器            | 线程                     | 新/老          | 算法          | 使用场景                   |
| ----------------- | ------------------------ | -------------- | ------------- | -------------------------- |
| Serial            | 单                       | 新生代         | 复制          | 单CPU                      |
| ParNew            | 多                       | 新生代         | 复制          | 多CPU                      |
| Parallel Scavenge | 多                       | 新生代         | 复制          | 多CPU，以吞吐量为优先      |
| Serial Old        | 单                       | 老年代         | 标记整理      | 单CPU                      |
| Parallel Old      | 多                       | 老年代         | 标记整理      | 多CPU，以吞吐量为优先      |
| CMS               | 多，可与用户线程同时执行 | 老年代         | 标记清除      | 多CPU，以减少STW为优先     |
| G1                | 多，可与用户线程同时执行 | 新生代，老年代 | 标记整理+复制 | 多CPU，更精准的控制STW时间 |

常用组合：
| 新生代            | 老年代       |
| ----------------- | ------------ |
| Serial            | Serial Old   |
| ParNew            | CMS          |
| Parallel Scavenge | Parallel Old |
| G1                | G1           |

