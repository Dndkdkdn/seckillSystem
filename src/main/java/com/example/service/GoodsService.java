package com.example.service;

import com.example.pojo.Goods;
import com.baomidou.mybatisplus.extension.service.IService;
import com.example.pojo.SeckillGoods;
import com.example.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuyu
 * @since 2024-08-08
 */
public interface GoodsService extends IService<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long id);


}
