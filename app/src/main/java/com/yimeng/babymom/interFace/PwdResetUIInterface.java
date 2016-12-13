package com.yimeng.babymom.interFace;

/**
 * 重置密码功能
 */

public interface PwdResetUIInterface extends GeneralUIInterface {
    /**
     * 重置密码
     */
    void resetPwd();

    /**
     * 跳转短信验证
     */
    void goToCheckSms();

    /**
     * 重置密码成功后，返回上个页面
     */
    void feedbackPre();
}
