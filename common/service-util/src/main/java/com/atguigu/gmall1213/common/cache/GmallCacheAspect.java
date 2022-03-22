package com.atguigu.gmall1213.common.cache;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall1213.common.constant.RedisConst;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Component//它的作用就是实现bean的注入
@Aspect//面向切面工作
public class GmallCacheAspect {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    //编写一个环绕通知
    @Around("")
    public Object cacheAroundAdvice(ProceedingJoinPoint point) {

        //不同方法的返回值不同，所以用Object来作为返回值，后面会再确定返回值
        Object result = null;
        //获取传递到方法上的参数
        Object[] args = point.getArgs();

        //获取方法上的注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        GmallCache gmallCache = signature.getMethod().getAnnotation(GmallCache.class);
        //获取注解上的prefix
        String prefix = gmallCache.prefix();
        //定义一个key
        String key = prefix + Arrays.asList(args).toString();
        //根据key获取缓存中的数据
        result = cacheHit(signature, key);

        //判断是否获得了数据
        if (null != result) {
            return result;
        }
        //如果得到的数据是空，就去数据库查询，并放入缓存，自定义一个锁
        RLock lock = redissonClient.getLock(key + "lock");
        try {
            boolean res = lock.tryLock(RedisConst.SKULOCK_EXPIRE_PX1, RedisConst.SKULOCK_EXPIRE_PX1, TimeUnit.SECONDS);
            try {
            //如果返回true，则代表上锁成功
            if (res) {

                    result = point.proceed(point.getArgs());//表执行带有@GmallCache 的方法体
                    if (result == null) {
                        Object o = new Object();
                        redisTemplate.opsForValue().set(key, JSON.toJSONString(o), RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                        //返回数据
                        return o;
                    }
                    //查询出来不是空
                    redisTemplate.opsForValue().set(key, JSON.toJSONString(result), RedisConst.SKUKEY_TIMEOUT, TimeUnit.SECONDS);
                    //返回数据
                    return result;
                }else {
                //其他线程睡眠
                Thread.sleep(1000);
                //继续获取数据
                return cacheHit(signature, key);
            }
                } catch (Throwable throwable){
                    throwable.printStackTrace();
                }finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    //表示获取缓存中的数据
    private Object cacheHit(MethodSignature signature, String key) {
        String o = (String) redisTemplate.opsForValue().get(key);
        if (!StringUtils.isEmpty(o)) {
            //表示缓存中有数据
            Class returnType = signature.getReturnType();

            return JSON.parseObject(o);

        }

        return null;
    }
}
