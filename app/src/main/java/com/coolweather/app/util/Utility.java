package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

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

}



































