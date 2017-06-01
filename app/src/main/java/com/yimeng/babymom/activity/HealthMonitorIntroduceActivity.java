package com.yimeng.babymom.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.yimeng.babymom.R;

/**
 * 胎教仪连接说明界面
 */
public class HealthMonitorIntroduceActivity extends BaseActivity {

    private HeadsetPlugReceiver headsetPlugReceiver;

    public class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state") && intent.getIntExtra("state", 0) == 1) {
                startActivity(new Intent(context, HealthMonitorActivity.class));
                finish();
            }
        }
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_health_monitor_pre;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void setListener() {
        registerHeadsetPlugReceiver();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        if (null != headsetPlugReceiver) {
            unregisterReceiver(headsetPlugReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected void onInnerClick(int viewId) {

    }

    private void registerHeadsetPlugReceiver() {
        headsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(headsetPlugReceiver, intentFilter);
    }
}
