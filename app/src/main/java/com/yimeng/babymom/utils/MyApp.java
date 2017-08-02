package com.yimeng.babymom.utils;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.os.SystemClock;

import com.squareup.leakcanary.LeakCanary;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import cn.smssdk.SMSSDK;
import okhttp3.OkHttpClient;

/**
 * 应用实例
 */
public class MyApp extends Application {
    private static final String APP_KEY_SMS = "19a9f1ec985f0";
    private static final String APP_SECRET_SMS = "33e722ae7da7d4d29560933c60ee71bd";
    /**
     * 存储本应用已经打开的activity的集合
     */
    private CopyOnWriteArrayList<Activity> activities = new CopyOnWriteArrayList<>();
    /**
     * 应用实例
     */
    private static MyApp instance;

    /**
     * 获得应用实例
     *
     * @return 应用实例
     */
    public static MyApp getAppContext() {
        return instance;
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);
        // initialize最好放在attachBaseContext最前面
        SophixManager.getInstance().setContext(this)
                .setAppVersion("1.0.0")
                .setAesKey(null)
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            // 表明补丁加载成功
                            SophixManager.getInstance().cleanPatches();
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            SophixManager.getInstance().cleanPatches();
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                        }
                    }
                }).initialize();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        LeakCanary.install(this);
//        ThreadUtils.runOnUIThread(null);
        activities.clear();
        PreferenceManager.init(this);
        SMSSDK.initSDK(this, APP_KEY_SMS, APP_SECRET_SMS);
        BugHandler.init();
//        Picasso.setSingletonInstance(Picasso.with(this));
        initHttpUtils();
        initJPush();

// queryAndLoadNewPatch不可放在attachBaseContext 中，否则无网络权限，建议放在后面任意时刻，如onCreate中
        SophixManager.getInstance().queryAndLoadNewPatch();
//        HuanXinHelper.getInstance().init(this);
    }

    /**
     * 启动activity时添加至集合
     *
     * @param activity 加入的activity
     */
    public void addActivity(Activity activity) {
        if (!activities.contains(activity)) {
            activities.add(activity);
        }
    }

    /**
     * 销毁activity时从集合移除
     *
     * @param activity 销毁的activity
     */
    public void removeActivity(Activity activity) {
        if (activities.contains(activity))
            activities.remove(activity);
    }

    /**
     * 完全退出本应用
     */
    public void finish() {
        new Thread() {
            @Override
            public void run() {
                SystemClock.sleep(2000);
//        EMClient.getInstance().logout(true);
                stopJPush();
                for (Activity activity : activities) {
                    activity.finish();
                }

                //退出程序
                Process.killProcess(Process.myPid());
                System.exit(0);
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        activityManager.killBackgroundProcesses(getPackageName());
            }
        }.start();
    }

    /**
     * 用清空别名的方式停止极光推送服务
     */
    private void stopJPush() {
//        JPushInterface.setAliasAndTags(MyApp.getAppContext(), "", new HashSet<String>(), new TagAliasCallback() {
//            @Override
//            public void gotResult(int i, String s, Set<String> set) {
//
//            }
//        });
    }

    /**
     * 初始化jpush
     */
    private void initJPush() {
//        JPushInterface.setDebugMode(false);
//        JPushInterface.init(this);
    }

    /**
     * 初始化httputils
     */
    private void initHttpUtils() {
        CookieJarImpl cookieJar = new CookieJarImpl(new PersistentCookieStore(getApplicationContext()));

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .addInterceptor(new LoggerInterceptor("TAG"))
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .cookieJar(cookieJar)
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

}
