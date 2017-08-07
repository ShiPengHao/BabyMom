package com.yimeng.babymom.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
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
     * 下载安装包，完成后自动安装，下载过程中用户按返回键取消下载
     *
     * @param activity    mActivity
     * @param requestCall http请求
     * @param fileName    文件名称
     * @param apkSize     文件大小
     * @param callback    异常回调
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
            private int progressMax = apkSize;

            @Override
            public void onBefore(Request request, int id) {

                progressDialog = new ProgressDialog(activity);
                progressDialog.setMessage("拼命下载中...");
                progressDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (keyCode == KeyEvent.KEYCODE_BACK && progressDialog != null && progressDialog.isShowing()) {
                            cancelByUser = true;
                            requestCall.cancel();
                            return true;
                        }
                        return false;
                    }
                });
                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.show();
                progressDialog.setMax(progressMax);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                if (total != 0 && total != progressMax) {
                    progressMax = (int) total;
                    progressDialog.setMax(progressMax);
                }
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.setProgress(progress > 0 ? (int) (progress * progressMax) : -(int) progress);
            }

            @Override
            public void onResponse(File file, int i) {
                progressDialog.dismiss();
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = Uri.fromFile(file);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(MyApp.getAppContext(), MyApp.getAppContext().getPackageName()+".fileprovider", file);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }
                intent.setDataAndType(uri, "application/vnd.android.package-archive");
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
