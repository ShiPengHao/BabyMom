package com.yimeng.babymom.interFace;

/**
 * 登陆功能
 */

public interface LoginUIInterface extends GeneralUIInterface, GeneralLoginInterface {

    /**
     * 登陆成功后保存需要的用户信息
     */
    void saveAccountInfo();

    /**
     * 登陆成功后，跳转主页
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
