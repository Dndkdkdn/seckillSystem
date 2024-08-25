package com.example.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * ClassName: GoodsVo
 * Package: com.example.vo
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/9 15:58
 * @Version 1.0
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoodsVo {
    private Long id;
    private BigDecimal seckillPrice;
    private Integer stockCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Long goodsId;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private String goodsDetail;
    private BigDecimal goodsPrice;
    private Integer goodsStock;


    public static GoodsVo init(Long id, BigDecimal seckillPrice,Integer stockCount,LocalDateTime startDate,LocalDateTime endDate,Long goodsId, String goodsName,String goodsTitle,String goodsImg,String goodsDetail,BigDecimal goodsPrice,Integer goodsStock){
        GoodsVo info = new GoodsVo();
        info.id = id;
        info.seckillPrice = seckillPrice;
        info.stockCount = stockCount;
        info.startDate = startDate;
        info.endDate = endDate;
        info.goodsId = goodsId;
        info.goodsName = goodsName;
        info.goodsTitle = goodsTitle;
        info.goodsImg = goodsImg;
        info.goodsDetail = goodsDetail;
        info.goodsPrice = goodsPrice;
        info.goodsStock = goodsStock;
        return info;
    }

}
