package com.example.hongjing.weather.util;

import android.util.Log;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by hongjing on 2017/1/5.
 */

public class HttpUtil {
    private static final String TAG = "HttpUtil";
    //发起OKHttp请求
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        Log.d(TAG, "sendOkHttpRequest: 请求数据");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);

    }

}
