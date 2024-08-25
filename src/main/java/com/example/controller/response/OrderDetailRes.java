package com.example.controller.response;

import com.example.pojo.Order;
import com.example.vo.GoodsVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: OrderDetailRes
 * Package: com.example.controller.response
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/19 22:31
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRes {

    private GoodsVo goods;
    private Order order;


    public static OrderDetailRes init(GoodsVo goods, Order order){
        OrderDetailRes info = new OrderDetailRes();
        info.goods = goods;
        info.order = order;
        return info;
    }
}
