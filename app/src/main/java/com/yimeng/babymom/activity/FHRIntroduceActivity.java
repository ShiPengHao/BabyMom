package com.yimeng.babymom.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.yimeng.babymom.R;

/**
 * 胎教仪连接说明界面，使用{@link Intent#ACTION_HEADSET_PLUG}广播实时检测耳机插孔状态，如果连接则跳转到{@link FHRMonitorActivity}页面
 */
public class FHRIntroduceActivity extends BaseActivity {

    private HeadsetPlugReceiver mHeadsetPlugReceiver;

    public class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra("state") && intent.getIntExtra("state", 0) == 1) {
                startActivity(new Intent(context, FHRMonitorActivity.class));
                finish();
            }
        }
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_health_monitor_introduce;
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
        if (null != mHeadsetPlugReceiver) {
            unregisterReceiver(mHeadsetPlugReceiver);
        }
        super.onDestroy();
    }

    @Override
    protected void onInnerClick(int viewId) {

    }

    private void registerHeadsetPlugReceiver() {
        mHeadsetPlugReceiver = new HeadsetPlugReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
        registerReceiver(mHeadsetPlugReceiver, intentFilter);
    }
}
