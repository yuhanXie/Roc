### 一. 基本概念

元注解（meta annotation）：用于修饰其他注解的注解

常用元注解：

@Documented：指示使用此注解的内容会生成到Java Doc中

@Retention：注解的生命周期

> SOURCE : 只存在Java源文件，不会被编译
>
> CLASS：会被编译到字节码文件，但不会运行（默认值）
>
> RUNTIME：运行期间，可在运行期间通过反射获取此注解

```java
public enum RetentionPolicy {
    /**
     * Annotations are to be discarded by the compiler.
     */
    SOURCE,

    /**
     * Annotations are to be recorded in the class file by the compiler
     * but need not be retained by the VM at run time.  This is the default
     * behavior.
     */
    CLASS,

    /**
     * Annotations are to be recorded in the class file by the compiler and
     * retained by the VM at run time, so they may be read reflectively.
     *
     * @see java.lang.reflect.AnnotatedElement
     */
    RUNTIME
}

```



@Target：限定注解能被应用在那些Java类型上

> TYPE：类，接口，枚举
>
> FIELD：变量
>
> METHOD：方法
>
> PARAMETER：方法的入参
>
> CONSTRUCTOR：构造器
>
> LOCAL_VARIABLE：局部变量
>
> ANNOTATION_TYPE：注解
>
> PACKAGE：包（没见过）
>
> TYPE_PARAMETER：类型参数，表示这个注解可以用在 Type的声明式前,jdk1.8引入。
>
> TYPE_USE：类型的注解，表示这个注解可以用在所有使用Type的地方（如：泛型，类型转换等），jdk1.8引入。

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Target {
    /**
     * Returns an array of the kinds of elements an annotation type
     * can be applied to.
     * @return an array of the kinds of elements an annotation type
     * can be applied to
     */
    ElementType[] value();
}

public enum ElementType {
    /** Class, interface (including annotation type), or enum declaration */
    TYPE,

    /** Field declaration (includes enum constants) */
    FIELD,

    /** Method declaration */
    METHOD,

    /** Formal parameter declaration */
    PARAMETER,

    /** Constructor declaration */
    CONSTRUCTOR,

    /** Local variable declaration */
    LOCAL_VARIABLE,

    /** Annotation type declaration */
    ANNOTATION_TYPE,

    /** Package declaration */
    PACKAGE,

    /**
     * Type parameter declaration
     *
     * @since 1.8
     */
    TYPE_PARAMETER,

    /**
     * Use of a type
     *
     * @since 1.8
     */
    TYPE_USE
}
```

@Inherited：子类是否可继承此注解

@Repeatable：定义此注解是否可以在一处重复定义

### 二. 测试用例

```java
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validate {

    /**
     * 最大值
     *
     * @return
     */
    int max();

    /**
     * 最小值
     *
     * @return
     */
    int min();
}

public class User {


    @Validate(min = 1, max = 50)
    public int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
```

```java
    public static void main(String[] args) {
        User user = new User();
        user.setAge(-5);
        check(user);
        user.setAge(15);
        check(user);
    }

    private static void check(User user)  {
        Class c = user.getClass();
        for (Field field : c.getDeclaredFields()) {
            if (field.isAnnotationPresent(Validate.class)) {
                //反射
                Validate validateVal = field.getAnnotation(Validate.class);
                field.setAccessible(true);
                int age = 0;
                try {
                    age = field.getInt(user);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if (age < validateVal.min() || age > validateVal.max()) {
                    System.out.println("error: age " + age + " is not correct");
                } else {
                    System.out.println("success: age is correct");
                }
            }
        }
    }
//测试结果：
//error: age -5 is not correct
//success: age is correct
```

### 三. 字节码分析

```java
Classfile /home/yuhan/Documents/workspace/Roc/target/classes/com/roc/spring/anonation/Validate.class
  Last modified Nov 5, 2021; size 467 bytes
  MD5 checksum 1e4af246079397127c737737ea76add1
  Compiled from "Validate.java"
// 注解就是一个继承了Annotation的接口类
public interface com.roc.spring.anonation.Validate extends java.lang.annotation.Annotation
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_INTERFACE, ACC_ABSTRACT, ACC_ANNOTATION
Constant pool:
   #1 = Class              #18            // com/roc/spring/anonation/Validate
   #2 = Class              #19            // java/lang/Object
   #3 = Class              #20            // java/lang/annotation/Annotation
   #4 = Utf8               max
   #5 = Utf8               ()I
   #6 = Utf8               min
   #7 = Utf8               SourceFile
   #8 = Utf8               Validate.java
   #9 = Utf8               RuntimeVisibleAnnotations
  #10 = Utf8               Ljava/lang/annotation/Target;
  #11 = Utf8               value
  #12 = Utf8               Ljava/lang/annotation/ElementType;
  #13 = Utf8               FIELD
  #14 = Utf8               Ljava/lang/annotation/Retention;
  #15 = Utf8               Ljava/lang/annotation/RetentionPolicy;
  #16 = Utf8               RUNTIME
  #17 = Utf8               Ljava/lang/annotation/Documented;
  #18 = Utf8               com/roc/spring/anonation/Validate
  #19 = Utf8               java/lang/Object
  #20 = Utf8               java/lang/annotation/Annotation
{
  public abstract int max();
    descriptor: ()I
    flags: ACC_PUBLIC, ACC_ABSTRACT

  public abstract int min();
    descriptor: ()I
    flags: ACC_PUBLIC, ACC_ABSTRACT
}
SourceFile: "Validate.java"
RuntimeVisibleAnnotations:
  // 常量池里找10，Target
  0: #10(#11=[e#12.#13])
  // 14:Retention   
  1: #14(#11=e#15.#16)
  //17:Documented     
  2: #17()

```

注解就是一个继承了Annotation的接口类，内部方法就和接口一致

RuntimeVisibleAnnotations：就是展示它带有的元注解列表

```java
public class com.roc.spring.anonation.User
  minor version: 0
  major version: 52
  flags: ACC_PUBLIC, ACC_SUPER
Constant pool:
   #1 = Methodref          #4.#26         // java/lang/Object."<init>":()V
   #2 = Fieldref           #3.#27         // com/roc/spring/anonation/User.age:I
   #3 = Class              #28            // com/roc/spring/anonation/User
   #4 = Class              #29            // java/lang/Object
   #5 = Utf8               age
   #6 = Utf8               I
   #7 = Utf8               RuntimeVisibleAnnotations
   #8 = Utf8               Lcom/roc/spring/anonation/Validate;
   #9 = Utf8               min
  #10 = Integer            1
  #11 = Utf8               max
  #12 = Integer            50
  #13 = Utf8               <init>
  #14 = Utf8               ()V
  #15 = Utf8               Code
  #16 = Utf8               LineNumberTable
  #17 = Utf8               LocalVariableTable
  #18 = Utf8               this
  #19 = Utf8               Lcom/roc/spring/anonation/User;
  #20 = Utf8               getAge
  #21 = Utf8               ()I
  #22 = Utf8               setAge
  #23 = Utf8               (I)V
  #24 = Utf8               SourceFile
  #25 = Utf8               User.java
  #26 = NameAndType        #13:#14        // "<init>":()V
  #27 = NameAndType        #5:#6          // age:I
  #28 = Utf8               com/roc/spring/anonation/User
  #29 = Utf8               java/lang/Object
{
  public int age;
    descriptor: I
    flags: ACC_PUBLIC
    RuntimeVisibleAnnotations:
      0: #8(#9=I#10,#11=I#12)

```

使用了validate注解的age，带了RuntimeVisibleAnnotations，#8(#9=I#10,#11=I#12)

#8：注解类，Lcom/roc/spring/anonation/Validate

#9：注解的min方法，#10表示设置的是10

#11：注解的max方法，#12表示设置的是50