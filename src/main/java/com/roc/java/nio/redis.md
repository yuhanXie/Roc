redis支持的数据结构：String，Hash，List，Set，SortedSet，Bitmap
（作用及底层数据结构）

防止缓存无限制膨胀，设置超时时间

定时清除超时缓存(可能无法一次清除所有过期key) ->
随机清除缓存(某些key逃脱) -> 在查询时发现过期清除(也没有被查询的超时key逃脱) 

内存不足时的策略：

| 策略名称        | key范围             | 策略                                  |
| --------------- | ------------------- | ------------------------------------- |
| noeviction      |                     | 不删除任何key                         |
| allkeys-lru     | all                 | LRU                                   |
| volatile-lru    | 设置了过期时间的key | LRU                                   |
| allkeys-random  | all                 | random                                |
| volatile-random | 设置了过期时间的key | random                                |
| volatile-ttl    | 设置了过期时间的key | 剩余时间最短的key(time to live)       |
| volatile-lfu    | 设置了过期时间的key | 使用频率最少的key(less frequent used) |
| allkeys-lfu     | all                 | 使用频率最少的key                     |

redis bloom filter

bloom fiter:使用一个超长的二进制数，若干hash算法。当一个key进来时，通过若干hash算法，将key转为若干正整数，或者说是二进制数上的位，将对应位设置位1.

当查询一个key是否存在时，也是通过若干hash算法，转为二进制数上的位，若对应位上不是1，说明该key一定不存在，若都是1，不一定存在。

hash冲突：不同的key，输出相同的数

用以海量数据查询，缓存穿透过滤

redis的bitmap可以实现bloom filter，解决缓存穿透问题

缓存击穿：某个热点key过期，导致大量请求直接查询数据库。

缓存雪崩：大量热点key同时过期，导致大量请求直接查询数据库

设置热点数据永不过期，设置key的过期时间随机
