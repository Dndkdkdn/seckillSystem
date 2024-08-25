package com.example.controller;

import com.example.controller.response.OrderDetailRes;
import com.example.pojo.Order;
import com.example.pojo.User;
import com.example.service.GoodsService;
import com.example.service.OrderService;
import com.example.vo.GoodsVo;
import com.example.vo.RespBean;
import com.example.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author yuyu
 * @since 2024-08-08
 */
@RestController
@RequestMapping("/order")
public class OrderController {


    @Autowired
    private OrderService orderService;
    @Autowired
    private GoodsService goodsService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean getOrderDetail(User user, Long orderId, Long id){
        if(null == user){
            return RespBean.error(RespBeanEnum.NOT_LOGIN_SESSION_ERROR);
        }
        //goods和order
        Order order = orderService.getDetail(orderId);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(id);
        OrderDetailRes response = OrderDetailRes.init(goods, order);
        return RespBean.success(response);
    }

}
