package com.yimeng.babymom.activity;

import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;

import com.yimeng.babymom.R;
import com.yimeng.babymom.adapter.SaveFragmentPagerAdapter;
import com.yimeng.babymom.fragment.HomeFragment;
import com.yimeng.babymom.fragment.MyFragment;
import com.yimeng.babymom.fragment.PromotionFragment;
import com.yimeng.babymom.interFace.HomeAInterface;

import java.util.ArrayList;

/**
 * 主页activity
 */

public class HomeActivity extends BaseActivity implements HomeAInterface {
    private ViewPager viewPager;
    private LinearLayout ll_tab;
    /**
     * 此activity中ViewPager控件持有的Fragment集合
     */
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;
    /**
     * 不销毁Fragment，只控制显示和隐藏的PagerAdapter
     */
    private SaveFragmentPagerAdapter mPagerAdapter;
    private int mLastPageIndex;
    /**
     * 点击返回键时的时间
     */
    private long mLastBackTime = -1L;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        viewPager = (ViewPager) findViewById(R.id.vp);
        ll_tab = (LinearLayout) findViewById(R.id.ll_tab);
        initIndicator();
        checkFirstRunning();
    }

    @Override
    protected void setListener() {
        mPagerAdapter = new SaveFragmentPagerAdapter(getSupportFragmentManager(), mFragments);
        viewPager.setAdapter(mPagerAdapter);
        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                if (position != mLastPageIndex) {
                    refreshIndicator(position);
                }
            }
        };
        viewPager.addOnPageChangeListener(mPageChangeListener);
    }

    @Override
    protected void initData() {
        mFragments.add(new HomeFragment());
        mFragments.add(new PromotionFragment());
        mFragments.add(new MyFragment());
        mPagerAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onInnerClick(int viewId) {
        if (viewId != mLastPageIndex) {
            viewPager.setCurrentItem(viewId);
        }
    }

    @Override
    public void checkFirstRunning() {
        if (mPrefManager.getAccountFirstRunning()) {
            showOkTips(getString(R.string.firstRunningTip), null);
            mPrefManager.setAccountFirstRunning(false);
        }
    }

    public void initIndicator() {
        int childCount = ll_tab.getChildCount();
        View view;
        for (int i = 0; i < childCount; i++) {
            view = ll_tab.getChildAt(i);
            view.setId(i);
            view.setOnClickListener(this);
            if (i == 0) {
                view.setEnabled(false);
            } else {
                view.setEnabled(true);
            }
        }
    }

    @Override
    public void refreshIndicator(int position) {
        ll_tab.getChildAt(position).setEnabled(false);
        ll_tab.getChildAt(mLastPageIndex).setEnabled(true);
        mLastPageIndex = position;
    }

    @Override
    protected void onDestroy() {
        if (viewPager != null && mPageChangeListener != null) {
            viewPager.removeOnPageChangeListener(mPageChangeListener);
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                long now = SystemClock.uptimeMillis();
                if (mLastBackTime == -1 || (now - mLastBackTime) > 2000) {
                    mLastBackTime = now;
                    showToast(getString(R.string.once_more_to_quit));
                } else {
                    finish();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
