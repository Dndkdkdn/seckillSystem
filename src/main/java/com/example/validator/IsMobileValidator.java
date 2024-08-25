package com.example.validator;

import com.example.utils.ValidatorUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.lang3.StringUtils;

/**
 * ClassName: IsMobileValidator
 * Package: com.example.validator
 * Description:手机号码校验规则类  用于手机号码自定义校验注解的使用
 *
 * @Author YUYU
 * @Create 2024/7/24 21:55
 * @Version 1.0
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    private boolean required = false;

    @Override
    public void initialize(IsMobile constraintAnnotation) {//初始化最主要的是去获取  是否是必填的
        required = constraintAnnotation.required();

    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){//是否是必填的，如果是必填的
            return ValidatorUtil.isMobile(s);
        }else{//如果是非必填的话
            if(StringUtils.isEmpty(s)) return true;//如果mobile为空的话
            else return ValidatorUtil.isMobile(s);
        }
    }
}
