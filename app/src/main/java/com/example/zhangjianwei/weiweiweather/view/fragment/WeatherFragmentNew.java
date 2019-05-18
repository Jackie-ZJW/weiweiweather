package com.example.zhangjianwei.weiweiweather.view.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.zhangjianwei.weiweiweather.R;
import com.example.zhangjianwei.weiweiweather.interfaces.WeatherInterface;
import com.example.zhangjianwei.weiweiweather.interfaces.impl.WeatherImpl;
import com.example.zhangjianwei.weiweiweather.util.ContentUtil;
import com.example.zhangjianwei.weiweiweather.util.IconUtils;
import com.example.zhangjianwei.weiweiweather.util.LogUtil;
import com.example.zhangjianwei.weiweiweather.util.TransUnitUtil;
import com.example.zhangjianwei.weiweiweather.view.horizonview.HourlyForecastView;
import com.example.zhangjianwei.weiweiweather.view.horizonview.IndexHorizontalScrollView;
import com.example.zhangjianwei.weiweiweather.view.horizonview.ScrollWatched;
import com.example.zhangjianwei.weiweiweather.view.horizonview.ScrollWatcher;
import com.example.zhangjianwei.weiweiweather.view.skyview.SunView;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.air.forecast.AirForecast;
import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.alarm.Alarm;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.hourly.Hourly;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.NowBase;

public class WeatherFragmentNew extends Fragment implements WeatherInterface {

    private static final String TAG = "ZJW WeatherFragmentNew";

    private static final String PARAM = "LOCATION";

    List<ScrollWatcher> watcherList;

    private List<TextView> tvWeekList = new ArrayList<>();
    private List<ImageView> ivDayList = new ArrayList<>();
    private List<ImageView> ivNightList = new ArrayList<>();
    private List<TextView> tvMinList = new ArrayList<>();
    private List<TextView> tvMaxList = new ArrayList<>();
    private List<TextView> textViewList = new ArrayList<>();
    private ImageView ivTodayDay;
    private ImageView ivTodayNight;
    private TextView tvTodayTitle;
    private TextView tvForecastTitle;
    private TextView tvTodayMin;
    private TextView tvTodayMax;
    private TextView tvTodayHum;
    private TextView tvTodayRain;
    private TextView tvTodayPressure;
    private TextView tvTodayVisible;
    private TextView tvWindDir;
    private TextView tvWindSc;
    private TextView tvMin;
    private TextView tvMax;
    private TextView tvRain;
    private TextView tvHum;
    private TextView tvPressure;
    private TextView tvVisible;
    private TextView tvAirTitle;
    private TextView tvAir;
    private TextView tvAirNum;
    private TextView tvTodayPm25;
    private TextView tvTodayPm10;
    private TextView tvTodaySo2;
    private TextView tvTodayNo2;
    private TextView tvTodayCo;
    private TextView tvTodayO3;
    private TextView tvSunTitle;
    private RelativeLayout rvAir;
    private HourlyForecastView hourlyForecastView;
    private ScrollWatched watched;
    private TextView tvLineMin;
    private TextView tvLineMax;
    private boolean isEn = false;
    private SunView sunView;
    private SunView moonView;
    private String tz = "-8.0";
    private String currentTime;
    private String sunrise;
    private String sunset;
    private String moonRise;
    private String moonSet;
    private boolean hasAni = false;
    private TextView tvCond;
    private TextView tvTmp;
    private View rootView;
    private String todayMaxTmp;
    private String todayMinTmp;
    private Forecast weatherForecastBean;
    private Hourly weatherHourlyBean;
    private String nowTmp;
    private String location;
    private String language;
    private ImageView ivBack;
    private String condCode;
    private ImageView ivLine;
    private GridLayout gridAir;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvWeek1;
    private TextView tvAlarm;

    public static WeatherFragmentNew newInstance(String cityId) {
        WeatherFragmentNew fragmentNew = new WeatherFragmentNew();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM, cityId);
        fragmentNew.setArguments(bundle);

        return fragmentNew;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_weather_new, container, false);
        }

        //LogUtil.d(TAG, "WeatherFragmentNew getArguments().getString(PARAM) is: " + this.getArguments().getString(PARAM));

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtil.d(TAG, "WeatherFragmentNew getArguments().getString(PARAM) is: " + getArguments().getString(PARAM));
        if (getArguments() != null) {
            isEn = ContentUtil.APP_SETTING_LANG.equals("en") || ContentUtil.APP_SETTING_LANG.equals("sys") && ContentUtil.SYS_LANG.equals("en");
            location = getArguments().getString(PARAM);
            LogUtil.d(TAG, "WeatherFragmentNew location is: " + location);
            initObserver();
            initView(view);
            initData(location);
        }
    }

    private void initObserver() {
        watcherList = new ArrayList<>();
        watched = new ScrollWatched() {
            @Override
            public void addWatcher(ScrollWatcher watcher) {
                watcherList.add(watcher);
            }

            @Override
            public void removeWatcher(ScrollWatcher watcher) {
                watcherList.remove(watcher);
            }

            @Override
            public void notifyWatcher(int x) {
                for (ScrollWatcher watcher : watcherList) {
                    watcher.update(x);
                }
            }
        };
    }

    private void initView(View view) {

        language = ContentUtil.SYS_LANG;
        DateTime now = DateTime.now(DateTimeZone.UTC);
        float a = Float.valueOf(tz);
        float minute = a * 60;
        now = now.plusMinutes(((int) minute));
        currentTime = now.toString("HH:mm");
        tvWeekList = new ArrayList<>();
        ivDayList = new ArrayList<>();
        ivNightList = new ArrayList<>();
        tvMinList = new ArrayList<>();
        tvMaxList = new ArrayList<>();
        tvCond = view.findViewById(R.id.tv_today_cond);
        tvTmp = view.findViewById(R.id.tv_today_tmp);
        textViewList.add(tvTmp);
        ivBack = view.findViewById(R.id.iv_back);
        ivLine = view.findViewById(R.id.iv_line2);
        gridAir = view.findViewById(R.id.grid_air);

        tvWeek1 = view.findViewById(R.id.tv_week1);
        TextView tvWeek2 = view.findViewById(R.id.tv_week2);
        TextView tvWeek3 = view.findViewById(R.id.tv_week3);
        TextView tvWeek4 = view.findViewById(R.id.tv_week4);
        TextView tvWeek5 = view.findViewById(R.id.tv_week5);
        tvWeekList.add(tvWeek1);
        tvWeekList.add(tvWeek2);
        tvWeekList.add(tvWeek3);
        tvWeekList.add(tvWeek4);
        tvWeekList.add(tvWeek5);
        setWeeks(tvWeekList);
        ImageView iv1Day = view.findViewById(R.id.iv_1_day);
        ImageView iv2Day = view.findViewById(R.id.iv_2_day);
        ImageView iv3Day = view.findViewById(R.id.iv_3_day);
        ImageView iv4Day = view.findViewById(R.id.iv_4_day);
        ImageView iv5Day = view.findViewById(R.id.iv_5_day);
        ivDayList.add(iv1Day);
        ivDayList.add(iv2Day);
        ivDayList.add(iv3Day);
        ivDayList.add(iv4Day);
        ivDayList.add(iv5Day);

        ImageView iv1Night = view.findViewById(R.id.iv_1_night);
        ImageView iv2Night = view.findViewById(R.id.iv_2_night);
        ImageView iv3Night = view.findViewById(R.id.iv_3_night);
        ImageView iv4Night = view.findViewById(R.id.iv_4_night);
        ImageView iv5Night = view.findViewById(R.id.iv_5_night);
        ivNightList.add(iv1Night);
        ivNightList.add(iv2Night);
        ivNightList.add(iv3Night);
        ivNightList.add(iv4Night);
        ivNightList.add(iv5Night);

        TextView tv1MinTemp = view.findViewById(R.id.tv_1_min);
        TextView tv2MinTemp = view.findViewById(R.id.tv_2_min);
        TextView tv3MinTemp = view.findViewById(R.id.tv_3_min);
        TextView tv4MinTemp = view.findViewById(R.id.tv_4_min);
        TextView tv5MinTemp = view.findViewById(R.id.tv_5_min);
        tvMinList.add(tv1MinTemp);
        tvMinList.add(tv2MinTemp);
        tvMinList.add(tv3MinTemp);
        tvMinList.add(tv4MinTemp);
        tvMinList.add(tv5MinTemp);

        TextView tv1MaxTemp = view.findViewById(R.id.tv_1_max);
        TextView tv2MaxTemp = view.findViewById(R.id.tv_2_max);
        TextView tv3MaxTemp = view.findViewById(R.id.tv_3_max);
        TextView tv4MaxTemp = view.findViewById(R.id.tv_4_max);
        TextView tv5MaxTemp = view.findViewById(R.id.tv_5_max);
        tvMaxList.add(tv1MaxTemp);
        tvMaxList.add(tv2MaxTemp);
        tvMaxList.add(tv3MaxTemp);
        tvMaxList.add(tv4MaxTemp);
        tvMaxList.add(tv5MaxTemp);

        tvTodayTitle = view.findViewById(R.id.tv_today_title);
        tvForecastTitle = view.findViewById(R.id.tv_forecast_title);
        textViewList.add(tvTodayTitle);
        textViewList.add(tvForecastTitle);
        ivTodayDay = view.findViewById(R.id.iv_today_day);
        ivTodayNight = view.findViewById(R.id.iv_today_night);
        tvTodayMin = view.findViewById(R.id.tv_min_tmp);
        textViewList.add(tvTodayMin);
        tvTodayMax = view.findViewById(R.id.tv_max_tmp);
        textViewList.add(tvTodayMax);
        tvTodayHum = view.findViewById(R.id.tv_today_hum);
        textViewList.add(tvTodayHum);
        tvTodayRain = view.findViewById(R.id.tv_today_rain);
        textViewList.add(tvTodayRain);
        tvTodayPressure = view.findViewById(R.id.tv_today_pressure);
        textViewList.add(tvTodayPressure);
        tvTodayVisible = view.findViewById(R.id.tv_today_visible);
        textViewList.add(tvTodayVisible);
        tvWindDir = view.findViewById(R.id.tv_wind_dir);
        textViewList.add(tvWindDir);
        tvWindSc = view.findViewById(R.id.tv_wind_sc);
        textViewList.add(tvWindSc);

        tvMin = view.findViewById(R.id.tv_min);
        textViewList.add(tvMin);
        tvMax = view.findViewById(R.id.tv_max);
        textViewList.add(tvMax);
        tvRain = view.findViewById(R.id.tv_rain);
        textViewList.add(tvRain);
        tvHum = view.findViewById(R.id.tv_hum);
        textViewList.add(tvHum);
        tvPressure = view.findViewById(R.id.tv_pressure);
        textViewList.add(tvPressure);
        tvVisible = view.findViewById(R.id.tv_visible);
        textViewList.add(tvVisible);

        tvAirTitle = view.findViewById(R.id.air_title);
        textViewList.add(tvAirTitle);
        rvAir = view.findViewById(R.id.rv_air);
        tvAir = view.findViewById(R.id.tv_air);
        textViewList.add(tvAir);
        tvAirNum = view.findViewById(R.id.tv_air_num);
        textViewList.add(tvAirNum);

        TextView tvPm25 = view.findViewById(R.id.tv_pm25);
        textViewList.add(tvPm25);
        tvTodayPm25 = view.findViewById(R.id.tv_today_pm25);
        textViewList.add(tvTodayPm25);
        TextView tvPm10 = view.findViewById(R.id.tv_pm10);
        textViewList.add(tvPm10);
        tvTodayPm10 = view.findViewById(R.id.tv_today_pm10);
        textViewList.add(tvTodayPm10);
        TextView tvSo2 = view.findViewById(R.id.tv_so2);
        textViewList.add(tvSo2);
        tvTodaySo2 = view.findViewById(R.id.tv_today_so2);
        textViewList.add(tvTodaySo2);
        TextView tvNo2 = view.findViewById(R.id.tv_no2);
        textViewList.add(tvNo2);
        tvTodayNo2 = view.findViewById(R.id.tv_today_no2);
        textViewList.add(tvTodayNo2);
        TextView tvCo = view.findViewById(R.id.tv_co);
        textViewList.add(tvCo);
        tvTodayCo = view.findViewById(R.id.tv_today_co);
        textViewList.add(tvTodayCo);
        TextView tvO3 = view.findViewById(R.id.tv_o3);
        textViewList.add(tvO3);
        tvTodayO3 = view.findViewById(R.id.tv_today_o3);
        textViewList.add(tvTodayO3);
        tvLineMin = view.findViewById(R.id.tv_line_min_tmp);
        textViewList.add(tvLineMin);
        tvLineMax = view.findViewById(R.id.tv_line_max_tmp);
        textViewList.add(tvLineMax);
        tvAlarm = view.findViewById(R.id.tv_today_alarm);
        textViewList.add(tvAlarm);

        TextView tvFrom = view.findViewById(R.id.tv_from);
        tvFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startUri();
            }
        });

        tvSunTitle = view.findViewById(R.id.tv_sun_title);
        textViewList.add(tvSunTitle);
        sunView = view.findViewById(R.id.sun_view);
        moonView = view.findViewById(R.id.moon_view);

        IndexHorizontalScrollView horizontalScrollView = view.findViewById(R.id.hsv);
        hourlyForecastView = view.findViewById(R.id.hourly);
        horizontalScrollView.setToday24HourView(hourlyForecastView);

        watched.addWatcher(hourlyForecastView);

        //横向滚动监听
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            horizontalScrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
                @Override
                public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    watched.notifyWatcher(scrollX);
                }
            });
        }

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initData(location);
            }
        });

    }


    private void startUri() {
        Uri uri = Uri.parse("https://www.heweather.com");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    private void setWeeks(List<TextView> tvWeekList) {
        DateTime now = DateTime.now();
        tvWeek1.setText(getString(R.string.today));
        for (int i = 1; i < tvWeekList.size(); i++) {
            tvWeekList.get(i).setText(getWeek(now.plusDays(i).getDayOfWeek()));
        }
    }

    private String getWeek(int num) {

        String week = " ";
        if (ContentUtil.APP_SETTING_LANG.equals("en") || ContentUtil.APP_SETTING_LANG.equals("sys") && ContentUtil.SYS_LANG.equals("en")) {
            switch (num) {
                case 1:
                    week = "Mon";
                    break;
                case 2:
                    week = "Tues";
                    break;
                case 3:
                    week = "Wed";
                    break;
                case 4:
                    week = "Thur";
                    break;
                case 5:
                    week = "Fri";
                    break;
                case 6:
                    week = "Sat";
                    break;
                case 7:
                    week = "Sun";
                    break;
            }
        } else {
            switch (num) {
                case 1:
                    week = "周一";
                    break;
                case 2:
                    week = "周二";
                    break;
                case 3:
                    week = "周三";
                    break;
                case 4:
                    week = "周四";
                    break;
                case 5:
                    week = "周五";
                    break;
                case 6:
                    week = "周六";
                    break;
                case 7:
                    week = "周日";
                    break;
            }
        }

        return week;
    }

    private void initData(String location) {
        WeatherImpl weatherImpl = new WeatherImpl(this.getActivity(), this);
        /*weatherImpl.getWeatherHourly(location);
        weatherImpl.getAirForecast(location);
        weatherImpl.getAirNow(location);
        weatherImpl.getAlarm(location);
        weatherImpl.getWeatherForecast(location);*/
        weatherImpl.getWeatherNow(location);
    }

    @Override
    public void getWeatherNow(Now bean) {
        if (bean != null && bean.getNow() != null) {
            NowBase now = bean.getNow();
            String rain = now.getPcpn();
            String hum = now.getHum();
            String pres = now.getPres();
            String vis = now.getVis();
            String windDir = now.getWind_dir();
            String windSc = now.getWind_sc();
            String condTxt = now.getCond_txt();
            condCode = now.getCond_code();
            nowTmp = now.getTmp();
            tvCond.setText(condTxt);
            //tvTmp.setText(nowTmp + "°");
            tvTmp.setText(nowTmp);
            if (ContentUtil.APP_SETTING_UNIT.equals("hua")) {
                tvTmp.setText(TransUnitUtil.getF(nowTmp) + "°");
            }
            tvTodayRain.setText(rain + "mm");
            tvTodayPressure.setText(pres + "HPA");
            tvTodayHum.setText(hum + "%");
            tvTodayVisible.setText(vis + "KM");
            tvWindDir.setText(windDir);
            tvWindSc.setText(windSc + "级");
            DateTime nowTime = DateTime.now();
            int hourOfDay = nowTime.getHourOfDay();
            if (hourOfDay > 6 && hourOfDay < 19) {
                ivBack.setImageResource(IconUtils.getDayBack(condCode));
            } else {
                ivBack.setImageResource(IconUtils.getNightBack(condCode));
            }
            if (isEn) {
                tvWindSc.setText("Level" + windSc);
            }
            swipeRefreshLayout.setRefreshing(false);
        }


    }

    @Override
    public void getWeatherForecast(Forecast bean) {

    }

    @Override
    public void getAlarm(Alarm bean) {

    }

    @Override
    public void getAirNow(AirNow bean) {

    }

    @Override
    public void getAirForecast(AirForecast bean) {

    }

    @Override
    public void getWeatherHourly(Hourly bean) {

    }
}