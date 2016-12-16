package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.fragment.PromotionFragment;

/**
 * 获得活动列表任务
 */

public class PromotionTask extends BaseTask<PromotionFragment> {

    public static final String METHOD = "promotion";


    public PromotionTask(PromotionFragment activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(PromotionFragment activity, String result) {
        activity.onPromotionResult(result);
    }
}
