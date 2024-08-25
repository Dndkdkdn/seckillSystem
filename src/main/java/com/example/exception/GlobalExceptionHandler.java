package com.example.exception;

import com.example.vo.RespBean;
import com.example.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;



/**
 * ClassName: GlobalExceptionHandler
 * Package: com.example.exception
 * Description:异常处理类
 *
 * @Author YUYU
 * @Create 2024/7/24 22:22
 * @Version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    //处理异常
    @ExceptionHandler(Exception.class) //指定能够处理的异常类型
    public RespBean ex(Exception e){
        if(e instanceof GlobalException){
            GlobalException globalException = (GlobalException) e;//异常属于定义的全局异常的对象或者子对象的话，进行类型提升？？强转？？
            return RespBean.error(globalException.getRespBeanEnum());
        }else if(e instanceof BindException){
            BindException bindException = (BindException) e;
            RespBean respBean =  RespBean.error(RespBeanEnum.VALIDATION_ERROR);
            respBean.setMessage(respBean.getMessage() + ":" + bindException.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }
        e.printStackTrace();//打印堆栈中的异常信息
        //捕获到异常之后，响应一个标准的RespBean
        return RespBean.error(RespBeanEnum.ERROR);
    }
}
