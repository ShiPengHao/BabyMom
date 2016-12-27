package com.yimeng.babymom.adapter;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.yimeng.babymom.R;
import com.yimeng.babymom.bean.DecorateImgBean;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyConstant;

import java.util.ArrayList;

/**
 * 通用的banner适配器
 */

public class DefaultBannerAdapter extends DefaultPagerAdapter {
    private static final int[] BANNER_PLACE_HOLDER = new int[]{R.drawable.bannermask1, R.drawable.bannermask2, R.drawable.bannermask3};
    private ArrayList<DecorateImgBean> mBannerBeans;

    private ViewPager viewPager;

    private final Context context = MyApp.getAppContext();

    public DefaultBannerAdapter(ArrayList<DecorateImgBean> bannerBeans, ViewPager viewPager) {
        mBannerBeans = bannerBeans;
        this.viewPager = viewPager;
    }

    @Override
    public int getCount() {
        return mBannerBeans.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Picasso.with(context)
                .load(MyConstant.NAMESPACE + mBannerBeans.get(position).decorate_img)
                .resize(viewPager.getWidth(), viewPager.getHeight())
                .placeholder(R.drawable.bannerplace)
                .error(BANNER_PLACE_HOLDER[position % 3])
//                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(imageView);
        container.addView(imageView);
        return imageView;
    }
}