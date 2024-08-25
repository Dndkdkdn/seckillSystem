package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.GoodsMapper;
import com.example.mapper.SeckillGoodsMapper;
import com.example.pojo.Goods;
import com.example.pojo.SeckillGoods;
import com.example.service.GoodsService;
import com.example.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2024-08-08
 */

/**
 * MySQL多表关联查询效率高点还是多次单表查询效率高，为什么？
 * https://www.cnblogs.com/goloving/p/14005404.html
 * 如果放到service层去做，最快的方式是，先查A表，得到一个小的结果集，一次 rpc，再根据结果集，拼凑出B表的查询条件，去B表查到一个结果集，再一次rpc，再把结果集拉回service层，再一次rpc，然后service层做合并
 */
@Service
public class GoodsServiceImpl extends ServiceImpl<GoodsMapper, Goods> implements GoodsService {

    @Autowired
    private GoodsMapper goodsMapper;
    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Override
    public List<GoodsVo> findGoodsVo() {
        LambdaQueryWrapper<SeckillGoods> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SeckillGoods::getStartDate);
        List<SeckillGoods> seckillGoods = seckillGoodsMapper.selectList(wrapper);
        List<GoodsVo> response = new ArrayList<>();
        for(SeckillGoods seckillGood : seckillGoods){
            Goods good = goodsMapper.selectById(seckillGood.getGoodsId());
            GoodsVo goodsVo = GoodsVo.init(seckillGood.getId(), seckillGood.getSeckillPrice(), seckillGood.getStockCount(),
                    seckillGood.getStartDate(), seckillGood.getEndDate(), seckillGood.getGoodsId(), good.getGoodsName(),
                    good.getGoodsTitle(), good.getGoodsImg(), good.getGoodsDetail(), good.getGoodsPrice(), good.getGoodsStock());
            response.add(goodsVo);
        }
        return response;
    }

    @Override
    public GoodsVo findGoodsVoByGoodsId(Long id) {
        SeckillGoods seckillGood = seckillGoodsMapper.selectById(id);
        Goods good = goodsMapper.selectById(seckillGood.getGoodsId());
        GoodsVo goodsVo = GoodsVo.init(seckillGood.getId(), seckillGood.getSeckillPrice(), seckillGood.getStockCount(),
                seckillGood.getStartDate(), seckillGood.getEndDate(), seckillGood.getGoodsId(), good.getGoodsName(),
                good.getGoodsTitle(), good.getGoodsImg(), good.getGoodsDetail(), good.getGoodsPrice(), good.getGoodsStock());
        return goodsVo;
    }
}
