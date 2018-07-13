package com.example.zhangjianwei.weiweiweather;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<Fragment> fragmentList = new ArrayList<>();

    private RadioGroup rgButtonGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rgButtonGroup = findViewById(R.id.rg_button_group);

        MyOnCheckedChangeListener listener = new MyOnCheckedChangeListener();
        rgButtonGroup.setOnCheckedChangeListener(listener);

        rgButtonGroup.check(R.id.rb_1);
        //SharedPreferences preferences = getSharedPreferences("weiweiweather", MODE_PRIVATE);

        /*SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        if (!TextUtils.isEmpty(preferences.getString("reponseWeatherText", null))) {
            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
            this.finish();
        }*/
    }

    private class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

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
