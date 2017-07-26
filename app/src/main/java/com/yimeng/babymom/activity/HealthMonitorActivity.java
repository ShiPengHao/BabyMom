package com.yimeng.babymom.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.widget.LinearLayout;

import com.yimeng.babymom.R;


/**
 * 健康监测页面
 */

public class HealthMonitorActivity extends BaseActivity {

    private LinearLayout ll_fhr_start;
    private LinearLayout ll_fhr_history;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_health_monitor;
    }

    @Override
    protected void initView() {
        ll_fhr_start = (LinearLayout) findViewById(R.id.ll_fhr_start);
        ll_fhr_history = (LinearLayout) findViewById(R.id.ll_fhr_history);
    }

    @Override
    protected void setListener() {
        ll_fhr_start.setOnClickListener(this);
        ll_fhr_history.setOnClickListener(this);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.ll_fhr_start:
                goFHRMonitor();
                break;
            case R.id.ll_fhr_history:
                goFHRHistory();
                break;
        }
    }

    /**
     * 跳转到胎心检测历史页面
     */
    private void goFHRHistory() {
        startActivity(new Intent(this, FHRHistoryActivity.class));
    }

    /**
     * 跳转到胎心检测页面
     */
    private void goFHRMonitor() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()) {
            startActivity(new Intent(this, FHRMonitorActivity.class));
        } else {
            startActivity(new Intent(this, FHRIntroduceActivity.class));
        }
    }
}
