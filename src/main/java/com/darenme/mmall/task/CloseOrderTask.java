package com.darenme.mmall.task;

import com.darenme.mmall.common.Const;
import com.darenme.mmall.common.RedissonManager;
import com.darenme.mmall.service.IOrderService;
import com.darenme.mmall.util.PropertiesUtil;
import com.darenme.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by darenme
 * date: 2018/8/19
 * time: 19:08
 *
 * 这里说的redis分布式锁，其实就是redis里的一行记录，一个键值对
 */
@Component
@Slf4j
public class CloseOrderTask {

    @Autowired
    private IOrderService iOrderService;

    @Autowired
    private RedissonManager redissonManager;


    /**
     * 没有分布式锁，运行起来来看日志。
     */
//    @Scheduled(cron="0 */1 * * * ?")//每1分钟(每个1分钟的整数倍)
    public void closeOrderTaskV1(){
        log.info("定时关单任务启动");
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
//        iOrderService.closeOrder(hour);
        log.info("定时关单任务结束");
    }

    /**
     * 可能出现死锁，虽然在执行close的时候有防死锁，但是还是会出现，继续演进V3
     */
//    @Scheduled(cron="0 */1 * * * ?")//每1分钟(每个1分钟的整数倍)
    public void closeOrderTaskV2() throws InterruptedException {
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","50000"));//锁5秒有效期
        //这个时间如何用呢，看下面。和时间戳结合起来用。
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setnxResult != null && setnxResult.intValue() == 1) {
            //如果返回值是1，代表设置成功，获取锁
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            log.info("没有获得分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        }
    }

    // 把流程图写出来
    @Scheduled(cron="0 */1 * * * ?")
    public void closeOrderTaskV3() throws InterruptedException {
        long lockTimeout = Long.parseLong(PropertiesUtil.getProperty("lock.timeout","5000"));//锁5秒有效期
        Long setnxResult = RedisShardedPoolUtil.setnx(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK, String.valueOf(System.currentTimeMillis()+lockTimeout));
        if(setnxResult != null && setnxResult.intValue() == 1) {
            closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
        } else {
            //如果setnxResult==null 或 setnxResult.intValue() ==0 即 != 1的时候
            //未获取到锁，继续判断,判断时间戳,看是否可以重置获取到锁
            String lockValueStr = RedisShardedPoolUtil.get(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);

            //如果lockValue不是空,并且当前时间大于锁的有效期,说明之前的lock的时间已超时,执行getset命令.
            // getSet具有原子性
            if(lockValueStr != null && System.currentTimeMillis() > Long.parseLong(lockValueStr)){
                String getSetResult = RedisShardedPoolUtil.getSet(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,String.valueOf(System.currentTimeMillis()+lockTimeout));
                //再次用当前时间戳getset，
                //返回给定 key 的旧值。  ->旧值判断，是否可以获取锁
                // 当 key 没有旧值时，即 key 不存在时，返回 nil 。 ->获取锁
                //这里我们set了一个新的value值，获取旧的值。
                if(getSetResult == null || (getSetResult !=null && StringUtils.equals(lockValueStr,getSetResult))){
                    //获取到锁
                    closeOrder(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }else{
                    log.info("没有获得分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
                }
            }else{
                log.info("没有获得分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
            }
        }
    }

    /**
     * Redisson分布式锁实现
     * @throws InterruptedException
     */
//    @Scheduled(cron="0 */1 * * * ?")//每1分钟(每个1分钟的整数倍)
//    public void closeOrderTaskV4() throws InterruptedException {
//        RLock lock = redissonManager.getRedisson().getLock(Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
//        boolean getLock = false;
//        try {
//            if(getLock = lock.tryLock(0 ,50, TimeUnit.SECONDS)){//trylock增加锁
//                log.info("===获取{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
//                int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
////                iOrderService.closeOrder(hour);
//            }else{
//                log.info("===没有获得分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
//            }
//        } catch (InterruptedException e) {
//            log.error("Redisson分布式锁获取异常", e);
//        }
//        finally {
//            if(!getLock){
//                return;
//            }
//            log.info("===释放分布式锁:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK);
//            lock.unlock();
//        }
//    }

    private void closeOrder(String lockName){
//        expire命令用于给该锁设定一个过期时间，用于防止线程crash，导致锁一直有效，从而导致死锁。
        RedisShardedPoolUtil.expire(lockName,5);//有效期50秒,防死锁
        log.info("获取{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        int hour = Integer.parseInt(PropertiesUtil.getProperty("close.order.task.time.hour","2"));
        iOrderService.closeOrder(hour);
        RedisShardedPoolUtil.del(lockName);//释放锁
        log.info("释放{},ThreadName:{}",Const.REDIS_LOCK.CLOSE_ORDER_TASK_LOCK,Thread.currentThread().getName());
        log.info("=============================");
    }
}
