package com.yimeng.babymom.bean;

import java.io.Serializable;

/**
 * 医院bean
 */
public class HospitalBean implements Serializable {
    public String province;
    public String city;
    public String area;
    public String hospital_name;
    public String doctor_adress;
    public int hospital_id;

    @Override
    public String toString() {
        return hospital_name;
    }
}
