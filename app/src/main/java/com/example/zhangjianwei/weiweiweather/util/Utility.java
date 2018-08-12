package com.example.zhangjianwei.weiweiweather.util;

import android.text.TextUtils;

import com.example.zhangjianwei.weiweiweather.db.City;
import com.example.zhangjianwei.weiweiweather.db.County;
import com.example.zhangjianwei.weiweiweather.db.Province;
import com.example.zhangjianwei.weiweiweather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utility {

    //解析返回的JSON格式的省份数据
    public static boolean parseProvinceResponse(String response) {

        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray provinceArray = new JSONArray(response);
                for (int i = 0; i < provinceArray.length(); i++) {
                    JSONObject provinceObject = provinceArray.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceCode(provinceObject.getInt("id"));
                    province.setProvinceName(provinceObject.getString("name"));
                    province.save();
                }

                return true;

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    //解析返回的JSON格式的城市数据
    public static boolean parseCityResponse(String response, int provinceId) {

        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray cityArray = new JSONArray(response);
                for (int i = 0; i < cityArray.length(); i++) {
                    JSONObject cityObject = cityArray.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(cityObject.getInt("id"));
                    city.setCityName(cityObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    //解析返回的JSON格式的县城数据
    public static boolean parseCountyResponse(String response, int cityId) {

        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray countyArray = new JSONArray(response);
                for (int i = 0; i < countyArray.length(); i++) {
                    JSONObject countyObject = countyArray.getJSONObject(i);
                    County county = new County();
                    county.setCountyCode(countyObject.getInt("id"));
                    county.setCountyName(countyObject.getString("name"));
                    county.setWeatherId(countyObject.getString("weather_id"));
                    county.setCityId(cityId);
                    county.save();
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    //解析返回的JSON格式的天气数据,使用GSON解析，直接解析成Weather实体类返回
    public static Weather parseWeatherResponse(String response) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            Gson gson = new Gson();
            Weather weather = gson.fromJson(weatherContent, Weather.class);

            return weather;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


}
