mybatis默认有两级缓存
一级缓存是基于sqlSession
二级缓存是基于namespace（相当于mapper文件）
在分布式系统的情况下，mybatis缓存都存在可能出现脏数据的情况，所以一般情况下都不开启mybatis缓存。
```java     
            SqlSessionFactory object = sqlSessionFactoryBean.getObject();
            object.getConfiguration().setMapUnderscoreToCamelCase(true);
            //关闭二级缓存
            object.getConfiguration().setCacheEnabled(false);
            //一级缓存无法关闭，但是可以通过设置为STATEMENT，相当于关掉
            object.getConfiguration().setLocalCacheScope(LocalCacheScope.STATEMENT);
```
如果想使用二级缓存的话，可以使用mybatis-redis组件，将二级缓存使用redis缓存，而非内存缓存，可保证一致性。