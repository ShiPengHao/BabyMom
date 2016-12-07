package com.yimeng.babymom.interFace;

/**
 * 重置密码
 */

public interface PwdResetInterface extends ViewInterface {
    /**
     * 重置密码
     */
    void resetPwd();

    /**
     * 跳转短信验证
     */
    void goToCheckSms();

    /**
     * 返回登陆页面
     */
    void backToLogin();
}
