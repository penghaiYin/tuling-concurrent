package com.penghai.lock;

import java.util.concurrent.locks.ReentrantLock;

import org.mortbay.log.Log;

/**
 * @author Fox
 * 可中断
 */
public class InterruptibleDemo {

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();

        Thread t1 = new Thread(() -> {

            Log.debug("t1启动...");

            try {
                lock.lockInterruptibly();
                try {
                    Log.debug("t1获得了锁");
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.debug("t1等锁的过程中被中断");
            }

        }, "t1");


        lock.lock();
        try {
            Log.debug("main线程获得了锁");
            t1.start();
            //先让线程t1执行
            Thread.sleep(1000);

            t1.interrupt();
            Log.debug("线程t1执行中断");
        } finally {
            lock.unlock();
        }

    }

}
