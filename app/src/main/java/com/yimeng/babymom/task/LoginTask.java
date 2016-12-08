package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.activity.LoginActivity;
import com.yimeng.babymom.activity.SplashActivity;
import com.yimeng.babymom.interFace.GeneralLoginInterface;

import org.json.JSONObject;

/**
 * 登录任务
 */

public class LoginTask extends SoapAsyncTask<GeneralLoginInterface> {

    public LoginTask(LoginActivity activity, View view) {
        super(activity, view);
    }

    public LoginTask(SplashActivity activity) {
        super(activity, null);
    }

    @Override
    public void parseResult(GeneralLoginInterface activity, String result) {
        try {
//                new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            JSONObject object = new JSONObject(result);
            if ("ok".equalsIgnoreCase(object.optString("status"))) {
                if (activity instanceof LoginActivity) {
                    ((LoginActivity) activity).saveAccountInfo();
                    ((LoginActivity) activity).goToHome();
                } else if (activity instanceof SplashActivity) {
                    ((SplashActivity) activity).goToHome();
                }
            } else {
                onError(activity);
            }
        } catch (Exception e) {
            onError(activity);
        }
    }

    @Override
    protected void onError(GeneralLoginInterface activity) {
        if (activity instanceof SplashActivity) {
            ((SplashActivity) activity).goToLogin();
        } else {
            super.onError(activity);
        }
    }
}
