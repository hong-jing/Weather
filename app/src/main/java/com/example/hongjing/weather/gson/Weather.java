package com.example.hongjing.weather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by hongjing on 2017/1/7.
 */

public class Weather {
    public String mStatus;

    public Basic mBasic;

    public AQI mAQI;

    public Now mNow;

    public Suggestion mSuggestion;

    @SerializedName("daily_foreast")
    public List<Foreast> mForeastList;



}
