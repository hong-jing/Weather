package com.example.hongjing.weather.db;

import org.litepal.crud.DataSupport;

/**
 * Created by hongjing on 2017/1/5.
 */

public class County extends DataSupport {
    //市区

    private int mId;
    private String mCountyName;
    private String mWeatherId;
    private int mCityId;

    public int getCityId() {
        return mCityId;
    }

    public void setCityId(int cityId) {
        mCityId = cityId;
    }

    public String getCountyName() {
        return mCountyName;
    }

    public void setCountyName(String countyName) {
        mCountyName = countyName;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getWeatherId() {
        return mWeatherId;
    }

    public void setWeatherId(String weatherId) {
        mWeatherId = weatherId;
    }
}
