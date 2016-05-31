package com.coolweather.app.util;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.widget.Toast;

/**
 * Created by huiyi on 2016/5/10.
 */
public class MyAlpplication extends Application {
    private static Context mContext;
    @SuppressLint("MissingSuperCall")
    @Override
    public void onCreate() {//自动执行
        mContext=getApplicationContext();
        Toast.makeText(mContext, "启动成功！", Toast.LENGTH_SHORT).show();
    }

    //外界调用
    public static Context getContext(){
        return mContext;
    }
}
