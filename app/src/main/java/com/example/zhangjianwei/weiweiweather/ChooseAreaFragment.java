package com.example.zhangjianwei.weiweiweather;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zhangjianwei.weiweiweather.adapter.AreaAdapter;
import com.example.zhangjianwei.weiweiweather.adapter.MyItemDecoration;
import com.example.zhangjianwei.weiweiweather.db.Area;
import com.example.zhangjianwei.weiweiweather.db.City;
import com.example.zhangjianwei.weiweiweather.db.County;
import com.example.zhangjianwei.weiweiweather.db.Province;
import com.example.zhangjianwei.weiweiweather.util.HttpUtil;
import com.example.zhangjianwei.weiweiweather.util.Utility;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChooseAreaFragment extends Fragment {

    private static final String TAG = "ChooseAreaFragment";

    private List<Area> areaList = new ArrayList<>();

    private AreaAdapter areaAdapter;

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

    private Area selectedArea;

    private Button btBackButton;

    private TextView tvTitleText;

    private RecyclerView rvRecyclerView;

    private ProgressDialog progressDialog;


    public ChooseAreaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_choose_area, container, false);
        btBackButton = view.findViewById(R.id.bt_back_button);
        tvTitleText = view.findViewById(R.id.tv_title_text);
        rvRecyclerView = view.findViewById(R.id.rv_recycle_view);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvRecyclerView.setLayoutManager(layoutManager);

        areaAdapter = new AreaAdapter(areaList);

        rvRecyclerView.setAdapter(areaAdapter);

        rvRecyclerView.addItemDecoration(new MyItemDecoration(getContext(), MyItemDecoration.VERTICAL_LIST));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        areaAdapter.setOnItemClickListener(new AreaAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {

                selectedArea = areaList.get(position);
                if ("province".equals(selectedArea.getAreaCategory())) {
                    selectedArea.setAreaCategory("city");
                    queryCities(selectedArea);
                } else if ("city".equals(selectedArea.getAreaCategory())) {
                    selectedArea.setAreaCategory("county");
                    queryCounties(selectedArea);
                } else if ("county".equals(selectedArea.getAreaCategory())) {
                    if (getActivity() instanceof MainActivity) {
                        /*Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", selectedArea.getCounty().getWeatherId());
                        startActivity(intent);
                        getActivity().finish();*/

                        MainActivity activity=(MainActivity) getActivity();
                        WeatherFragment weatherFragment=(WeatherFragment) activity.getSupportFragmentManager().findFragmentById(R.id.fl_selected_fragment);
                        weatherFragment.drawerLayout.closeDrawers();
                        weatherFragment.requestWeather(selectedArea.getCounty().getWeatherId());

                    } else if (getActivity() instanceof WeatherActivity) {
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.dlDrawerLayout.closeDrawers();
                        activity.requestWeather(selectedArea.getCounty().getWeatherId());
                    }
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        });

        btBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ("city".equals(selectedArea.getAreaCategory())) {
                    queryProvinces();
                } else if ("county".equals(selectedArea.getAreaCategory())) {
                    queryCities(selectedArea);
                }
            }
        });

        queryProvinces();
    }

    private void queryProvinces() {
        tvTitleText.setText("中国");
        btBackButton.setVisibility(View.GONE);
        provinceList = LitePal.findAll(Province.class);
        if (provinceList.size() > 0) {
            areaList.clear();
            for (Province province : provinceList) {
                Area area = new Area();
                area.setAreaName(province.getProvinceName());
                area.setAreaCategory("province");
                area.setProvince(province);
                areaList.add(area);
            }
            Log.i(TAG, "local areaList provinces are: " + areaList.toString());
            areaAdapter.notifyDataSetChanged();
        } else {
            String provinceAddress = "http://guolin.tech/api/china/";
            selectedArea = new Area();
            selectedArea.setAreaCategory("province");
            queryFromServer(provinceAddress, selectedArea);
        }

    }

    private void queryCities(Area selectedArea) {
        Log.i(TAG, "selectedArea.getAreaCategory() is" + selectedArea.getAreaCategory());
        tvTitleText.setText(selectedArea.getProvince().getProvinceName());
        btBackButton.setVisibility(View.VISIBLE);
        cityList = LitePal.where("provinceId=?", String.valueOf(selectedArea.getProvince().getId())).find(City.class);
        if (cityList.size() > 0) {
            areaList.clear();
            for (City city : cityList) {
                Area area = new Area();
                area.setAreaName(city.getCityName());
                area.setAreaCategory("city");
                area.setProvince(selectedArea.getProvince());
                area.setCity(city);
                areaList.add(area);
            }

            selectedArea.setAreaCategory("city");
            areaAdapter.notifyDataSetChanged();
            rvRecyclerView.getLayoutManager().smoothScrollToPosition(rvRecyclerView, null, 0);
            //rvRecyclerView.set
            //areaAdapter.set
        } else {
            String cityAddress = "http://guolin.tech/api/china/" + selectedArea.getProvince().getProvinceCode();
            Log.i(TAG, "cityAddress is: " + cityAddress);
            queryFromServer(cityAddress, selectedArea);
        }
    }

    private void queryCounties(Area selectedArea) {
        tvTitleText.setText(selectedArea.getCity().getCityName());
        btBackButton.setVisibility(View.VISIBLE);
        countyList = LitePal.where("cityId=?", String.valueOf(selectedArea.getCity().getId())).find(County.class);
        if (countyList.size() > 0) {
            areaList.clear();
            for (County county : countyList) {
                Area area = new Area();
                area.setAreaName(county.getCountyName());
                area.setAreaCategory("county");
                area.setProvince(selectedArea.getProvince());
                area.setCity(selectedArea.getCity());
                area.setCounty(county);
                areaList.add(area);
            }
            areaAdapter.notifyDataSetChanged();
            //本行代码是为了让recyclerview列表每次都默认显示在第一行的位置
            rvRecyclerView.getLayoutManager().smoothScrollToPosition(rvRecyclerView,null,0);
        } else {
            String countyAddress = "http://guolin.tech/api/china/" + selectedArea.getProvince().getProvinceCode() + "/" + selectedArea.getCity().getCityCode();
            queryFromServer(countyAddress, selectedArea);
        }

    }


    private void queryFromServer(String address, final Area selectedArea) {
        showProgressDialog("正在加载中......");
        HttpUtil.sendRequestWithOkHttp(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.i(TAG, "response is: " + responseText);
                Boolean result = false;
                if ("province".equals(selectedArea.getAreaCategory())) {
                    result = Utility.parseProvinceResponse(responseText);
                } else if ("city".equals(selectedArea.getAreaCategory())) {
                    result = Utility.parseCityResponse(responseText, selectedArea.getProvince().getId());
                } else if ("county".equals(selectedArea.getAreaCategory())) {
                    result = Utility.parseCountyResponse(responseText, selectedArea.getCity().getId());
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(selectedArea.getAreaCategory())) {
                                queryProvinces();
                            } else if ("city".equals(selectedArea.getAreaCategory())) {
                                queryCities(selectedArea);
                            } else if ("county".equals(selectedArea.getAreaCategory())) {
                                queryCounties(selectedArea);
                            }
                        }
                    });
                }
            }
        });
        /*HttpUtil.sendRequestWithHttpURLConnection(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                Log.i(TAG, "response is: " + response);
                Boolean result = false;
                if ("province".equals(type)) {
                    result = Utility.parseProvinceWithJsonObject(response);
                } else if ("city".equals(type)) {

                } else if ("county".equals(type)) {

                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            } else if ("city".equals(type)) {

                            } else if ("county".equals(type)) {

                            }
                        }
                    });

                }


            }

            @Override
            public void onError(Exception e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });*/
    }

    void showProgressDialog(String message) {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setMessage(message);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        progressDialog.show();

    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }
}
