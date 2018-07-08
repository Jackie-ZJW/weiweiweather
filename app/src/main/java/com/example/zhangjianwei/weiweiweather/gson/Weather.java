package com.example.zhangjianwei.weiweiweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {

    public String status;

    public Aqi aqi;

    public Basic basic;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

    public Now now;

    public Suggestion suggestion;

}
