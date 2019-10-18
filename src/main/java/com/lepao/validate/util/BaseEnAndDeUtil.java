package com.lepao.validate.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Base64;

public final class BaseEnAndDeUtil {

    private final static  Logger log = LoggerFactory.getLogger(BaseEnAndDeUtil.class);
    /*
    使用base64编码
     */
    public static String encode(String text)
    {
        try {
        final Base64.Encoder encoder = Base64.getEncoder();

        final byte[] textByte;

        textByte = text.getBytes("UTF-8");

//编码
        final String encodedText = encoder.encodeToString(textByte);
        System.out.println(encodedText);
        return encodedText;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
