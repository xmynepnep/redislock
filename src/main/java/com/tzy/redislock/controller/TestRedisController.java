package com.tzy.redislock.controller;

import com.tzy.redislock.service.RedisService;
import com.tzy.redislock.util.RedisLockUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpSession;
import java.util.UUID;
@RestController
public class TestRedisController {

    @Autowired
    RedisService redisService;

    @Autowired
    private JedisPool jedisPool;

    @RequestMapping(value="set")
    public String set(){
        String uuid = UUID.randomUUID().toString();
        redisService.set("randomSet",uuid);
        return uuid;
    }

    @RequestMapping(value="get")
    public String get(){
        String result = redisService.get("randomSet");
        return result;
    }

    @RequestMapping(value="testRedisLock")
    public String testRedisLock(HttpSession session){
        String sessionId = session.getId();
        String redisRequestId = "testApiLock";
        Jedis jedis = jedisPool.getResource();
        boolean result = RedisLockUtil.getLock(jedis,sessionId,redisRequestId,5000);
        if(result){
            Thread t = new Thread(new ReleseLockThread(jedis,sessionId,redisRequestId));
            t.start();
            return "获取锁成功，进程结束后将解锁";
        }

        else {
            return "已经被占用";
        }

    }

    class ReleseLockThread implements Runnable{
        private Jedis jedis ;
        private String key;
        private String redisRequestId;

        ReleseLockThread(Jedis jedis,String key,String redisRequestId){
            this.jedis = jedis;
            this.key = key;
            this.redisRequestId = redisRequestId;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean result = RedisLockUtil.releaseLock(jedis,key,redisRequestId);
            if(result){
                System.out.println("线程释放锁成功");
            }
            else {
                System.out.println("线程释放锁失败");
            }
        }
    }

}
