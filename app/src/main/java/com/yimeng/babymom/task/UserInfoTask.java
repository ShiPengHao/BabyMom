package com.yimeng.babymom.task;

import android.view.View;

import com.google.gson.Gson;
import com.yimeng.babymom.bean.UserBean;
import com.yimeng.babymom.interFace.GeneralUserInfoInterface;

import org.json.JSONObject;

/**
 * 获取用户信息
 */

public class UserInfoTask extends WebServiceTask<GeneralUserInfoInterface> {
    public static final String METHOD = "getUser";

    public static final String PHONE = "phone";

    public UserInfoTask(GeneralUserInfoInterface activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(GeneralUserInfoInterface activity, String result) {
        try {
//                new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            JSONObject object = new JSONObject(result);
            if ("ok".equalsIgnoreCase(object.optString("status"))) {
                activity.onUserInfoResult(new Gson().fromJson(object.optString("user"), UserBean.class));
            } else {
                activity.onUserError();
            }
        } catch (Exception e) {
            activity.onUserError();
        }
    }

    @Override
    protected void onError(GeneralUserInfoInterface activity) {
        activity.onUserError();
    }
}
