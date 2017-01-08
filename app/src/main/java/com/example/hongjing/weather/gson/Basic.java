package com.example.hongjing.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongjing on 2017/1/7.
 */

public class Basic {
    @SerializedName("city")
    public String mcityName;

    @SerializedName("id")
    public String mWeatherId;

    public Update mUpdate;

    public class Update{
        @SerializedName("loc")
        public String mUpdateTime;
    }

}
