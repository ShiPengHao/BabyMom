package com.yimeng.babymom.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.BitmapUtils;
import com.yimeng.babymom.utils.DensityUtil;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.MyNetUtils;
import com.yimeng.babymom.utils.MyToast;
import com.yimeng.babymom.utils.WebServiceUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.request.RequestCall;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 闪屏界面，处理apk版本更新，自动登陆，页面跳转等逻辑
 */
public class SplashActivity extends BaseActivity {

    private SharedPreferences spAccount;
    private Handler handler;
    private String downloadUrl;
    private String fileDir;
    private AlertDialog updateDialog;
    private int apkSize;
    private ProgressDialog progressDialog;
    private RequestCall requestCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getImage();
        handler = new Handler();

        spAccount = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE);
        if (isFirstRunning()) {
            copyDataToLocal();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToIntroduce();
                }
            }, 2000);
        } else if (MyNetUtils.isConnected()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkUpdate();
                }
            }, 2000);
        } else {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToLogin();
                }
            }, 2000);
        }
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

    /**
     * 获得闪屏界面的背景图片
     */
    private void getImage() {
        ImageView iv = (ImageView) findViewById(R.id.iv);
        if (iv != null) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash);
            iv.setImageBitmap(BitmapUtils.zoomBitmap(bitmap, DensityUtil.SCREEN_WIDTH, DensityUtil.SCREEN_HEIGHT));
            bitmap.recycle();
        }
    }

    /**
     * 将资源文件拷贝到本地
     */
    private void copyDataToLocal() {

    }


    /**
     * 自动登陆
     */
    private void attemptToLogin() {
        String username = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, "");
        String pwd = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_PASSWORD, "");
        String type = spAccount.getString(MyConstant.KEY_ACCOUNT_LAST_TYPE, "");
        String method = null;
        if (type.equalsIgnoreCase("patient")) {
            method = "User_Login";
        } else if (type.equalsIgnoreCase("doctor")) {
            method = "Doctor_Login";
        } else if (type.equalsIgnoreCase("shop")) {
            method = "Shop_Login";
        }
        if (TextUtils.isEmpty(method)) {
            goToLogin();
            return;
        }
        HashMap<String, Object> param = new HashMap<>();
        param.put("user", username);
        param.put("pwd", pwd);
        requestLogin(method, param);
    }

    /**
     * 执行异步任务，登录
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    public void requestLogin(Object... params) {
        new AsyncTask<Object, Object, String>() {
            @Override
            protected String doInBackground(Object... params) {
                if (params != null && params.length == 2) {
                    String result = WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                            (Map<String, Object>) params[1]);
                    if (result == null) {
                        goToLogin();
                        return null;
                    }
                    try {
//                      new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
                        JSONObject object = new JSONObject(result);
                        if ("ok".equalsIgnoreCase(object.optString("status"))) {
                            loginHuanXin(object);
                        } else {
                            goToLogin();
                        }
                    } catch (Exception e) {
                        goToLogin();
                        e.printStackTrace();
                    }
                } else {
                    goToLogin();
                }
                return null;
            }
        }.execute(params);
    }

    /**
     * 登陆成功后登陆环信服务器
     */
    private void loginHuanXin(final JSONObject object) {
//        new AsyncTask<Object, Object, Object>() {
//            @Override
//            protected Object doInBackground(Object... params) {
//                try {
////                    EMClient.getInstance().createAccount(username, password);//同步方法
//                    String username = object.optString("username");
//                    String password = object.optString("password");
//                    while (EMClient.getInstance().isLoggedInBefore()
//                            && !username.equalsIgnoreCase(EMClient.getInstance().getCurrentUser())) {
//                        EMClient.getInstance().logout(true);
//                    }
//                    EMClient.getInstance().login(username, password, new EMCallBack() {//回调
//                        @Override
//                        public void onSuccess() {
//                            EMClient.getInstance().groupManager().loadAllGroups();
//                            EMClient.getInstance().chatManager().loadAllConversations();
//                            String type = object.optString("type");
//                            String id = object.optString("id");
//                            setJPushAliasAndTag(type, id);
//                        }
//
//                        @Override
//                        public void onProgress(int progress, String status) {
//
//                        }
//
//                        @Override
//                        public void onError(int code, String message) {
//                            MyLog.i(getClass(), message);
//                            String type = object.optString("type");
//                            String id = object.optString("id");
//                            setJPushAliasAndTag(type, id);
//                        }
//                    });
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute();
    }

    /**
     * 登陆成功后为本应用用户绑定JPush的别名和标签，别名为账号类型+"-"+id，标签为账号类型，设置成功以后跳转到主页
     */
    private void setJPushAliasAndTag(final String type, final String id) {
        final HashSet<String> tags = new HashSet<>();
        tags.add(type);
//        ThreadUtils.runOnBackThread(new Runnable() {
//            @Override
//            public void run() {
////                JPushInterface.setAliasAndTags(MyApp.getAppContext(), type + "+" + id, tags, new TagAliasCallback() {
////                    @Override
////                    public void gotResult(int i, String s, Set<String> set) {
////                        if (i != 0) {
////                            MyLog.i("JPush", "set alias and tag error");
////                            goToLogin();
////                        } else {
////                            goToHome(type);
////                        }
////                    }
////                });
//            }
//        });
    }

    /**
     * 查询最新版本，比较版本号，如果有新版本，提示用户
     */
    private void checkUpdate() {
        final String packageName = getPackageName();
        HashMap<String, Object> map = new HashMap<>();
        map.put("app_type", MyConstant.ANDROID);
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                if (s == null) {
                    if (isAutoLogin())
                        attemptToLogin();
                    else
                        goToLogin();
                    return;
                }
                try {
                    int localVersionCode = getPackageManager().getPackageInfo(packageName, 0).versionCode;
                    JSONObject object = new JSONObject(s).optJSONArray("data").optJSONObject(0);
                    if (object.optInt("version_Number") > localVersionCode) {
                        apkSize = object.optInt("version_Size");
                        downloadUrl = object.optString("version_Url");
                        showUpdateDialog();
                    } else {
                        if (isAutoLogin())
                            attemptToLogin();
                        else
                            goToLogin();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (isAutoLogin())
                        attemptToLogin();
                    else
                        goToLogin();
                }
            }
        }.execute("Get_VersionCode", map);
    }

    /**
     * 弹出一个对话框，提示用户更新，如果更新，则下载新版本，不更新则跳到登陆页面
     */
    private void showUpdateDialog() {
        updateDialog = new AlertDialog.Builder(SplashActivity.this).setTitle("发现新版本!")
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
                        if (isAutoLogin())
                            attemptToLogin();
                        else
                            goToLogin();
                        dialog.dismiss();
                    }
                })
                .create();
        String wifiTip = "";
        if (!MyNetUtils.isWifi(this)) {
            wifiTip = "检测到您的手机当前并非在wifi环境下，";
        }
        updateDialog.setMessage(String.format("新版本安装包大小为%s，%s确定更新？", android.text.format.Formatter.formatFileSize(this, apkSize), wifiTip));
        updateDialog.show();
    }

    /**
     * 下载apk安装包
     */
    public void downPackage() {
        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
            fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            fileDir = getFilesDir().getAbsolutePath();
        }
        requestCall = OkHttpUtils.get().url(downloadUrl).build().connTimeOut(300000).readTimeOut(300000).writeTimeOut(300000);
        requestCall.execute(new FileCallBack(fileDir, getString(R.string.apk_name)) {

            @Override
            public void onBefore(Request request, int id) {
                int contentLength = 0;
                try {
                    contentLength = (int) request.body().contentLength();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog = new ProgressDialog(SplashActivity.this);
                progressDialog.setMessage("拼命下载中...");
                progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            requestCall.cancel();
                            MyToast.show("下载已取消");
                            return true;
                        }
                        return false;
                    }
                });
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                progressDialog.setMax(contentLength == 0 ? apkSize : contentLength);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.setProgress(progress > 0 ? (int) (progress * progressDialog.getMax()) : -(int) progress);
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
                e.printStackTrace();
                progressDialog.dismiss();
                if (isAutoLogin())
                    attemptToLogin();
                else
                    goToLogin();

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

    /**
     * 跳转到登陆页面
     */
    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    /**
     * 跳转到主页，根据账号类型判断
     */
    private void goToHome(String type) {
        finish();

    }

    /**
     * 跳转到引导界面
     */
    private void goToIntroduce() {
        startActivity(new Intent(this, IntroduceActivity.class));
        finish();
    }


    /**
     * 判断本应用是否在本机首次运行
     *
     * @return
     */
    private boolean isFirstRunning() {
        return spAccount.getBoolean(MyConstant.KEY_ACCOUNT_FIRSTRUNNING, true);
    }

    /**
     * 判断是否设置自动更新
     *
     * @return
     */
    private boolean isAutoUpdate() {
        return spAccount.getBoolean(MyConstant.KEY_ACCOUNT_AUTOUPDATE, false);
    }

    /**
     * 判断是否设置自动登陆
     *
     * @return
     */
    private boolean isAutoLogin() {
        return spAccount.getBoolean(MyConstant.KEY_ACCOUNT_AUTOLOGIN, false);
    }
}
