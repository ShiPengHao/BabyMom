package com.yimeng.babymom.bean;

import java.io.Serializable;

/**
 * 省市区地址的bean
 */
public class AddressBean implements Serializable {
    public String code;
    public String name;
    public String provincecode;
    public String citycode;

    @Override
    public String toString() {
        return name;
    }
}
