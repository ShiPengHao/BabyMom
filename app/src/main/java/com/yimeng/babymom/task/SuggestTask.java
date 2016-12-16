package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.activity.SuggestActivity;

import org.json.JSONObject;

/**
 * 提交建议任务
 */

public class SuggestTask extends BaseTask<SuggestActivity> {

    public static final String METHOD = "AddGuestbook";

    public static final String PHONE = "phone";

    public static final String MSG = "msg";


    public SuggestTask(SuggestActivity activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(SuggestActivity activity, String result) {
        try {
            JSONObject object = new JSONObject(result);
            activity.showToast(object.optString("msg"));
            if ("ok".equalsIgnoreCase(object.optString("status")))
                activity.finish();
        } catch (Exception e) {
            super.onError(activity);
        }
    }
}
