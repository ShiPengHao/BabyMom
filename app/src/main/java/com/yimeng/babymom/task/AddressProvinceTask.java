package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.interFace.AddressInterface;

/**
 * 获取一个省集合数据的任务
 */

public class AddressProvinceTask extends WebServiceTask<AddressInterface> {

    public static final String METHOD = "GetProvince";

    public AddressProvinceTask(AddressInterface activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(AddressInterface activity, String result) {
        activity.onProvinceResult(result);
    }


}
