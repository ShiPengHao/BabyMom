package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.interFace.HospitalAreaInterface;

/**
 * 获取一个banner集合数据的任务
 */

public class HospitalAreaTask extends WebServiceTask<HospitalAreaInterface> {

    public static final String METHOD = "Load_Hospital";
    public static final String PROVINCE = "province";
    public static final String CITY = "city";
    public static final String AREA = "area";

    public HospitalAreaTask(HospitalAreaInterface activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(HospitalAreaInterface activity, String result) {
        activity.onHospitalResult(result);
    }

    @Override
    protected void onError(HospitalAreaInterface activity) {
    }
}
