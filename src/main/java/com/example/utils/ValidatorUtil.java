package com.example.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName: ValidatorUtil
 * Package: com.example.utils
 * Description:登录模块，手机号码校验类
 *   手机号码有多种常见的校验方式  这里使用正则表达式
 * @Author YUYU
 * @Create 2024/7/24 15:33
 * @Version 1.0
 */
public class ValidatorUtil {

    private static final Pattern mobile_pattern = Pattern.compile("^1[3-9]\\d{9}$");

    public static boolean isMobile(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return false;
        }
        Matcher matcher = mobile_pattern.matcher(mobile);
        return matcher.matches();
    }

}
