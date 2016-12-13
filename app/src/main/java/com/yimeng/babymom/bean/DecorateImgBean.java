package com.yimeng.babymom.bean;

import java.io.Serializable;

/**
 * 新闻图片bean
 */

public class DecorateImgBean implements Serializable {
    /**
     * decorate_id : 1
     * decorate_name : 轮播图标题或者新闻标题，尽量简短
     * decorate_key : 图片类型：LBT轮播图，NEWS资讯新闻
     * decorate_value : 对应的web链接url
     * decorate_img : 图片相对路径，例如：/upload/201610/10/201610101050576684.jpg
     * decorate_explain : 图片简介，例如：医院简介，如何如何牛逼等等
     */

    public int decorate_id;
    public String decorate_name;
    public String decorate_key;
    public String decorate_value;
    public String decorate_img;
    public String decorate_explain;
}
