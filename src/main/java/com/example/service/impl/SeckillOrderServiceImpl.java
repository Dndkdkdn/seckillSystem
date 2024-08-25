package com.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.OrderMapper;
import com.example.mapper.SeckillGoodsMapper;
import com.example.mapper.SeckillOrderMapper;
import com.example.pojo.Order;
import com.example.pojo.SeckillGoods;
import com.example.pojo.SeckillOrder;
import com.example.pojo.User;
import com.example.service.SeckillOrderService;
import com.example.vo.GoodsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2024-08-08
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SeckillOrder> implements SeckillOrderService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Order add(User user, GoodsVo goods) {

        SeckillGoods seckillGoods = seckillGoodsMapper.selectById(goods.getId());
        if(seckillGoods.getStockCount() < 1){
            redisTemplate.opsForValue().set("SeckillResult:" + user.getId() + ":" + goods.getGoodsId(), true);//秒杀失败
            return null;
        }
//        int res = seckillGoodsMapper.updateById(seckillGoods);
        //防止库存超卖，做出调整
        UpdateWrapper<SeckillGoods> wrapper = new UpdateWrapper<SeckillGoods>().setSql(
                "stock_count = " + "stock_count - 1")
                .eq("id", goods.getId())
                .gt("stock_count", 0);
        int res = seckillGoodsMapper.update(wrapper);
        if(res < 1){
            redisTemplate.opsForValue().set("SeckillResult:" + user.getId() + ":" + goods.getGoodsId(), true);//秒杀失败
            return null;
        }
        Order order = Order.init(user.getId(), seckillGoods.getGoodsId(), 0L, goods.getGoodsName(), 1, seckillGoods.getSeckillPrice(), (byte) 1, (byte) 0);
        orderMapper.insert(order);

        SeckillOrder seckillOrder = SeckillOrder.init(user.getId(), order.getId(), seckillGoods.getGoodsId());
        seckillOrderMapper.insert(seckillOrder);

        redisTemplate.opsForValue().set("order:" + user.getId() + ":" + goods.getGoodsId(), seckillOrder);//解决库存超卖，将生成的秒杀订单在redis中进行缓存
        return order;
    }

    @Override
    public SeckillOrder getSeckillOrder(Long userId, Long goodsId) {
        LambdaQueryWrapper wrapper = new LambdaQueryWrapper<SeckillOrder>().eq(SeckillOrder::getUserId, userId).eq(SeckillOrder::getGoodsId, goodsId);
        SeckillOrder seckillOrder = seckillOrderMapper.selectOne(wrapper);
        return seckillOrder;
    }

    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = (SeckillOrder) redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            return seckillOrder.getOrderId();
        }else if(redisTemplate.hasKey("SeckillResult:" + user.getId() + ":" + goodsId)){
            if((Boolean) redisTemplate.opsForValue().get("SeckillResult:" + user.getId() + ":" + goodsId)){
                return -1L;
            }
        }
        return 0L;//正在排队
    }
}
