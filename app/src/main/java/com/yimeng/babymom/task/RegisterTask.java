package com.yimeng.babymom.task;

import android.content.DialogInterface;
import android.view.View;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.RegisterActivity;

import org.json.JSONObject;

/**
 * 注册任务
 */

public class RegisterTask extends SoapAsyncTask<RegisterActivity> {

    public RegisterTask(RegisterActivity activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(final RegisterActivity activity, String result) {
        try {
            JSONObject object = new JSONObject(result);
            if ("ok".equalsIgnoreCase(object.optString("status"))) {
                activity.showOkTips(activity.getString(R.string.register_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.feedbackPre();
                    }
                });
            } else {
                activity.showToast(object.optString("msg"));
            }
        } catch (Exception e) {
            activity.showToast(activity.getString(R.string.connect_error));
            e.printStackTrace();
        }
    }

}