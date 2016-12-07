package com.yimeng.babymom.task;

import android.widget.Button;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.LoginActivity;

import org.json.JSONObject;

/**
 * 登录任务
 */

public class LoginTask extends SoapAsyncTask<LoginActivity> {

    public LoginTask(LoginActivity activity, Button button) {
        super(activity, button);
    }

    @Override
    public void parseResult(LoginActivity activity, String result) {
        try {
//                new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            JSONObject object = new JSONObject(result);
            if ("ok".equalsIgnoreCase(object.optString("status"))) {
                activity.saveAccountInfo();
                activity.goToHome();
            } else {
                activity.showToast(object.optString("msg"));
            }
        } catch (Exception e) {
            activity.showToast(activity.getString(R.string.connect_error));
            e.printStackTrace();
        }
    }
}
