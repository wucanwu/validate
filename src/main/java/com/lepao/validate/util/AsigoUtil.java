package com.lepao.validate.util;



import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class AsigoUtil {
    public String LogisticsDummySend(){
        String appKey = "2019101515362111803";
        String appSecret = "bytehhxu7x2khyru43crdr35h8m3udww";
        String accessToken = "AppId_2019101515362111803";
        return null;
    }
    //参数签名
    public static String Sign(Map<String, String> params,String appSecret) throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        String[] keys = params.keySet().toArray(new String[0]);
        Arrays.sort(keys);

        StringBuilder query = new StringBuilder();
        query.append(appSecret);
        for (String key : keys) {
            String value = params.get(key);
            query.append(key).append(value);
        }
        query.append(appSecret);
        System.out.println(query);
        byte[] md5byte = encryptMD5(query.toString());

        return  byte2hex(md5byte);
    }
    //byte数组转成16进制字符串
    public static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex.toLowerCase());
        }
        return sign.toString();
    }
    //Md5摘要
    public static byte[] encryptMD5(String data) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        return md5.digest(data.getBytes("UTF-8"));
    }



}
