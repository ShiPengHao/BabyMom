package com.yimeng.babymom.activity;

import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.DensityUtil;

/**
 * 用户协议activity
 */

public class UserProtocolActivity extends BaseActivity {

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_user_protocol;
    }

    @Override
    protected void initView() {
        TextView tv_protocol = (TextView) findViewById(R.id.tv_protocol);
        tv_protocol.setTextSize((float) DensityUtil.SCREEN_WIDTH / 40f);
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onInnerClick(int viewId) {

    }

}
