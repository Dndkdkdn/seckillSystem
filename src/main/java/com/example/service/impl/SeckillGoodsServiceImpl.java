package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.controller.request.SeckillRequest;
import com.example.mapper.SeckillGoodsMapper;
import com.example.pojo.SeckillGoods;
import com.example.pojo.User;
import com.example.service.SeckillGoodsService;
import com.example.utils.MD5Util;
import com.example.utils.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2024-08-08
 */
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods> implements SeckillGoodsService {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;
    @Autowired
    private RedisTemplate redisTemplate;


    @Override
    public SeckillGoods querySekillGoodsById(Long id) {
        return seckillGoodsMapper.selectById(id);
    }

    @Override
    public String createSeckillPath(User user, SeckillRequest request) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "GoodsId:" + request.getGoodsId() + "SeckillGoodsId:" + request.getId());//生成接口地址随机值
        //接口地址生成之后需要存起来，因为在真正秒杀时还要对秒杀地址做校验
        //存入redis 并设置失效时间为 1 minute 失效后需要获取新的秒杀地址
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + request.getGoodsId(), str, 60, TimeUnit.SECONDS);
        return str;
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if(StringUtils.isEmpty(captcha) || Objects.isNull(user)){
            return false;
        }
        if(redisTemplate.hasKey("captcha:" + user.getId() + ":" + goodsId)){
            String verifyCaptcha = (String)redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
            return verifyCaptcha.equals(captcha);
        }
        return false;
    }
}
