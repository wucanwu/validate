package com.lepao.validate.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public final class HttpRequestUtil {
    /*
    发送get请求
     */
    public static String sendGet(String url, String param, String auth) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlAndParam = null;
            if (param == null) {
                urlAndParam = url;
            } else {
                //把url和参数就行连接
                urlAndParam = url + "?" + param;
            }
            //创建url对象并打开连接
            URL realUrl = new URL(urlAndParam);
            URLConnection connection = realUrl.openConnection();
            //设置请求的通用属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (auth != null) {
                connection.setRequestProperty("Authorization", "Basic " + auth);
            }
            //建立实际的连接
            connection.connect();
            //读取响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.println("发送post请求出现异常");
            e.printStackTrace();
        } finally {
            //关闭流
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        System.out.println(result);
        return result;
    }

    public static String sendGet(String url, String auth) {
        return sendGet(url, null, auth);
    }


    /*
    发送post请求，里面携带json数据
     */
    public static String sendPost(String url, String param, String auth) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");

            connection.setRequestProperty("Content-Type", "application/json");

            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (auth != null) {
                connection.setRequestProperty("Authorization", "Basic " + auth);
            }
            //发送post请求必须设置
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //获取输出流
            out = new PrintWriter(connection.getOutputStream());
            //发送参数
            out.print(param);
            out.flush();
            //获取输入流
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp = null;
            while ((temp = in.readLine()) != null) {
                result += temp;
            }
        } catch (Exception e) {
            System.out.println("发送post请求异常");
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println(result);
        return result;
    }

    /*
   发送post请求，里面携带json数据
    */
    public static String sendPostByAsigh(String url, String param, String auth) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl = new URL(url);
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            if (auth != null) {
                connection.setRequestProperty("Authorization", "Bearer " + auth);
            }
            //发送post请求必须设置
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //获取输出流
            out = new PrintWriter(connection.getOutputStream());
            //发送参数
            out.print(param);
            out.flush();
            //获取输入流
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String temp = null;
            while ((temp = in.readLine()) != null) {
                result += temp;
            }
        } catch (Exception e) {
            System.out.println("发送post请求异常");
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println(result);
        return result;
    }
}