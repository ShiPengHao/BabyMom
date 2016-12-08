package com.yimeng.babymom.interFace;

/**
 * 通用检查版本更新接口
 */

public interface GeneralUpdateInterface {
    /**
     * 检查版本更新
     */
    void checkUpdate();

    /**
     * 弹出一个对话框，提示用户更新，如果更新，则下载新版本
     */
    void showUpdateDialog();

    /**
     * 下载apk安装包
     */
    void downPackage();

    /**
     * 设置文件大小
     * @param apkSize 文件大小
     */
    void setApkSize(int apkSize);

    /**
     * 设置文件下载地址
     * @param downloadUrl 下载地址
     */
    void setDownloadUrl(String downloadUrl);
}
