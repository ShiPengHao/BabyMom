package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.R;
import com.yimeng.babymom.fragment.HomeFragment;

import org.json.JSONObject;

/**
 * 签到任务
 */

public class SignTask extends BaseTask<HomeFragment> {
    public static final String METHOD = "sign";

    public static final String PHONE = "phone";

    public SignTask(HomeFragment activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(HomeFragment activity, String result) {
        try {
//                new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            JSONObject object = new JSONObject(result);
            activity.showToast(object.optString("status"));
        } catch (Exception e) {
            activity.showToast(activity.getString(R.string.connect_error));
        }
    }
}
