package com.example.utils;

import java.util.UUID;

/**
* ClassName: UUIDUtil
* Package: com.example.utils
* Description: 
* @Author YUYU
* @Create 2024/8/6 13:47 
* @Version 1.0   
*/public class UUIDUtil {
    public static String uuid(){
        return UUID.randomUUID().toString().replace("-", "");
    }
}
