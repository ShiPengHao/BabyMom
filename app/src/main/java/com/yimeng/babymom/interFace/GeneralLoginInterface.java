package com.yimeng.babymom.interFace;

/**
 * 通用登陆功能
 */

public interface GeneralLoginInterface {
    /**
     * 登陆
     */
    void login();

    /**
     * 登陆成功回调
     */
    void onLoginOk();

    /**
     * 登陆失败回调
     */
    void onLoginError();

}
