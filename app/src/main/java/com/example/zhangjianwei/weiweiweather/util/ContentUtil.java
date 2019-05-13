package com.example.zhangjianwei.weiweiweather.util;


import com.example.zhangjianwei.weiweiweather.MyApplication;

/**
 * Created by niuchong on 2019/4/7.
 */

public class ContentUtil {

    //用户id
    //public static final String APK_USERNAME = "和风天气获取的ID";
    public static final String APK_USERNAME = "HE1801182213151894";
    //用户key
    //public static final String APK_KEY = "和风天气获取的KEY";
    public static final String APK_KEY = "35719fcccc6e44a9b55b6fdfdd0655e4";
    //当前所在位置
    public static Double NOW_LON = 116.40;
    public static Double NOW_LAT = 39.9;

    //当前城市
    public static String NOW_CITY_ID = SpUtils.getString(MyApplication.getContext(), "lastLocation", "CN101010100");
    public static String NOW_CITY_NAME = SpUtils.getString(MyApplication.getContext(), "nowCityName", "北京");

    public static boolean FIRST_OPEN = SpUtils.getBoolean(MyApplication.getContext(), "first_open", true);

    //应用设置里的文字
    public static String SYS_LANG = "zh";
    public static String APP_SETTING_LANG = SpUtils.getString(MyApplication.getContext(), "language", "sys");
    public static String APP_SETTING_UNIT = SpUtils.getString(MyApplication.getContext(), "unit", "she");
    public static String APP_SETTING_TESI = SpUtils.getString(MyApplication.getContext(), "size", "mid");
    public static String APP_PRI_TESI = SpUtils.getString(MyApplication.getContext(), "size", "mid");
    public static String APP_SETTING_THEME = SpUtils.getString(MyApplication.getContext(), "theme", "浅色");


    public static boolean UNIT_CHANGE = false;
    public static boolean CHANGE_LANG = false;
    public static boolean CITY_CHANGE = false;

}
