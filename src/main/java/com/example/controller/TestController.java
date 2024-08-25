package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * ClassName: TestController
 * Package: com.example.controller
 * Description:测试项目创建是否成功
 *
 * @Author YUYU
 * @Create 2024/7/17 16:27
 * @Version 1.0
 */

/**
 * 使用@RestController和@RequestMapping：@RestController是@Controller和@ResponseBody的组合注解，主要用于返回JSON或XML等内容，而不是视图页面。
 * 返回视图的方式：在Spring Boot中，如果你想返回一个视图页面而不是JSON，需要使用@Controller而不是@RestController。
 */

/**
 * 将@RestController改为@Controller后，Spring Boot会将返回的字符串视为视图名，
 * 而不是返回JSON。在访问http://localhost:8080/test/hello时，
 * Spring Boot会找到名为hello的模板并渲染它，同时将name属性传递给视图，
 * 从而在页面上显示hello yuyu。
 */
//@RestController
@Controller
@RequestMapping("/test")
public class TestController {

//    @Autowired
//    private RabbitMQSender rabbitMQSender;
//
//
//    /**
//     * 测试页面跳转
//     * @param model
//     * @return
//     */
//    @RequestMapping("/hello")
//    public String hello(Model model){
//        model.addAttribute("name", "yuyu");
//        return "hello";
//    }
//
//
//    /**
//     * 测试RabbitMQ----发送消息
//     */
//    @RequestMapping("/mq")
//    @ResponseBody
//    public void mq(){
//        rabbitMQSender.send("Hello RabbitMQ!");
//    }
//
//    /**
//     * 测试RabbitMQ----Fanout发送消息
//     */
//    @RequestMapping("/fanout")
//    @ResponseBody
//    public void fanoutTest(){
//        rabbitMQSender.sendFanout("Hello RabbitMQ!");
//    }
//
//    /**
//     * 测试RabbitMQ----direct发送消息
//     */
//    @RequestMapping("/direct")
//    @ResponseBody
//    public void directTest(){
//        rabbitMQSender.sendDirect("Hello RabbitMQ!", "1");
//    }
//
//
//    /**
//     * 测试RabbitMQ----topic发送消息
//     */
//    @RequestMapping("/topic")
//    @ResponseBody
//    public void topicTest(){
//        rabbitMQSender.sendTopic("Hello RabbitMQ china.news!", "china.news");//queue01&queue02
//        rabbitMQSender.sendTopic("Hello RabbitMQ china.news.queue1!", "china.news.queue1");//queue01
//        rabbitMQSender.sendTopic("Hello RabbitMQ queue2.news!", "queue2.news");//queue02
//        rabbitMQSender.sendTopic("Hello RabbitMQ abandon.queue2.news!", "abandon.queue2.news");//丢弃
//    }
//
//
//    /**
//     * 测试RabbitMQ----headers发送消息
//     */
//    @RequestMapping("/headers")
//    @ResponseBody
//    public void headersTest(){
//        rabbitMQSender.sendHeadersTwo("Hello RabbitMQ queue01&02!");//queue01&queue02
//        rabbitMQSender.sendHeadersQueue01("Hello RabbitMQ queue01!");//queue01
//
//    }
}
