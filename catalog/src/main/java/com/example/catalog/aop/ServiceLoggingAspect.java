package com.example.catalog.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // outermost: also measures calls answered from the cache
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    // Pointcut: every method of every class in the service package
    @Pointcut("execution(* com.example.catalog.service..*(..))")
    public void serviceLayer() {
    }

    // Around advice: runs before and after the target method
    @Around("serviceLayer()")
    public Object logAndMeasure(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        log.debug("-> {} args={}", method, Arrays.toString(joinPoint.getArgs()));

        long start = System.nanoTime();
        try {
            Object result = joinPoint.proceed();
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.debug("<- {} finished in {} ms", method, elapsedMs);
            return result;
        } catch (Throwable ex) {
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;
            log.warn("!! {} failed after {} ms: {}: {}", method, elapsedMs,
                    ex.getClass().getSimpleName(), ex.getMessage());
            throw ex;
        }
    }
}