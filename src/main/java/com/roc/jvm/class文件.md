magic number: CAFE BABE
minor version(小版本号): 00 00
major version(大版本好):00 34 (52, jdk 1.8就是52)
constant_pool_count(常量池的常量数量)：00 10(最大2^16 - 1) 因为常量是从1开始
constant_pool：若干字节
access_flags

整体结构

```java
ClassFile {
u4             magic; 
u2             minor_version;
u2             major_version;
u2             constant_pool_count;
cp_info        constant_pool[constant_pool_count-1];
u2             access_flags;
u2             this_class;
u2             super_class;
u2             interfaces_count;
u2             interfaces[interfaces_count];
u2             fields_count;
field_info     fields[fields_count];
u2             methods_count;
method_info    methods[methods_count];
u2             attributes_count;
attribute_info attributes[attributes_count];
}
```

备注：u表示字节数，u4表示4个字节，u2表示2个字节

使用工具
javap -v xx.class 
jclasslib


字节码介绍：
https://docs.oracle.com/javase/specs/jvms/se8/html/jvms-4.html
