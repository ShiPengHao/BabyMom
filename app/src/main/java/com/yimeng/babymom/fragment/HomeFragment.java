package com.yimeng.babymom.fragment;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.WebViewActivity;
import com.yimeng.babymom.adapter.DefaultAdapter;
import com.yimeng.babymom.adapter.DefaultPagerAdapter;
import com.yimeng.babymom.bean.DecorateImgBean;
import com.yimeng.babymom.bean.PicDesBean;
import com.yimeng.babymom.holder.BaseHolder;
import com.yimeng.babymom.holder.PicDesHolder;
import com.yimeng.babymom.interFace.HomeFInterface;
import com.yimeng.babymom.utils.DensityUtil;
import com.yimeng.babymom.utils.LocationUtils;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.view.CycleViewPager;
import com.yimeng.babymom.view.GridViewForScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 主页fragment
 */

public class HomeFragment extends BaseFragment implements View.OnClickListener, HomeFInterface, CycleViewPager.OnItemClickListener, LocationUtils.UpdateLocationListener, AdapterView.OnItemClickListener {
    private TextView tv_location;
    private TextView tv_img_title;
    private TextView tv_user_status;
    private TextView item_status;
    private TextView item_title;
    private TextView item_des;
    private TextView tv_sign;
    private CycleViewPager viewPager;
    private LinearLayout ll_points;
    private GridViewForScrollView gd_fun;
    private RelativeLayout rl_status;
    private RelativeLayout rl_tip;

    private ArrayList<DecorateImgBean> mBannerBeans = new ArrayList<>();
    private static final int[] BANNER_PLACE_HOLDER = new int[]{R.drawable.banner_mask1, R.drawable.banner_mask2, R.drawable.banner_mask3};
    private PagerAdapter mBannerPagerAdapter;
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    private String mLocationCity;
    private AsyncTask<Location, Void, String> mLocationTask;

    private DefaultAdapter<PicDesBean> mFunGridAdapter;
    private static final int[] FUN_ICON_IDS = new int[]{R.drawable.sultation, R.drawable.monitor, R.drawable.measure,
            R.drawable.encyclo, R.drawable.diet};
    private static final String[] mFunDes = MyApp.getAppContext().getResources().getStringArray(R.array.home_fun);
    private ArrayList<PicDesBean> mFunPicBeans = new ArrayList<>();

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initView(View view) {
        tv_location = (TextView) view.findViewById(R.id.tv_location);
        tv_sign = (TextView) view.findViewById(R.id.tv_sign);
        tv_img_title = (TextView) view.findViewById(R.id.tv_img_title);
        tv_user_status = (TextView) view.findViewById(R.id.tv_user_status);
        item_status = (TextView) view.findViewById(R.id.item_status);
        item_title = (TextView) view.findViewById(R.id.item_title);
        item_des = (TextView) view.findViewById(R.id.item_des);

        viewPager = (CycleViewPager) view.findViewById(R.id.vp);
        viewPager.setLayoutParams(new RelativeLayout.LayoutParams(DensityUtil.SCREEN_WIDTH, DensityUtil.SCREEN_WIDTH / 2));

        ll_points = (LinearLayout) view.findViewById(R.id.ll_points);

        gd_fun = (GridViewForScrollView) view.findViewById(R.id.gd_fun);

        rl_status = (RelativeLayout) view.findViewById(R.id.rl_status);
        rl_tip = (RelativeLayout) view.findViewById(R.id.rl_tip);

    }

    @Override
    protected void setListener() {
        tv_location.setOnClickListener(this);
        tv_sign.setOnClickListener(this);
        rl_status.setOnClickListener(this);
        rl_tip.setOnClickListener(this);

        initFunGridListener();
        initBannerPagerListener();
        LocationUtils.setUpdateLocationListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_sign:
                sign();
                break;
            case R.id.tv_location:
                break;
        }
    }

    private void initFunGridListener() {
        mFunGridAdapter = new DefaultAdapter<PicDesBean>(mFunPicBeans) {
            @Override
            protected BaseHolder getHolder() {
                return new PicDesHolder();
            }
        };
        gd_fun.setAdapter(mFunGridAdapter);
        gd_fun.setOnItemClickListener(this);
    }

    private void initBannerPagerListener() {
        mBannerPagerAdapter = new MyBannerAdapter();
        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (null == mBannerBeans || mBannerBeans.size() <= 1)
                    return;
                tv_img_title.setText(mBannerBeans.get(position).decorate_name);
                int childCount = ll_points.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    if (i == position) {
                        ll_points.getChildAt(i).setEnabled(false);
                    } else {
                        ll_points.getChildAt(i).setEnabled(true);
                    }
                }
            }
        };
        viewPager.addOnPageChangeListener(mPageChangeListener);
        viewPager.setOnItemClickListener(this);
    }

    /**
     * 初始化页码指示器
     */
    private void initDots() {
        ll_points.removeAllViews();
        int size = mBannerBeans.size();
        if (null == mBannerBeans || size < 2)
            return;
        ImageView imageView;
        LinearLayout.LayoutParams startParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams otherParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        otherParams.leftMargin = DensityUtil.dip2px(15);
        for (int i = 0; i < size; i++) {
            imageView = new ImageView(activity);
            imageView.setBackgroundResource(R.drawable.selector_dot);
            if (i == 0) {
                imageView.setEnabled(false);
                ll_points.addView(imageView, startParams);
            } else {
                ll_points.addView(imageView, otherParams);
            }
        }
    }

    @Override
    protected void initData() {
        requestBannerData();
        getUserInfo();
        setFunGridData();
    }

    private void setFunGridData() {
        mFunPicBeans.clear();
        for (int i = 0; i < FUN_ICON_IDS.length; i++) {
            mFunPicBeans.add(new PicDesBean(FUN_ICON_IDS[i], mFunDes[i]));
        }
        mFunGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void requestBannerData() {
        mBannerBeans.clear();
    }

    @Override
    public void sign() {

    }

    @Override
    public void getUserInfo() {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gd_fun:
                dispatchFun(position);
                break;
        }
    }

    /**
     * 分发功能点击时间
     *
     * @param position 位置
     */
    private void dispatchFun(int position) {
        switch (position) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
        }
    }

    @Override
    public void updateWithNewLocation(Location location) {
        if (mLocationTask != null)
            mLocationTask.cancel(true);
        mLocationTask = new LocationAsyncTask(this).execute(location);
    }

    @Override
    public void onItemClick(int index) {
        String url = mBannerBeans.get(index).decorate_value;
        if (!TextUtils.isEmpty(url) && url.matches(MyConstant.URL_PATTERN)) {
            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", url));
        }
    }

    @Override
    public void onResume() {
        tv_location.setText(mLocationCity);
        if (mBannerPagerAdapter.getCount() > 1)
            viewPager.startRoll();
        super.onResume();
    }

    @Override
    public void onPause() {
        viewPager.stopRoll();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (viewPager != null && mPageChangeListener != null)
            viewPager.removeOnPageChangeListener(mPageChangeListener);
        if (mLocationTask != null)
            mLocationTask.cancel(true);
        super.onDestroy();
    }

    private class MyBannerAdapter extends DefaultPagerAdapter {
        @Override
        public int getCount() {
            return mBannerBeans.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(activity);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            Picasso.with(getContext())
                    .load(MyConstant.NAMESPACE + mBannerBeans.get(position).decorate_img)
                    .resize(viewPager.getWidth(), viewPager.getHeight())
                    .placeholder(BANNER_PLACE_HOLDER[position % 3])
                    .error(BANNER_PLACE_HOLDER[position % 3])
//                        .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                    .into(imageView);
            container.addView(imageView);
            return imageView;
        }
    }


    private class LocationAsyncTask extends AsyncTask<Location, Void, String> {
        private WeakReference<HomeFragment> wrHomeFragment;

        LocationAsyncTask(HomeFragment homeFragment) {
            wrHomeFragment = new WeakReference<>(homeFragment);
        }

        @Override
        protected String doInBackground(Location... params) {
            if (wrHomeFragment.get() == null || null == params || null == params[0]) {
                return null;
            }
            Location location = params[0];
            double lat = location.getLatitude();
            double lng = location.getLongitude();
            List<Address> addList;
            Geocoder ge = new Geocoder(MyApp.getAppContext());
            try {
                addList = ge.getFromLocation(lat, lng, 1);
                if (addList != null && addList.size() > 0) {
                    Address ad = addList.get(0);
                    return ad.getLocality();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String locationCity) {
            HomeFragment homeFragment = wrHomeFragment.get();
            if (homeFragment != null)
                homeFragment.refreshLocationIndicator(locationCity);
        }
    }

    /**
     * 刷新显示位置
     *
     * @param locationCity 新位置
     */
    private void refreshLocationIndicator(String locationCity) {
        if (!TextUtils.isEmpty(locationCity) && !locationCity.equalsIgnoreCase(mLocationCity)) {
            mLocationCity = locationCity;
            tv_location.setText(locationCity);
        }
    }
}
