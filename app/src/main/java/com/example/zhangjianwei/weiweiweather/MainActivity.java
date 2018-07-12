package com.example.zhangjianwei.weiweiweather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //SharedPreferences preferences = getSharedPreferences("weiweiweather", MODE_PRIVATE);
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (!TextUtils.isEmpty(preferences.getString("reponseWeatherText", null))) {
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
            this.finish();
        }
    }
}
