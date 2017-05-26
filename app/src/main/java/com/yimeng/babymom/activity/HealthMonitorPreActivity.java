package com.yimeng.babymom.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.SystemClock;

import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.MyApp;

/**
 * 胎教仪说明界面
 */
public class HealthMonitorPreActivity extends BaseActivity {

    private HeadsetPlugReceiver headsetPlugReceiver;

    public class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state") && intent.getIntExtra("state", 0) == 1) {
                isDestroy = true;
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

    private boolean isDestroy;

    @Override
    protected void setListener() {
        registerHeadsetPlugReceiver();
        new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(3000);
                if (isDestroy) {
                    return;
                }
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                showToast("测试时自动跳转");
                                startActivity(new Intent(MyApp.getAppContext(), HealthMonitorActivity.class));
                                finish();
                            }
                        }
                );
            }
        }).start();
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onDestroy() {
        if (null != headsetPlugReceiver) {
            unregisterReceiver(headsetPlugReceiver);
        }
        isDestroy = true;
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
