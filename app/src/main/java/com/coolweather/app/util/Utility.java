package com.coolweather.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by huiyi on 2016/5/30.
 */
public class Utility {
    /*
    * 用来解析和处理 "代号|城市，代号|城市"这种格式的返回数据
    * */
    public synchronized static boolean handlerProvinceResponse(CoolWeatherDB coolWeatherDB
    ,String response){
        //满足要求
        if(!TextUtils.isEmpty(response)){
            //以","来分隔各个省
            String[] allProvinces =response.split(",");
            if(allProvinces!=null&&allProvinces.length>0){
                for (String p:allProvinces){
                    //将每个省的代号和城市分开,按name和code存储到Province中，并存储在SQLite中
                    String[] array=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    //存储在数据库的Province表中
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }

        }
        //不满足要求
        return false;
    }

    public static synchronized boolean handlerCitiesResponse(CoolWeatherDB coolWeatherDB
            ,String response,int provinceId){
        /*
        * 解析和处理返回的市级数据
        * */
        if(!TextUtils.isEmpty(response)){
            //按","获得不同的地级市
            String[] allcities=response.split(",");
            if(allcities!=null&&allcities.length>0){
                //将地级市的信息分解
                for (String c:allcities) {
                    String[] array=c.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }

    public static synchronized boolean handlerCountiesResponse(CoolWeatherDB coolWeatherDB
            ,String response,int cityId){
        /*
        * 解析和处理县级数据，并存储在数据库
        * */
        if (!TextUtils.isEmpty(response)){
            String[] allcounties=response.split(",");
            if(allcounties!=null&&allcounties.length>0){
                for(String c:allcounties){
                    String[] array=c.split("\\|");
                    County county=new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                    //LogUtil.d("ww", String.valueOf(coolWeatherDB.loadCounties(cityId).size()));
                }
                return true;
            }
        }
        return false;
    }


    /*
    *解析服务器返回的Json数据，并将解析出的数据存储到本地,不需要存储在数据库
    * */
    public static void handlerWeatherResponse(Context context
        ,String response){
        try {
            JSONObject jsonObject=new JSONObject(response);
            JSONObject weatherInfo=jsonObject.getJSONObject("weatherinfo");
            String cityName=weatherInfo.getString("city");
            String weatherCode=weatherInfo.getString("cityid");
            String temp1=weatherInfo.getString("temp1");
            String temp2=weatherInfo.getString("temp2");
            String weatherDesp=weatherInfo.getString("weather");
            String publishTime=weatherInfo.getString("ptime");
            //保存在SharedPreferences文件中
            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
    * 将服务器返回的所有天气信息存储到SharedPreferences文件中
    * */
    public static void saveWeatherInfo(Context context
            ,String cityName,String weatherCode,String temp1,String temp2
            ,String weatherDesp,String publishTime){
        //格式化时间
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor= PreferenceManager.
                getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name", cityName);
        editor.putString("weather_code", weatherCode);
        editor.putString("temp1", temp1);
        editor.putString("temp2", temp2);
        editor.putString("weather_desp", weatherDesp);
        editor.putString("publish_time", publishTime);
       // LogUtil.d("ww",simpleDateFormat.format(new Date()));
        editor.putString("current_data",simpleDateFormat.format(new Date()));
        //editor提交
        editor.commit();
    }
}



































