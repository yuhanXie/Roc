

作用

数据隔离，只有当前线程可以访问。

应用场景

全局session保存：前端把token放在header中上传，在拦截器中用threadLocal保存token中的用户信息，一方面可以**保证不同请求线程的用户信息隔离**；另一方面**在当前请求线程可以获取用户信息，无需参数传递**

实现原理
threadLocal对象本身不是线程隔离的

> 当调用threadLocal.set/get方法时，会先判断当前**Thread的私有变量threadLocals**是否为null，若为null，则创建新的ThreadLocalMap；
> set(Object)时，就是threadLocals.set,key是threadLocal.hashCode, value就Object
> get时，就是用threadLocal.hashCode取threadLocals中取值

因为threadLocals是Thread的私有变量，所以保证了线程隔离。ThreadLocalMap其实是个entry数组，entry包含了key(ThreadLocal)和Object(value)

```java
public class Thread implements Runnable {
    ThreadLocal.ThreadLocalMap threadLocals = null;
}

public class ThreadLocal<T> {
    
    static class ThreadLocalMap {
    }

    public void set(T value) {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null)
            map.set(this, value);
        else
            createMap(t, value);
    }

    public T get() {
        Thread t = Thread.currentThread();
        ThreadLocalMap map = getMap(t);
        if (map != null) {
            ThreadLocalMap.Entry e = map.getEntry(this);
            if (e != null) {
                @SuppressWarnings("unchecked")
                T result = (T)e.value;
                return result;
            }
        }
        return setInitialValue();
    }
    ThreadLocalMap getMap(Thread t) {
        return t.threadLocals;
    }
    
    void createMap(Thread t, T firstValue) {
        t.threadLocals = new ThreadLocalMap(this, firstValue);
    }
}

```

ThreadLocal为什么要用static修饰
非static修饰的话，每new一次，都会创建一个threadLocal对象，没有意义

ThreadLocal的异常情况
脏数据
当我们使用线程池复用线程时，第一次使用线程时，使用ThreadLocal存数据后，第二次复用该线程池时，ThreadLocal数据不会被清空，导致脏数据出现。

```java
    public void testDirtyData() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2, 2, 10,
                TimeUnit.HOURS, new LinkedBlockingDeque<>());
        Runnable runnable = () -> {
            String data = threadLocal2.get();
            System.out.println(Thread.currentThread().getName() + ":" + data);
            threadLocal2.set("set value");
        };
        for (int i = 0; i < 3; i++) {
            executor.execute(runnable);
        }
    }
```
```java
pool-1-thread-1:null
pool-1-thread-2:null
pool-1-thread-2:set value
```
我用线程池执行了3次任务，每次都是直接从threadLocal中取值，理论上来说应该3次都取不到值。
但是很明显，第三次是复用了thread-2，并且取到了值。因为我线程池最大线程数是2个，所以第3个任务一定会复用之前的线程，就会读到之前的脏数据。
解决办法：在使用完threadLocal后，记得调用threadLocal.remove();

ThreadLocal的内存泄漏

其实threadLocal发生内存泄露的条件很苛刻，

1. 不能用static修饰threadLocal，否则就一直有强引用
2. Thread长期不销毁，Thread销毁的话，threadLocals就会销毁
3. 

Entry中保存threadLocal是弱引用

InheritableThreadLocal
内存泄露
hash冲突页



SearchRequest{searchType=QUERY_THEN_FETCH, indices=[fund_company], indicesOptions=IndicesOptions[ignore_unavailable=false, allow_no_indices=true, expand_wildcards_open=true, expand_wildcards_closed=false, expand_wildcards_hidden=false, allow_aliases_to_multiple_indices=true, forbid_closed_indices=true, ignore_aliases=false, ignore_throttled=true], types=[], routing='null', preference='null', requestCache=null, scroll=null, maxConcurrentShardRequests=0, batchedReduceSize=512, preFilterShardSize=null, allowPartialSearchResults=null, localClusterAlias=null, getOrCreateAbsoluteStartMillis=-1, ccsMinimizeRoundtrips=true, source=



{"from":0,"size":10,"query":{"bool":{"must":[{"bool":{"should":[{"wildcard":{"partyShortNamePinyin":{"wildcard":"*QMSJJJ*","boost":1.0}}},{"wildcard":{"partyFullNamePinyin":{"wildcard":"*QMSJJJ*","boost":1.0}}}],"adjust_pure_negative":true,"boost":1.0}}],"adjust_pure_negative":true,"boost":1.0}},"track_total_hits":2147483647}}