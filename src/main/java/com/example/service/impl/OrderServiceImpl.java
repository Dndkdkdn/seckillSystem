package com.example.service.impl;

import com.example.exception.GlobalException;
import com.example.pojo.Order;
import com.example.mapper.OrderMapper;
import com.example.service.OrderService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author yuyu
 * @since 2024-08-08
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private OrderMapper orderMapper;



    @Override
    public Order getDetail(Long orderId) {
        if (null == orderId){
            throw new GlobalException(RespBeanEnum.ORDER_IS_NOT_EXIST);
        }
        return orderMapper.selectById(orderId);
    }
}
