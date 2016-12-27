package com.yimeng.babymom.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.adapter.DefaultPagerAdapter;
import com.yimeng.babymom.interFace.IntroduceInterface;
import com.yimeng.babymom.utils.BitmapUtils;

/**
 * 引导界面，应用第一次运行时展示应用信息
 */
public class IntroduceActivity extends BaseActivity implements IntroduceInterface {
    private static final int[] PIC_RES_IDS = new int[]{R.drawable.welcome1, R.drawable.welcome2, R.drawable.welcome3};
    private static final int PIC_COUNT = PIC_RES_IDS.length;
    private static final int PAGE_JUMP_TIME_DELAY = 5;
    private int mTimeCount = PAGE_JUMP_TIME_DELAY;
    private ViewPager mViewPager;
    private TextView tv;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case PAGE_JUMP_TIME_DELAY:
                    refreshIndicator();
                    break;
            }
            return true;
        }
    });


    private class IntroduceAdapter extends DefaultPagerAdapter {

        @Override
        public int getCount() {
            return PIC_COUNT;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(mActivity);
//            imageView.setBackgroundResource(PIC_RES_IDS[position]);
            imageView.setBackgroundDrawable(BitmapUtils.bitmapToDrawable(mActivity, BitmapUtils.getResImg(mActivity, PIC_RES_IDS[position])));
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
        tv = (TextView) findViewById(R.id.tv);
    }

    protected void setListener() {
        mViewPager.setAdapter(new IntroduceAdapter());
        tv.setOnClickListener(this);
    }

    protected void initData() {
        mHandler.sendEmptyMessageDelayed(PAGE_JUMP_TIME_DELAY, 3000);
    }

//    public void initDots() {
//        ll_points.removeAllViews();
//        ImageView imageView;
//        LinearLayout.LayoutParams startParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        LinearLayout.LayoutParams otherParams = new LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        otherParams.leftMargin = DensityUtil.dip2px(20);
//        for (int i = 0; i < PIC_COUNT; i++) {
//            imageView = new ImageView(this);
//            imageView.setBackgroundResource(R.drawable.selector_dot);
//            if (i == 0) {
//                imageView.setEnabled(false);
//                ll_points.addView(imageView, startParams);
//            } else
//                ll_points.addView(imageView, otherParams);
//        }
//    }

//    private void updateDotsIndicator(int position) {
//        for (int i = 0; i < PIC_COUNT; i++) {
//            if (i == position)
//                ll_points.getChildAt(i).setEnabled(false);
//            else
//                ll_points.getChildAt(i).setEnabled(true);
//
//        }
//    }


    public void refreshIndicator() {
        if (mTimeCount > 0) {
            tv.setText(String.format("%s，%s秒", getString(R.string.skip_introduce), mTimeCount--));
            mHandler.sendEmptyMessageDelayed(PAGE_JUMP_TIME_DELAY, 1000);
        } else {
            goToLogin();
        }
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
        super.onDestroy();
    }

    @Override
    protected void setStatusBar() {
//        super.setStatusBar();
    }

}
