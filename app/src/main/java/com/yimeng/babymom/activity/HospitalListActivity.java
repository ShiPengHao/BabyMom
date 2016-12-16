package com.yimeng.babymom.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.bean.HospitalBean;
import com.yimeng.babymom.utils.MyConstant;

import java.util.ArrayList;


/**
 * 医院列表的activity
 */

public class HospitalListActivity extends BaseActivity implements AdapterView.OnItemClickListener {
    private ListView lv_hospital;

    private ArrayAdapter<HospitalBean> mHospitalAdapter;
    private ArrayList<HospitalBean> mHospitalList = new ArrayList<>();
    private int mRequestCode;

    @Override
    protected void onInnerClick(int viewId) {

    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_hospital_list;
    }

    @Override
    protected void initView() {
        lv_hospital = (ListView) findViewById(R.id.lv_hospital);
    }

    @Override
    protected void setListener() {
        mHospitalAdapter = new ArrayAdapter<>(this, R.layout.item_text1, mHospitalList);
        lv_hospital.setAdapter(mHospitalAdapter);
        lv_hospital.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        try {
            Intent intent = getIntent();
            mRequestCode = intent.getIntExtra(MyConstant.REQUEST_CODE, 0);
            ArrayList<HospitalBean> hospital = (ArrayList<HospitalBean>) intent.getSerializableExtra("hospital");
            mHospitalList.clear();
            mHospitalList.addAll(hospital);
            mHospitalAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int hospital_id = mHospitalList.get(position).hospital_id;
        switch (mRequestCode) {
            case MyConstant.REQUEST_HOSPITAL:// 如果启动意图是获得医院信息，则返回医院信息
                setResult(RESULT_OK, new Intent().putExtra("hospitalId", hospital_id));
                break;
            default:// 默认跳转到医院页面
                startActivity(new Intent(this, DepartmentActivity.class).putExtra("hospitalId", hospital_id));
                break;

        }
        finish();
    }


}
