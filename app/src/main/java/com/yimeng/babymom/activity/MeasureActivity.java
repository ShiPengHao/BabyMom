package com.yimeng.babymom.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.adapter.DefaultAdapter;
import com.yimeng.babymom.adapter.SaveFragmentPagerAdapter;
import com.yimeng.babymom.bean.PicDesBean;
import com.yimeng.babymom.fragment.BabyHeightFragment;
import com.yimeng.babymom.fragment.BabyWeightFragment;
import com.yimeng.babymom.fragment.BirthDayFragment;
import com.yimeng.babymom.fragment.BloodTypeFragment;
import com.yimeng.babymom.holder.BaseHolder;
import com.yimeng.babymom.holder.PicDesHolder;
import com.yimeng.babymom.utils.KeyBoardUtils;
import com.yimeng.babymom.view.GridViewForScrollView;

import java.util.ArrayList;

/**
 * 测算工具
 */

public class MeasureActivity extends BaseActivity implements AdapterView.OnItemClickListener, View.OnTouchListener {
    private GridViewForScrollView gd_fun;
    private TextView tv_title;
    private ViewPager viewPager;
    private Button bt_submit;
    /**
     * 功能文字说明
     */
    private final String[] FUN_DES = new String[]{"宝宝血型", "预产期", "胎儿体重", "身高预测"};
    /**
     * 功能图片资源
     */
    private final int[] FUN_PICS = new int[]{R.drawable.blood, R.drawable.date, R.drawable.weight, R.drawable.height};
    private int mCheckedItemPosition;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private ArrayList<PicDesBean> mFunPicBeans = new ArrayList<>();
    private DefaultAdapter<PicDesBean> mFunGridAdapter;
    private SaveFragmentPagerAdapter mPagerAdapter;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_measure;
    }

    @Override
    protected void initView() {
        gd_fun = (GridViewForScrollView) findViewById(R.id.gd_fun);
        tv_title = (TextView) findViewById(R.id.tv_title);
        tv_title.setText(FUN_DES[0]);
        viewPager = (ViewPager) findViewById(R.id.vp);
        bt_submit = (Button) findViewById(R.id.bt_submit);
    }

    @Override
    protected void setListener() {
        mFunGridAdapter = new DefaultAdapter<PicDesBean>(mFunPicBeans) {
            @Override
            protected BaseHolder getHolder() {
                return new PicDesHolder();
            }
        };
        gd_fun.setAdapter(mFunGridAdapter);
        gd_fun.setOnItemClickListener(this);


        mPagerAdapter = new SaveFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        viewPager.setAdapter(mPagerAdapter);
        viewPager.setOnTouchListener(this);
        bt_submit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        mFunPicBeans.clear();
        // 默认选中第一个条目
        mFunPicBeans.add(new PicDesBean(FUN_PICS[0], FUN_DES[0], false));
        for (int i = 1; i < FUN_DES.length; i++) {
            mFunPicBeans.add(new PicDesBean(FUN_PICS[i], FUN_DES[i]));
        }
        mFunGridAdapter.notifyDataSetChanged();

        fragments.clear();
        fragments.add(new BloodTypeFragment());
        fragments.add(new BirthDayFragment());
        fragments.add(new BabyWeightFragment());
        fragments.add(new BabyHeightFragment());
        mPagerAdapter.notifyDataSetChanged();
    }

    public interface MeasureSubmitListener {
        /**
         * 点击提交
         */
        void onSubmit();
    }

    @Override
    protected void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.bt_submit:
                ((MeasureSubmitListener) fragments.get(mCheckedItemPosition)).onSubmit();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        KeyBoardUtils.closeKeyboard(viewPager);
        viewPager.setCurrentItem(position);
        tv_title.setText(FUN_DES[position]);
        if (position != mCheckedItemPosition) {
            mFunPicBeans.get(mCheckedItemPosition).enable = true;
            mFunPicBeans.get(position).enable = false;
            mCheckedItemPosition = position;
            mFunGridAdapter.notifyDataSetChanged();
        }
    }

    @Override
    // 禁用viewpager的滑动事件
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
