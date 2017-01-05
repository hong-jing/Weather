package com.example.hongjing.weather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by hongjing on 2017/1/5.
 */

public class HttpUtil {
    //发起OKhttp请求
    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);

    }

}
