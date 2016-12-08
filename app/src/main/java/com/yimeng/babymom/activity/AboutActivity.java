package com.yimeng.babymom.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.text.format.Formatter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.interFace.AboutInterface;
import com.yimeng.babymom.task.UpdateTask;
import com.yimeng.babymom.utils.DownloadManager;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.MyNetUtils;
import com.yimeng.babymom.utils.MyToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.request.RequestCall;

import java.util.HashMap;
import java.util.Random;


/**
 * 关于 界面
 */
public class AboutActivity extends BaseActivity implements AboutInterface {

    private TextView tv_version;
    private LinearLayout ll_check_update;
    private LinearLayout ll_suggest;
    private int mApkSize;
    private String mDownloadUrl;
    private AlertDialog mUpdateDialog;
    private RequestCall mRequestCall;
    private static final int VIDEO_SIZE = 1024 * 1024 * (2 + new Random().nextInt(6)) + new Random().nextInt(1000);
    private AsyncTask<Object, Object, String> mUpdateTask;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        tv_version = (TextView) findViewById(R.id.tv_version);
        ll_check_update = (LinearLayout) findViewById(R.id.ll_check_update);
        ll_suggest = (LinearLayout) findViewById(R.id.ll_suggest);
    }

    @Override
    protected void setListener() {
        ll_check_update.setOnClickListener(this);
        ll_suggest.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        try {
            PackageInfo pi = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null && pi.versionName != null) {
                tv_version.setText(pi.versionName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.ll_suggest:
                goToSuggest();
                break;
            case R.id.ll_check_update:
                checkUpdate();
                break;
        }
    }

    public void goToSuggest() {
        startActivity(new Intent(this, SuggestActivity.class));
    }

    public void checkUpdate() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("app_type", MyConstant.ANDROID);
        mUpdateTask = new UpdateTask(this, ll_check_update).execute("getUpdate", map);
    }

    public void showUpdateDialog() {
        mUpdateDialog = new AlertDialog.Builder(this).setTitle("发现新版本!")
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
                        dialog.dismiss();
                    }
                })
                .create();
        String wifiTip = "";
        if (!MyNetUtils.isWifi()) {
            wifiTip = "检测到您的手机当前并非在wifi环境下，";
        }
        mUpdateDialog.setMessage(String.format("新版本安装包大小为%s，%s确定更新？", Formatter.formatFileSize(this, mApkSize), wifiTip));
        mUpdateDialog.show();
    }


    public void setApkSize(int apkSize) {
        this.mApkSize = apkSize;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.mDownloadUrl = downloadUrl;
    }

    public void downPackage() {
        if (mRequestCall != null) {
            mRequestCall.cancel();
        }
        mRequestCall = OkHttpUtils.get().url(mDownloadUrl).build().connTimeOut(300000).readTimeOut(300000).writeTimeOut(300000);
        DownloadManager.downPackage(activity, mRequestCall, getString(R.string.app_name), mApkSize == 0 ? VIDEO_SIZE : mApkSize, new DownloadManager.ErrorCallback() {
            @Override
            public void onError() {
                MyToast.show(activity, getString(R.string.connect_error));
            }

            @Override
            public void onCancel() {
                MyToast.show(activity, getString(R.string.down_cancel));
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (mUpdateDialog != null && mUpdateDialog.isShowing())
            mUpdateDialog.dismiss();
        if (mRequestCall != null)
            mRequestCall.cancel();
        if (mUpdateTask != null)
            mUpdateTask.cancel(true);
        super.onDestroy();
    }

}
