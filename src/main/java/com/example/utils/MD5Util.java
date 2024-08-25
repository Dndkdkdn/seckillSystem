package com.example.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;



/**
 * ClassName: MD5Util
 * Package: com.example.utils
 * Description:
 *
 * @Author YUYU
 * @Create 2024/7/17 23:02
 * @Version 1.0
 */
@Component
public class MD5Util {
    /**
     * MD5加密
     * @param src
     * @return
     */
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt = "1a2b3c4d";//准备第一次MD5加密时用的固定的salt(自定义的salt)
    //此处的salt是为了和前端的salt进行统一，因为第一次加密是用户输完密码之后，传到后端的时候，在前端进行的。后端也需要这个salt获取到真正的密码，
    //然后将真正的密码通过第一次MD5加密后，获取到的秘钥，再与数据库中存储的随机salt进行二次MD5加密

    /**
     * 第一次MD5加密
     * @param inputPass
     * @return
     */
    public static String inputPassToFromPass(String inputPass){
        String src = "" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);//此处可以随意用salt和inputPass构造要进行MD5加密的字符串
        return md5(src);
    }

    /**
     * 第二次MD5加密
     * @param fromPass
     * @param salt
     * @return
     */
    public static String fromPassToDBPass(String fromPass, String salt){//此处的参数salt与上面自定的salt不是一个（此处指的是数据库中存储的salt【二次加密所用的随机的salt】）
        String src = "" + salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(5) + salt.charAt(4);//此处可以随意用salt和inputPass构造要进行MD5加密的字符串
        return md5(src);
    }

    /**
     * 上述两个方法整合为为外部调用的方法
     * @param inputPass
     * @param salt
     * @return 返回的密文 是最终要存到数据库的password
     */
    public static String inputPassToDBPass(String inputPass, String salt){//salt指的是随机的salt
        String fromPass = inputPassToFromPass(inputPass);
        return fromPassToDBPass(fromPass, salt);

    }


    public static void main(String[] args) {
        System.out.println(inputPassToFromPass("123456"));//d3b1294a61a07da9b49b6e22b2cbd7f9
        System.out.println(fromPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9", "1a2b3c4d"));//此处第二个参数salt是随机salt,随便写一个测试用 0687f9701bca74827fcefcd7e743d179
        System.out.println(inputPassToDBPass("123456", "1a2b3c4d"));//b7797cce01b4b131b433b6acf4add449
    }

}


