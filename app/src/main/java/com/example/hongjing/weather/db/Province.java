package com.example.hongjing.weather.db;


import org.litepal.crud.DataSupport;

/**
 * Created by hongjing on 2017/1/5.
 */

public class Province extends DataSupport {
    //省
    private int id;
    private String provinceName;
    private int provinceCode;

    public int getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
