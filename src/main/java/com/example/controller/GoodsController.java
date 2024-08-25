package com.example.controller;

import com.example.controller.response.GoodsRes;
import com.example.pojo.User;
import com.example.service.GoodsService;
import com.example.service.UserService;
import com.example.vo.GoodsVo;
import com.example.vo.RespBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

//javax.servlet.http.HttpServletRequest
/**
 * ClassName: GoodsController
 * Package: com.example.controller
 * Description:商品
 *
 * @Author YUYU
 * @Create 2024/7/26 0:21
 * @Version 1.0
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {


    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemlate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @Autowired
    ApplicationContext applicationContext;


    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response){

        ValueOperations valueOperations = redisTemlate.opsForValue();
        String html = (String) valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        //session用于获取cookie以及用户信息   model用于跳转页面时，从上一个页面传到下一个页面的信息的传递  ticket cookie的值，我们可以通过注解@CookieValue 名为userTicket的拿到
//    if(StringUtils.isEmpty(ticket)){//如果ticket的值为空，则说明未登录
//        return "login";
//    }
////这里需修改成从redis获取User对象 信息
////    User user = (User)session.getAttribute(ticket);//通过Key ticket获取到session中存储的用户的信息
//    User user = userService.getUserByCookie(ticket, request,response);
//    if(null == user){
//        return "login";
//    }
        //如果都没问题的话  就把用户信息传到前端跳转页面去
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        // 修改后 Spring Boot3.0：
        JakartaServletWebApplication jakartaServletWebApplication = JakartaServletWebApplication.buildApplication(request.getServletContext());
        WebContext ctx = new WebContext(jakartaServletWebApplication.buildExchange(request, response), request.getLocale(), model.asMap());
        //手动渲染
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", ctx);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
//        return "goodsList";
        return html;
    }//cookie的写法


//    @RequestMapping(value = "/toDetail/{id}", produces = "text/html;charset=utf-8")
//    @ResponseBody
//    public String toDetail(Model model, User user, @PathVariable Long id, HttpServletRequest request, HttpServletResponse response){
//        ValueOperations valueOperations = redisTemlate.opsForValue();
//        String html = (String) valueOperations.get("goodsDetail:" + id);
//        if(!StringUtils.isEmpty(html)){
//            return html;
//        }
//
//        model.addAttribute("user", user);
//        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(id);
//        LocalDateTime startDate = goodsVo.getStartDate();
//        LocalDateTime endDate = goodsVo.getEndDate();
//        LocalDateTime nowDate = LocalDateTime.now();
//        int seckillStatus = 0;
//        long remainSeconds = 0;
//        System.out.println(nowDate);
//        System.out.println(startDate);
//        System.out.println(endDate);
//        if(nowDate.isBefore(startDate)){
//            remainSeconds = Duration.between( nowDate, startDate).getSeconds();
//        }
//        else if(nowDate.isAfter(endDate)){
//            seckillStatus = 2;
//            remainSeconds = -1;
//        }else{
//            seckillStatus = 1;
//            remainSeconds = 0;
//        }
//        model.addAttribute("remainSeconds", remainSeconds);
//        model.addAttribute("seckillStatus", seckillStatus);
//        model.addAttribute("goods", goodsVo);
//        JakartaServletWebApplication jakartaServletWebApplication = JakartaServletWebApplication.buildApplication(request.getServletContext());
//        WebContext ctx = new WebContext(jakartaServletWebApplication.buildExchange(request, response), request.getLocale(), model.asMap());
//        //手动渲染
//        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", ctx);
//        if(!StringUtils.isEmpty(html)){
//            valueOperations.set("goodsDetail:" + id, html, 60, TimeUnit.SECONDS);
//        }
////        return "goodsDetail";
//        return html;
//    }


    @RequestMapping("/detail/{id}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable Long id){

        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(id);
        LocalDateTime startDate = goodsVo.getStartDate();
        LocalDateTime endDate = goodsVo.getEndDate();
        LocalDateTime nowDate = LocalDateTime.now();
        int seckillStatus = 0;
        long remainSeconds = 0;
        System.out.println(nowDate);
        System.out.println(startDate);
        System.out.println(endDate);
        if(nowDate.isBefore(startDate)){
            remainSeconds = Duration.between( nowDate, startDate).getSeconds();
        }
        else if(nowDate.isAfter(endDate)){
            seckillStatus = 2;
            remainSeconds = -1;
        }else{
            seckillStatus = 1;
            remainSeconds = 0;
        }
        GoodsRes goodsRes = new GoodsRes(user, goodsVo, seckillStatus, remainSeconds);
        return RespBean.success(goodsRes);

    }

//    @RequestMapping("/goodsDetail/{id}")


}