package com.yimeng.babymom.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.adapter.DefaultBannerAdapter;
import com.yimeng.babymom.adapter.DoctorAdapter;
import com.yimeng.babymom.bean.DecorateImgBean;
import com.yimeng.babymom.bean.DoctorBean;
import com.yimeng.babymom.interFace.GeneralBannerInterface;
import com.yimeng.babymom.task.BannerTask;
import com.yimeng.babymom.task.LoadDoctorTask;
import com.yimeng.babymom.utils.DensityUtil;
import com.yimeng.babymom.utils.JsonUtils;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.UiUtils;
import com.yimeng.babymom.view.CycleViewPager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;


/**
 * 科室activity
 */
public class DepartmentActivity extends BaseActivity implements AdapterView.OnItemClickListener, CycleViewPager.OnItemClickListener, GeneralBannerInterface {
    private ListView lv_doctor;
    private TextView tv_doctor_tip;
    private String departments_id;
    private TextView tv_schedule;
    private Dialog scheduleDialog;
    private CycleViewPager viewPager;
    private LinearLayout ll_points;
    private TextView tv_img_title;

    private String[] days = new String[]{"一", "二", "三", "四", "五", "六", "日"};

    private ArrayList<DecorateImgBean> mBannerBeans = new ArrayList<>();
    private PagerAdapter mBannerAdapter;
    private ArrayList<DoctorBean> mDutyDoctors = new ArrayList<>();
    private ArrayList<DoctorBean> mAllDoctors = new ArrayList<>();
    private DoctorAdapter mDoctorAdapter;
    private ViewPager.SimpleOnPageChangeListener mPageChangeListener;
    private AsyncTask<Object, Object, String> mBannerTask;
    private AsyncTask<Object, Object, String> mAllDoctorTask;
    private AsyncTask<Object, Object, String> mDutyDoctorTask;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_department;
    }

    @Override
    protected void initView() {
        lv_doctor = (ListView) findViewById(R.id.lv);
        tv_schedule = (TextView) findViewById(R.id.tv_schedule);
        tv_doctor_tip = (TextView) findViewById(R.id.tv_doctor_tip);
        tv_doctor_tip.setVisibility(View.GONE);

        viewPager = (CycleViewPager) findViewById(R.id.vp);
        ll_points = (LinearLayout) findViewById(R.id.ll_points);
        tv_img_title = (TextView) findViewById(R.id.tv_img_title);

    }

    @Override
    protected void setListener() {
        tv_schedule.setOnClickListener(this);
        setDoctorListListener();
        setBannerPagerListener();
    }

    /**
     * 设置banner
     */
    private void setBannerPagerListener() {
        mBannerAdapter = new DefaultBannerAdapter(mBannerBeans, viewPager);
        mPageChangeListener = new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                if (null == mBannerBeans || mBannerBeans.size() < 2)
                    return;
                tv_img_title.setText(mBannerBeans.get(position).decorate_name);
                for (int i = 0; i < ll_points.getChildCount(); i++) {
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
     * 设置今日值班医生列表
     */
    private void setDoctorListListener() {
        mDoctorAdapter = new DoctorAdapter(mDutyDoctors);
        lv_doctor.setAdapter(mDoctorAdapter);
        lv_doctor.setOnItemClickListener(this);
    }

    @Override
    protected void initData() {
        try {
            Intent intent = getIntent();
            departments_id = intent.getStringExtra("departments_id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        requestDutyDoctors();
        requestBanner();
        requestAllDoctors();
    }

    /**
     * 请求值班医师
     */
    public void requestDutyDoctors() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(LoadDoctorTask.DEPARTMENTS_ID, departments_id);
        hashMap.put(LoadDoctorTask.WEEK, getDayOfWeek());
        mDutyDoctorTask = new LoadDoctorTask(this, LoadDoctorTask.FLAG_DUTY).execute(LoadDoctorTask.METHOD, hashMap);
    }

    /**
     * 获得值班医生
     *
     * @param result 结果
     */
    public void onDutyDoctors(String result) {
        try {
            JsonUtils.parseListResult(mDutyDoctors, DoctorBean.class, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Collections.sort(mDutyDoctors);
        mDoctorAdapter.notifyDataSetChanged();
        lv_doctor.setVisibility(mDutyDoctors.size() > 0 ? View.VISIBLE : View.INVISIBLE);
        tv_doctor_tip.setVisibility(mDutyDoctors.size() > 0 ? View.GONE : View.VISIBLE);
    }

    /**
     * 请求所有医生
     */
    public void requestAllDoctors() {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put(LoadDoctorTask.DEPARTMENTS_ID, departments_id);
        hashMap.put(LoadDoctorTask.WEEK, 0);
        mAllDoctorTask = new LoadDoctorTask(this, LoadDoctorTask.FLAG_ALL).execute(LoadDoctorTask.METHOD, hashMap);
    }

    /**
     * 获得可是所有医生
     *
     * @param result 结果
     */
    public void onAllDoctors(String result) {
        try {
            JsonUtils.parseListResult(mAllDoctors, DoctorBean.class, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获得当天星期几
     *
     * @return 星期几，1-7
     */
    private int getDayOfWeek() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        calendar.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0)
            dayOfWeek = 7;
        return dayOfWeek;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        DoctorBean doctorBean = mDutyDoctors.get(position);
//                switch (chatOrBooking) {
//                    case EXTRA_BOOKING:
//                        startActivity(new Intent(this, DoctorDetailActivity.clazz).putExtra("mDutyDoctors", doctorBean));
//                        break;
//                    case EXTRA_CHAT:
//                        startActivity(new Intent(this, ChatActivity.clazz).putExtra(EaseConstant.EXTRA_USER_ID, "doctor_" + doctorBean.doctor_id));
//                        break;
//                }
    }

    @Override
    public void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.tv_schedule:
                if (mAllDoctors.size() > 0)
                    showScheduleWindow();
                else
                showToast(getString(R.string.no_doctor));
                break;
        }
    }

    /**
     * 显示科室排班表窗口
     */
    private void showScheduleWindow() {
        if (scheduleDialog == null) {
            scheduleDialog = new Dialog(this);
            scheduleDialog.setTitle(R.string.doctor_duty_list);
            scheduleDialog.setContentView(initScheduleView());
            Window dialogWindow = scheduleDialog.getWindow();
            assert dialogWindow != null;
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = DensityUtil.SCREEN_WIDTH; // 宽度
            dialogWindow.setAttributes(lp);
        }
        scheduleDialog.show();
    }

    /**
     * 创建排班表视图
     *
     * @return view视图
     */
    private View initScheduleView() {
        View parentView = UiUtils.inflate(R.layout.layout_schedule_parent);
        initDoctorNameView(parentView);
        initScheduleGridView(parentView);
        return parentView;
    }

    /**
     * 创建排班表的排班视图
     *
     * @param parentView 父控件
     */
    private void initScheduleGridView(View parentView) {
        // 排班表视图
        final GridView gridView = (GridView) parentView.findViewById(R.id.gd_schedule);
        BaseAdapter gridViewAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return (mAllDoctors.size() + 1) * 7;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    convertView = UiUtils.inflate(R.layout.item_text_schedule);
                    TextView textView = (TextView) convertView.findViewById(R.id.tv);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int doctorIndex = position / 7 - 1;
                            if (doctorIndex >= 0) {
//                                startActivity(new Intent(DepartmentActivity.this, DoctorDetailActivity.clazz).putExtra("mDutyDoctors", mAllDoctors.get(doctorIndex)));
                                dismissDialog();
                            }
                        }
                    });
                    convertView.setTag(textView);
                }
                TextView textView = (TextView) convertView.getTag();
                if (position < 7)
                    textView.setText(days[position]);
//                else if (position % 8 == 0)
//                    textView.setText(mAllDoctors.get(position / 8 - 1).doctor_name);
                else if (mAllDoctors.get(position / 7 - 1).Is_Order == 1 || mAllDoctors.get(position / 7 - 1).weekday.contains(String.valueOf(position % 7 + 1))) {
                    textView.setText("班");
                } else
                    textView.setText("");

                return convertView;
            }
        };
        gridView.setAdapter(gridViewAdapter);
    }

    /**
     * 创建医生姓名视图
     *
     * @param parentView 父控件
     */
    private void initDoctorNameView(View parentView) {
        // 医生姓名列表
        ListView listView = (ListView) parentView.findViewById(R.id.lv);
        listView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return mAllDoctors.size() + 1;
            }

            @Override
            public Object getItem(int position) {
                return null;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @NonNull
            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {
                if (convertView == null) {
                    convertView = UiUtils.inflate(R.layout.item_text_schedule);
                    TextView textView = (TextView) convertView.findViewById(R.id.tv);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (position >= 1) {
//                                startActivity(new Intent(DepartmentActivity.this, DoctorDetailActivity.clazz).putExtra("mDutyDoctors", mAllDoctors.get(position - 1)));
                                dismissDialog();
                            }
                        }
                    });
                    convertView.setTag(textView);
                }
                TextView textView = (TextView) convertView.getTag();
                textView.setText(position == 0 ? "" : mAllDoctors.get(position - 1).doctor_name);
                return convertView;
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        viewPager.startRoll();
    }

    @Override
    public void onPause() {
        super.onPause();
        viewPager.stopRoll();
    }

    @Override
    protected void onDestroy() {
        if (viewPager != null && mPageChangeListener != null)
            viewPager.removeOnPageChangeListener(mPageChangeListener);
        if (mBannerTask != null)
            mBannerTask.cancel(true);
        if (mAllDoctorTask != null)
            mAllDoctorTask.cancel(true);
        if (mDutyDoctorTask != null)
            mDutyDoctorTask.cancel(true);
        dismissDialog();
        super.onDestroy();
    }

    private void dismissDialog() {
        if (scheduleDialog != null && scheduleDialog.isShowing())
            scheduleDialog.dismiss();
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
            imageView = new ImageView(mActivity);
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
    /**
     * 轮播图条目被点击回调
     */
    public void onItemClick(int index) {
        if (index < mBannerBeans.size() && index >= 0) {
            String url = mBannerBeans.get(index).decorate_value;
            if (!TextUtils.isEmpty(url) && url.matches(MyConstant.URL_PATTERN)) {
                startActivity(new Intent(this, WebViewActivity.class).putExtra("url", url));
            }
        }
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
        viewPager.setAdapter(mBannerAdapter);
    }
}
