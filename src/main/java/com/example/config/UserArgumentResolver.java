package com.example.config;

import com.example.pojo.User;
import com.example.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;


/**
 * ClassName: UserArgumentResolver
 * Package: com.example.config
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/6 14:46
 * @Version 1.0
 */

@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        Class<?> clazz = parameter.getParameterType();
        return clazz == User.class;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return UserContext.getUser();
//        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
//        HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
//        String ticket = CookieUtil.getCookieValue(request, "userTicket");
////        System.out.println(ticket);
//        //session用于获取cookie以及用户信息   model用于跳转页面时，从上一个页面传到下一个页面的信息的传递  ticket cookie的值，我们可以通过注解@CookieValue 名为userTicket的拿到
//        if(StringUtils.isEmpty(ticket)){//如果ticket的值为空，则说明未登录
//            return null;
//        }
//    //这里需修改成从redis获取User对象 信息
//    //    User user = (User)session.getAttribute(ticket);//通过Key ticket获取到session中存储的用户的信息
//        return userService.getUserByCookie(ticket, request,response);
    }
}
