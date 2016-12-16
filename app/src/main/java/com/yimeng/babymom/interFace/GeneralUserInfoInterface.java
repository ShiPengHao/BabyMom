package com.yimeng.babymom.interFace;

import com.yimeng.babymom.bean.UserBean;

/**
 * 用户信息
 */

public interface GeneralUserInfoInterface {
    /**
     * 请求用户相关的信息：孕周、检查提醒、检查状态等
     */
    void getUserInfo();

    /**
     * 解析用户信息
     * @param userBean 用户
     */
    void onUserInfoResult(UserBean userBean);

    /**
     * 获取用户信息失败
     */
    void onUserError();
}
