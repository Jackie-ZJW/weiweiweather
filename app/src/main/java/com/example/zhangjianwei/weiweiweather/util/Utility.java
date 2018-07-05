package com.example.zhangjianwei.weiweiweather.util;

import android.text.TextUtils;

import com.example.zhangjianwei.weiweiweather.db.Province;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utility {

    public static boolean parseProvinceWithJsonObject(String response) {

        if (!TextUtils.isEmpty(response)) {
            try {
                JSONArray provinceArray = new JSONArray(response);
                for (int i=0;i<provinceArray.length();i++){
                    JSONObject provinceObject=provinceArray.getJSONObject(i);
                    Province province=new Province();
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




}
