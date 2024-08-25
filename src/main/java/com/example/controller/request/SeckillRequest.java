package com.example.controller.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: SeckillRequest
 * Package: com.example.controller.request
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/20 11:33
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SeckillRequest {
    private Long id;
    private Long goodsId;
    private String captcha;
}
