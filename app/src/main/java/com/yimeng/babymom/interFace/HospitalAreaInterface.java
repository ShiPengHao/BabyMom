package com.yimeng.babymom.interFace;

/**
 * 获取医院
 */

public interface HospitalAreaInterface {
    /**
     * 请求医院数据
     */
    void requestAreaHospital();

    /**
     * 获取数据成功
     * @param result 返回的数据
     */
    void onHospitalResult(String result);

}
