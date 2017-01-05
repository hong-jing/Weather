package com.example.hongjing.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by hongjing on 2017/1/5.
 */

public class City extends DataSupport {
    //城市
    private int mId;
    private String mCityName;
    private int mCityCode;
    private int mProvinceId;

    public int getCityCode() {
        return mCityCode;
    }

    public void setCityCode(int cityCode) {
        mCityCode = cityCode;
    }

    public String getCityName() {
        return mCityName;
    }

    public void setCityName(String cityName) {
        mCityName = cityName;
    }

    public int getProvinceId() {
        return mProvinceId;
    }

    public void setProvinceId(int provinceId) {
        mProvinceId = provinceId;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
