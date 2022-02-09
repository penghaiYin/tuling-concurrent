package com.penghai.jmm;

/**
 * @author Fox
 *
 * -XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -Xcomp
 * hsdis-amd64.dll
 *  可见性案例
 */
public class VisibilityTest {

    // lock前缀指令不是内存屏障的指令，但是有内存屏障的效果   缓存失效
    private boolean flag = true;  //可见性问题，线程B更改值，线程A不会跳出循环
    private int count = 0;
    //1、以下三种方式可以跳出循环
//    private volatile boolean flag = true; //volatitle关键字底层原理:  JVM层面 storeLoad内存屏障
//                                                    ----> (汇编层面指令)  lock();写操作写回主内存，会使其他cache中的副本失效
//    private volatile int count = 0;
//    private Integer count = 0;// 底层是使用final 关键字保证可见性，也会重新加载主内存

    public void refresh() {
        flag = false;
        System.out.println(Thread.currentThread().getName() + "修改flag:"+flag);
    }

    public void load() {
        System.out.println(Thread.currentThread().getName() + "开始执行.....");
        while (flag) {
            //TODO  业务逻辑
            count++;
//            UnsafeFactory.getUnsafe().storeFence();   // 2、内存屏障，可以跳出循环。底层也是调 storeLoad
//            Thread.yield();                           //3、能够跳出循环。释放时间片，上下文切换，切回时需要去主存加载
//            System.out.println(count);                //4、使用synchronized，底层还是使用内存屏障，底层调storeFence
//            LockSupport.unpark(Thread.currentThread());

            //shortWait(1000000); //5、会使本地内存缓存淘汰，重新去主内存读取

//            try {
//                Thread.sleep(1);   //内存屏障
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            //总结：  Java中可见性如何保证？ 方式归类有两种：
            //1.  jvm层面 storeLoad内存屏障    ===>  x86   lock替代了mfence
            // 2.  上下文切换   Thread.yield();


        }
        System.out.println(Thread.currentThread().getName() + "跳出循环: count=" + count);
    }

    public static void main(String[] args) throws InterruptedException {
        VisibilityTest test = new VisibilityTest();

        // 线程threadA模拟数据加载场景
        Thread threadA = new Thread(() -> test.load(), "threadA");
        threadA.start();

        // 让threadA执行一会儿
        Thread.sleep(1000);
        // 线程threadB通过flag控制threadA的执行时间
        Thread threadB = new Thread(() -> test.refresh(), "threadB");
        threadB.start();

    }


    public static void shortWait(long interval) {
        long start = System.nanoTime();
        long end;
        do {
            end = System.nanoTime();
        } while (start + interval >= end);
    }
}
