package com.yimeng.babymom.interFace;

/**
 * homeFragment主页功能
 */
public interface HomeFInterface extends HospitalCityInterface, GeneralUserInfoInterface, GeneralBannerInterface {

    /**
     * 用户签到
     */
    void sign();

    /**
     * 手动选择地区
     */
    void getCity();

    /**
     * 在线咨询
     */
    void goToChat();

    /**
     * 跳转测算工具
     */
    void goToMeasure();

    /**
     * 跳转健康检测
     */
    void goToHealthMonitor();
}
