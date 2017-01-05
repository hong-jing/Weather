package com.example.hongjing.weather.db;


import org.litepal.crud.DataSupport;

/**
 * Created by hongjing on 2017/1/5.
 */

public class Province extends DataSupport {
    //省
    private int mId;
    private String mProvinceName;
    private int mProvinceCode;

    public int getProvinceCode() {
        return mProvinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        mProvinceCode = provinceCode;
    }

    public String getProvinceName() {
        return mProvinceName;
    }

    public void setProvinceName(String provinceName) {
        mProvinceName = provinceName;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
