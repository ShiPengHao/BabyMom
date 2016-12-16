package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.activity.LoginActivity;
import com.yimeng.babymom.activity.SplashActivity;
import com.yimeng.babymom.interFace.GeneralLoginInterface;

import org.json.JSONObject;

/**
 * 登录任务
 */

public class LoginTask extends BaseTask<GeneralLoginInterface> {

    public static final String METHOD = "login";

    public static final String PHONE = "phone";

    public static final String PASSWORD = "password";


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
                activity.onLoginOk();
            } else {
                activity.onLoginError();
            }
        } catch (Exception e) {
            activity.onLoginError();
        }
    }

    @Override
    protected void onError(GeneralLoginInterface activity) {
        activity.onLoginError();
    }
}
