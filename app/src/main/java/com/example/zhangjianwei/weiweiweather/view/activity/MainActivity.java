package com.example.zhangjianwei.weiweiweather.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.example.zhangjianwei.weiweiweather.R;
import com.example.zhangjianwei.weiweiweather.adapter.MyViewPagerAdapter;
import com.example.zhangjianwei.weiweiweather.adapter.ViewPagerAdapter;
import com.example.zhangjianwei.weiweiweather.db.CityList;
import com.example.zhangjianwei.weiweiweather.db.Newcity;
import com.example.zhangjianwei.weiweiweather.gson.Weather;
import com.example.zhangjianwei.weiweiweather.interfaces.DataInterface;
import com.example.zhangjianwei.weiweiweather.interfaces.DataUtil;
import com.example.zhangjianwei.weiweiweather.util.ContentUtil;
import com.example.zhangjianwei.weiweiweather.util.DisplayUtil;
import com.example.zhangjianwei.weiweiweather.util.HttpUtil;
import com.example.zhangjianwei.weiweiweather.util.IconUtils;
import com.example.zhangjianwei.weiweiweather.util.LogUtil;
import com.example.zhangjianwei.weiweiweather.util.SpUtils;
import com.example.zhangjianwei.weiweiweather.util.Utility;
import com.example.zhangjianwei.weiweiweather.view.fragment.MineFragment;
import com.example.zhangjianwei.weiweiweather.view.fragment.RealtimeWeatherPicFragment;
import com.example.zhangjianwei.weiweiweather.view.fragment.WeatherFragment;
import com.example.zhangjianwei.weiweiweather.view.fragment.WeatherFragmentNew;

import org.joda.time.DateTime;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.basic.Basic;
import interfaces.heweather.com.interfacesmodule.bean.search.Search;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity implements View.OnClickListener, DataInterface {

    private static final String TAG = "ZJW MainActivity";
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //private List<Newcity> newcityList;
    CityList cityList = new CityList();
    private List<Fragment> fragmentList = new ArrayList<>();
    private String address;
    private String current_province;
    private String current_city;
    private String cid;
    private ViewPager viewPager;
    private LinearLayout llRound;
    private TextView tvLocation;
    private ImageView ivLoc;
    private MenuItem menuItem;
    //private BottomNavigationView bottomNavigationView;
    private RelativeLayout rlTitle;
    private Newcity currentCity;
    private List<String> locations;
    private List<String> locationsEn;
    private List<String> cityIds;
    private List<Fragment> fragments;
    private List<Fragment> fragmentList1 = new ArrayList<>();
    private List<Fragment> fragmentList2 = new ArrayList<>();
    private RadioGroup radioGroup;

    private FrameLayout frameLayout;
    private RelativeLayout rlPicTitle;
    private RelativeLayout rlMineTitle;

    private int mNum = 0;
    private String condCode;
    private ImageView ivBack;
    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation aMapLocation) {
            if (aMapLocation != null) {
                if (aMapLocation.getErrorCode() == 0) {
                    //定位成功回调信息，设置相关消息
                    ContentUtil.NOW_LON = aMapLocation.getLongitude();
                    ContentUtil.NOW_LAT = aMapLocation.getLatitude();
                    LogUtil.d(TAG, "aMapLocation success! " + "NOW LON: " + ContentUtil.NOW_LON + "NOW LAT: " + ContentUtil.NOW_LAT);
                    getNowCity(true);
                    mLocationClient.onDestroy();

                } else {
                    LogUtil.d(TAG, "aMapLocation fail ! " + "NOW LON: " + ContentUtil.NOW_LON + " NOW LAT: " + ContentUtil.NOW_LAT);
                    getNowCity(true);
                    mLocationClient.onDestroy();
                }
            }
        }
    };
    private ImageView ivAdd;
    private ImageView ivSet;

    //获取状态栏的高度
    private static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //设置系统状态栏与activity标题栏融为一体，背景颜色透明。
        /*if (Build.VERSION.SDK_INT > 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }*/

        //setContentView(R.layout.activity_main);

        //新的主界面activity布局
        setContentView(R.layout.activity_main_new);

        //设置系统状态栏透明
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        viewPager = findViewById(R.id.view_pager);

        tvLocation = findViewById(R.id.tv_location);

        ivLoc = findViewById(R.id.iv_loc);

        llRound = findViewById(R.id.ll_round);

        rlTitle = findViewById(R.id.rl_title);

        ivBack = findViewById(R.id.iv_main_back);

        ivAdd = findViewById(R.id.iv_add_city);

        ivSet = findViewById(R.id.iv_set);

        radioGroup = findViewById(R.id.rg_bottom_item);

        frameLayout = findViewById(R.id.fl_two_fragments);

        frameLayout.setVisibility(View.GONE);

        rlPicTitle = findViewById(R.id.rl_pic_title);
        rlMineTitle = findViewById(R.id.rl_mine_title);

        ivAdd.setOnClickListener(this);
        ivSet.setOnClickListener(this);

        //setupViewPager(viewPager);

        //bottomNavigationView = findViewById(R.id.bottom_navigation);

        //bottomNavigationView默认在item>=4时选中某个item时item会自动变大，而且效果体验不佳，会影响ViewPager滑动切换时的效果，故可以利用反射去掉

        /*bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.item_weather_info:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.item_weather_pic:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.item_mine_info:
                        viewPager.setCurrentItem(2);
                        break;
                }
                return false;
            }
        });*/

        //获取系统状态栏高度
        /*private static int getStatusBarHeight (Context context){

            return 1;
        }*/

        //LitePal.getDatabase();

        Intent intent = getIntent();

        address = intent.getStringExtra("address");

        //获取当前地址所在省份,城市名称。
        current_province = address.substring(address.indexOf("国") + 1, address.indexOf("省"));
        current_city = address.substring(address.indexOf("省") + 1, address.indexOf("市"));

        LogUtil.d(TAG, "current_province: " + current_province);
        LogUtil.d(TAG, "current_city: " + current_city);

        //请求和风天气官网的城市信息

        current_city = URLEncoder.encode(current_city);

        //String address1 = "https://search.heweather.net/find?" + current_city + "&" + "35719fcccc6e44a9b55b6fdfdd0655e4";
        String address1 = "https://search.heweather.net/find?" + "location=" + current_city + "&" + "key=" + "35719fcccc6e44a9b55b6fdfdd0655e4";

        /*HttpUtil.sendRequestWithOkHttp(address1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();

                //处理返回的城市信息responseText
                try {
                    JSONObject jsonObject = new JSONObject(responseText);
                    JSONArray heWeather6 = jsonObject.getJSONArray("HeWeather6");
                    JSONObject object1 = heWeather6.getJSONObject(0);
                    String status = object1.getString("status");
                    LogUtil.d(TAG, "status: " + status);


                    JSONArray basic = object1.getJSONArray("basic");

                    //JSONArray cityInfos = new JSONArray(responseText);
                    LogUtil.d(TAG, "cityInfos: " + basic.toString());
                    for (int i = 0; i < basic.length(); i++) {
                        JSONObject cityObject = basic.getJSONObject(i);
                        Newcity newcity = new Newcity();
                        newcity.setCid(cityObject.getString("cid"));
                        newcity.setLocation(cityObject.getString("location"));
                        newcity.setParent_city(cityObject.getString("parent_city"));
                        newcity.setAdmin_area(cityObject.getString("admin_area"));
                        newcity.save();
                        LogUtil.d(TAG, "newcity: " + newcity.toString());

                        newcityList = LitePal.where("location=?", "深圳").find(Newcity.class);

                        currentCity = newcityList.get(0);

                        //获取当前所在城市的天气id
                        cid = currentCity.getCid();
                    }
                } catch (JSONException e) {
                    LogUtil.d(TAG, "newcity: " + "遇到异常");
                    e.printStackTrace();
                }


                LogUtil.d(TAG, "current_city_info: " + responseText);
            }
        });*/


        //SharedPreferences preferences = getSharedPreferences("weiweiweather", MODE_PRIVATE);

        /*SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (!TextUtils.isEmpty(preferences.getString("reponseWeatherText", null))) {
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
            this.finish();
        }*/

        setMargins(viewPager, 0, getStatusBarHeight(this) + DisplayUtil.dip2px(this, 50), 0, DisplayUtil.dip2px(this, 48));
        setMargins(rlTitle, 0, getStatusBarHeight(this), 0, 0);

        initFragments(true);

        radioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
        radioGroup.check(R.id.rb_weather_now);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataUtil.setDataInterface(this);
        /*if (!ContentUtil.APP_PRI_TESI.equalsIgnoreCase(ContentUtil.APP_SETTING_TESI)) {
            if (fragments != null && fragments.size() > 0) {
                for (Fragment fragment : fragments) {
                    WeatherFragment weatherFragment = (WeatherFragment) fragment;
                    weatherFragment.changeTextSize();
                }
            }
            if ("small".equalsIgnoreCase(ContentUtil.APP_SETTING_TESI)) {
                tvLocation.setTextSize(15);
            } else if ("large".equalsIgnoreCase(ContentUtil.APP_SETTING_TESI)) {
                tvLocation.setTextSize(17);
            } else {
                tvLocation.setTextSize(16);
            }
            ContentUtil.APP_PRI_TESI = ContentUtil.APP_SETTING_TESI;
        }
        if (ContentUtil.CHANGE_LANG) {
            if (ContentUtil.SYS_LANG.equalsIgnoreCase("en")) {
                changeLang(Lang.ENGLISH);
            } else {
                changeLang(Lang.CHINESE_SIMPLIFIED);
            }
            ContentUtil.CHANGE_LANG = false;
        }
        if (ContentUtil.CITY_CHANGE) {
            initFragments(true);
            ContentUtil.CITY_CHANGE = false;
        }
        if (ContentUtil.UNIT_CHANGE) {
            for (Fragment fragment : fragments) {
                WeatherFragment weatherFragment = (WeatherFragment) fragment;
                weatherFragment.changeUnit();
            }
            ContentUtil.UNIT_CHANGE = false;
        }*/
    }

    private void initFragments(boolean first) {

        cityList = SpUtils.getBean(MainActivity.this, "Newcity", CityList.class);
        CityList cityListEn = SpUtils.getBean(MainActivity.this, "NewcityEn", CityList.class);
        CityList cityList = SpUtils.getBean(MainActivity.this, "Newcity", CityList.class);

        locationsEn = new ArrayList<>();
        locations = new ArrayList<>();

        if (cityListEn != null) {
            for (Newcity newcity : cityListEn.getCityList()) {
                String cityName = newcity.getCityName();
                String cid = newcity.getCityId(); //我自己测试添加
                LogUtil.d(TAG, "MainActivity initFragments cityListEn cityName is: " + cityName);
                LogUtil.d(TAG, "MainActivity initFragments cityListEn cid is: " + cid);
                locationsEn.add(cityName);
            }
        }

        if (cityList != null) {
            for (Newcity newcity : cityList.getCityList()) {
                String cityName = newcity.getCityName();
                String cid = newcity.getCityId(); //我自己测试添加
                LogUtil.d(TAG, "MainActivity initFragments cityList cityName is: " + cityName);
                LogUtil.d(TAG, "MainActivity initFragments cityList cid is: " + cid);
                locations.add(cityName);
            }
        }

        cityIds = new ArrayList<>();
        fragments = new ArrayList<>();

        if (first) {
            initLocation();
        } else {
            getNowCity(false);
        }

    }

    private void initLocation() {
        //初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());

        //声明AMapLocationClientOption对象
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
        mLocationOption.setInterval(10000);
        //设置定位超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //设置定位监听
        mLocationClient.setLocationListener(mLocationListener);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();

    }

    private void getNowCity(final boolean first) {

        Lang lang;
        if (ContentUtil.APP_SETTING_LANG.equals("en") || ContentUtil.APP_SETTING_LANG.equals("sys") && ContentUtil.SYS_LANG.equals("en")) {
            lang = Lang.ENGLISH;
        } else {
            lang = Lang.CHINESE_SIMPLIFIED;
        }

        HeWeather.getSearch(this, ContentUtil.NOW_LON + "," + ContentUtil.NOW_LAT, "cn,overseas", 3, lang, new HeWeather.OnResultSearchBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                LogUtil.d(TAG, "getNowCity getSearch error, error info: " + throwable.toString());
            }

            @Override
            public void onSuccess(Search search) {
                LogUtil.d(TAG, "getSearch success !");
                Basic basic = search.getBasic().get(0);
                String cid = basic.getCid();
                String location = basic.getLocation();

                LogUtil.d(TAG, "cid is: " + cid + " location is: " + location);

                if (first) {
                    ContentUtil.NOW_CITY_ID = cid;
                    ContentUtil.NOW_CITY_NAME = location;
                }

                List<Newcity> newcities = new ArrayList<>();
                Newcity newcity = new Newcity();
                newcity.setCityName(location);
                newcity.setCityId(cid);

                locations.add(0, location);
                locationsEn.add(0, location);

                if (cityList != null && cityList.getCityList() != null && cityList.getCityList().size() > 0) {
                    newcities = cityList.getCityList();
                    newcities.add(0, newcity);
                } else {
                    newcities.add(newcity);
                }

                tvLocation.setText(location);
                getData(newcities, first);


            }
        });
    }

    private void getData(List<Newcity> newcities, boolean first) {
        fragments = new ArrayList<>();
        llRound.removeAllViews();
        LogUtil.d(TAG, "newcities size is: " + newcities.size());
        for (Newcity newcity : newcities) {
            String cityId = newcity.getCityId();
            LogUtil.d(TAG, "cityId is: " + cityId);
            cityIds.add(cityId);
            WeatherFragmentNew weatherFragmentNew = WeatherFragmentNew.newInstance(cityId);
            fragments.add(weatherFragmentNew);
        }

        LogUtil.d(TAG, "getData fragments.size() is: " + fragments.size());

        if (cityIds.get(0).equalsIgnoreCase(ContentUtil.NOW_CITY_ID)) {
            ivLoc.setVisibility(View.VISIBLE);
        } else {
            ivLoc.setVisibility(View.INVISIBLE);
        }

        //配置切换不同城市时底部的小圆点布局
        View view;
        for (int i = 0; i < fragments.size(); i++) {
            //创建底部指示器（小圆点）
            view = new View(MainActivity.this);
            view.setBackgroundResource(R.drawable.background);
            view.setEnabled(false);
            //设置宽高
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DisplayUtil.dp2px(this, 4), DisplayUtil.dp2px(this, 4));
            //设置间距
            if (fragments.get(i) != fragments.get(0)) {
                layoutParams.leftMargin = 10;
            }
            //添加到LinearLayout
            llRound.addView(view, layoutParams);
        }

        viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
        //第一次显示小白点
        llRound.getChildAt(0).setEnabled(true);
        mNum = 0;
        if (fragments.size() == 1) {
            llRound.setVisibility(View.GONE);
        } else {
            llRound.setVisibility(View.VISIBLE);
        }

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                if (cityIds.get(i).equalsIgnoreCase(ContentUtil.NOW_CITY_ID)) {
                    ivLoc.setVisibility(View.VISIBLE);
                } else {
                    ivLoc.setVisibility(View.INVISIBLE);
                }

                llRound.getChildAt(mNum).setEnabled(false);
                llRound.getChildAt(i).setEnabled(true);
                mNum = i;

                tvLocation.setText(locations.get(i));
                if (ContentUtil.SYS_LANG.equalsIgnoreCase("en")) {
                    tvLocation.setText(locationsEn.get(i));
                }

                LogUtil.d(TAG, "MainActivity getData tvLocation text is: " + locations.get(i));

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        LogUtil.d(TAG, "MainActivity getData fragments.size() is: " + fragments.size());

        if (!first && fragments.size() > 1) {
            viewPager.setCurrentItem(1);
            getNow(cityIds.get(1), false);
        } else {
            viewPager.setCurrentItem(0);
            getNow(ContentUtil.NOW_LON + "," + ContentUtil.NOW_LAT, true);
        }


    }

    private void getNow(String location, final boolean nowCity) {

        HeWeather.getSearch(this, location, "cn,overseas", 3, Lang.CHINESE_SIMPLIFIED, new HeWeather.OnResultSearchBeansListener() {
            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onSuccess(Search search) {
                Basic basic = search.getBasic().get(0);
                String cid = basic.getCid();
                String location = basic.getLocation();
                if (nowCity) {
                    ContentUtil.NOW_CITY_ID = cid;
                    ContentUtil.NOW_CITY_NAME = location;
                    if (cityIds != null && cityIds.size() > 0) {
                        cityIds.add(0, cid);
                        cityIds.remove(1);
                    }
                }
                HeWeather.getWeatherNow(MainActivity.this, cid, new HeWeather.OnResultWeatherNowBeanListener() {
                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onSuccess(Now now) {
                        condCode = now.getNow().getCond_code();
                        DateTime nowTime = DateTime.now();
                        int hourOfDay = nowTime.getHourOfDay();
                        if (hourOfDay > 6 && hourOfDay < 19) {
                            ivBack.setImageResource(IconUtils.getDayBack(condCode));
                        } else {
                            ivBack.setImageResource(IconUtils.getNightBack(condCode));
                        }
                    }

                });
            }
        });
    }

    //兼容全面屏的状态栏高度
    public void setMargins(View view, int l, int t, int r, int b) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    private void setupViewPager(ViewPager viewPager) {

        fragmentList.add(new WeatherFragmentNew());
        fragmentList.add(new RealtimeWeatherPicFragment());
        fragmentList.add(new MineFragment());

        MyViewPagerAdapter myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), fragmentList);

        viewPager.setAdapter(myViewPagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                // ?
                /*if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                menuItem = bottomNavigationView.getMenu().getItem(i);
                menuItem.setChecked(true);*/

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        viewPager.setCurrentItem(0);
    }


    public void requestWeather(final WeatherFragment weatherFragment, String cid) {
        //查询当前所在城市的天气信息
        String weatherUrl = "http://guolin.tech/api/weather?" + "cityid=" + cid + "&key=35719fcccc6e44a9b55b6fdfdd0655e4";
        LogUtil.d(TAG, "cityid: " + cid);

        HttpUtil.sendRequestWithOkHttp(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.d(TAG, "请求当前城市天气数据失败！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String weatherResponse = response.body().string();
                final Weather weather = Utility.parseWeatherResponse(weatherResponse);
                LogUtil.d("zhangjianwei weather:", weather.toString());

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit();
                            editor.putString("weather", weatherResponse);
                            editor.apply();
                            showWeatherInfo(weather, weatherFragment);
                        }

                    }
                });


            }
        });
    }

    private void showWeatherInfo(Weather weather, WeatherFragment weatherFragment) {
        String cityName = weather.basic.cityName;
        String degree = weather.now.temperature + "℃";
        View weatherFragmentView = weatherFragment.getView();
        TextView city_name = weatherFragmentView.findViewById(R.id.tv_title_city);
        city_name.setText(weather.basic.cityName);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_set:

                break;
            case R.id.iv_add_city:
                startActivity(new Intent(this, SearchActivity.class));
                break;
        }

    }

    @Override
    public void setCid(String cid) {
        LogUtil.d(TAG, "MainActivity setCid cid is: " + cid);
        initFragments(false);
    }

    @Override
    public void deleteID(int index) {

    }

    @Override
    public void changeBack(String condCode) {

    }

    public class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        public MyOnCheckedChangeListener() {
            RealtimeWeatherPicFragment realtimeWeatherPicFragment = new RealtimeWeatherPicFragment();
            fragmentList.add(realtimeWeatherPicFragment);
            MineFragment mineFragment = new MineFragment();
            fragmentList.add(mineFragment);
        }

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (i) {
                case R.id.rb_weather_now:
                    /*ft.replace(R.id.fl_selected_fragment, fragmentList.get(0));
                     *//*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    String weatherText = pref.getString("weather", null);*//*
                    requestWeather((WeatherFragment) fragmentList.get(0), cid);
                    //showWeatherInfo(weather, weatherFragment);*/

                    if (frameLayout.VISIBLE == View.VISIBLE || rlPicTitle.VISIBLE == View.VISIBLE || rlMineTitle.VISIBLE == View.VISIBLE) {
                        frameLayout.setVisibility(View.GONE);
                        rlPicTitle.setVisibility(View.GONE);
                        rlMineTitle.setVisibility(View.GONE);
                    }

                    viewPager.setVisibility(View.VISIBLE);
                    rlTitle.setVisibility(View.VISIBLE);
                    //viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager(), fragments));
                    break;
                case R.id.rb_weather_pic:
                    if (viewPager.VISIBLE == View.VISIBLE || rlTitle.VISIBLE == View.VISIBLE || rlMineTitle.VISIBLE == View.VISIBLE) {
                        viewPager.setVisibility(View.GONE);
                        rlTitle.setVisibility(View.GONE);
                        rlMineTitle.setVisibility(View.GONE);
                    }
                    frameLayout.setVisibility(View.VISIBLE);
                    rlPicTitle.setVisibility(View.VISIBLE);
                    setMargins(frameLayout, 0, getStatusBarHeight(getApplicationContext()) + DisplayUtil.dip2px(getApplicationContext(), 50), 0, DisplayUtil.dip2px(getApplicationContext(), 48));
                    setMargins(rlPicTitle, 0, getStatusBarHeight(getApplicationContext()), 0, 0);
                    ft.replace(R.id.fl_two_fragments, fragmentList.get(0));
                    //viewPager.setAdapter(new ViewPagerAdapterPic(getSupportFragmentManager(), fragmentList1));
                    break;
                case R.id.rb_mine:
                    if (viewPager.VISIBLE == View.VISIBLE || rlTitle.VISIBLE == View.VISIBLE || rlPicTitle.VISIBLE == View.VISIBLE) {
                        viewPager.setVisibility(View.GONE);
                        rlTitle.setVisibility(View.GONE);
                        rlPicTitle.setVisibility(View.GONE);
                    }
                    frameLayout.setVisibility(View.VISIBLE);
                    rlMineTitle.setVisibility(View.VISIBLE);
                    setMargins(frameLayout, 0, getStatusBarHeight(getApplicationContext()) + DisplayUtil.dip2px(getApplicationContext(), 50), 0, DisplayUtil.dip2px(getApplicationContext(), 48));
                    setMargins(rlMineTitle, 0, getStatusBarHeight(getApplicationContext()), 0, 0);
                    ft.replace(R.id.fl_two_fragments, fragmentList.get(1));
                    break;
                default:
                    break;
            }

            ft.commit();
        }
    }


}
