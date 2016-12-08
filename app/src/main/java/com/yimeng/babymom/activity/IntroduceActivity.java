package com.yimeng.babymom.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.yimeng.babymom.R;
import com.yimeng.babymom.adapter.DefaultPagerAdapter;
import com.yimeng.babymom.interFace.IntroduceInterface;
import com.yimeng.babymom.utils.DensityUtil;

/**
 * 引导界面，应用第一次运行时展示应用信息
 */
public class IntroduceActivity extends BaseActivity implements IntroduceInterface {
    private static final int[] PIC_RES_IDS = new int[]{R.drawable.welcome1, R.drawable.welcome2, R.drawable.welcome3};
    private static final int PIC_COUNT = PIC_RES_IDS.length;
    private static final int PAGE_JUMP_TIME_DELAY = 5;
    private int mTimeCount = PAGE_JUMP_TIME_DELAY;
    private ViewPager mViewPager;
    private Button bt_login;
    private LinearLayout ll_points;
    private MyPageListener mPageChangeListener;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PAGE_JUMP_TIME_DELAY:
                    updateTextIndicator(PIC_COUNT - 1);
                    break;
            }
        }
    };


    /**
     * 实改变页码指示
     */
    private class MyPageListener extends ViewPager.SimpleOnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            refreshIndicator(position);
        }
    }

    private class IntroduceAdapter extends DefaultPagerAdapter {

        @Override
        public int getCount() {
            return PIC_COUNT;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(activity);
            imageView.setBackgroundResource(PIC_RES_IDS[position]);
            container.addView(imageView);
            return imageView;
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
        initDots();
    }

    protected void setListener() {
        mViewPager.setAdapter(new IntroduceAdapter());
        mPageChangeListener = new MyPageListener();
        mViewPager.addOnPageChangeListener(mPageChangeListener);
        bt_login.setOnClickListener(this);
    }

    protected void initData() {
    }

    @Override
    public int setStatusBarColor() {
        return Color.parseColor("#3F3B54");
    }

    public void initDots() {
        ll_points.removeAllViews();
        ImageView imageView;
        LinearLayout.LayoutParams startParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams otherParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        otherParams.leftMargin = DensityUtil.dip2px(20);
        for (int i = 0; i < PIC_COUNT; i++) {
            imageView = new ImageView(this);
            imageView.setBackgroundResource(R.drawable.selector_dot);
            if (i == 0) {
                imageView.setEnabled(false);
                ll_points.addView(imageView, startParams);
            } else
                ll_points.addView(imageView, otherParams);
        }
    }

    private void updateDotsIndicator(int position) {
        for (int i = 0; i < PIC_COUNT; i++) {
            if (i == position)
                ll_points.getChildAt(i).setEnabled(false);
            else
                ll_points.getChildAt(i).setEnabled(true);

        }
    }

    private void updateTextIndicator(int position) {
        if (position == PIC_COUNT - 1) {
            if (mTimeCount > 0) {
                bt_login.setText(String.format("%s，%s秒", getString(R.string.skip_introduce), mTimeCount--));
                mHandler.sendEmptyMessageDelayed(PAGE_JUMP_TIME_DELAY, 1000);
            } else {
                goToLogin();
            }
        } else {
            mHandler.removeCallbacksAndMessages(null);
            mTimeCount = PAGE_JUMP_TIME_DELAY;
            bt_login.setText(getString(R.string.skip_introduce));
        }
    }

    /**
     * 刷新页码指示
     *
     * @param position 页码
     */
    public void refreshIndicator(int position) {
        updateDotsIndicator(position);
        updateTextIndicator(position);
    }

    @Override
    protected void onInnerClick(int id) {
        goToLogin();
    }

    /**
     * 去登陆页面
     */
    public void goToLogin() {
        startActivity(new Intent(IntroduceActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        if (mViewPager != null && mPageChangeListener != null)
            mViewPager.removeOnPageChangeListener(mPageChangeListener);
        super.onDestroy();
    }

}
