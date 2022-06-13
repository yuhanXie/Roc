作为一个Java开发人员，对事务的使用不应该只停留在方法上加上@Transactional注解就完事了，该如何正确的使用@Transactional。


```java
@AliasFor("transactionManager")
String value() default "";

@AliasFor("value")
String transactionManager() default "";

Propagation propagation() default Propagation.REQUIRED;

Isolation isolation() default Isolation.DEFAULT;
//事务的超时时间，防止死锁
int timeout() default TransactionDefinition.TIMEOUT_DEFAULT;
//事务是只有查询，无更新操作
boolean readOnly() default false;

Class<? extends Throwable>[] rollbackFor() default {};

String[] rollbackForClassName() default {};

String[] noRollbackForClassName() default {};
```

transactionManager： 事务管理器，用于事务的开始，提交，超时，回滚等操作，一般默认使用DataSourceTransationManager。

propagation：传播级别

```java
//如果当前没有事务，则新建一个事务；当前存在事务，则加入当前事务
REQUIRED(TransactionDefinition.PROPAGATION_REQUIRED),

//如果当前存在事务，则加入当前事务；若不存在事务，则不开启事务
SUPPORTS(TransactionDefinition.PROPAGATION_SUPPORTS),

//如果当前存在事务，则加入当前事务；如果当前不存在事务，则抛出异常
MANDATORY(TransactionDefinition.PROPAGATION_MANDATORY),

//无论当前是否存在事务，都新建一个事务
REQUIRES_NEW(TransactionDefinition.PROPAGATION_REQUIRES_NEW),

//无论当前是否存在事务，都以非事务的方式执行
NOT_SUPPORTED(TransactionDefinition.PROPAGATION_NOT_SUPPORTED),

//不使用事务，如果当前存在事务，则抛出异常
NEVER(TransactionDefinition.PROPAGATION_NEVER),

//如果当前存在事务，则创建一个子事务，加入到母事务中，如果母事务异常，则子事务与母事务一起回滚
//如果子事务异常，调用此事务方法没有catch异常，则一起回滚，如果catch了异常且未抛出，则仅子
//事务回滚;当前不存在事务，就开启一个事务
NESTED(TransactionDefinition.PROPAGATION_NESTED);
```

事务的失效场景

private
