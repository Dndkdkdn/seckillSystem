package com.example.config;

import com.example.pojo.User;
import com.example.service.UserService;
import com.example.utils.CookieUtil;
import com.example.vo.RespBean;
import com.example.vo.RespBeanEnum;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: AccessLimitInterceptor
 * Package: com.example.config
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/24 17:24
 * @Version 1.0
 */
@Component//当前拦截器对象由Spring创建和管理
@Slf4j
public class AccessLimitInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(handler instanceof HandlerMethod){
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod hm = (HandlerMethod) handler;
            AccessLimit accessLimit = hm.getMethodAnnotation(AccessLimit.class);
            if(Objects.isNull(accessLimit)){
                return true;
            }
            int second = accessLimit.second();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String uri = request.getRequestURI();//请求地址
            if(needLogin){
                if(Objects.isNull(user)){
                    render(response, RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
                    return false;
                }
                uri = user.getId() + ":" + uri;
            }
//            System.out.println(uri);
            ValueOperations valueOperations = redisTemplate.opsForValue();
            //限制访问次数，5S内最多访问5次
            Integer requestCount = (Integer)valueOperations.get(uri);
//            System.out.println(requestCount);
            if(Objects.isNull(requestCount)){
                valueOperations.set(uri, 1, second, TimeUnit.SECONDS);
            }else if(requestCount < maxCount){
                valueOperations.increment(uri);
            }else{
//                System.out.println("ok");
                render(response, RespBeanEnum.REQUEST_IS_FREQUENCY);
                return false;
            }
        }
        return true;
    }

    /**
     * 构建返回对象（JSON数据类型）
     * @param response
     * @param respBeanEnum
     */
    private void render(HttpServletResponse response, RespBeanEnum respBeanEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        out.write(new ObjectMapper().writeValueAsString(RespBean.error(respBeanEnum)));
        out.flush();
        out.close();
    }

    private User getUser(HttpServletRequest request, HttpServletResponse response){
        String ticket = CookieUtil.getCookieValue(request, "userTicket");
        if(StringUtils.isEmpty(ticket)){//如果ticket的值为空，则说明未登录
            return null;
        }
        return userService.getUserByCookie(ticket, request,response);
    }
}
