package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.interFace.GeneralBannerInterface;

/**
 * 获取一个banner集合数据的任务
 */

public class BannerTask extends BaseTask<GeneralBannerInterface> {

    public static final String METHOD = "getBanner";

    public static final String PHONE = "phone";


    public BannerTask(GeneralBannerInterface activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(GeneralBannerInterface activity, String result) {
        activity.onBannerResult(result);
    }

    @Override
    protected void onError(GeneralBannerInterface activity) {
    }
}
