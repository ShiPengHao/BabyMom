package com.yimeng.babymom.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

/**
 * pagerAdapter的简单包装
 */

public abstract class DefaultPagerAdapter extends PagerAdapter {
    @Override
    public abstract int getCount();

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
