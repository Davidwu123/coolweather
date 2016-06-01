package com.coolweather.app.util;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by huiyi on 2016/5/27.
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        //开启网络连接线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection= null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    if (listener != null) {
                        //回调onFinish()方法
                        listener.onFinish(response.toString());
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        //回调onError()方法
                        listener.onError(e);
                    }
                } finally {
                    if (connection!=null){
                        //断开连接
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
    public static void sendHttpClientRequest(final String address, final HttpCallbackListener listener){
        //开启网络连接线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpResponse httpResponse=null;
                String response=null;
                HttpGet httpGet=new HttpGet(address);
                HttpClient httpClient=new DefaultHttpClient();
                try {
                    httpResponse=httpClient.execute(httpGet);
                    if (httpResponse.getStatusLine().getStatusCode()==200){
                        response= EntityUtils.toString(httpResponse.getEntity(),"utf-8");
                       // LogUtil.d("ww",response);
                    }
                    if (listener != null) {
                        //回调onFinish()方法
                        listener.onFinish(response);
                    }
                } catch (Exception e) {
                    if (listener != null) {
                        //回调onError()方法
                        listener.onError(e);
                    }
                }
            }
        }).start();
    }
}
