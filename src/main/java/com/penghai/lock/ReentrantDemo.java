package com.penghai.lock;

import org.mortbay.log.Log;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Fox
 * 可重入
 */
public class ReentrantDemo {

    public static ReentrantLock lock = new ReentrantLock();

    public static void main(String[] args) {
        method1();
    }


    public static void method1() {
        lock.lock();
        try {
            Log.debug("execute method1");
            method2();
        } finally {
            lock.unlock();
        }
    }
    public static void method2() {
        lock.lock();
        try {
            Log.debug("execute method2");
            method3();
        } finally {
            lock.unlock();
        }
    }
    public static void method3() {
        lock.lock();
        try {
            Log.debug("execute method3");
        } finally {
            lock.unlock();
        }
    }
}
