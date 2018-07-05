package com.example.zhangjianwei.weiweiweather;


import android.app.ProgressDialog;
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
import com.example.zhangjianwei.weiweiweather.db.Area;
import com.example.zhangjianwei.weiweiweather.db.City;
import com.example.zhangjianwei.weiweiweather.db.County;
import com.example.zhangjianwei.weiweiweather.db.Province;
import com.example.zhangjianwei.weiweiweather.util.HttpCallbackListener;
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

    public static final int LEVEL_PROVINCE = 0;

    public static final int LEVEL_CITY = 1;

    public static final int LEVEL_COUNTY = 2;

    private int currentLevel;

    private List<Area> areaList = new ArrayList<>();

    private AreaAdapter areaAdapter;

    private List<Province> provinceList;

    private List<City> cityList;

    private List<County> countyList;

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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
                areaList.add(area);
            }
            Log.i(TAG,"areaList size is: "+areaList.size());
            areaAdapter.notifyDataSetChanged();
            //rvRecyclerView.getLayoutManager().smoothScrollToPosition(rvRecyclerView,null,0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String provinceAddress = "http://guolin.tech/api/china/";
            queryFromServer(provinceAddress, "province");
        }

    }

    private void queryFromServer(String address, final String type) {
        showProgressDialog("正在加载中......");
        /*HttpUtil.sendRequestWithOkHttp(address, new Callback() {
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
                if ("province".equals(type)) {
                    result = Utility.parseProvinceWithJsonObject(responseText);
                }

                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)) {
                                queryProvinces();
                            }
                        }
                    });
                }
            }
        });*/
        HttpUtil.sendRequestWithHttpURLConnection(address, new HttpCallbackListener() {
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
        });
    }

    private void showProgressDialog(String message) {
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
