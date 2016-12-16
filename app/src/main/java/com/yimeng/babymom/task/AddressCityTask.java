package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.interFace.AddressInterface;

/**
 * 获取一个市集合数据的任务
 */

public class AddressCityTask extends BaseTask<AddressInterface> {
    public static final String METHOD = "GetCity";
    public static final String PROVINCECODE = "provincecode";

    public AddressCityTask(AddressInterface activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(AddressInterface activity, String result) {
        activity.onCityResult(result);
    }

}
