package com.yimeng.babymom.interFace;

/**
 * 获取地址--省市区
 */

public interface AddressInterface {
    /**
     * 请求省
     */
    void requestProvince();

    /**
     * 解析省数据
     *
     * @param result 结果
     */
    void onProvinceResult(String result);

    /**
     * 请求市
     */
    void requestCity();

    /**
     * 解析市数据
     *
     * @param result 结果
     */
    void onCityResult(String result);

    /**
     * 请求区
     */
    void requestArea();

    /**
     * 解析区数据
     *
     * @param result 结果
     */
    void onAreaResult(String result);
}
