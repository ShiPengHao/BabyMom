package com.yimeng.babymom.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.bean.AddressBean;
import com.yimeng.babymom.bean.HospitalBean;
import com.yimeng.babymom.interFace.AddressInterface;
import com.yimeng.babymom.interFace.HospitalAreaInterface;
import com.yimeng.babymom.task.AddressAreaTask;
import com.yimeng.babymom.task.AddressCityTask;
import com.yimeng.babymom.task.AddressProvinceTask;
import com.yimeng.babymom.task.HospitalAreaTask;
import com.yimeng.babymom.utils.JsonUtils;
import com.yimeng.babymom.utils.MyConstant;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 选择地址的activity
 */
public class AddressListActivity extends BaseActivity implements AdapterView.OnItemClickListener, AddressInterface, HospitalAreaInterface {
    private ListView lv_province;
    private ListView lv_city;
    private ListView lv_area;
    private View view_divider_city;

    private ArrayList<AddressBean> mProvinceList = new ArrayList<>();
    private ArrayList<AddressBean> mCityList = new ArrayList<>();
    private ArrayList<AddressBean> mAreaList = new ArrayList<>();
    private ArrayList<HospitalBean> mHospitalList = new ArrayList<>();
    private ArrayAdapter<AddressBean> mProvinceAdapter;
    private ArrayAdapter<AddressBean> mCityAdapter;
    private ArrayAdapter<AddressBean> mAreaAdapter;
    HashMap<String, Object> params = new HashMap<>();
    private int mProvincePosition;
    private int mCityPosition;
    private int mRequestCode;
    private AsyncTask<Object, Object, String> mProvinceTask;
    private AsyncTask<Object, Object, String> mCityTask;
    private AsyncTask<Object, Object, String> mAreaTask;
    private AsyncTask<Object, Object, String> mHospitalTask;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_address_list;
    }

    @Override
    protected void initView() {
        lv_province = (ListView) findViewById(R.id.lv_province);
        lv_city = (ListView) findViewById(R.id.lv_city);
        lv_area = (ListView) findViewById(R.id.lv_area);
        view_divider_city = findViewById(R.id.view_divider_city);
        view_divider_city.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void setListener() {
        mProvinceAdapter = new ArrayAdapter<>(this, R.layout.item_text1, mProvinceList);
        lv_province.setAdapter(mProvinceAdapter);
        lv_province.setOnItemClickListener(this);
        mCityAdapter = new ArrayAdapter<>(this, R.layout.item_text1, mCityList);
        lv_city.setAdapter(mCityAdapter);
        lv_city.setOnItemClickListener(this);
        mAreaAdapter = new ArrayAdapter<>(this, R.layout.item_text1, mAreaList);
        lv_area.setAdapter(mAreaAdapter);
        lv_area.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        try {
            mRequestCode = getIntent().getIntExtra(MyConstant.REQUEST_CODE, MyConstant.REQUEST_CITY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestProvince();
    }

    @Override
    protected void onInnerClick(int viewId) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.lv_province:// 点击某个省，获得市的信息，同时清空区的信息
                mProvincePosition = position;
                params.clear();
                params.put(AddressCityTask.PROVINCECODE, mProvinceList.get(position).code);
                requestCity();
                view_divider_city.setVisibility(View.INVISIBLE);
                if (mAreaList.size() > 0) {
                    mAreaList.clear();
                    mAreaAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.lv_city:// 点击市，如果启动意图是获得城市名字，则返回城市名字，否则级联获得区县信息
                switch (mRequestCode) {
                    case MyConstant.REQUEST_CITY:
                        String cityName = mCityList.get(position).name;
                        feedBackCityName(cityName);
                        break;
                    default:
                        mCityPosition = position;
                        params.clear();
                        params.put(AddressAreaTask.CITYCODE, mCityList.get(position).code);
                        requestArea();
                        break;
                }
                break;
            case R.id.lv_area:// 点击县区，请求该区县医院信息，完成后跳转到医院列表界面
                params.clear();
                params.put(HospitalAreaTask.PROVINCE, mProvinceList.get(mProvincePosition).code);
                params.put(HospitalAreaTask.CITY, mCityList.get(mCityPosition).code);
                params.put(HospitalAreaTask.AREA, mAreaList.get(position).code);
                requestAreaHospital();
                break;
        }
    }

    public void feedBackCityName(String cityName) {
        setResult(RESULT_OK, new Intent().putExtra("city", cityName));
        finish();
    }

    @Override
    public void requestProvince() {
        if (mProvinceTask != null)
            mProvinceTask.cancel(true);
        mProvinceTask = new AddressProvinceTask(this, null).execute(AddressProvinceTask.METHOD);
    }

    @Override
    public void onProvinceResult(String result) {
        try {
            JsonUtils.parseListResult(mProvinceList, AddressBean.class, result);
        } catch (Exception e) {
            showToast(getString(R.string.connect_error));
            e.printStackTrace();
        }
        mProvinceAdapter.notifyDataSetChanged();
    }

    @Override
    public void requestCity() {
        if (mCityTask != null)
            mCityTask.cancel(true);
        if (mAreaTask != null)
            mAreaTask.cancel(true);
        mCityTask = new AddressCityTask(this, null).execute(AddressCityTask.METHOD, params);
    }

    @Override
    public void onCityResult(String result) {
        try {
            JsonUtils.parseListResult(mCityList, AddressBean.class, result);
        } catch (Exception e) {
            showToast(getString(R.string.connect_error));
            e.printStackTrace();
        }
        mCityAdapter.notifyDataSetChanged();
        view_divider_city.setVisibility(mCityList.size() > 0 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public void requestArea() {
        if (mAreaTask != null)
            mAreaTask.cancel(true);
        mAreaTask = new AddressAreaTask(this, null).execute(AddressAreaTask.METHOD, params);
    }

    @Override
    public void onAreaResult(String result) {
        try {
            JsonUtils.parseListResult(mAreaList, AddressBean.class, result);
        } catch (Exception e) {
            showToast(getString(R.string.connect_error));
            e.printStackTrace();
        }
        mAreaAdapter.notifyDataSetChanged();
    }

    @Override
    public void requestAreaHospital() {
        if (mHospitalTask != null)
            mHospitalTask.cancel(true);
        mHospitalTask = new HospitalAreaTask(this, null).execute(HospitalAreaTask.METHOD, params);
    }

    @Override
    public void onHospitalResult(String result) {
        try {
            JsonUtils.parseListResult(mHospitalList, HospitalBean.class, result);
        } catch (Exception e) {
            showToast(getString(R.string.connect_error));
            e.printStackTrace();
        }
        if (mHospitalList.size() > 0)
            goToHospitalList();
        else
            showToast(getString(R.string.no_hospital));
    }

    /**
     * 跳转到医院列表
     */
    public void goToHospitalList() {
        startActivityForResult(new Intent(this, HospitalListActivity.class).putExtra(MyConstant.REQUEST_CODE, mRequestCode), mRequestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    protected void onDestroy() {
        if (mProvinceTask != null)
            mProvinceTask.cancel(true);
        if (mCityTask != null)
            mCityTask.cancel(true);
        if (mAreaTask != null)
            mAreaTask.cancel(true);
        if (mHospitalTask != null)
            mHospitalTask.cancel(true);
        super.onDestroy();
    }


}
