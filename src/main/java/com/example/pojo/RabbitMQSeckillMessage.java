package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: RabbitMQSeckillMessage
 * Package: com.example.pojo
 * Description:
 *
 * @Author YUYU
 * @Create 2024/8/22 14:02
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RabbitMQSeckillMessage {
    private User user;

    private Long id;
}
