package com.yimeng.babymom.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.babymom.R;
import com.yimeng.babymom.bean.DecorateImgBean;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.UiUtils;

/**
 * 新闻咨询条目
 */

public class NewsHolder extends BaseHolder<DecorateImgBean> {

    private ImageView item_icon;
    private TextView item_name;
    private TextView item_content;

    @Override
    protected View initView() {
        View view = UiUtils.inflate(R.layout.layout_pic_title_des);
        item_icon = (ImageView) view.findViewById(R.id.item_icon);
        item_name = (TextView) view.findViewById(R.id.item_name);
        item_content = (TextView) view.findViewById(R.id.item_des);
        return view;
    }

    @Override
    public void bindData(DecorateImgBean data) {
        Picasso.with(MyApp.getAppContext())
                .load(MyConstant.NAMESPACE + data.decorate_value)
                .resize(item_icon.getWidth(), item_icon.getHeight())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(item_icon);
        item_name.setText(data.decorate_name);
        item_content.setText(data.decorate_explain);
    }
}
