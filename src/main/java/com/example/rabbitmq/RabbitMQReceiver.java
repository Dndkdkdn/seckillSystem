package com.example.rabbitmq;

import com.alibaba.fastjson.JSON;
import com.example.pojo.RabbitMQSeckillMessage;
import com.example.pojo.SeckillOrder;
import com.example.pojo.User;
import com.example.service.GoodsService;
import com.example.service.SeckillOrderService;
import com.example.vo.GoodsVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * ClassName: RabbitMQReceiver
 * Package: com.example.rabbitmq
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/21 0:04
 * @Version 1.0
 */
@Service
@Slf4j
public class RabbitMQReceiver {


    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private SeckillOrderService seckillOrderService;


    //发布订阅-topic主题路由消息模型  实现异步下单操作
    @RabbitListener(queues = "seckill.topic.queue")
    public void listenSeckillTopicQueue(String msg){
        log.info("从topic.queue接收消息" + msg);
        RabbitMQSeckillMessage message = JSON.parseObject(msg, RabbitMQSeckillMessage.class);
        Long id = message.getId();
        User user = message.getUser();
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(id);
        if(goods.getStockCount() < 1){
            redisTemplate.opsForValue().set("SeckillResult:" + user.getId() + ":" + goods.getGoodsId(), true);//秒杀失败
            return ;//库存不足
        }
        //为了防止并发情况下，同一用户两个线程由于并发操作导致生成了两个订单（重复抢购）
        //先判断是否重复抢购
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goods.getGoodsId());
        if(seckillOrder != null){
            redisTemplate.opsForValue().set("SeckillResult:" + user.getId() + ":" + goods.getGoodsId(), true);//秒杀失败
            return ;//重复抢购
        }
        //下单
        seckillOrderService.add(user, goods);
    }


//    //简单消息模型
//    @RabbitListener(queues = "queue")
//    public void receive(Object msg){
//        log.info("接收消息：" + msg);
//    }
//
//    //发布订阅-fanout广播消息模型
//    @RabbitListener(queues = "fanout.queue01")
//    public void listenFanoutQueue1(String msg){
//        log.info("从fanoutqueue01接收消息：" + msg);
//    }
//
//    @RabbitListener(queues = "fanout.queue02")
//    public void listenFanoutQueue2(String msg){
//        log.info("从fanoutqueue02接收消息：" + msg);
//    }
//
//    //发布订阅-direct直接路由消息模型
//    /**
//     * 基于@Bean的方式声明队列和交换机比较麻烦，Spring还提供了基于注解方式来声明。
//     *
//     * 在consumer的SpringRabbitListener中添加两个消费者，同时基于注解来声明队列和交换机：
//     *
//     */
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "direct.queue01"),
//            exchange = @Exchange(name = "direct.exchange", type = ExchangeTypes.DIRECT),
//            key = {"1","2"}
//    ))
//    public void listenDirectQueue1(String msg){
//        log.info("从direct.queue01接收消息：" + msg);
//    }
//
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "direct.queue02"),
//            exchange = @Exchange(name = "direct.exchange", type = ExchangeTypes.DIRECT),
//            key = {"1","3"}
//    ))
//    public void listenDirectQueue2(String msg){
//        log.info("从direct.queue02接收消息：" + msg);
//    }
//
//    //发布订阅-topic主题路由消息模型
//    @RabbitListener(queues = "topic.queue01")
//    public void listenTopicQueue1(String msg){
//        log.info("从topic.queue01接收消息" + msg);
//    }
//
//    @RabbitListener(queues = "topic.queue02")
//    public void listenTopicQueue2(String msg){
//        log.info("从topic.queue02接收消息" + msg);
//    }
//
//
//    //发布订阅-headers头部路由消息模型
//    @RabbitListener(queues = "headers.queue01")
//    public void listenHeadersQueue1(Message msg){
//        log.info("从headers.queue01接收消息" + msg);
//        log.info("从headers.queue01接收消息" + new String(msg.getBody()));
//    }
//
//    @RabbitListener(queues = "headers.queue02")
//    public void listenHeadersQueue2(Message msg){
//        log.info("从headers.queue02接收消息" + msg);
//        log.info("从headers.queue02接收消息" + new String(msg.getBody()));
//    }


}
