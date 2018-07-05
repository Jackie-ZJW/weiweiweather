package com.example.zhangjianwei.weiweiweather.util;

public interface HttpCallbackListener {

    void onFinish(String response);

    void onError(Exception e);

}
