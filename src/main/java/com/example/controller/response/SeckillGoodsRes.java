package com.example.controller.response;

import com.example.pojo.Order;
import com.example.vo.GoodsVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: SeckillGoodsRes
 * Package: com.example.controller.response
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/19 22:35
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillGoodsRes {
    private GoodsVo goods;
    private Order order;


    public static SeckillGoodsRes init(GoodsVo goods, Order order){
        SeckillGoodsRes info = new SeckillGoodsRes();
        info.goods = goods;
        info.order = order;
        return info;
    }
}
