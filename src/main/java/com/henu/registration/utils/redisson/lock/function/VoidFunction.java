package com.henu.registration.utils.redisson.lock.function;

/**
 * 分布式锁中所用到的函数式接口
 *
 * @author stephenqiu
 */
@FunctionalInterface
public interface VoidFunction {

    void method();

}