package com.example.zhangjianwei.weiweiweather;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class WeatherActivity extends AppCompatActivity {

    private Button btNavButton;

    private TextView tvCityTitle;

    private TextView tvUpdateTime;

    private TextView tvDegreeText;

    private TextView tvWeatherInfoText;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
    }
}
