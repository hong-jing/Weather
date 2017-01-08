package com.example.hongjing.weather;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.hongjing.weather.activity.MainActivity;
import com.example.hongjing.weather.activity.WeatherActivity;
import com.example.hongjing.weather.db.City;
import com.example.hongjing.weather.db.County;
import com.example.hongjing.weather.db.Province;
import com.example.hongjing.weather.util.HttpUtil;
import com.example.hongjing.weather.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by hongjing on 2017/1/5.
 */

public class ChooseFragment extends Fragment {
    private Context mContext;

    private static final String TAG = "ChooseFragment";
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;
    private ProgressDialog mProgressDialog;
    private TextView mTitleText;
    private Button mBackButton;
    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<String> mDataList = new ArrayList<>();
    //省列表
    private List<Province> mProvinceList;
    //市列表
    private List<City> mCityList;
    //县列表
    private List<County> mCountyList;
    //选中的省份
    private Province mSelectedProvince;
    //选中的城市
    private City mSelectedCity;
    //当前选中的级别
    private int mCurrentLevel;


    @TargetApi(Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);
        mTitleText = (TextView) view.findViewById(R.id.title_text);
        mBackButton = (Button) view.findViewById(R.id.back_button);
        mListView = (ListView) view.findViewById(R.id.list_view);
        Log.d(TAG, "onCreateView: 初始化完成");
        mAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, mDataList);
        mListView.setAdapter(mAdapter);

        return view;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mCurrentLevel == LEVEL_PROVINCE){
                    mSelectedProvince = mProvinceList.get(position);
                    queryCities();
                }else if (mCurrentLevel == LEVEL_CITY){
                    mSelectedCity = mCityList.get(position);
                    queryCounties();
                }else if (mCurrentLevel == LEVEL_COUNTY){
                    String weatherId = mCountyList.get(position).getWeatherId();
                    if (getActivity() instanceof MainActivity){
                        Intent intent = new Intent(getActivity(), WeatherActivity.class);
                        intent.putExtra("weather_id", weatherId);
                        startActivity(intent);
                        getActivity().finish();
                    }else if (getActivity() instanceof WeatherActivity){
                        WeatherActivity activity = (WeatherActivity) getActivity();
                        activity.mDrawerLayout.closeDrawers();
                        activity.mSwipeRefreshLayout.setRefreshing(true);
                        activity.requestWeather(weatherId);

                    }

                }
            }
        });

        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentLevel == LEVEL_COUNTY){
                    queryCities();
                }else if (mCurrentLevel == LEVEL_CITY){
                    queryProvinces();
                }
            }
        });

        queryProvinces();
    }

    //查询市区
    private void queryCounties() {
        mTitleText.setText(mSelectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        mCountyList = DataSupport.where("cityid = ?",
            String.valueOf(mSelectedCity.getId())).find(County.class);

        if (mCountyList.size() > 0){
            mDataList.clear();
            for (County county : mCountyList){
                mDataList.add(county.getCountyName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_COUNTY;
        }else {
            int provinceCode = mSelectedProvince.getProvinceCode();
            int cityCode = mSelectedCity.getCityCode();
            String address = "http://guolin.tech/api/china/" + provinceCode + "/" + cityCode;
            queryFromServer(address, "county");
        }
    }
    //查询市
    private void queryCities() {
        mTitleText.setText(mSelectedProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        mCityList = DataSupport.where("provinceid = ?", String.valueOf(mSelectedProvince.getId())).find(City.class);
        Log.d(TAG, "queryCities: " + mCityList.toString());
        if (mCityList.size() > 0){
            mDataList.clear();
            for (City city : mCityList){
                mDataList.add(city.getCityName());
            }
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_CITY;
        }else {
            int provinceCode = mSelectedProvince.getProvinceCode();
            String address = "http://guolin.tech/api/china/" + provinceCode;
            queryFromServer(address, "city");
        }
    }
    //查询省
    private void queryProvinces(){
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.GONE);
        mProvinceList = DataSupport.findAll(Province.class);

        if (mProvinceList.size() > 0){
            mDataList.clear();
            for (Province province : mProvinceList){
                mDataList.add(province.getProvinceName());
            }
            //更新数据
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            mCurrentLevel = LEVEL_PROVINCE;
        }else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }

    //从服务器返回数据
    private void queryFromServer(String address,final String type) {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG, "onResponse Text:" + responseText);
                boolean result = false;

                if ("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);

                }else if ("city".equals(type)){
                    result = Utility.handleCityResponse(responseText, mSelectedProvince.getId());
                }else if ("county".equals(type)){
                    result = Utility.handleCountyResponce(responseText, mSelectedCity.getId());
                }

                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //关闭刷新栏
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });

    }

    //关闭进度条
    private void closeProgressDialog() {
        if (mProgressDialog != null){
            mProgressDialog.dismiss();
        }
    }

    //显示进度条
    private void showProgressDialog() {
        if (mProgressDialog == null){
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载...");
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }
}
