package com.coolweather.app.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

import java.util.ArrayList;
import java.util.List;

public class ChooseAreaAty extends AppCompatActivity {
    private ProgressDialog mProgressDialog;
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    //当前选中的级别
    private int currentLevel;
    //选中的省、市、县
    private Province selectedProvince;
    private City selectedCity;
    private County selectedCounty;
    private CoolWeatherDB mCoolWeatherDB;
    private RecyclerView mRecyclerView;
    private TextView mTextView;
    private List<Province> mProvinceList;
    private List<City> mCityList;
    private List<County> mCountyList;
    private HomeAdapter mHomeAdapter;
    private List<String> datalist=new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_area_aty);

        mRecyclerView= (RecyclerView) findViewById(R.id.review);
        mTextView= (TextView) findViewById(R.id.title_text);
        mHomeAdapter=new HomeAdapter(ChooseAreaAty.this,datalist);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(ChooseAreaAty.this
                , DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setAdapter(mHomeAdapter);

        mHomeAdapter.setOnItemClickListener(new HomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = mProvinceList.get(position);
                    //查询该省份的地级市
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = mCityList.get(position);


                    //查询该地级市的所有县
                    queryCounties();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        mCoolWeatherDB=CoolWeatherDB.getInstance(this);
        //初始化时自动查询所有省级数据
        queryProvinces();


    }

    /*
    * 查询所有省份，优先从数据库中查询，如果没有再连接服务器查询
    * */
    private void queryProvinces(){
        mProvinceList=mCoolWeatherDB.loadProvinces();
        if(mProvinceList.size()>0){//数据库中有数据
            datalist.clear();//清除缓存列表
            //依次取出全国省份信息
            for(Province province:mProvinceList){
                datalist.add(province.getProvinceName());
            }
            //通知适配器有数据变化
            mHomeAdapter.notifyDataSetChanged();
            mTextView.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else {
            //从服务器加载数据并存储在数据库
            queryFromServer(null, "province");
        }
    }

    /*
    *从数据库查询地级市数据，若没有则连接网络查询并insert进数据库
    * */
    private void queryCities(){
        mCityList=mCoolWeatherDB.loadCities(selectedProvince.getId());
        if(mCityList.size()>0){
            datalist.clear();
            for (City city:mCityList){
                datalist.add(city.getCityName());
            }
            mHomeAdapter.notifyDataSetChanged();
            mTextView.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else {
            queryFromServer(selectedProvince.getProvinceCode(),"city");
        }
    }

    /*
    *从数据库查询县数据，若没有则连接网络查询并insert进数据库
    * */
    private void queryCounties(){
        mCountyList=mCoolWeatherDB.loadCounties(selectedCity.getId());

        if(mCountyList.size()>0){
            datalist.clear();
            for (County county:mCountyList){
                datalist.add(county.getCountyName());
            }
            mHomeAdapter.notifyDataSetChanged();
            mTextView.setText(selectedCity.getCityName());
            currentLevel=LEVEL_COUNTY;
        }else {
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }

    /*
    * 根据传入的代号和类型从服务器(最终也存储在数据库)上查询省市县数据
    * */
    private void queryFromServer(final String code,final String type){
        String address;
        if(!TextUtils.isEmpty(code)){//说明查询的是市（19）或县（1907）信息，查询地址唯一
            address="http://www.weather.com.cn/data/list3/city"+code+".xml";
        }else {//code为null,此时查询省份信息
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        //从网络上查询要弹出对话框，否则界面有延迟，影响用户体验
        showProgressDialog();
        //按地址查询数据，并返回（回调监听来获得）
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                switch (type) {
                    case "province":
                        result = Utility.handlerProvinceResponse(mCoolWeatherDB, response);
                        break;
                    case "city":
                        result = Utility.handlerCitiesResponse(mCoolWeatherDB, response,
                                selectedProvince.getId());
                        break;
                    case "county":

                        result = Utility.handlerCountiesResponse(mCoolWeatherDB, response,
                                selectedCity.getId());
                        break;
                }
                //LogUtil.d("ww",String.valueOf(result) );
                if (result) {//说明插入数据库成功
                    //通过runOnUiThread()方法回到主线程处理逻辑，即从数据库查询并显示在界面上
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            switch (type) {
                                case "province":
                                    queryProvinces();
                                    break;
                                case "city":
                                    queryCities();
                                    break;
                                case "county":
                                    queryCounties();
                                    break;
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                //通过runOnUiThread()方法回到主线程处理逻辑，告知网络加载失败
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaAty.this, "加载失败",
                                Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    /*
    * 显示进度对话框
    * */
    private void showProgressDialog(){
        //当之前没有新建的时候新建
        if(mProgressDialog==null){
            mProgressDialog=new ProgressDialog(this);
            mProgressDialog.setMessage("正在加载...");
            //正在执行过程中不可按返回
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    /*
    * 当加载完成之后关闭对话框
    * */
    private void closeProgressDialog(){
        if(mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if(currentLevel==LEVEL_COUNTY){
            queryCities();
        }else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else {
            finish();
        }
    }
}
