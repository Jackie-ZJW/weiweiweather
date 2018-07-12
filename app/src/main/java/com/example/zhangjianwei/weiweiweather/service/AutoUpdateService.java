package com.example.zhangjianwei.weiweiweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.example.zhangjianwei.weiweiweather.gson.Weather;
import com.example.zhangjianwei.weiweiweather.util.HttpUtil;
import com.example.zhangjianwei.weiweiweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {

    private static final String TAG = "zhangjianwei123";

    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        updateWeather();
        updateBingPic();

        //AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        AlarmManager alarmManager = getSystemService(AlarmManager.class);
        int oneMinute = 1 * 30 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + oneMinute;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        alarmManager.cancel(pi);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        Log.e(TAG, "后台自动每分钟更新一次。。。");

        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeather() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this);
        String responseWeatherText = pref.getString("reponseWeatherText", null);
        Log.e(TAG, "responseWeatherText is: " + responseWeatherText);
        if (!TextUtils.isEmpty(responseWeatherText)) {
            //有缓存时直接解析天气数据
            Weather weather = Utility.parseWeatherResponse(responseWeatherText);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=35719fcccc6e44a9b55b6fdfdd0655e4";
            HttpUtil.sendRequestWithOkHttp(weatherUrl, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Log.e(TAG, "responseText is: " + responseText);
                    Weather weather1 = Utility.parseWeatherResponse(responseText);
                    if (weather1 != null && "ok".equals(weather1.status)) {
                        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editor.putString("reponseWeatherText", responseText);
                        editor.apply();
                        Log.e(TAG, "后台自动每分钟更新天气成功！");
                    }
                }
            });
        }
    }

    private void updateBingPic() {
        String bingPicUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendRequestWithOkHttp(bingPicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String bingPicResponseText = response.body().string();
                Log.e(TAG, "bingPicResponseText is: " + bingPicResponseText);
                if (!TextUtils.isEmpty(bingPicResponseText)) {
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("bingPic_ResponseText", bingPicResponseText);
                    editor.apply();
                    Log.e(TAG, "后台自动每分钟更新背景图片成功！");
                }
            }
        });
    }
}
