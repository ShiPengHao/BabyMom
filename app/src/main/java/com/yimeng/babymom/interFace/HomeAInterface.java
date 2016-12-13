package com.yimeng.babymom.interFace;

/**
 * HomeActivity主页功能
 */

public interface HomeAInterface {

    /**
     * 检查是否首次登录
     */
    void checkFirstRunning();

    /**
     * 初始化底部导航
     */
    void initIndicator();

    /**
     * 刷新页面指示--底部导航
     *
     * @param position 当前页码
     */
    void refreshIndicator(int position);
}
