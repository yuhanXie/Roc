package com.roc.spring.anonation;

import java.lang.annotation.*;

/**
 * @author xiegang
 */
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
