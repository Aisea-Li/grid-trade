package com.example.gridtrade.client;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;


/**
 * 限流AOP切面
 */
@Slf4j
@Aspect
@Component
public class FeignClientLimitAop {

    private static volatile long lastTime = 0L;

    private final static Lock LOCK = new ReentrantLock();

    private final static long MIN_INTERVAL = 100L;

    /**
     * 指定切点
     * 匹配 com.example.gridtrade.client包及其子包下的所有类的所有public方法
     */
    @Pointcut("@within(org.springframework.cloud.openfeign.FeignClient)")
    public void feignClient() {
    }

    /**
     * 环绕处理
     *
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("feignClient()")
    protected Object doLogProcess(ProceedingJoinPoint pjp) throws Throwable {
        LOCK.lock();
        long diff = System.currentTimeMillis() - lastTime;
        if (diff < MIN_INTERVAL) {
            LockSupport.parkNanos(TimeUnit.MILLISECONDS.toNanos(MIN_INTERVAL - diff));
        }
        lastTime = System.currentTimeMillis();
        try {
            Object response = pjp.proceed();
            return response;
        } catch (Exception e) {
            throw e;
        } finally {
            LOCK.unlock();
        }
    }


}
