package com.haizhi.graph.common.concurrent;

import com.haizhi.graph.common.concurrent.executor.AsyncJoinExecutor;
import org.junit.Test;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Created by chengmo on 2018/2/5.
 */
public class AsyncJoinExecutorTest {

    @Test
    public void actionGet() throws InterruptedException {
        AsyncJoinExecutor<String> joinExecutor = new AsyncJoinExecutor<>();
        Random rand = new Random();
        joinExecutor.join(() -> {
            try {
                Thread.sleep(10000 + rand.nextInt(2000));
                System.out.println("111");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "111";
        });

        joinExecutor.join("task2", () -> {
            try {
                Thread.sleep(10000 + rand.nextInt(2000));
                System.out.println("222");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "222";
        });

        joinExecutor.join(() -> {
            try {
                Thread.sleep(10000 + rand.nextInt(2000));
                System.out.println("333");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "333";
        });

        joinExecutor.join(() -> {
            try {
                Thread.sleep(10000 + rand.nextInt(2000));
                System.out.println("444");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "444";
        });

        joinExecutor.setTimeout(5);
        Map<String, String> results = joinExecutor.actionGet();
        System.out.println(results);

        CountDownLatch cdl = new CountDownLatch(1);
        cdl.await(5, TimeUnit.SECONDS);
    }
}
