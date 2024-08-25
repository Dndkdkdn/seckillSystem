package com.example.controller;

import com.example.pojo.User;
import com.example.vo.RespBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2024-07-22
 */
@RestController
@RequestMapping("/user")
public class UserController {


    /**
     * 用户信息（专门用来jmeter配置同一用户和不同用户测试用的）
     * @param user
     * @return
     */

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }
}
