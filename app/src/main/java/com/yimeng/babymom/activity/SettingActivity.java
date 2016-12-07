package com.yimeng.babymom.activity;

import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ToggleButton;

import com.yimeng.babymom.R;


/**
 * 设置界面
 */

public class SettingActivity extends BaseActivity implements CompoundButton.OnCheckedChangeListener {

    private ToggleButton tb_sound;
    private ToggleButton tb_vibrate;
    private ToggleButton tb_autoLogin;
    private ImageView iv_back;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_setting;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);

        tb_sound = (ToggleButton) findViewById(R.id.tb_sound);
        tb_vibrate = (ToggleButton) findViewById(R.id.tb_vibrate);
        tb_autoLogin = (ToggleButton) findViewById(R.id.tb_autoLogin);

        tb_sound.setChecked(mPrefManager.getSettingMsgSound());
        tb_vibrate.setChecked(mPrefManager.getSettingMsgVibrate());
        tb_autoLogin.setChecked(mPrefManager.getAccountAutoLogin());
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        tb_sound.setOnCheckedChangeListener(this);
        tb_vibrate.setOnCheckedChangeListener(this);
        tb_autoLogin.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onInnerClick(int viewId) {

    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.tb_sound:
                mPrefManager.setSettingMsgSound(isChecked);
                break;
            case R.id.tb_vibrate:
                mPrefManager.setSettingMsgVibrate(isChecked);
                break;
            case R.id.tb_autoLogin:
                mPrefManager.setAccountAutoLogin(isChecked);
                break;
        }
    }
}
