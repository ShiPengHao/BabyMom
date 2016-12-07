package com.yimeng.babymom.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.task.SoapAsyncTask;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.MyNetUtils;
import com.yimeng.babymom.utils.MyToast;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Request;

import static com.yimeng.babymom.R.string.apk_name;


/**
 * 关于 界面
 */
public class AboutActivity extends BaseActivity{

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
    private void checkUpdate() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("app_type", MyConstant.ANDROID);

    }

    /**
     * 弹出一个对话框，提示用户更新，如果更新，则下载新版本，不更新则跳到登陆页面
     */
    private void showUpdateDialog() {
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
        updateDialog.setMessage(String.format("新版本安装包大小为%s，%s确定更新？", Formatter.formatFileSize(this,apkSize), wifiTip));
        updateDialog.show();
    }

    /**
     * 下载apk安装包
     */
    public void downPackage(final boolean selfUpdate) {
        if (requestCall != null) {
            requestCall.cancel();
            requestCall = null;
        }
        String fileDir;
        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
            fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            fileDir = getFilesDir().getAbsolutePath();
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

        requestCall.execute(new FileCallBack(fileDir, fileName) {

            private boolean cancelByUser;

            @Override
            public void onBefore(Request request, int id) {
                cancelByUser = false;
                int contentLength = 0;
                try {
                    contentLength = (int) request.body().contentLength();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog = new ProgressDialog(AboutActivity.this);
                progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            requestCall.cancel();
                            progressDialog.dismiss();
                            cancelByUser = true;
                            MyToast.show(activity,"下载已取消");
                            return true;
                        }
                        return false;
                    }
                });

                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                if (selfUpdate)
                    progressDialog.setMax(contentLength == 0 ? apkSize : contentLength);
                else {
                    progressDialog.setMax(contentLength == 0 ? VIDEO_SIZE : contentLength);
                }
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.setProgress(progress > 0 ? (int) (progress * progressDialog.getMax()) : -(int) progress);
                }
            }

            @Override
            public void onResponse(File file, int i) {
                progressDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                startActivity(intent);
            }

            @Override
            public void onError(Call call, Exception e, int i) {
                if (!cancelByUser) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    MyToast.show(activity,getString(R.string.connect_error));
                }
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
        super.onDestroy();
    }

}
