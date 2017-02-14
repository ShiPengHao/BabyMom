package com.yimeng.babymom.interFace;

/**
 * 根据城市名称获取医院
 */

public interface HospitalCityInterface {
    /**
     * 根据城市名称请求医院数据
     */
    void requestCityHospital();

    /**
     * 获取数据成功
     * @param result 返回的数据
     */
    void onHospitalResult(String result);

}
