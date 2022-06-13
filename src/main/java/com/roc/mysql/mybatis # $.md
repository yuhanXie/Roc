## 一. #{}和${}的区别

动态sql是mybatis是强大特性，#{}，${}都支持传参。区别在于：

>#{}在动态sql解析阶段会被解析为一个占位符 ？，并且有类型校验，而变量的替换是在数据库中，
>
>${}在动态sql解析阶段就会以String字符串替换传入的参数，在数据库中执行时，变量已经被替换

举个例子：

```sql
//方法1
select * 
from user
where USER_ID = #{queryUser}
//方法2
select * 
from user
where USER_ID = ${queryUser}
```

使用#{}时，会对类型做校验，类型不符会报错，类型相符，假设是String的话，在查询时，会带上引号；而使用${}就是无脑替换，假设queryUser是11的话，上面那个sql语句就会转为以下两条语句：

```sql
//方法1
select * 
from user
where USER_ID = '11'
//方法2
select * 
from user
where USER_ID = 11
```

方法2在执行过程中一定会报错的，若使用${}的话，需要加上引号

```sql
//方法2
select * 
from user
where USER_ID = '${queryUser}'
```



## 二. #{}和${}的使用场景

### 能用#{}的情况下尽量用#{}

例如，我们写like语句时，我们有如下两种实现方式

```sql
### 方法1
select * 
from user
where USER_ID like CONCAT('%',#{queryUser},'%' )
### 方法2
select * 
from user
where USER_ID like '%${queryUser}%'               
```

我们都会推荐使用方法1，其核心原因是使用${}会导致**sql注入**的问题，假设queryUser传入的字段是`test'or 1 = 1 or USER_ID like '1`，那么我们把string替换进去之后看一下

```sql
select * 
from user
where USER_ID like '%test'or 1 = 1 or USER_ID like '1%'  
```

这一看就能看出问题来了，like语句是没有生效的，变成了全量查询用户表，查询还只是对数据库的压力一下子上来，如果是update的话，那问题就更大了。所以能用#{}，千万不要用${}。

### 使用${}的场景

使用#{}的话，在替换变量时，会加上''，而一些场景下，是不能加引号的：

1. order by，group by等传入列字段时，必须使用${}，否则会不生效；

2. 传入table name的时候 也只能使用${}

再次提醒：在使用${} 需要考虑sql注入的情况。

