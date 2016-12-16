package com.yimeng.babymom.bean;

import java.io.Serializable;

/**
 * 科室
 */
public class DepartmentBean implements Serializable {
    public int departments_id;
    public String departments_name;
    public int parentid;
    public int hospital_id;

    @Override
    public String toString() {
        return departments_name;
    }
}
