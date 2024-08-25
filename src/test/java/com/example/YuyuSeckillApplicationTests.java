package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class YuyuSeckillApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private RedisScript<Boolean> redisScript;

    @Test
    public void textLock01() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1");//当当前设置的key存在的情况下是设置不成功的，只有当该key不存在才能设置成功

        //这样会存在多线程删除不属于自己的Key的情况

        if (isLock){
            valueOperations.set("name", "yuyu1");
            System.out.println((String) valueOperations.get("name"));
//            System.out.println(1/0);//模拟异常
            redisTemplate.delete("k1");
        }else{
            System.out.println("有其他线程正在使用，请稍等");
        }
    }


    @Test
    public void textLock02() {//当当前设置的key存在的情况下是设置不成功的，只有当该key不存在才能设置成功
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //配置超时时间 避免一直握锁 导致死锁
        //给锁添加一个过期时间，防止应用在运行过程中抛出异常导致锁无法正常释放
        Boolean isLock = valueOperations.setIfAbsent("k1", "v1", 5, TimeUnit.SECONDS);

        //这样会存在多线程删除不属于自己的Key的情况

        if (isLock){
            valueOperations.set("name", "yuyu1");
            System.out.println(1/0);//模拟异常
            System.out.println((String) valueOperations.get("name"));
            redisTemplate.delete("k1");
        }else{
            System.out.println("有其他线程正在使用，请稍等");
        }
    }

    @Test
    public void textLock03() {//当当前设置的key存在的情况下是设置不成功的，只有当该key不存在才能设置成功
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String value = UUID.randomUUID().toString();
        //配置超时时间 避免一直握锁 导致死锁
        Boolean isLock = valueOperations.setIfAbsent("k1",value, 120, TimeUnit.SECONDS);

        //这样会存在多线程删除不属于自己的Key的情况

        if (isLock){
            valueOperations.set("name", "yuyu1");
//            System.out.println(1/0);//模拟异常
            System.out.println((String) valueOperations.get("name"));
            Boolean res = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), value);
            System.out.println(res);
        }else{
            System.out.println("有其他线程正在使用，请稍等");
        }
    }

}
