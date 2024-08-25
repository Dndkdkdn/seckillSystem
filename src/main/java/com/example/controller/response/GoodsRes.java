package com.example.controller.response;

import com.example.pojo.User;
import com.example.vo.GoodsVo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: GoodsRes
 * Package: com.example.controller.response
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/19 14:10
 * @Version 1.0
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsRes {
    private User user;

    private GoodsVo goodsVo;

    private int seckillStatus;

    private long remainSeconds;

}
