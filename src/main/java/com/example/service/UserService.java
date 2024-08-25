package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.controller.request.LoginRequest;
import com.example.pojo.User;
import com.example.vo.RespBean;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuyu
 * @since 2024-07-22
 */
public interface UserService extends IService<User> {

    /**
     * 登录
     * @param request
     * @return
     */
    RespBean doLogin(LoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse response);


    /**
     * 根据cookie ticket从redis中获取用户
     * @param
     * @return
     */
    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);

    RespBean updateCode(String userTicket, String password, HttpServletRequest request, HttpServletResponse response);
}
