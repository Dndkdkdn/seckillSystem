package com.example.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 
 * </p>
 *
 * @author yuyu
 * @since 2024-08-08
 */
@Getter
@Setter
@TableName("t_order")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 订单ID
     */
      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long goodsId;

    /**
     * 收货地址ID
     */
    private Long deliveryAddrId;

    /**
     * 冗余过来的商品名称
     */
    private String goodsName;

    /**
     * 商品数量
     */
    private Integer goodsCount;

    /**
     * 商品单价
     */
    private BigDecimal goodsPrice;

    /**
     * 1pc, 2android,3ios
     */
    private Byte orderChannel;

    /**
     * 订单状态，0新建未支付，1已支付，2已发货，3已收货，4己退款，5已完成
     */
    private Byte status;

    /**
     * 订单的创建时间
     */
    private LocalDateTime createDate;

    /**
     * 支付时间
     */
    private LocalDateTime payDate;


    public static Order init(Long userId, Long goodsId, Long deliveryAddrId, String goodsName, Integer goodsCount, BigDecimal goodsPrice, Byte orderChannel, Byte status){
        Order info = new Order();
        info.userId = userId;
        info.goodsId = goodsId;
        info.deliveryAddrId = deliveryAddrId;
        info.goodsName = goodsName;
        info.goodsCount = goodsCount;
        info.goodsPrice = goodsPrice;
        info.orderChannel = orderChannel;
        info.status = status;
        info.createDate = LocalDateTime.now();
        return info;
    }
}
