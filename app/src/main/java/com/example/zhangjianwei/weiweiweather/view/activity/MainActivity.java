package com.example.zhangjianwei.weiweiweather.view.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhangjianwei.weiweiweather.view.fragment.MineFragment;
import com.example.zhangjianwei.weiweiweather.R;
import com.example.zhangjianwei.weiweiweather.view.fragment.RealtimeWeatherPicFragment;
import com.example.zhangjianwei.weiweiweather.view.fragment.WeatherFragment;
import com.example.zhangjianwei.weiweiweather.adapter.MyViewPagerAdapter;
import com.example.zhangjianwei.weiweiweather.db.Newcity;
import com.example.zhangjianwei.weiweiweather.gson.Weather;
import com.example.zhangjianwei.weiweiweather.util.DisplayUtil;
import com.example.zhangjianwei.weiweiweather.util.HttpUtil;
import com.example.zhangjianwei.weiweiweather.util.LogUtil;
import com.example.zhangjianwei.weiweiweather.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends BaseActivity {

    private List<Fragment> fragmentList = new ArrayList<>();

    private String address;

    private String current_province;

    private String current_city;

    private String cid;

    private ViewPager viewPager;

    private MenuItem menuItem;

    private BottomNavigationView bottomNavigationView;

    private RelativeLayout rvTitle;

    private List<Newcity> newcityList;

    private Newcity currentCity;

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

        //设置状态栏透明
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        viewPager = findViewById(R.id.view_pager);

        rvTitle = findViewById(R.id.rv_title);

        setupViewPager(viewPager);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        //bottomNavigationView默认在item>=4时选中某个item时item会自动变大，而且效果体验不佳，会影响ViewPager滑动切换时的效果，故可以利用反射去掉

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
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
        });

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

        LogUtil.e("zhangjianwei1234 current_province: ", current_province);
        LogUtil.e("zhangjianwei1234 current_city: ", current_city);

        //请求和风天气官网的城市信息

        current_city = URLEncoder.encode(current_city);

        //String address1 = "https://search.heweather.net/find?" + current_city + "&" + "35719fcccc6e44a9b55b6fdfdd0655e4";
        String address1 = "https://search.heweather.net/find?" + "location=" + current_city + "&" + "key=" + "35719fcccc6e44a9b55b6fdfdd0655e4";

        HttpUtil.sendRequestWithOkHttp(address1, new Callback() {
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
                    LogUtil.e("zhangjianwei1234 status: ", status);


                    JSONArray basic = object1.getJSONArray("basic");

                    //JSONArray cityInfos = new JSONArray(responseText);
                    LogUtil.e("zhangjianwei1234 cityInfos: ", basic.toString());
                    for (int i = 0; i < basic.length(); i++) {
                        JSONObject cityObject = basic.getJSONObject(i);
                        Newcity newcity = new Newcity();
                        newcity.setCid(cityObject.getString("cid"));
                        newcity.setLocation(cityObject.getString("location"));
                        newcity.setParent_city(cityObject.getString("parent_city"));
                        newcity.setAdmin_area(cityObject.getString("admin_area"));
                        newcity.save();
                        LogUtil.e("zhangjianwei1234 newcity: ", newcity.toString());

                        newcityList = LitePal.where("location=?", "深圳").find(Newcity.class);

                        currentCity = newcityList.get(0);

                        //获取当前所在城市的天气id
                        cid = currentCity.getCid();
                    }
                } catch (JSONException e) {
                    LogUtil.e("zhangjianwei1234 newcity: ", "遇到异常");
                    e.printStackTrace();
                }


                LogUtil.e("zhangjianwei1234 current_city_info: ", responseText);
            }
        });




        //SharedPreferences preferences = getSharedPreferences("weiweiweather", MODE_PRIVATE);

        /*SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (!TextUtils.isEmpty(preferences.getString("reponseWeatherText", null))) {
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
            this.finish();
        }*/

        setMargins(viewPager, 0, getStatusBarHeight(this) + DisplayUtil.dip2px(this, 52), 0, 0);
        setMargins(rvTitle, 0, getStatusBarHeight(this), 0, 0);
    }

    //兼容全面屏的状态栏高度
    public void setMargins(View view, int l, int t, int r, int b) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(l, t, r, b);
            view.requestLayout();
        }
    }

    //获取状态栏的高度
    private static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    private void setupViewPager(ViewPager viewPager) {

        fragmentList.add(new WeatherFragment());
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
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    bottomNavigationView.getMenu().getItem(0).setChecked(false);
                }

                menuItem = bottomNavigationView.getMenu().getItem(i);
                menuItem.setChecked(true);

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
        LogUtil.e("zhangjianwei1234 cityid", cid);

        HttpUtil.sendRequestWithOkHttp(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LogUtil.e("zhangjianwei1234", "请求当前城市天气数据失败！");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String weatherResponse = response.body().string();
                final Weather weather = Utility.parseWeatherResponse(weatherResponse);
                LogUtil.e("zhangjianwei weather:", weather.toString());

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

    public class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        public MyOnCheckedChangeListener() {
            fragmentList.add(new WeatherFragment());
            fragmentList.add(new RealtimeWeatherPicFragment());
            fragmentList.add(new MineFragment());
        }

        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            switch (i) {
                case R.id.rb_1:
                    ft.replace(R.id.fl_selected_fragment, fragmentList.get(0));
                    /*SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    String weatherText = pref.getString("weather", null);*/
                    requestWeather((WeatherFragment) fragmentList.get(0), cid);
                    //showWeatherInfo(weather, weatherFragment);
                    break;
                case R.id.rb_2:
                    ft.replace(R.id.fl_selected_fragment, fragmentList.get(1));
                    break;
                case R.id.rb_3:
                    ft.replace(R.id.fl_selected_fragment, fragmentList.get(2));
                    break;
                default:
                    break;
            }

            ft.commit();
        }
    }


}
