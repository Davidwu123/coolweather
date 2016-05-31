package com.coolweather.app.util;

/**
 * Created by huiyi on 2016/5/27.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
