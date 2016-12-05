package com.yimeng.babymom.utils;

import android.widget.Toast;

/**
 * 静态吐司
 * Created by 依萌 on 2016/6/13.
 */
public class MyToast {
    private static Toast toast;

    /**
     * toast一个内容
     * @param content 内容
     */
    public static void show(final String content) {
        ThreadUtils.runOnUIThread(new Runnable() {
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
