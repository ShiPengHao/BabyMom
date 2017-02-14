package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.interFace.HospitalCityInterface;

/**
 * 获取一个banner集合数据的任务
 */

public class HospitalCityTask extends BaseTask<HospitalCityInterface> {

    public static final String METHOD = "get_City_Hospital";
    public static final String CITYNAME = "cityname";

    public HospitalCityTask(HospitalCityInterface activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(HospitalCityInterface activity, String result) {
        activity.onHospitalResult(result);
    }

    @Override
    protected void onError(HospitalCityInterface activity) {
    }
}
