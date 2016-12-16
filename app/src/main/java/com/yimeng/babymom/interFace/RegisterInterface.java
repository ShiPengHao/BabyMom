package com.yimeng.babymom.interFace;

/**
 * 注册功能
 */

public interface RegisterInterface extends GeneralUIInterface {
    /**
     * 注册
     */
    void register();

    /**
     * 短信验证
     */
    void goToCheckSms();

    /**
     * 注册完成后返回上个页面
     */
    void feedbackPre();

    /**
     * 跳转到用户协议
     */
    void goToProtocol();
}
