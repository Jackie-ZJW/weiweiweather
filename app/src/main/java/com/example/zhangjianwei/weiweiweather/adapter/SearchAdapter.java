package com.example.zhangjianwei.weiweiweather.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhangjianwei.weiweiweather.R;
import com.example.zhangjianwei.weiweiweather.db.CityList;
import com.example.zhangjianwei.weiweiweather.db.Newcity;
import com.example.zhangjianwei.weiweiweather.interfaces.DataUtil;
import com.example.zhangjianwei.weiweiweather.util.ContentUtil;
import com.example.zhangjianwei.weiweiweather.util.LogUtil;
import com.example.zhangjianwei.weiweiweather.util.SpUtils;
import com.example.zhangjianwei.weiweiweather.view.activity.SearchActivity;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.basic.Basic;
import interfaces.heweather.com.interfacesmodule.bean.search.Search;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

/**
 * 最近搜索
 */
public class SearchAdapter extends Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ZJW SearchAdapter";

    private List<Newcity> data;
    private SearchActivity activity;
    private String searchText;
    private Lang lang;
    private CityList cityList = new CityList();
    private boolean isSearching;

    public SearchAdapter(SearchActivity activity, List<Newcity> data, String searchText, boolean isSearching) {
        this.activity = activity;
        this.data = data;
        this.searchText = searchText;
        this.isSearching = isSearching;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (ContentUtil.APP_SETTING_LANG.equals("en") || ContentUtil.APP_SETTING_LANG.equals("sys") && ContentUtil.SYS_LANG.equals("en")) {
            lang = Lang.ENGLISH;
        } else {
            lang = Lang.CHINESE_SIMPLIFIED;
        }
        View view;
        if (isSearching) {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_searching, viewGroup, false);
        } else {
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_history, viewGroup, false);
        }
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder myViewHolder, @SuppressLint("RecyclerView") final int i) {
        MyViewHolder viewHolder = (MyViewHolder) myViewHolder;
        View itemView = viewHolder.itemView;
        String name = data.get(i).getCityName();
        int x = name.indexOf("-");
        String parentCity = name.substring(0, x);
        String location = name.substring(x + 1);

        String cityName = location + "，" + parentCity + "，" + data.get(i).getAdminArea() + "，" + data.get(i).getCnty();
        if (TextUtils.isEmpty(data.get(i).getAdminArea())) {
            cityName = location + "，" + parentCity + "，" + data.get(i).getCnty();
        }
        if (!TextUtils.isEmpty(cityName)) {
            viewHolder.tvCity.setText(cityName);
            if (cityName.contains(searchText)) {
                int index = cityName.indexOf(searchText);
                //创建一个 SpannableString对象
                SpannableString sp = new SpannableString(cityName);
                //设置高亮样式一
                sp.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.light_text_color)), index, index + searchText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.tvCity.setText(sp);
            }
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String cid = data.get(i).getCityId();
                LogUtil.d(TAG, "SearchAdapter onBindViewHolder i= " + i);
                LogUtil.d(TAG, "SearchAdapter onBindViewHolder getCityName()= " + data.get(i).getCityName());
                LogUtil.d(TAG, "SearchAdapter lang is: " + lang);
                if (lang.equals(Lang.CHINESE_SIMPLIFIED)) {
                    saveData(Lang.ENGLISH, "NewcityEn", cid);
                    saveBean("Newcity", cid, i);
                } else {
                    saveData(Lang.CHINESE_SIMPLIFIED, "Newcity", cid);
                    saveBean("NewcityEn", cid, i);
                }

            }
        });
    }


    private void saveBean(final String key, String cid, int x) {
        List<Newcity> citys = new ArrayList<>();
        cityList = SpUtils.getBean(activity, key, CityList.class);
        if (cityList != null && cityList.getCityList() != null) {
            citys = cityList.getCityList();
        }

        LogUtil.d(TAG, "saveBean cityBeanList.getCityBeans().size() is: " + citys.size());

        for (int i = 0; i < citys.size(); i++) {
            LogUtil.d(TAG, "saveBean citys.get(i).getCityId() is: " + citys.get(i).getCityId());
            LogUtil.d(TAG, "saveBean cid is: " + cid);

            if (citys.get(i).getCityId().equals(cid)) {
                citys.remove(i);
            }
        }
        if (citys.size() == 10) {
            citys.remove(9);
        }

        LogUtil.d(TAG, "saveBean citys.size() is: " + citys.size());

        citys.add(0, data.get(x));

        LogUtil.d(TAG, "saveBean citys.size() is: " + citys.size());

        CityList cityBeans = new CityList();
        cityBeans.setCityList(citys);
        SpUtils.saveBean(activity, key, cityBeans);

    }

    private void saveData(Lang lang, final String key, final String cid) {
        HeWeather.getSearch(activity, cid, "cn,overseas", 1, lang, new HeWeather.OnResultSearchBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                LogUtil.d(TAG, "SearchAdapter saveData onError: " + throwable.toString());
                activity.onBackPressed();
            }

            @Override
            public void onSuccess(Search search) {
                List<Newcity> citys = new ArrayList<>();
                if (!search.getStatus().equals("unknown city") && !search.getStatus().equals("noData")) {
                    List<Basic> basic = search.getBasic();
                    Basic basicData = basic.get(0);
                    String parentCity = basicData.getParent_city();
                    String adminArea = basicData.getAdmin_area();
                    String cnty = basicData.getCnty();
                    if (TextUtils.isEmpty(parentCity)) {
                        parentCity = adminArea;
                    }
                    if (TextUtils.isEmpty(adminArea)) {
                        parentCity = cnty;
                    }
                    Newcity cityBean = new Newcity();
                    cityBean.setCityName(parentCity + " - " + basicData.getLocation());
                    cityBean.setCityId(basicData.getCid());
                    cityBean.setCnty(cnty);
                    cityBean.setAdminArea(adminArea);

                    LogUtil.d(TAG, "saveData onSuccess CityName is: " + parentCity + " - " + basicData.getLocation());
                    LogUtil.d(TAG, "saveData onSuccess cid is: " + basicData.getCid());

                    cityList = SpUtils.getBean(activity, key, CityList.class);
                    if (cityList != null && cityList.getCityList() != null) {
                        citys = cityList.getCityList();
                    }
                    for (int i = 0; i < citys.size(); i++) {
                        if (citys.get(i).getCityId().equals(cid)) {
                            citys.remove(i);
                        }
                    }
                    if (citys.size() == 10) {
                        citys.remove(9);
                    }
                    citys.add(0, cityBean);
                    CityList cityBeans = new CityList();
                    cityBeans.setCityList(citys);
                    SpUtils.saveBean(activity, key, cityBeans);
                    LogUtil.d(TAG, "saveData onSuccess key is: " + key);
                    DataUtil.setCid(cid);
                    activity.onBackPressed();
                }
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCity;

        MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCity = itemView.findViewById(R.id.tv_item_history_city);

        }
    }
}
