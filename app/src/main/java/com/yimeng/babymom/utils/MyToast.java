package com.yimeng.babymom.utils;

import android.app.Activity;
import android.widget.Toast;

/**
 * 静态吐司
 */
public class MyToast {
    private static Toast toast;


    /**
     * toast一个内容
     *
     * @param content  内容
     * @param activity activity
     */
    public static void show(Activity activity, final String content) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (toast == null) {
                    toast = Toast.makeText(MyApp.getAppContext(), content, Toast.LENGTH_SHORT);
                } else {
                    toast.setText(content);
                }
                toast.show();
            }
        });
    }

    /**
     * 取消toast
     */
    public static void close() {
        if (toast != null) {
            toast.cancel();
        }
    }
}
