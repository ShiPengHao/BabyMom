package com.yimeng.babymom.interFace;

/**
 * 登陆功能
 */

public interface LoginInterface extends GeneralUIInterface, GeneralLoginInterface {

    /**
     * 跳转主页
     */
    void goToHome();

    /**
     * 跳转注册
     */
    void goToRegister();

    /**
     * 忘记密码，跳转修改密码
     */
    void goToPwdReset();
}
