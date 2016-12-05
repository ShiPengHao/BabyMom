package com.yimeng.babymom.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yimeng.babymom.R;
import com.yimeng.babymom.fragment.IntroduceFragment;
import com.yimeng.babymom.utils.DensityUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 引导界面，应用第一次运行时展示应用信息
 */
public class IntroduceActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    private PagerAdapter adapter;
    private List<Fragment> fragments = new ArrayList<>();
    private boolean isRunning = true;
    private Button bt_login;
    private LinearLayout ll_points;

    /**
     * 实现：1.去黑边 2.改变页码指示器 3.检测最后一张图片的检测
     */
    private class MyPageListener implements ViewPager.OnPageChangeListener{
        private int count = 0;
        private EdgeEffectCompat leftEdge;
        private EdgeEffectCompat rightEdge;

        public MyPageListener(){
            try {
                Field leftEdgeField = mViewPager.getClass().getDeclaredField("mLeftEdge");
                Field rightEdgeField = mViewPager.getClass().getDeclaredField("mRightEdge");
                if(leftEdgeField != null && rightEdgeField != null){
                    leftEdgeField.setAccessible(true);
                    rightEdgeField.setAccessible(true);
                    leftEdge = (EdgeEffectCompat) leftEdgeField.get(mViewPager);
                    rightEdge = (EdgeEffectCompat) rightEdgeField.get(mViewPager);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if(leftEdge != null && rightEdge != null){
                leftEdge.finish();
                rightEdge.finish();
                leftEdge.setSize(0, 0);
                rightEdge.setSize(0, 0);
            }

            if (isRunning && position == mViewPager.getAdapter().getCount() - 1) {
                if (count > 5) {
                    isRunning = false;
                    goToLogin();
                } else {
                    count++;
                }
            }
        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < fragments.size(); i++) {
                if (i == position) {
                    ll_points.getChildAt(i).setEnabled(false);
                } else {
                    ll_points.getChildAt(i).setEnabled(true);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                count = 0;
            }
        }
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_introduce;
    }

    protected void initView() {
        mViewPager = (ViewPager) findViewById(R.id.vp);
        bt_login = (Button) findViewById(R.id.bt_login);
        ll_points = (LinearLayout) findViewById(R.id.ll_points);
    }

    protected void setListener() {
        adapter = new IntroduceFragmentAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mViewPager.addOnPageChangeListener(new MyPageListener());
        bt_login.setOnClickListener(this);
    }

    protected void initData() {
        Fragment fragment1 = new IntroduceFragment();
        Bundle bundle1 = new Bundle();
        bundle1.putInt("resId", R.drawable.welcome1);
        fragment1.setArguments(bundle1);
        fragments.add(fragment1);

        Fragment fragment2 = new IntroduceFragment();
        Bundle bundle2 = new Bundle();
        fragment2.setArguments(bundle2);
        bundle2.putInt("resId", R.drawable.welcome2);
        fragments.add(fragment2);

        Fragment fragment3 = new IntroduceFragment();
        Bundle bundle3 = new Bundle();
        fragment3.setArguments(bundle3);
        bundle3.putInt("resId", R.drawable.welcome3);
        fragments.add(fragment3);

        initDots();

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化页码指示器
     */
    private void initDots() {
        ll_points.removeAllViews();
        for (int i = 0; i < fragments.size(); i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.selector_dot);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i == 0) {
                imageView.setEnabled(false);
            } else {
                layoutParams.leftMargin = DensityUtil.dip2px(20);
            }
            ll_points.addView(imageView, layoutParams);
        }
    }

    @Override
    public void onClick(View v) {
        goToLogin();
    }

    /**
     * 去登陆页面
     */
    private void goToLogin() {
        // 去登录页面
        startActivity(new Intent(IntroduceActivity.this, LoginActivity.class));
        finish();
    }


    private class IntroduceFragmentAdapter extends FragmentPagerAdapter {

        public IntroduceFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
