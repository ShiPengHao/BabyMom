package com.yimeng.babymom.interFace;

/**
 * 引导功能
 */

public interface IntroduceInterface {
    /**
     * 跳转登陆
     */
    void goToLogin();

    /**
     * 初始化页码指示器
     */
    void initDots();

    /**
     * 根据页码更新页码指示
     * @param position 当前页码
     */
    void refreshIndicator(int position);
}
