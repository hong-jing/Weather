package com.example.hongjing.weather.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hongjing.weather.R;
import com.example.hongjing.weather.gson.Foreast;
import com.example.hongjing.weather.gson.Weather;
import com.example.hongjing.weather.util.HttpUtil;
import com.example.hongjing.weather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hongjing on 2017/1/8.
 */

public class WeatherActivity extends AppCompatActivity{
    private ScrollView mWeatherLayout;
    public SwipeRefreshLayout mSwipeRefreshLayout;

    private TextView mTitleCity;
    private TextView mTitlrUpdateTime;
    private TextView mDrgreeText;
    private TextView mWeatherInfoText;
    private LinearLayout mForcastLayout;
    private ImageView mBingPicImg;

    private TextView mAqiText;
    private TextView mPm25Text;
    private TextView mComfortText;
    private TextView mCarWashText;
    private TextView mSportText;

    public DrawerLayout mDrawerLayout;
    private Button mNavButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View DecorView = getWindow().getDecorView();
        DecorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_weather);
        initView();
    }

    private void initView() {
        mWeatherLayout = (ScrollView) findViewById(R.id.weather_layout);
        mTitleCity = (TextView) findViewById(R.id.title_city);
        mTitlrUpdateTime = (TextView) findViewById(R.id.title_update_time);
        mDrgreeText = (TextView) findViewById(R.id.degree_text);
        mWeatherInfoText = (TextView) findViewById(R.id.weather_info_text);
        mForcastLayout = (LinearLayout) findViewById(R.id.foreast_layout);
        mBingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setColorSchemeColors(R.color.colorPrimary);

        mAqiText = (TextView) findViewById(R.id.aqi_text);
        mPm25Text = (TextView) findViewById(R.id.pm25_text);
        mComfortText = (TextView) findViewById(R.id.comfort_text);
        mCarWashText = (TextView) findViewById(R.id.car_wash_text);
        mSportText = (TextView) findViewById(R.id.sport_text);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        String mWeatherString = preferences.getString("weather", null);
        final String weatherId;
        if (mWeatherString != null){
            //有缓存时直接解析数据
            Weather weather = Utility.handleWeatherResponse(mWeatherString);
            weatherId = weather.mBasic.mWeatherId;
            showWeatherInfo(weather);
        }else {
            //无法缓存时去服务器查询数据
            weatherId = getIntent().getStringExtra("weather_id");
            mWeatherLayout.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }

        //背景图
        String bingPic = preferences.getString("bing_pic", null);
        if (bingPic != null){
            Glide.with(this).load(bingPic).into(mBingPicImg);
        }else {
            loadBingPic();

        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavButton = (Button) findViewById(R.id.nav_button);
        mNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

    }

    //加载必应每日一图
    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this)
                .edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(mBingPicImg);
                    }
                });
            }
        });
    }

    public void requestWeather(String weatherId) {
        //根据天气的ID请求查询天气信息
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId +
                         "&key=fec4cc7f6e284f62b7d3e107ecdabf2f";

        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String reponseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(reponseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.mStatus)){
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", reponseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        }else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

    }

    //处理并展示Weather实体类中的数据
    private void showWeatherInfo(Weather weather) {
        String cityName = weather.mBasic.mcityName;
        String updateTime = weather.mBasic.mUpdate.mUpdateTime.split(" ")[1];
        String degree = weather.mNow.mTemperature + "℃";
        String weatherInfo = weather.mNow.mMore.mInfo;

        mTitleCity.setText(cityName);
        mTitlrUpdateTime.setText(updateTime);
        mDrgreeText.setText(degree);
        mWeatherInfoText.setText(weatherInfo);

        mForcastLayout.removeAllViews();

        for (Foreast foreast : weather.mForeastList){
            View view = LayoutInflater.from(this).inflate(R.layout.foreast_item, mForcastLayout, false);

            TextView dateView = (TextView) view.findViewById(R.id.date_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);

            dateView.setText(foreast.mDate);
            infoText.setText(foreast.mMore.mInfo);
            maxText.setText(foreast.mTemperature.mMax);
            minText.setText(foreast.mTemperature.mMin);
            mForcastLayout.addView(view);
        }

        if (weather.mAQI != null){
            mAqiText.setText(weather.mAQI.mCity.mAqi);
            mPm25Text.setText(weather.mAQI.mCity.mPm25);
        }
        String comfort = "舒适度：" + weather.mSuggestion.mComfort.mInfo;
        String carWash = "洗车细数："+ weather.mSuggestion.mCarWash.mInfo;
        String sport = "运动建议：" + weather.mSuggestion.mSport.mInfo;

        mCarWashText.setText(carWash);
        mComfortText.setText(comfort);
        mSportText.setText(sport);
        mWeatherLayout.setVisibility(View.VISIBLE);


    }
}
