package com.yimeng.babymom.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

/**
 * 医生
 */
public class DoctorBean implements Serializable, Comparable<DoctorBean> {
    public int doctor_id;//id
    public int doctor_UCL;// 预约上限，-1不限
    public int Is_Order;// 是否接受预约0不接受1接受2排班
    public int doctor_title; //职称类型  int   (0：村医 1：医师 2：主治医师 3：副主任医师 4：主任医师)
    public String doctor_user;//用户名
    public String doctor_name;//姓名
    public String doctor_avatar;//头像相对路径
    public String doctor_qualification;//医生资格证相对路径
    public String E_signature;//电子签名相对路径
    public String doctor_sex;//性别
    public String doctor_age;//年龄
    public String doctor_phone;//电话
    public String doctor_WeChat;//电话
    public String doctor_email;//邮箱
    public String doctor_ICode;//邀请码
    public String remark;//简介
    public String departments_id;//科室id
    public String hospital_id;//科室id
    public String doctor_Audit;//审核状态（0未受理 1已通过 -1未通过）
    public String add_time;//注册时间
    public String weekday;//坐诊时间

    @Override
    public int compareTo(@NonNull DoctorBean another) {
        return another.doctor_title - doctor_title;
    }

    @Override
    public String toString() {
        return doctor_name;
    }
}
