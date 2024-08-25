package com.example.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * ClassName: RabbitMQConfig
 * Package: com.example.config
 * Description: RabbitMQ配置类 其主要的作用是用来准备队列
 *  RabbitMQ是典型的生产者和消费者的模型
 * @Author YUYU
 * @Create 2024/8/20 23:54
 * @Version 1.0
 */
@Configuration
public class RabbitMQConfig {

    //topic
    private static final String SECKILLTOPICQUEUE = "seckill.topic.queue";
    private static final String SECKILLTOPICEXCHANGE = "seckill.topic.exchange";
    private static final String SECKILLTOPICROUTINGKEY = "doSeckill.#";



    //topic

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(SECKILLTOPICEXCHANGE);
    }

    @Bean
    public Queue seckillTopicQueue(){
        return new Queue(SECKILLTOPICQUEUE, true);
    }

    /**
     * 绑定队列和交换机
     */
    @Bean
    public Binding bindingTopicQueue(Queue seckillTopicQueue, TopicExchange topicExchange){
        return BindingBuilder.bind(seckillTopicQueue).to(topicExchange).with(SECKILLTOPICROUTINGKEY);
    }




//    //fanout
//    private static final String FANOUTQUEUE01 = "fanout.queue01";
//    private static final String FANOUTQUEUE02 = "fanout.queue02";
//    private static final String FANOUTEXCHANGE = "fanout.exchange";
//
//
//    //topic
//    private static final String TOPICQUEUE01 = "topic.queue01";
//    private static final String TOPICQUEUE02 = "topic.queue02";
//    private static final String TOPICEXCHANGE = "topic.exchange";
//    private static final String TOPICROUTINGKEY01 = "china.#";
//    private static final String TOPICROUTINGKEY02 = "*.news";
//
//    //headers
//    private static final String HEADERSQUEUE01 = "headers.queue01";
//    private static final String HEADERSQUEUE02 = "headers.queue02";
//    private static final String HEADERSEXCHANGE = "headers.exchange";
////    private static final String HEADERSROUTINGKEY01 = "china.#";
////    private static final String HEADERSROUTINGKEY02 = "*.news";



    //headers

//    @Bean
//    public HeadersExchange headersExchange(){
//        return new HeadersExchange(HEADERSEXCHANGE);
//    }
//
//    @Bean
//    public Queue headersQueue1(){
//        return new Queue(HEADERSQUEUE01, true);
//    }
//
//
//    /**
//     * 绑定队列和交换机
//     */
//    /**
//     * 另一种写法
//     * @Bean
//     * public Binding binding01(){
//     *     return BindingBuilder.bind(queue01()).to(fanoutExchange());
//     * }
//     */
//    @Bean
//    public Binding bindingHeadersQueue1(Queue headersQueue1, HeadersExchange headersExchange){
//        Map<String, Object> map = new HashMap<>();
//        map.put("color", "red");
//        map.put("size", "small");
//        return BindingBuilder.bind(headersQueue1).to(headersExchange).whereAny(map).match();
//    }
//
//
//
//    @Bean
//    public Queue headersQueue2(){//如果这个函数名字不叫fanoutQueue2，则bindingQueue2中参数编译报错：could not autowire,There is more than one bean of 'Queue' type.
//        return new Queue(HEADERSQUEUE02, true);
//    }
//    /**
//     * 绑定队列和交换机
//     */
//    /**
//     * 另一种写法
//     * @Bean
//     * public Binding binding02(){
//     *     return BindingBuilder.bind(queue02()).to(fanoutExchange());
//     * }
//     */
//    @Bean
//    public Binding bindingHeadersQueue2(Queue headersQueue2, HeadersExchange headersExchange){
//        Map<String, Object> map = new HashMap<>();
//        map.put("color", "red");
//        map.put("size", "big");
//        return BindingBuilder.bind(headersQueue2).to(headersExchange).whereAll(map).match();
//    }

//    //topic
//
//    @Bean
//    public TopicExchange topicExchange(){
//        return new TopicExchange(TOPICEXCHANGE);
//    }
//
//    @Bean
//    public Queue topicQueue1(){
//        return new Queue(TOPICQUEUE01, true);
//    }
//
//
//    /**
//     * 绑定队列和交换机
//     */
//    /**
//     * 另一种写法
//     * @Bean
//     * public Binding binding01(){
//     *     return BindingBuilder.bind(queue01()).to(fanoutExchange());
//     * }
//     */
//    @Bean
//    public Binding bindingTopicQueue1(Queue topicQueue1, TopicExchange topicExchange){
//        return BindingBuilder.bind(topicQueue1).to(topicExchange).with(TOPICROUTINGKEY01);
//    }
//
//    @Bean
//    public Queue topicQueue2(){//如果这个函数名字不叫fanoutQueue2，则bindingQueue2中参数编译报错：could not autowire,There is more than one bean of 'Queue' type.
//        return new Queue(TOPICQUEUE02, true);
//    }
//    /**
//     * 绑定队列和交换机
//     */
//    /**
//     * 另一种写法
//     * @Bean
//     * public Binding binding02(){
//     *     return BindingBuilder.bind(queue02()).to(fanoutExchange());
//     * }
//     */
//    @Bean
//    public Binding bindingTopicQueue2(Queue topicQueue2, TopicExchange topicExchange){
//        return BindingBuilder.bind(topicQueue2).to(topicExchange).with(TOPICROUTINGKEY02);
//    }


//    //fanout
//    @Bean
//    public FanoutExchange fanoutExchange(){
//        return new FanoutExchange(FANOUTEXCHANGE);
//    }
//
//    @Bean
//    public Queue fanoutQueue1(){
//        return new Queue(FANOUTQUEUE01, true);
//    }
//
//
//    /**
//     * 绑定队列和交换机
//     */
//    /**
//     * 另一种写法
//     * @Bean
//     * public Binding binding01(){
//     *     return BindingBuilder.bind(queue01()).to(fanoutExchange());
//     * }
//     */
//    @Bean
//    public Binding bindingFanoutQueue1(Queue fanoutQueue1, FanoutExchange fanoutExchange){
//        return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
//    }
//
//    @Bean
//    public Queue fanoutQueue2(){//如果这个函数名字不叫fanoutQueue2，则bindingQueue2中参数编译报错：could not autowire,There is more than one bean of 'Queue' type.
//        return new Queue(FANOUTQUEUE02, true);
//    }
//    /**
//     * 绑定队列和交换机
//     */
//    /**
//     * 另一种写法
//     * @Bean
//     * public Binding binding02(){
//     *     return BindingBuilder.bind(queue02()).to(fanoutExchange());
//     * }
//     */
//    @Bean
//    public Binding bindingFanoutQueue2(Queue fanoutQueue2, FanoutExchange fanoutExchange){
//        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
//    }
//
//
//
//
//    @Bean
//    public Queue queue(){
//        return new Queue("queue", true); //队列配置为持久化
//    }
}
