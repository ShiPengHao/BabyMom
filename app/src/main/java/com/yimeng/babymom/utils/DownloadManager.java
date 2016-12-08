package com.yimeng.babymom.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.KeyEvent;

import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.request.RequestCall;

import java.io.File;

import okhttp3.Call;
import okhttp3.Request;

/**
 * 下载工具
 */

public class DownloadManager {
    /**
     * 下载异常回调
     */
    public interface ErrorCallback {
        /**
         * 错误回调
         */
        void onError();

        /**
         * 用户取消下载回调
         */
        void onCancel();
    }

    /**
     * 下载安装包，完成后自动安装
     * @param activity activity
     * @param requestCall http请求
     * @param fileName 文件名称
     * @param apkSize 文件大小
     * @param callback 异常回调
     */
    public static void downPackage(final Activity activity, final RequestCall requestCall, String fileName, final int apkSize, final ErrorCallback callback) {
        String fileDir;
        if (Environment.MEDIA_MOUNTED.equalsIgnoreCase(Environment.getExternalStorageState())) {
            fileDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            fileDir = activity.getFilesDir().getAbsolutePath();
        }
        requestCall.execute(new FileCallBack(fileDir, fileName) {

            ProgressDialog progressDialog;
            private boolean cancelByUser;

            @Override
            public void onBefore(Request request, int id) {
                int contentLength = 0;
                try {
                    contentLength = (int) request.body().contentLength();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("拼命下载中...");
                progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            cancelByUser = true;
                            requestCall.cancel();
                            MyToast.show(activity, "下载已取消");
                            return true;
                        }
                        return false;
                    }
                });
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                progressDialog.setMax(contentLength == 0 ? apkSize : contentLength);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.setProgress(progress > 0 ? (int) (progress * progressDialog.getMax()) : -(int) progress);
            }

            @Override
            public void onResponse(File file, int i) {
                progressDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                activity.startActivity(intent);
            }

            @Override
            public void onError(Call call, Exception e, int i) {
                e.printStackTrace();
                progressDialog.dismiss();
                if (cancelByUser)
                    callback.onCancel();
                else
                    callback.onError();
            }
        });
    }
}
