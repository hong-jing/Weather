package com.example.hongjing.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongjing on 2017/1/7.
 */

public class Foreast {
    public String mDate;

    @SerializedName("tmp")
    public Temperature mTemperature;

    @SerializedName("cond")
    public More mMore;

    public class Temperature {
        public String mMax;
        public String mMin;
    }

    public class More{
        @SerializedName("txt_d")
        public String mInfo;
    }
}
