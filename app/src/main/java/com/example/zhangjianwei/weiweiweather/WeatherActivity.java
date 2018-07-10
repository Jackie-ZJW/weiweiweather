package com.example.zhangjianwei.weiweiweather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhangjianwei.weiweiweather.gson.Forecast;
import com.example.zhangjianwei.weiweiweather.gson.Weather;
import com.example.zhangjianwei.weiweiweather.util.HttpUtil;
import com.example.zhangjianwei.weiweiweather.util.Utility;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private Button btNavButton;

    private TextView tvCityTitle;

    private TextView tvUpdateTime;

    private TextView tvDegreeText;

    private TextView tvWeatherInfoText;

    private String weatherId;

    private LinearLayout llForecastLayout;

    private TextView tvAqiText;

    private TextView tvPM25Text;

    private TextView tvComfortText;

    private TextView tvSportText;

    private TextView tvWashCarText;

    private String responseWeatherText;

    public DrawerLayout dlDrawerLayout;

    private SwipeRefreshLayout swipeRefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        btNavButton = findViewById(R.id.bt_nav_button);
        tvCityTitle = findViewById(R.id.tv_title_city);
        tvUpdateTime = findViewById(R.id.tv_title_update_time);
        tvDegreeText = findViewById(R.id.tv_degree_text);
        tvWeatherInfoText = findViewById(R.id.tv_weather_info_text);

        llForecastLayout = findViewById(R.id.ll_forecast_layout);

        tvAqiText = findViewById(R.id.tv_aqi_text);
        tvPM25Text = findViewById(R.id.tv_pm25_txt);

        tvComfortText = findViewById(R.id.tv_comfort_text);
        tvSportText = findViewById(R.id.tv_sport_text);
        tvWashCarText = findViewById(R.id.tv_washcar_text);

        dlDrawerLayout = findViewById(R.id.dl_drawer_layout);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);

        btNavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        SharedPreferences preferences = getSharedPreferences("weiweiweather", MODE_PRIVATE);
        responseWeatherText = preferences.getString("reponseWeatherText", null);
        if (!TextUtils.isEmpty(responseWeatherText)) {
            //如果已经存在之前缓存的天气数据时就直接解析该数据并显示到界面上
            Weather weather = Utility.parseWeatherResponse(responseWeatherText);
            weatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            weatherId = getIntent().getStringExtra("weather_id");
            requestWeather(weatherId);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(weatherId);
            }
        });

    }

    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=35719fcccc6e44a9b55b6fdfdd0655e4";
        HttpUtil.sendRequestWithOkHttp(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气数据失败2！", Toast.LENGTH_SHORT).show();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String reponseWeatherText = response.body().string();
                final Weather weather = Utility.parseWeatherResponse(reponseWeatherText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = getSharedPreferences("weiweiweather", MODE_PRIVATE).edit();
                            editor.putString("reponseWeatherText", reponseWeatherText);
                            editor.apply();
                            //这一行代码很重要，可以解决在进入weatherActivity活动界面之后的后续手动刷新天气数据时不会变成第一次进入时显示的城市的天气
                            weatherId=weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气数据失败1！", Toast.LENGTH_SHORT).show();
                        }

                        swipeRefreshLayout.setRefreshing(false);

                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weather) {

        String updateTime = weather.basic.update.updateTime.split(" ")[1];

        tvCityTitle.setText(weather.basic.cityName);
        tvUpdateTime.setText(updateTime);
        tvDegreeText.setText(weather.now.temperature);
        tvWeatherInfoText.setText(weather.now.more.info);

        List<Forecast> forecastList = weather.forecastList;
        llForecastLayout.removeAllViews();
        for (Forecast forecast : forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, llForecastLayout, false);
            TextView tvDateText = view.findViewById(R.id.tv_date_text);
            TextView tvWeatherInfoText = view.findViewById(R.id.tv_weather_info_text);
            TextView tvMaxText = view.findViewById(R.id.tv_max_text);
            TextView tvMinText = view.findViewById(R.id.tv_min_text);
            tvDateText.setText(forecast.date);
            tvWeatherInfoText.setText(forecast.more.info);
            tvMaxText.setText(forecast.temperature.max);
            tvMinText.setText(forecast.temperature.min);

            llForecastLayout.addView(view);
        }

        tvAqiText.setText(weather.aqi.city.aqi);
        tvPM25Text.setText(weather.aqi.city.pm25);

        tvComfortText.setText(weather.suggestion.comfort.info);
        tvSportText.setText(weather.suggestion.sport.info);
        tvWashCarText.setText(weather.suggestion.washCar.info);


    }
}
