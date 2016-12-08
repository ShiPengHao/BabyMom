package com.yimeng.babymom.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;

import com.yimeng.babymom.R;
import com.yimeng.babymom.interFace.SplashInterface;
import com.yimeng.babymom.task.LoginTask;
import com.yimeng.babymom.task.UpdateTask;
import com.yimeng.babymom.utils.DownloadManager;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.MyNetUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.HashMap;


/**
 * 闪屏界面，处理apk版本更新，自动登陆，页面跳转等逻辑
 */
public class SplashActivity extends BaseActivity implements SplashInterface {

    private static Handler handler = new Handler();
    private String mDownloadUrl;
    private AlertDialog mUpdateDialog;
    private int mApkSize;
    private RequestCall mRequestCall;
    private HashMap<String, Object> mParams = new HashMap<>();
    private AsyncTask<Object, Object, String> mLoginTask;
    private AsyncTask<Object, Object, String> mUpdateTask;
    private String mPhone;
    private String mPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchEvent();
    }

    @Override
    public int setStatusBarColor() {
        return Color.parseColor("#3F3B54");
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_splash;
    }

    @Override
    protected void initView() {
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


    public void copyRes2Local() {

    }

    public void dispatchEvent() {
        if (mPrefManager.getAccountFirstRunning()) {
            copyRes2Local();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToIntroduce();
                }
            }, 2000);
        } else
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkUpdate();
                }
            }, 2000);

    }

    public void autoLogin() {
        mPhone = mPrefManager.getAccountUsername();
        mPassword = mPrefManager.getAccountPassword();
        if (isEmpty(mPhone) || isEmpty(mPassword))
            goToLogin();
        else
            login();
    }

    public void login() {
        if (mLoginTask != null)
            mLoginTask.cancel(true);
        mParams.clear();
        mParams.put("phone", mPhone);
        mParams.put("password", mPassword);
        mLoginTask = new LoginTask(this).execute("login", mParams);
    }

    public void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void goToHome() {
//        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    public void goToIntroduce() {
        startActivity(new Intent(this, IntroduceActivity.class));
        finish();
    }

    public void checkUpdate() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("app_type", MyConstant.ANDROID);
        mUpdateTask = new UpdateTask(this).execute("getUpdate", map);
    }

    public void showUpdateDialog() {
        mUpdateDialog = new AlertDialog.Builder(SplashActivity.this).setTitle("发现新版本!")
                // 限制对话框取消动作
                .setCancelable(false)
                .setPositiveButton("我要！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downPackage();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("下次再说", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dispatchLogin();
                        dialog.dismiss();
                    }
                })
                .create();
        String wifiTip = "";
        if (!MyNetUtils.isWifi()) {
            wifiTip = "检测到您的手机当前并非在wifi环境下，";
        }
        mUpdateDialog.setMessage(String.format("新版本安装包大小为%s，%s确定更新？", android.text.format.Formatter.formatFileSize(this, mApkSize), wifiTip));
        mUpdateDialog.show();
    }

    /**
     * 判断是否自动登陆，是则尝试自动登陆，否则跳转登陆页面
     */
    public void dispatchLogin() {
        if (mPrefManager.getAccountAutoLogin())
            autoLogin();
        else
            goToLogin();
    }

    public void downPackage() {
        if (mRequestCall != null) {
            mRequestCall.cancel();
        }
        mRequestCall = OkHttpUtils.get().url(mDownloadUrl).build().connTimeOut(300000).readTimeOut(300000).writeTimeOut(300000);
        DownloadManager.downPackage(activity, mRequestCall, getString(R.string.apk_name), mApkSize, new DownloadManager.ErrorCallback() {
            @Override
            public void onError() {
                dispatchLogin();
            }

            @Override
            public void onCancel() {
                dispatchLogin();
            }
        });
    }

    public void setApkSize(int mApkSize) {
        this.mApkSize = mApkSize;
    }

    public void setDownloadUrl(String mDownloadUrl) {
        this.mDownloadUrl = mDownloadUrl;
    }

    @Override
    protected void onDestroy() {
        if (mUpdateDialog != null && mUpdateDialog.isShowing())
            mUpdateDialog.dismiss();
        if (mRequestCall != null)
            mRequestCall.cancel();
        if (mUpdateTask != null)
            mUpdateTask.cancel(true);
        if (mLoginTask != null)
            mLoginTask.cancel(true);
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }
}
