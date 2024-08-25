package com.example.service;

import com.example.pojo.*;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.vo.GoodsVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuyu
 * @since 2024-08-08
 */
public interface SeckillOrderService extends IService<SeckillOrder> {

    Order add(User user, GoodsVo goods);
    SeckillOrder getSeckillOrder(Long userId, Long goodsId);

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return orderId:下单成功  -1:秒杀失败  0:排队中
     */
    Long getResult(User user, Long goodsId);
}
