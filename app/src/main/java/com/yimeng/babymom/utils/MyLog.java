package com.yimeng.babymom.utils;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * 打印日志工具类
 */
public class MyLog {
    /**
     * 调试模式/日志打印的开关
     */
    public static final boolean DEBUG = false;

    public static void i(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void i(@NonNull Class<?> cls, String msg) {
        if (DEBUG) {
            Log.i(cls.getSimpleName(), msg);
        }
    }

}
