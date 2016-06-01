package com.coolweather.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweather.app.R;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.LogUtil;
import com.coolweather.app.util.Utility;

public class WeatherAty extends Activity {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;
    private TextView publishText;
    private TextView weatherDespText;
    private TextView temp1Text,temp2Text;
    private TextView currentDateText;
    private Button switchCity,refreshWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);
        //初始化各控件
        weatherInfoLayout= (LinearLayout) findViewById(R.id.weather_info_layout);
        cityNameText= (TextView) findViewById(R.id.city_name);
        publishText= (TextView) findViewById(R.id.publish_text);
        weatherDespText= (TextView) findViewById(R.id.weather_desp);
        temp1Text= (TextView) findViewById(R.id.temp1);
        temp2Text= (TextView) findViewById(R.id.temp2);
        currentDateText= (TextView) findViewById(R.id.current_data);
        switchCity= (Button) findViewById(R.id.switch_city);
        refreshWeather= (Button) findViewById(R.id.refresh_weather);
        switchCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到选择地区界面
                Intent intent=new Intent(WeatherAty.this,ChooseAreaAty.class);
                intent.putExtra("from_weather_aty",true);
                startActivity(intent);
                finish();
            }
        });
        refreshWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishText.setText("同步中...");
                SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(WeatherAty.this);
                String weatherCode=preferences.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);
                }
            }
        });


        //通过选择地区Aty传送进来的县代号来查询相应的天气信息
        String countyCode=getIntent().getStringExtra("county_code");
        //连接网络查询，并保存在SharedPreferences里面
        if(!TextUtils.isEmpty(countyCode)){
            //用户效果：同步中...
            publishText.setText("同步中...");
            //先让天气信息布局和城市名不显示，因为还没有同步完成
            weatherInfoLayout.setVisibility(View.INVISIBLE);
            cityNameText.setVisibility(View.INVISIBLE);
            //查询天气代号
            queryWeatherCode(countyCode);

        }else {
            //没有县级代号时直接显示本地天气
            showWeather();
        }
    }

    /*
    *查询县级代号所对应的天气代号
    * */
    private void queryWeatherCode(String countyCode){
        String address="http://www.weather.com.cn/data/list3/city"+countyCode+".xml";
       // LogUtil.d("ww",address);
        queryFromServer(address,"countyCode");
    }


    /*
    * 存储天气代号所对应的天气信息
    * */
    private void queryWeatherInfo(String weatherCode){
        String address="http://www.weather.com.cn/data/cityinfo/"+weatherCode+".html";
       // LogUtil.d("ww",address);
        queryFromServer(address, "weatherCode");
    }

    /*
    *根据传入的地址和类型去向服务器查询天气代号或天气信息
    * */

    private void queryFromServer(final String address,final String type){
        HttpUtil.sendHttpClientRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(final String response) {
                switch (type){
                    case "countyCode":
                        //获得相应县的天气代码
                        if(!TextUtils.isEmpty(response)){
                            String[] array=response.split("\\|");//只有一组数据
                            if(array!=null&&array.length==2){
                                String weatherCode=array[1];
                                LogUtil.d("ww",weatherCode);
                                queryWeatherInfo(weatherCode);
                            }
                        }
                        break;
                    case "weatherCode":
                        //处理从服务器返回的天气信息
                        Utility.handlerWeatherResponse(WeatherAty.this,response);
                        //将天气信息显示在主UI界面上
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                showWeather();
                            }
                        });
                        break;
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishText.setText("同步失败");
                    }
                });
            }
        });
    }

    /*
    * 从SharedPreferences中读取存储的天气信息，并显示到界面上
    * */
    private void showWeather(){
        SharedPreferences getPreferences= PreferenceManager.
                getDefaultSharedPreferences(this);
        cityNameText.setText(getPreferences.getString("city_name",""));
        temp1Text.setText(getPreferences.getString("temp1",""));
        temp2Text.setText(getPreferences.getString("temp2",""));
        weatherDespText.setText(getPreferences.getString("weather_desp",""));
        publishText.setText("发布时间："+getPreferences.getString("publish_time",""));
        currentDateText.setText(getPreferences.getString("current_data", ""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }



}
