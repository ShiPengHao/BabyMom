package com.yimeng.babymom.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.AddressListActivity;
import com.yimeng.babymom.activity.DepartmentActivity;
import com.yimeng.babymom.activity.HealthMonitorActivity;
import com.yimeng.babymom.activity.HealthMonitorIntroduceActivity;
import com.yimeng.babymom.activity.HospitalListActivity;
import com.yimeng.babymom.activity.MeasureActivity;
import com.yimeng.babymom.activity.TestActivity;
import com.yimeng.babymom.activity.WebViewActivity;
import com.yimeng.babymom.adapter.DefaultAdapter;
import com.yimeng.babymom.adapter.DefaultBannerAdapter;
import com.yimeng.babymom.bean.DecorateImgBean;
import com.yimeng.babymom.bean.HospitalBean;
import com.yimeng.babymom.bean.PicDesBean;
import com.yimeng.babymom.bean.UserBean;
import com.yimeng.babymom.holder.BaseHolder;
import com.yimeng.babymom.holder.PicDesHolder;
import com.yimeng.babymom.interFace.HomeFInterface;
import com.yimeng.babymom.task.BannerTask;
import com.yimeng.babymom.task.HospitalCityTask;
import com.yimeng.babymom.task.SignTask;
import com.yimeng.babymom.task.UserInfoTask;
import com.yimeng.babymom.utils.DensityUtil;
import com.yimeng.babymom.utils.JsonUtils;
import com.yimeng.babymom.utils.LocationUtils;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.view.CycleViewPager;
import com.yimeng.babymom.view.GridViewForScrollView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.yimeng.babymom.utils.LocationUtils.sLocationReceiverIntent;

/**
 * 主页fragment
 */

public class HomeFragment extends BaseFragment implements HomeFInterface, CycleViewPager.OnItemClickListener, AdapterView.OnItemClickListener {
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
    private PagerAdapter mBannerPagerAdapter;
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;

    private DefaultAdapter<PicDesBean> mFunGridAdapter;
    private static final int[] FUN_ICON_IDS = new int[]{
            R.drawable.sultation, R.drawable.monitor, R.drawable.measure, R.drawable.encyclo
            , R.drawable.diet, R.drawable.jifen, R.drawable.help, R.drawable.quan
            , R.drawable.clazz, R.drawable.story, R.drawable.safe};
    private static final String[] Fun_Des = MyApp.getAppContext().getResources().getStringArray(R.array.home_fun);
    private ArrayList<PicDesBean> mFunPicBeans = new ArrayList<>();
    private AsyncTask<Object, Object, String> mBannerTask;
    private AsyncTask<Object, Object, String> mSignTask;
    private AsyncTask<Object, Object, String> mUserInfoTask;
    private ArrayList<HospitalBean> mHospitalList = new ArrayList<>();
    private AsyncTask<Object, Object, String> mHospitalTask;
    private UserBean mUserBean;
    private String mCityName;
    private AsyncTask<Location, Void, String> mLocationTask;
    private LocationReceiver mLocationReceiver;

    private class LocationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Location location = (Location) intent.getExtras().get(LocationManager.KEY_LOCATION_CHANGED);
                if (location != null)
                    getLocationCity(location);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

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
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_sign:
                sign();
                break;
            case R.id.tv_location:
                getCity();
                break;
            default:
                showToast(getString(R.string.fun_undo));
        }
    }

    public void getCity() {
        startActivityForResult(new Intent(activity, AddressListActivity.class), MyConstant.REQUEST_CITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case MyConstant.REQUEST_CITY:
                refreshLocationIndicator(data.getStringExtra("city"));
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 设置功能区域
     */
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

    /**
     * 设置轮播图
     */
    private void initBannerPagerListener() {
        mBannerPagerAdapter = new DefaultBannerAdapter(mBannerBeans, viewPager);
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
        requestBanner();
        getUserInfo();
        setFunGridData();
    }

    /**
     * 获取功能区域数据
     */
    private void setFunGridData() {
        mFunPicBeans.clear();
        for (int i = 0; i < FUN_ICON_IDS.length; i++) {
            mFunPicBeans.add(new PicDesBean(FUN_ICON_IDS[i], Fun_Des[i]));
        }
        mFunGridAdapter.notifyDataSetChanged();
    }

    @Override
    public void requestBanner() {
        if (mBannerTask != null)
            mBannerTask.cancel(true);
        HashMap<String, Object> params = new HashMap<>();
        mBannerTask = new BannerTask(this, null).execute(BannerTask.METHOD, params);

    }

    @Override
    public void onBannerResult(String result) {
        try {
            JsonUtils.parseListResult(mBannerBeans, DecorateImgBean.class, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        initDots();
        mBannerPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public void sign() {
        if (mSignTask != null)
            mSignTask.cancel(true);
        HashMap<String, Object> params = new HashMap<>();
        params.put(SignTask.PHONE, activity.mPrefManager.getAccountUsername());
        mSignTask = new SignTask(this, tv_sign).execute(SignTask.METHOD, params);
    }

    @Override
    public void getUserInfo() {
        if (mUserInfoTask != null)
            mUserInfoTask.cancel(true);
        HashMap<String, Object> params = new HashMap<>();
        params.put(UserInfoTask.PHONE, activity.mPrefManager.getAccountUsername());
        mUserInfoTask = new UserInfoTask(this, null).execute(UserInfoTask.METHOD, params);
    }

    @Override
    public void onUserInfoResult(UserBean userBean) {
        mUserBean = userBean;
        if (mUserBean == null)
            onUserError();
        else {
            tv_user_status.setText(userBean.user_status);
            item_status.setText(userBean.pregnant_check_status);
            item_title.setText(userBean.pregnant_check_item);
            item_des.setText(userBean.pregnant_check_detail);
        }
    }

    @Override
    public void onUserError() {
        showToast(getString(R.string.user_error));//TODO 广播不解
        LocationUtils.addReceiverIntent(sLocationReceiverIntent);
        mLocationReceiver = new LocationReceiver();
        activity.registerReceiver(mLocationReceiver, new IntentFilter(LocationUtils.LOCATION_ACTION));
        tv_location.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gd_fun:
                switch (position) {
                    case 0:
                        goToChat();
                        break;
                    case 1:
                        goToHealthMonitor();
                        break;
                    case 2:
                        goToMeasure();
                        break;
                    case 3:
                        startActivity(new Intent(activity, TestActivity.class));
                        break;
                    default:
                        showToast(String.format("%s%s", Fun_Des[position], getString(R.string.fun_undo)));
                }
                break;
        }
    }

    public void goToHealthMonitor() {
        AudioManager audioManager = (AudioManager) activity.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.isWiredHeadsetOn()) {
            startActivity(new Intent(activity, HealthMonitorActivity.class));
        }else {
            startActivity(new Intent(activity, HealthMonitorIntroduceActivity.class));
        }
    }

    public void goToMeasure() {
        startActivity(new Intent(activity, MeasureActivity.class));
    }

    public void goToChat() {
        if (mUserBean != null && mUserBean.bindHospitalId != null)
            goToHospital();
        else
            goToHospitalList();
    }

    private void goToHospital() {
        startActivity(new Intent(getActivity(), DepartmentActivity.class).putExtra("hospitalId", mUserBean.bindHospitalId));
    }

    private void goToHospitalList() {
        if (checkCityNameAndHospital())
            startActivity(new Intent(getActivity(), HospitalListActivity.class).putExtra("hospital", mHospitalList));
    }

    @Override
    public void onItemClick(int index) {
        String url = mBannerBeans.get(index).decorate_value;
        if (!TextUtils.isEmpty(url) && url.matches(MyConstant.URL_PATTERN)) {
            startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("url", url));
        }
    }

    /**
     * 验证城市名字
     *
     * @return 非空
     */
    private boolean checkCityNameAndHospital() {
        if (TextUtils.isEmpty(mCityName)) {
            showToast(getString(R.string.select_city));
            return false;
        }
        if (mHospitalList == null || mHospitalList.size() == 0) {
            showToast(getString(R.string.no_hospital));
            return false;
        }
        return true;

    }


    @Override
    public void onResume() {
        tv_location.setText(mCityName);
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
        if (mLocationReceiver != null)
            activity.unregisterReceiver(mLocationReceiver);
        LocationUtils.removeReceiverIntent(LocationUtils.sLocationReceiverIntent);
        if (viewPager != null && mPageChangeListener != null)
            viewPager.removeOnPageChangeListener(mPageChangeListener);
        if (mLocationTask != null)
            mLocationTask.cancel(true);
        if (mBannerTask != null)
            mBannerTask.cancel(true);
        if (mSignTask != null)
            mSignTask.cancel(true);
        if (mUserInfoTask != null)//凯旋
            mUserInfoTask.cancel(true);
        if (mHospitalTask != null)
            mHospitalTask.cancel(true);
        super.onDestroy();
    }


    /**
     * 根据定位信息，获取对应的城市信息
     *
     * @param location 定位
     */
    public void getLocationCity(Location location) {
        if (mLocationTask != null)
            mLocationTask.cancel(true);
        mLocationTask = new LocationAsyncTask(HomeFragment.this).execute(location);
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
     * @param locationCity 新位置-城市名
     */
    private void refreshLocationIndicator(String locationCity) {
        if (!TextUtils.isEmpty(locationCity) && !locationCity.equalsIgnoreCase(mCityName)) {
            mCityName = locationCity;
            tv_location.setText(locationCity);
            requestCityHospital();
        }
    }

    @Override
    public void requestCityHospital() {
        if (mHospitalTask != null)
            mHospitalTask.cancel(true);
        HashMap<String, Object> params = new HashMap<>();
        params.put(HospitalCityTask.CITYNAME, mCityName);
        mHospitalTask = new HospitalCityTask(this, null).execute(HospitalCityTask.METHOD, params);
    }

    @Override
    public void onHospitalResult(String result) {
        try {
            JsonUtils.parseListResult(mHospitalList, HospitalBean.class, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
