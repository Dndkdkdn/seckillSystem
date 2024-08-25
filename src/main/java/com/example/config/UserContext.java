package com.example.config;

import com.example.pojo.User;

/**
 * ClassName: UserContext
 * Package: com.example.config
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/24 21:30
 * @Version 1.0
 */
public class UserContext {
    private static ThreadLocal<User> userHolder = new ThreadLocal<User>();

    /**
     * ThreadLocal 解决线程安全问题 每个线程绑定自己的值 秒杀场景为高并发多线程
     * 不能在线程中公共的存用户信息，会导致错误。
     * 每个线程里面都要存储自己的用户信息（私有数据）
     *访问ThreadLocal这个变量的话，他会获取到自己变量的本地副本
     */
    public static void setUser(User user){
        userHolder.set(user);
    }
    public static User getUser(){
        return userHolder.get();
    }
}
