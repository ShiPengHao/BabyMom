package com.yimeng.babymom.interFace;

/**
 * homeFragment主页功能
 */
public interface HomeFInterface {
    /**
     * 获取轮播图的内容
     */
    void requestBannerData();

    /**
     * 用户签到
     */
    void sign();

    /**
     * 获得用户相关的信息：孕周、检查提醒、检查状态等
     */
    void getUserInfo();
}
