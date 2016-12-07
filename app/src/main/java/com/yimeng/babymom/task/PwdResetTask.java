package com.yimeng.babymom.task;

import android.content.DialogInterface;
import android.widget.Button;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.PwdResetActivity;

import org.json.JSONObject;

/**
 * 充值密码任务
 */

public class PwdResetTask extends SoapAsyncTask<PwdResetActivity> {
    public PwdResetTask(PwdResetActivity activity, Button button) {
        super(activity, button);
    }

    @Override
    public void parseResult(final PwdResetActivity activity, String result) {
        try {
            JSONObject object = new JSONObject(result);
            if ("ok".equalsIgnoreCase(object.optString("status"))) {
                activity.showOkTips(activity.getString(R.string.pwd_reset_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.backToLogin();
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
