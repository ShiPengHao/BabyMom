package com.yimeng.babymom.task;

import android.view.View;

import org.json.JSONObject;

/**
 * 上传图片任务
 */

public class UploadImgTask extends BaseTask<UploadImgTask.UploadImgInterface> {
    public static final String METHOD = "upload_img";
    public static final String FILE_NAME = "fileName";
    public static final String DEL_FILE_PATH = "DelFilePath";
    public static final String IMAGE = "image";

    public UploadImgTask(UploadImgInterface activity, View view) {
        super(activity, view);
    }

    @Override
    public void parseResult(UploadImgInterface activity, String s) {
        try {
            JSONObject object = new JSONObject(s);
            if ("ok".equalsIgnoreCase(object.optString("status"))) {
                activity.onSuccess(object.optString("data"));
            } else {
                activity.onError();
            }
        } catch (Exception e) {
            e.printStackTrace();
            activity.onError();
        }
    }

    /**
     * 上传图片接口
     */
    public interface UploadImgInterface {
        /**
         * 上传
         */
        void uploadImg();

        /**
         * @param imgUrl 成功后返回的图片url
         */
        void onSuccess(String imgUrl);

        void onError();
    }
}
