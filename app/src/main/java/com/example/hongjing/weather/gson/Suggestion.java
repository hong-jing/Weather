package com.example.hongjing.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hongjing on 2017/1/7.
 */

public class Suggestion {
    @SerializedName("comf")
    public Comfort mComfort;

    @SerializedName("cw")
    public CarWash mCarWash;

    public Sport mSport;


    public class Comfort {
        @SerializedName("txt")
        public String mInfo;
    }


    public class CarWash {
        @SerializedName("txt")
        public String mInfo;
    }

    public class Sport {
        @SerializedName("txt")
        public String mInfo;
    }
}
