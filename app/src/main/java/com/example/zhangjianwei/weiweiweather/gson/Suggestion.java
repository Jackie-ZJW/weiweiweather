package com.example.zhangjianwei.weiweiweather.gson;

import com.google.gson.annotations.SerializedName;

public class Suggestion {

    @SerializedName("comf")
    public Comfort comfort;

    public Sport sport;

    @SerializedName("cw")
    public WashCar washCar;

    public class Comfort {

        @SerializedName("txt")
        public String info;
    }

    public class Sport {

        @SerializedName("txt")
        public String info;
    }

    public class WashCar {

        @SerializedName("txt")
        public String info;
    }
}
