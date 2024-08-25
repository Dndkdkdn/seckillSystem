package com.example.service;

import com.example.controller.request.SeckillRequest;
import com.example.pojo.SeckillGoods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuyu
 * @since 2024-08-08
 */
public interface SeckillGoodsService extends IService<SeckillGoods> {

    SeckillGoods querySekillGoodsById(Long id);

    /**
     * 获取秒杀商品与用户相关的秒杀地址
     * @param user
     * @param request
     * @return
     */
    String createSeckillPath(User user, SeckillRequest request);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
