package com.kevin.chap10.multiindex;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 类名: MultiTheadIndexTest<br/>
 * 包名：com.kevin.chap10<br/>
 * 作者：kevin[wangqi2017@xinhua.org]<br/>
 * 时间：2018/9/27 10:05<br/>
 * 版本：1.0<br/>
 * 描述：<br/>
 */
public class MultiTheadIndexTest {

    /**
     * 创建5个线程同时创建索引
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {
        int nThreads = 2;
        ExecutorService pool = Executors.newFixedThreadPool(nThreads);
        CountDownLatch latch1 = new CountDownLatch(1);
        CountDownLatch latch2 = new CountDownLatch(nThreads);
        for (int i = 0; i < nThreads; i++) {
            Runnable runnable = new IndexCreator("D:/doc" + (i + 1),
                    "D:/index" + (i + 1), latch1, latch2);
            pool.submit(runnable);
        }

        latch1.countDown();
        System.out.println("开始创建索引");
        latch2.await(); // 等待所有线程都完成
        System.out.println("所有线程都创建索引完毕");
        pool.shutdown();
    }
}
