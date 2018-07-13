package com.example.zhangjianwei.weiweiweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhangjianwei.weiweiweather.gson.Weather;
import com.example.zhangjianwei.weiweiweather.util.Utility;

public class WeatherFragment extends Fragment {

    private Button btNavButton;
    private Button btEditCityButton;
    private TextView tvCityTitle;
    private TextView tvUpdateTime;

    private TextView tvDegreeText;
    private TextView tvWeatherInfoText;

    private LinearLayout llForecastLayout;

    private TextView tvAqiText;
    private TextView tvPM25Text;

    private TextView tvComfortText;
    private TextView tvSportText;
    private TextView tvWashCarText;

    private String weatherResponseText;

    private String mWeatherId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_weather,container,false);

        btNavButton=view.findViewById(R.id.bt_nav_button);
        btEditCityButton=view.findViewById(R.id.bt_edit_city);
        tvCityTitle=view.findViewById(R.id.tv_title_city);
        tvUpdateTime=view.findViewById(R.id.tv_title_update_time);

        tvDegreeText=view.findViewById(R.id.tv_degree_text);
        tvWeatherInfoText=view.findViewById(R.id.tv_weather_info_text);

        llForecastLayout=view.findViewById(R.id.ll_forecast_layout);

        tvAqiText=view.findViewById(R.id.tv_aqi_text);
        tvPM25Text=view.findViewById(R.id.tv_pm25_txt);

        tvComfortText=view.findViewById(R.id.tv_comfort_text);
        tvSportText=view.findViewById(R.id.tv_sport_text);
        tvWashCarText=view.findViewById(R.id.tv_washcar_text);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity= getActivity();

        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(activity);
        weatherResponseText=preferences.getString("reponseWeatherText",null);
        if (!TextUtils.isEmpty(weatherResponseText)){
            //如果已经存在之前缓存的天气数据时就直接解析该数据并显示到界面上
            Weather weather= Utility.parseWeatherResponse(weatherResponseText);
            mWeatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        }else {
            
        }
    }

    private void showWeatherInfo(Weather weather) {

    }
}
