package com.yimeng.babymom.interFace;

/**
 * 闪屏功能
 */

public interface SplashInterface extends GeneralLoginInterface, GeneralUpdateInterface {
    /**
     * 根据应用状态，分发apk版本更新，自动登陆，页面跳转等逻辑
     */
    void dispatchEvent();

    /**
     * 将资源文件拷贝到本地
     */
    void copyRes2Local();

    /**
     * 自动登陆
     */
    void autoLogin();

    /**
     * 自动登陆成功后跳转主页
     */
    void goToHome();

    /**
     * 跳转到登陆
     */
    void goToLogin();

    /**
     * 跳转引导界面
     */
    void goToIntroduce();

}
