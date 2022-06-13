package com.roc.spring.anonation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author xiegang
 */
@Aspect
public class Aop {
    
    
    @Around(value = "@annotation(com.roc.spring.anonation.MethodLog)")
    public Object methodLog(ProceedingJoinPoint joinPoint) {
        Object[] params = joinPoint.getArgs();
        StringBuilder stringBuilder = new StringBuilder();
        for (Object param : params) {
            stringBuilder.append(param.toString());
            stringBuilder.append(";");
        }
        System.out.println("入参：" + stringBuilder);
        Object object = null;
        try {
            object = joinPoint.proceed();
            System.out.println("出参：" + object.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return object;
    }
    
}
