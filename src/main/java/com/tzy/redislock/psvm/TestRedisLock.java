package com.tzy.redislock.psvm;

import com.tzy.redislock.util.RedisLockUtil;
import com.tzy.redislock.util.RedisPool;
import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TestRedisLock {
    public static void main(String[] args) throws Exception {
        Thread.sleep(5000);
        System.out.println("start");
        Thread t1 = new Thread(new MyThread());
        Thread t2 = new Thread(new MyThread());
        Thread t3 = new Thread(new MyThread());
        Thread t4 = new Thread(new MyThread());
        Thread t5 = new Thread(new MyThread());
        Thread t6 = new Thread(new MyThread());
        Thread t7 = new Thread(new MyThread());
        Thread t8 = new Thread(new MyThread());
        Thread t9 = new Thread(new MyThread());
        Thread t10 = new Thread(new MyThread());
        System.out.println("create end");
        Thread.sleep(5000);
        ExecutorService pool = new ThreadPoolExecutor(
                3,
                3,
                0,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
        );
        pool.execute(t1);
        pool.execute(t2);
        pool.execute(t3);
        pool.execute(t4);
        pool.execute(t5);
        pool.execute(t6);
        pool.execute(t7);
        pool.execute(t8);
        pool.execute(t9);
        pool.execute(t10);
        pool.shutdown();
//        t1.start();
//        t2.start();
//        t3.start();
//        t4.start();
//        t5.start();

//        int waitTime = 10000;
//        boolean flag = false;
//
//        Jedis conn = null;
//        conn = RedisPool.getJedis();
//        flag = RedisLockUtil.getLock(conn,"testLock","requestId1",waitTime);
//        System.out.println(flag);
//        flag = RedisLockUtil.getLock(conn,"testLock","requestId1",waitTime);
//        System.out.println(flag);

    }

    static class MyThread implements Runnable{
        Random random = new Random();

        public void run(){
            int waitTime = 10000;
            Jedis conn = null;
            boolean flag = false;
            boolean flag2 = false;
            try{
                conn = RedisPool.getJedis();
                do{
                    System.out.println(Thread.currentThread().getName()+"  尝试获取锁");
                    flag = RedisLockUtil.getLock(conn,"testLock","requestId1",5000);
                    if(flag==true){
                        System.out.println(Thread.currentThread().getName()+"  获取锁成功");
                    }
                    else {
                        //  System.out.println(Thread.currentThread().getName()+"  获取锁失败");
                        // int num = random.nextInt(10000);
                        // Thread.sleep(num);
                    }
                }while(!flag);
                System.out.println(Thread.currentThread().getName()+"  争取到锁 开始执行3秒");
                Thread.sleep(3000);
                flag2 = RedisLockUtil.releaseLock(conn,"testLock","requestId1");
                if(flag2){
                    System.out.println(Thread.currentThread().getName()+"  解锁成功");
                }
                else {
                    System.out.println(Thread.currentThread().getName()+"  解锁失败");
                }
            }catch(Exception e){
                e.printStackTrace();
            }finally{
                if(conn!=null)RedisPool.close(conn);
            }
        }

    }

}