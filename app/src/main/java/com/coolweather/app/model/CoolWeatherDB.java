package com.coolweather.app.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.db.CoolWeatherOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huiyi on 2016/5/27.
 * 对常用的数据库进行封装
 */
public class CoolWeatherDB {
    /*
    * 数据库名
    * */
    public final static String DB_NAME="cool_weather";

    /*
    * 数据库版本
    * */
    public final static int VERSION=1;
    //对数据库实例化
    private static CoolWeatherDB sCoolWeatherDB;
    private SQLiteDatabase mSQLiteDatabase;

    //将构造方法私有化
    private CoolWeatherDB(Context context){
        //建立数据库
        CoolWeatherOpenHelper dbHelper=new CoolWeatherOpenHelper
                (context,DB_NAME,null,VERSION);
        //获得可操作的数据库对象
        mSQLiteDatabase=dbHelper.getWritableDatabase();
    }

    //用同步方法实现操作数据库的实例，即同一时刻只能由一个线程对该方法进行操作，也就是只能实例化一次
    // 外部调用首先调用的是该方法，将context传进来，并执行构造函数
    public synchronized static CoolWeatherDB getInstance(Context context){
        if(sCoolWeatherDB==null){
            //若没有被实例化，则实例化
            sCoolWeatherDB =new CoolWeatherDB(context);
        }
        return sCoolWeatherDB;
    }

    /*
    * 将Province实例存储到数据库(存储所有省份信息)
    * */
    public void saveProvince(Province province){
        if (province!=null){
            //定义数据集合
            ContentValues valuesProvince=new ContentValues();
            valuesProvince.put("province_name",province.getProvinceName());
            valuesProvince.put("province_code",province.getProvinceCode());
            mSQLiteDatabase.insert("Province",null,valuesProvince);
        }
    }

    /*
    * 将所有全国省份信息都读出（返回的是包含Province信息的list）
    * */
    public List<Province> loadProvinces(){
        List<Province> listProvince=new ArrayList<Province>();
        //cursor类似于java中的result
        Cursor cursor=mSQLiteDatabase.query("Province",null,null,null,null,null,null);
        if (cursor.moveToFirst()){//遍历所有数据
            do {
                //按id的不同放在不同的province中
                Province province=new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                listProvince.add(province);
            }while (cursor.moveToNext());
        }
        return  listProvince;
    }


    /*
    * 将City实例存储到数据库
    * */
    public void saveCity(City city){
        if (city!=null) {
            ContentValues valuesCity = new ContentValues();
            valuesCity.put("city_name", city.getCityName());
            valuesCity.put("city_code", city.getCityCode());
            valuesCity.put("province_id", city.getProvinceId());
            mSQLiteDatabase.insert("City", null, valuesCity);
        }
    }

    /*
    * 根据provinceId来读出该省所有城市信息
    * */
    public List<City> loadCities(int provinceId){
        List<City> listCity=new ArrayList<City>();
        //按条件查询
        Cursor cursor=mSQLiteDatabase.query("City",null,"province_id=?",
                new String[]{String.valueOf(provinceId)},null,null,null);
        if (cursor.moveToFirst()){
            do {
                City city=new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                listCity.add(city);
            }while (cursor.moveToNext());
        }
        return listCity;
    }

    /*
    * 将County存储到数据库
    * */
    public void saveCounty(County county){
        if (county!=null) {
            ContentValues valuesCounty = new ContentValues();
            valuesCounty.put("county_name", county.getCountyName());
            valuesCounty.put("county_code", county.getCountyCode());
            valuesCounty.put("city_id", county.getCityId());
            mSQLiteDatabase.insert("County", null, valuesCounty);
        }
    }

    /*
    * 取出cityId下的所有县信息
    * */
    public List<County> loadCounties(int cityId){
        List<County> listCounty=new ArrayList<County>();
        Cursor cursor=mSQLiteDatabase.query("County",null,"city_id=?",
                new String[]{String.valueOf(cityId)},null,null,null);
        if (cursor.moveToFirst()){
            do {
                County county=new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                listCounty.add(county);
            }while (cursor.moveToNext());
        }
        return listCounty;
    }

}






















