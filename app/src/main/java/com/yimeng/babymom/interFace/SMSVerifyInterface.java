package com.yimeng.babymom.interFace;

/**
 * 短信验证功能
 */

public interface SMSVerifyInterface extends GeneralUIInterface {
    /**
     * 注册短信验证回调监听
     */
    void registerSmsListener();

    /**
     * 解绑短信验证回调监听
     */
    void unregisterSmsListener();

    /**
     * 请求短信验证码
     */
    void requestSmsCode();

    /**
     * 验证短信验证码
     */
    void checkSmsCode();

    /**
     * 验证成功后返回上个页面
     */
    void feedbackPre();

    /**
     * 更新请求验证码时间间隔的倒计时：获取验证码成功后，告诉用户60s后才能再次点击
     */
    void updateTextIndicator();

    /**
     * 短信验证码监听接口
     */
    interface SMSListener{
        /**
         * 分发成功回调，主要处理请求成功和验证成功事件
         *
         * @param event 事件
         * @param data  数据
         */
        void dispatchOkEvent(int event, Object data);

        /**
         * 显示短信验证平台返回的错误信息
         * @param data 错误信息
         */
        void showErrorTip(Throwable data);
    }
}
