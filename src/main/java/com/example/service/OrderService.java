package com.example.service;

import com.example.pojo.Order;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author yuyu
 * @since 2024-08-08
 */
public interface OrderService extends IService<Order> {

    Order getDetail(Long orderId);
}
