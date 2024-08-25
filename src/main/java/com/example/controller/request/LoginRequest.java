package com.example.controller.request;

import com.example.validator.IsMobile;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * ClassName: LoginRequest
 * Package: com.example.vo.request
 * Description:
 *
 * @Author YUYU
 * @Create 2024/7/22 16:54
 * @Version 1.0
 */
@Data
@AllArgsConstructor
public class LoginRequest {
    @NotNull
    @IsMobile //并不是validation组件自带的校验注解  所以需要自定义参数校验注解
    private String mobile;

    @NotNull
//    @Length(min = 8) //md5加密完之后密码应该是32位，所以加个长度的校验  至少32位
    private String password;
}
