package com.example.exception;

import com.example.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * ClassName: GlobalException
 * Package: com.example.exception
 * Description:全局异常对象类  异常pojo类
 *
 * @Author YUYU
 * @Create 2024/7/24 22:21
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;
}
