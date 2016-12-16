package com.yimeng.babymom.bean;

import java.io.Serializable;

/**
 * 用户
 */
public class UserBean implements Serializable {
    public int user_id;//id
    public String user_name;//姓名
    public String user_avatar;//头像相对路径
    public String patient_phone;//电话
    public String user_WeChat;//电话
    public String user_email;//邮箱
    public String add_time;//注册时间
    public String user_ICode;//邀请码
    public String user_status;//孕周状态
    public String pregnant_check_item;//孕周检查项目
    public String pregnant_check_detail;//孕周检查项目详情
    public String pregnant_check_status;//孕周检查项目状态
    public String bindHospitalId;

}