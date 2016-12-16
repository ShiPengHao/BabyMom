package com.yimeng.babymom.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.AboutActivity;
import com.yimeng.babymom.activity.SettingActivity;
import com.yimeng.babymom.utils.MyApp;


/**
 * 我的对应fragment
 */
public class MyFragment extends BaseFragment {

    private LinearLayout ll_account_info;
    private LinearLayout ll_appointment_history;
    private LinearLayout ll_settings;
    private LinearLayout ll_about;
    private LinearLayout ll_quit;

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_my;
    }

    @Override
    protected void initView(View view) {
        ll_account_info = (LinearLayout) view.findViewById(R.id.ll_account_info);
        ll_appointment_history = (LinearLayout) view.findViewById(R.id.ll_appointment_history);
        ll_settings = (LinearLayout) view.findViewById(R.id.ll_settings);
        ll_about = (LinearLayout) view.findViewById(R.id.ll_about);
        ll_quit = (LinearLayout) view.findViewById(R.id.ll_quit);
    }

    @Override
    protected void setListener() {
        ll_account_info.setOnClickListener(this);
        ll_appointment_history.setOnClickListener(this);
        ll_settings.setOnClickListener(this);
        ll_about.setOnClickListener(this);
        ll_quit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_account_info:
//                startActivity(new Intent(activity, AccountInfoActivity.class));
                break;
            case R.id.ll_appointment_history:
//                startActivity(new Intent(activity, AppointHistoryActivity.class));
                break;
            case R.id.ll_settings:
                startActivity(new Intent(activity, SettingActivity.class));
                break;
            case R.id.ll_about:
                startActivity(new Intent(activity, AboutActivity.class));
                break;
            case R.id.ll_quit:
                showToast(getString(R.string.app_exit));
                MyApp.getAppContext().finish();
                break;
        }
    }
}
