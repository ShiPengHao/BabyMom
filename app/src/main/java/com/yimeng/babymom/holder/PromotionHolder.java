package com.yimeng.babymom.holder;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.babymom.R;
import com.yimeng.babymom.bean.PromotionBean;
import com.yimeng.babymom.utils.DensityUtil;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.UiUtils;

/**
 * 活动holder
 */

public class PromotionHolder extends BaseHolder<PromotionBean> {
    private ImageView iv;
    private TextView tv_title;
    private TextView tv_time;
    private View view;
    private static Context context = MyApp.getAppContext();

    @Override
    protected View initView() {
        view = UiUtils.inflate(R.layout.item_promotion);
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.SCREEN_WIDTH / 2));
        iv = (ImageView) view.findViewById(R.id.iv);
        tv_time = (TextView) view.findViewById(R.id.tv_time);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        return view;
    }

    @Override
    public void bindData(PromotionBean data) {
        tv_title.setText(data.title);
        Picasso.with(context)
                .load(MyConstant.NAMESPACE + data.url)
                .resize(view.getWidth(), view.getHeight())
                .error(R.drawable.banner_mask1)
                .placeholder(R.drawable.banner_mask1)
                .into(iv);
        if (data.endTime == 0)
            return;
        long time = data.endTime - System.currentTimeMillis();
        if (time < 0) {
            tv_time.setText(context.getString(R.string.promotion_over));
            tv_time.setTextColor(Color.RED);
        } else {
            long day = time / (24 * 60 * 60 * 1000);
            long hour = (time / (60 * 60 * 1000) - day * 24);
            long min = ((time / (60 * 1000)) - day * 24 * 60 - hour * 60);
            long s = (time / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
            tv_time.setText(context.getString(R.string.promotion_time) + ":" + day + "天" + hour + "小时" + min + "分" + s + "秒");
        }

    }
}
