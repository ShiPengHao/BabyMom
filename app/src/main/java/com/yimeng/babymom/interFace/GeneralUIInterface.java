package com.yimeng.babymom.interFace;

/**
 * activity和fragment都要实现的接口，具有校验用户输入功能
 */

interface GeneralUIInterface {
    /**
     * 提交数据之前校验用户输入
     */
    void checkInput();
}
