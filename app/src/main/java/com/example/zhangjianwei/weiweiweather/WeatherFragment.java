package com.example.zhangjianwei.weiweiweather;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zhangjianwei.weiweiweather.gson.Forecast;
import com.example.zhangjianwei.weiweiweather.gson.Weather;
import com.example.zhangjianwei.weiweiweather.util.HttpUtil;
import com.example.zhangjianwei.weiweiweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherFragment extends Fragment {

    private Button btNavButton;
    private Button btEditCityButton;
    private TextView tvCityTitle;
    private TextView tvUpdateTime;

    private TextView tvDegreeText;
    private TextView tvWeatherInfoText;

    private LinearLayout llForecastLayout;
    public DrawerLayout drawerLayout;

    private TextView tvAqiText;
    private TextView tvPM25Text;

    private TextView tvComfortText;
    private TextView tvSportText;
    private TextView tvWashCarText;

    private String weatherResponseText;
    private String bingPicResponseText;

    private String mWeatherId;

    private ImageView ivBingPic;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (Build.VERSION.SDK_INT > 21) {
            View decorView = getActivity().getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getActivity().getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        View view = inflater.inflate(R.layout.fragment_weather, container, false);

        btNavButton = view.findViewById(R.id.bt_nav_button);
        btEditCityButton = view.findViewById(R.id.bt_edit_city);
        tvCityTitle = view.findViewById(R.id.tv_title_city);
        tvUpdateTime = view.findViewById(R.id.tv_title_update_time);

        tvDegreeText = view.findViewById(R.id.tv_degree_text);
        tvWeatherInfoText = view.findViewById(R.id.tv_weather_info_text);

        llForecastLayout = view.findViewById(R.id.ll_forecast_layout);
        drawerLayout = view.findViewById(R.id.dl_drawer_layout);

        tvAqiText = view.findViewById(R.id.tv_aqi_text);
        tvPM25Text = view.findViewById(R.id.tv_pm25_txt);

        tvComfortText = view.findViewById(R.id.tv_comfort_text);
        tvSportText = view.findViewById(R.id.tv_sport_text);
        tvWashCarText = view.findViewById(R.id.tv_washcar_text);

        ivBingPic = view.findViewById(R.id.iv_bing_pic_image);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
        weatherResponseText = preferences.getString("reponseWeatherText", null);
        bingPicResponseText = preferences.getString("bingPic_ResponseText", null);
        if (!TextUtils.isEmpty(weatherResponseText)) {
            //如果已经存在之前缓存的天气数据时就直接解析该数据并显示到界面上
            Weather weather = Utility.parseWeatherResponse(weatherResponseText);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            //待定

        }

        if (!TextUtils.isEmpty(bingPicResponseText)) {
            //如果已经存在之前缓存的背景图片数据就直接解析该数据并将背景图片显示出来
            Glide.with(getContext()).load(bingPicResponseText).into(ivBingPic);
        } else {
            loadPic();
        }

        /*btNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });*/

        btEditCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

    }

    public void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=35719fcccc6e44a9b55b6fdfdd0655e4";
        HttpUtil.sendRequestWithOkHttp(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "获取天气数据失败2！", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseWeatherText = response.body().string();
                final Weather weather = Utility.parseWeatherResponse(responseWeatherText);
                final Activity activity = getActivity();
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putString("reponseWeatherText", responseWeatherText);
                            editor.apply();
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(getContext(), "获取天气数据失败1！", Toast.LENGTH_SHORT).show();
                        }

                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        loadPic();
    }

    private void loadPic() {
        String bingPicUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendRequestWithOkHttp(bingPicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPicResponseText = response.body().string();
                if (!TextUtils.isEmpty(bingPicResponseText)) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putString("bingPic_ResponseText", bingPicResponseText);
                    editor.apply();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(getContext()).load(bingPicResponseText).into(ivBingPic);
                        }
                    });
                }
            }
        });
    }

    private void showWeatherInfo(Weather weather) {
        tvCityTitle.setText(weather.basic.cityName);
        String updateTime = weather.basic.update.updateTime.split(" ")[1];
        tvUpdateTime.setText(updateTime);
        tvDegreeText.setText(weather.now.temperature + "℃");
        tvWeatherInfoText.setText(weather.now.more.info);

        llForecastLayout.removeAllViews();
        for (Forecast forecast : weather.forecastList) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.forecast_item, llForecastLayout, false);

            TextView dateText = view.findViewById(R.id.tv_date_text);
            TextView infoText = view.findViewById(R.id.tv_weather_info_text);
            TextView maxText = view.findViewById(R.id.tv_max_text);
            TextView minText = view.findViewById(R.id.tv_min_text);

            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);

            llForecastLayout.addView(view);
        }

        tvAqiText.setText(weather.aqi.city.aqi);
        tvPM25Text.setText(weather.aqi.city.pm25);

        tvComfortText.setText(weather.suggestion.comfort.info);
        tvSportText.setText(weather.suggestion.sport.info);
        tvWashCarText.setText(weather.suggestion.washCar.info);

    }
}
