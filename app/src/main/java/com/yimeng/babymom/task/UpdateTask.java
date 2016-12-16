package com.yimeng.babymom.task;

import android.view.View;

import com.yimeng.babymom.activity.AboutActivity;
import com.yimeng.babymom.activity.SplashActivity;
import com.yimeng.babymom.interFace.GeneralUpdateInterface;
import com.yimeng.babymom.utils.MyApp;

import org.json.JSONObject;


/**
 * 检查更新任务
 */

public class UpdateTask extends BaseTask<GeneralUpdateInterface> {

    public static final String METHOD = "getUpdate";

    public static final String APP_TYPE = "app_type";


    public UpdateTask(SplashActivity activity) {
        super(activity, null);
    }

    public UpdateTask(AboutActivity activity, View view) {
        super(activity, view);
    }


    @Override
    public void parseResult(GeneralUpdateInterface activity, String result) {
        try {
            int localVersionCode = MyApp.getAppContext().getPackageManager().getPackageInfo(MyApp.getAppContext().getPackageName(), 0).versionCode;
            JSONObject object = new JSONObject(result).optJSONArray("data").optJSONObject(0);
            if (object.optInt("version_Number") > localVersionCode) {
                activity.setApkSize(object.optInt("version_Size"));
                activity.setDownloadUrl(object.optString("version_Url"));
                activity.showUpdateDialog();
            } else {
                onError(activity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            onError(activity);
        }
    }

    @Override
    protected void onError(GeneralUpdateInterface activity) {
        if (activity instanceof SplashActivity)
            ((SplashActivity) activity).dispatchLogin();
        else
            super.onError(activity);
    }
}
