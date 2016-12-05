package com.yimeng.babymom.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.widget.Toast;


import com.yimeng.babymom.R;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


/**
 * Bug收集工具类
 */

public class BugHandler implements Thread.UncaughtExceptionHandler {
    private static final boolean DEBUG = false;//调试模式
    private static BugHandler instance;
    //用来存储设备信息
    private Map<String, String> infos = new HashMap<>();
    //用于格式化日期,作为日志文件名的一部分
    private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.CHINA);
    private Toast toast;

    private BugHandler() {

    }

    /**
     * 初始化，设置异常捕获监听
     */
    public synchronized static void init() {
        if (instance == null) {
            instance = new BugHandler();
            Thread.currentThread().setUncaughtExceptionHandler(instance);
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        // 新建handlerThread处理toast提示
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                toast = Toast.makeText(MyApp.getAppContext(), "抱歉，" + MyApp.getAppContext().getString(R.string.app_name) + "程序崩溃，应用将退出！", Toast.LENGTH_SHORT);
                toast.show();
                Looper.loop();
            }
        }.start();
        collectDeviceInfo();
        saveCrashInfo(ex);
    }

    /**
     * 收集应用版本信息和设备参数信息
     */
    private void collectDeviceInfo() {
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将设备信息和异常信息综合后保存到sdcard/crash/目录文件中或者上传到服务器
     *
     * @param ex 异常
     */
    private void saveCrashInfo(Throwable ex) {
        StringBuilder sb = new StringBuilder();
        // append device info
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key).append("=").append(value).append("\n");
        }
        // append exception info
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        // append version info
        try {
            PackageManager pm = MyApp.getAppContext().getPackageManager();
            PackageInfo pi = pm.getPackageInfo(MyApp.getAppContext().getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = String.valueOf(pi.versionCode);
                sb.insert(0, "versionName=" + versionName + "\n" + "versionCode=" + versionCode + "\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // append 北京time
        String time = formatter.format(new Date());
        sb.insert(0, "time=" + time + "\n");
        if (DEBUG) {// 调试时将错误日志写入本地文件
            // create file name
            String fileName = "crash-" + time + "-" + System.currentTimeMillis() + ".log";
            // save string to local file
            try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/crash/" + MyApp.getAppContext().getString(R.string.apk_short_name) + "/";
                    File dir = new File(path);
                    if (dir.exists() || dir.mkdirs()) {
                        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path + fileName));
                        bos.write(sb.toString().getBytes());
                        bos.close();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (toast != null)
                toast.cancel();
            MyApp.getAppContext().finish();
        } else {// 上线后将错误日志上传提交到服务器
            // map request params
            HashMap<String, Object> map = new HashMap<>();
            map.put("msg", sb.toString());
            // upload bug to server
            uploadBug("AddBug", map);
        }
    }

    /**
     * 上传bug到服务器之后退出应用
     *
     * @param params 方法名+参数列表（哈希表形式）
     */
    private void uploadBug(Object... params) {
        new AsyncTask<Object, Void, Void>() {
            @Override
            protected Void doInBackground(Object... params) {
                try {
                    WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE
                            , (String) params[0], (Map<String, Object>) params[1]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (toast != null)
                    toast.cancel();
                MyApp.getAppContext().finish();
                return null;
            }
        }.execute(params);
    }
}
