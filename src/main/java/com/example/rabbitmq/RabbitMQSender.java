package com.example.rabbitmq;


import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassName: RabbitMQSender
 * Package: com.example.rabbitmq
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/21 0:01
 * @Version 1.0
 */
@Service
@Slf4j
public class RabbitMQSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void sendDoSeckill(String msg, String routingKey){
        log.info("发送消息：" + msg);
        rabbitTemplate.convertAndSend("seckill.topic.exchange", routingKey, msg);//发送消息到指定广播模式交换机
        //rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
    }

//    public void send(Object msg){
//        log.info("发送消息：" + msg);
//        rabbitTemplate.convertAndSend("queue", msg);//发送消息到指定队列
//    }
//    public void sendFanout(String msg){
//        log.info("发送消息：" + msg);
//        rabbitTemplate.convertAndSend("fanout.exchange", "", msg);//发送消息到指定广播模式交换机
//        //rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
//    }
//
//    public void sendDirect(String msg, String routingKey){
//        log.info("发送消息：" + msg);
//        rabbitTemplate.convertAndSend("direct.exchange", routingKey, msg);//发送消息到指定广播模式交换机
//        //rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
//    }
//
//    public void sendTopic(String msg, String routingKey){
//        log.info("发送消息：" + msg);
//        rabbitTemplate.convertAndSend("topic.exchange", routingKey, msg);//发送消息到指定广播模式交换机
//        //rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
//    }
//    public void sendHeadersTwo(String msg){
//        log.info("发送queue01&queue02消息：" + msg);
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setHeader("color", "red");
//        messageProperties.setHeader("size", "big");
//        Message message = new Message(msg.getBytes(), messageProperties);
//        rabbitTemplate.convertAndSend("headers.exchange", "", message);//发送消息到指定广播模式交换机
//        // rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
//    }
//
//    public void sendHeadersQueue01(String msg){
//        log.info("发送queue01消息：" + msg);
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setHeader("color", "red");
//        messageProperties.setHeader("size", "midle");
//        Message message = new Message(msg.getBytes(), messageProperties);
//        rabbitTemplate.convertAndSend("headers.exchange", "", message);//发送消息到指定广播模式交换机
//        //rabbitTemplate.convertAndSend("fanout.exchange", "", msg);三个参数exchange routingKey message
//    }



}
