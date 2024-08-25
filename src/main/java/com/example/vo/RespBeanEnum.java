package com.example.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * ClassName: RespBeanEnum
 * Package: com.example.vo
 * Description: 公共返回对象枚举类
 *
 * @Author YUYU
 * @Create 2024/7/21 18:42
 * @Version 1.0
 */
@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    //通用
    SUCCESS(200, "SUCCESS"),
    ERROR(500, "服务端异常"),


    //登录模块
    LOGIN_ERROR(50020, "用户名或密码为空"),
    MOBILE_ERROR(50021, "手机号码格式不正确"),
    USER_NOT_EXIST(50022, "该用户不存在"),
    MOBILE_ERROR_UPDATE_CODE_FAILE(50028, "手机号输入错误-修改密码失败"),
    USER_OR_PASSWORD_ERROR(50023, "用户名或密码错误"),
    PASSWORD_UPDATE_FAIL(50029, "更新密码失败"),
    VALIDATION_ERROR(50024, "参数校验异常"),
    NOT_LOGIN(50025, "用户未登录！"),
    NOT_LOGIN_SESSION_ERROR(50030, "用户cookie获取为空"),
    STOCK_IS_EMPTY(50026, "秒杀商品库存为空"),
    SECKILL_FAIL_REPEAT(50027, "秒杀失败，重复抢购"),
    ORDER_IS_NOT_EXIST(50031, "订单不存在"),
    SECKILL_PATH_NULL_ERROR(50032, "秒杀地址获取失败/为空，请重新获取"),
    SECKILL_PATH_VALIDATION_ERROR(50033, "秒杀地址验证不一致，请重新获取"),
    VERIFY_CODE_ERROR(50034, "验证码有误，请确认后重新输入！"),
    REQUEST_IS_FREQUENCY(50035, "请求过于频繁，请稍后再试！");

    private final Integer code;//状态码
    private final String message;//信息
}
