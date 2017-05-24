package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.interFace.AddressInterface;

/**
 * 获取一个区集合数据的任务
 */

public class AddressAreaTask extends WebServiceTask<AddressInterface> {

    public static final String METHOD = "GetArea";

    public static final String CITYCODE = "citycode";

    public AddressAreaTask(AddressInterface activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(AddressInterface activity, String result) {
        activity.onAreaResult(result);
    }

}
