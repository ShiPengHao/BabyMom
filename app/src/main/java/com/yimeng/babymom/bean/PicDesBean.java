package com.yimeng.babymom.bean;

/**
 * 上图下文的视图数据
 */

public class PicDesBean {
    public int picResId;
    public String des;
    public boolean enable = true;

    public PicDesBean(int picResId, String des) {
        this.picResId = picResId;
        this.des = des;
    }

    public PicDesBean(int picResId, String des, boolean enable) {
        this.picResId = picResId;
        this.des = des;
        this.enable = enable;
    }
}
