package com.example.hongjing.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongjing on 2017/1/7.
 */

public class Now {

    @SerializedName("tmp")
    public String mTemperature;

    @SerializedName("cond")
    public More mMore;

    public class More{
        @SerializedName("txt")
        public String mInfo;
    }
}
