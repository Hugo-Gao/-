package com.gyf.miaosha.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util
{
    public static String md5(String src)
    {
        return DigestUtils.md5Hex(src);
    }

    /**
     * 固定的salt
     */
    private static final String SALT = "1a2b3c4d";

    public static String inputPassToFormPass(String inputPass)
    {
        String str = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(str);
    }

    public static String formPassToDBPass(String formPass,String salt)
    {
        String str = "" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String input, String saltDB)
    {
        //一次加密
        String formPass = inputPassToFormPass(input);
        //两次加密
        return formPassToDBPass(formPass, saltDB);
    }

    public static void main(String[] args)
    {
        System.out.println(inputPassToDbPass("123456", "1a2b3c4d"));
    }
}
