package com.example.habitassistant.utils;




import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;


public class OkhttpHelper {
    public static void getRequest(String url,String token, Callback callback){

        //使用get异步请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header("Authorization", "Bearer " + token)
                .build();
        client.newCall(request).enqueue(callback);
    }
}

