package com.example.controller;

import com.example.controller.request.LoginRequest;
import com.example.service.UserService;
import com.example.vo.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * ClassName: LoginController
 * Package: com.example.controller
 * Description:
 *
 * @Author YUYU
 * @Create 2024/7/21 16:44
 * @Version 1.0
 */
//@RestController  //页面跳转不能用@RestController
@Controller
@RequestMapping("/login")
@Slf4j
//lombok生成日志
public class LoginController {


    @Autowired
    private UserService userService;
    /**
     * 跳转登录页面
     * @return
     */
    @RequestMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    /**
     * 登录功能
     * @param request
     * @return
     */
    @PostMapping("/doLogin")
    @ResponseBody()//@RequestBody注解必须得加，不然会返回 参数校验异常：手机号码格式错误
    public RespBean doLogin(@Valid @RequestBody LoginRequest request, HttpServletRequest httpServletRequest, HttpServletResponse response){
        log.info("{}", request);
//        // 获取session中的验证码
//        String sessionCode = (String) httpServletRequest.getSession().getAttribute("captcha");
//        // 判断验证码
//        if (Objects.isNull(request.getVerCode()) || !sessionCode.equals(request.getVerCode().trim().toLowerCase())) {
//            return RespBean.error(RespBeanEnum.VERIFY_CODE_ERROR);
//        }
        return userService.doLogin(request, httpServletRequest, response);
    }
}
