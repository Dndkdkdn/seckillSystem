package com.example.service.impl;

import com.example.exception.GlobalException;
import com.example.pojo.User;
import com.example.mapper.UserMapper;
import com.example.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.utils.*;
import com.example.vo.RespBean;
import com.example.vo.RespBeanEnum;
import com.example.controller.request.LoginRequest;;


import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2024-07-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    /**
     * 登录
     * @param request
     * @return
     */
    @Override
    public RespBean doLogin(LoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        String mobile = request.getMobile();
        System.out.println(mobile);
        String password = request.getPassword();
        //参数校验  自定义了参数校验注解以及validation组件自带的注解之后就不需要了
//        if(StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        if(!ValidatorUtil.isMobile(mobile)) return RespBean.error(RespBeanEnum.MOBILE_ERROR);
        User user = userMapper.selectById(mobile);
        if(Objects.isNull(user)) {
//            return RespBean.error(RespBeanEnum.USER_NOT_EXIST);//在定义全局异常之前直接返回错误信息
            //但是定义了异常之后，可以选择抛出异常
            throw new GlobalException(RespBeanEnum.USER_NOT_EXIST);
        }
        //判断密码是否正确
        if(!user.getPassword().equals(MD5Util.fromPassToDBPass(password, user.getSalt()))) {
//            return RespBean.error(RespBeanEnum.USER_OR_PASSWORD_ERROR);//在定义全局异常之前直接返回错误信息
            //但是定义了异常之后，可以选择抛出异常
            throw new GlobalException(RespBeanEnum.USER_OR_PASSWORD_ERROR);
        }
        //登录成功，创建对应的cookie
        // cookie的写法为
        String ticket = UUIDUtil.uuid();//生成cookie
//        多两个参数 HttpServletRequest request, HttpServletResponse response
//        httpServletRequest.getSession().setAttribute(ticket, user);//设置cookie 键为生成的ticket 值为user对象
        redisTemplate.opsForValue().set("user:" + ticket, user);//操作string数据类型
        CookieUtil.setCookie(httpServletRequest, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }
    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicket)){
            return null;
        }
        User user = (User)redisTemplate.opsForValue().get("user:" + userTicket);
        if(!Objects.isNull(user)){
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }

    @Override
    public RespBean updateCode(String userTicket, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(userTicket, request, response);
        if(null == user){
            throw new GlobalException(RespBeanEnum.MOBILE_ERROR_UPDATE_CODE_FAILE);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int res = userMapper.updateById(user);
        if(res > 0){
            redisTemplate.delete("user:" + userTicket);
            return  RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }


}
