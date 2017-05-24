package com.yimeng.babymom.task;

import android.content.DialogInterface;
import android.view.View;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.PwdResetActivity;

import org.json.JSONObject;

/**
 * 重置密码任务
 */

public class PwdResetTask extends WebServiceTask<PwdResetActivity> {
    public static final String METHOD = "pwdRest";

    public static final String PHONE = "phone";

    public static final String PASSWORD = "password";

    public PwdResetTask(PwdResetActivity activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(final PwdResetActivity activity, String result) {
        try {
            JSONObject object = new JSONObject(result);
            if ("ok".equalsIgnoreCase(object.optString("status"))) {
                activity.showOkTips(activity.getString(R.string.pwd_reset_ok), new DialogInterface.OnClickListener() {
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
