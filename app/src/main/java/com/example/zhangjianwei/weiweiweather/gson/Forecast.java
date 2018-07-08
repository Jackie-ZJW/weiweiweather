package com.example.zhangjianwei.weiweiweather.gson;

import com.google.gson.annotations.SerializedName;

public class Forecast {

    public String date;

    @SerializedName("cond")
    public More more;

    @SerializedName("tmp")
    public Temperature temperature;

    public class Temperature{

        public int max;

        public int min;
    }


    public class More{

        @SerializedName("txt_d")
        public String info;
    }
}
