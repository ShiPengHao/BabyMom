package com.yimeng.babymom.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;

import com.yimeng.babymom.R;


/**
 * 健康监测页面
 */

public class HealthMonitorActivity extends BaseActivity {

    private LinearLayout ll_fhr_start;
    private LinearLayout ll_fhr_history;
    private final int REQUEST_CODE = 321;

    /**
     * 所需权限授权标志，true为已授权
     */
    private boolean isPermitted;
    /**
     * 要申请的权限
     */
    private final String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS};

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
        checkPermissions();
    }

    @Override
    protected void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.ll_fhr_start:
                if (isPermitted) {
                    goFHRMonitor();
                } else {
                    showToast(getString(R.string.permission_audio));
                }
                break;
            case R.id.ll_fhr_history:
                goFHRHistory();
                break;
        }
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int i = ContextCompat.checkSelfPermission(this, PERMISSIONS[0]);
            if (i == PackageManager.PERMISSION_GRANTED) {
                isPermitted = true;
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_CODE);
            }
        } else {
            isPermitted = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE && grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            isPermitted = true;
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
