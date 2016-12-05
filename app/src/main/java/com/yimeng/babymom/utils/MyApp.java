package com.yimeng.babymom.utils;

import android.app.Activity;
import android.app.Application;
import android.os.SystemClock;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * 应用实例
 */
public class MyApp extends Application {
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
    public void onCreate() {
        super.onCreate();
        instance = this;
//        LeakCanary.install(this);
        ThreadUtils.runOnUIThread(null);
        activities.clear();

        BugHandler.init();
//        Picasso.setSingletonInstance(Picasso.with(this));
        initHttpUtils();
        initJPush();
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
        SystemClock.sleep(2000);
//        EMClient.getInstance().logout(true);
        stopJPush();
        for (int j = 0; j < activities.size(); j++) {
            activities.get(j).finish();
        }

        //退出程序
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
//        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        activityManager.killBackgroundProcesses(getPackageName());
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
