package com.yimeng.babymom.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.bean.PicDesBean;
import com.yimeng.babymom.utils.UiUtils;

/**
 * 上图下文holder
 */

public class PicDesHolder extends BaseHolder<PicDesBean> {
    private ImageView iv;
    private TextView tv;
    private View view;

    @Override
    protected View initView() {
        view = UiUtils.inflate(R.layout.layout_pic_des);
        iv = (ImageView) view.findViewById(R.id.iv);
        tv = (TextView) view.findViewById(R.id.tv);
        return view;
    }

    @Override
    public void bindData(PicDesBean data) {
        iv.setImageResource(data.picResId);
        tv.setText(data.des);
        view.setEnabled(data.enable);
    }
}
