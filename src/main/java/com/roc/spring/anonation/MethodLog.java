package com.roc.spring.anonation;

import java.lang.annotation.*;

/**
 * @author xiegang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MethodLog {
}
