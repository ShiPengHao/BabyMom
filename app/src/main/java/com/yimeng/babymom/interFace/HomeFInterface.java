package com.yimeng.babymom.interFace;

import com.yimeng.babymom.utils.LocationUtils;

/**
 * homeFragment主页功能
 */
public interface HomeFInterface extends HospitalCityInterface, GeneralUserInfoInterface, GeneralBannerInterface, LocationUtils.UpdateLocationListener {

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
}
