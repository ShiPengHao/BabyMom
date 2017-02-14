package com.yimeng.babymom.utils;

import android.view.View;

/**
 * ui工具类
 */
public class UiUtils {
    /**
     * 填充布局
     * @param layoutId 布局id
     * @return view
     */
    public static View inflate(int layoutId){
        return View.inflate(MyApp.getAppContext(),layoutId,null);
    }
}
