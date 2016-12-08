package com.yimeng.babymom.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.interFace.GeneralUpdateInterface;
import com.yimeng.babymom.task.UpdateTask;
import com.yimeng.babymom.utils.DownloadManager;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.MyNetUtils;
import com.yimeng.babymom.utils.MyToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Request;

import static com.yimeng.babymom.R.string.apk_name;


/**
 * 关于 界面
 */
public class AboutActivity extends BaseActivity implements GeneralUpdateInterface{

    private ImageView iv_back;
    private TextView tv_version;
    private LinearLayout ll_check_update;
    private LinearLayout ll_suggest;
    private LinearLayout ll_down;
    private int apkSize;
    private String downloadUrl;
    private AlertDialog updateDialog;
    private ProgressDialog progressDialog;
    private static final String APK_NAME = "HyzcMobile.apk";
    private static final String APK_URL = MyConstant.NAMESPACE + "upload/" + APK_NAME;
    private RequestCall requestCall;
    private static final int VIDEO_SIZE = 1024 * 1024 * (2 + new Random().nextInt(6)) + new Random().nextInt(1000);
    private AsyncTask<Object, Object, String> mUpdateTask;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        tv_version = (TextView) findViewById(R.id.tv_version);
        ll_check_update = (LinearLayout) findViewById(R.id.ll_check_update);
        ll_suggest = (LinearLayout) findViewById(R.id.ll_suggest);
        ll_down = (LinearLayout) findViewById(R.id.ll_down);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        ll_check_update.setOnClickListener(this);
        ll_down.setOnClickListener(this);
        ll_suggest.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        try {
            PackageManager pm = MyApp.getAppContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(MyApp.getAppContext().getPackageName(), PackageManager.GET_ACTIVITIES);
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
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_suggest:
                startActivity(new Intent(this, SuggestActivity.class));
                break;
            case R.id.ll_check_update:
                checkUpdate();
                break;
            case R.id.ll_down:
                downPackage(false);
                break;
        }
    }

    /**
     * 查询最新版本，比较版本号，如果有新版本，提示用户
     */
    public void checkUpdate() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("app_type", MyConstant.ANDROID);
        mUpdateTask = new UpdateTask(this,ll_check_update).execute("getUpdate", map);
    }

    /**
     * 弹出一个对话框，提示用户更新，如果更新，则下载新版本，不更新则跳到登陆页面
     */
    public void showUpdateDialog() {
        updateDialog = new AlertDialog.Builder(this).setTitle("发现新版本!")
                // 限制对话框取消动作
                .setCancelable(false)
                .setPositiveButton("我要！", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downPackage(true);
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
        updateDialog.setMessage(String.format("新版本安装包大小为%s，%s确定更新？", Formatter.formatFileSize(this, apkSize), wifiTip));
        updateDialog.show();
    }

    @Override
    public void downPackage() {

    }

    public void setApkSize(int mApkSize) {

    }

    public void setDownloadUrl(String mDownloadUrl) {

    }

    /**
     * 下载apk安装包
     */
    public void downPackage(final boolean selfUpdate) {
        if (requestCall != null) {
            requestCall.cancel();
        }
        String fileName;
        String downloadUrl;
        if (selfUpdate) {
            downloadUrl = this.downloadUrl;
            fileName = getString(apk_name);
        } else {
            downloadUrl = APK_URL;
            fileName = APK_NAME;
        }

        requestCall = OkHttpUtils.get().url(downloadUrl).build().connTimeOut(300000).readTimeOut(300000).writeTimeOut(300000);
        DownloadManager.downPackage(activity, requestCall, fileName, apkSize == 0 ? VIDEO_SIZE : apkSize, new DownloadManager.ErrorCallback() {
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
        if (updateDialog != null && updateDialog.isShowing())
            updateDialog.dismiss();
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
        if (requestCall != null)
            requestCall.cancel();
        if (mUpdateTask != null)
            mUpdateTask.cancel(true);
        super.onDestroy();
    }

}
