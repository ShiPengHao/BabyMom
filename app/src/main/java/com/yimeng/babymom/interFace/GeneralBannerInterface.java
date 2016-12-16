package com.yimeng.babymom.interFace;

/**
 * 获取banner
 */

public interface GeneralBannerInterface {
    /**
     * 请求banner数据
     */
    void requestBanner();

    /**
     * 获取数据成功
     * @param result 返回的数据
     */
    void onBannerResult(String result);

}
