package com.yimeng.babymom.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jumper.data.FHRInfo;
import com.nineoldandroids.animation.ObjectAnimator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.yimeng.babymom.R;
import com.yimeng.babymom.service.FHRService;
import com.yimeng.babymom.utils.ChartUtils;
import com.yimeng.babymom.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


/**
 * 健康监测页面，主要功能有：<br/>
 * <li>通过绑定{@link FHRService}服务获取胎心率
 * <li>使用第三方图表控件库MPAndroidChart的{@link LineChart}展示胎心率波形，这里封装了一个图表有关的工具类{@link ChartUtils}
 * <li>通过{@link AsyncTask}缓存、读取胎心率数据到本地SharedPreference
 * <li>使用{@link MaterialCalendarView}展示日历，充当查看历史纪录的入口
 * <li>使用{@link Intent#ACTION_BATTERY_CHANGED}粘性广播在每次开始记录胎心率时查看电量，并及时提醒用户充电但不拒绝操作
 * <li>使用{@link Intent#ACTION_HEADSET_PLUG}广播实时检测耳机插孔状态，如果拔出则跳转到{@link HealthMonitorIntroduceActivity}页面
 */

public class HealthMonitorActivity extends BaseActivity implements OnDateSelectedListener, OnChartValueSelectedListener, FHRService.FHRReceiver {

    private MaterialCalendarView mCalendarView;
    private LineChart mLineChart;
    private TextView tv_beat_cur;
    private Button bt_submit;
    private Button bt_save_img;
    private Button bt_save_data;
    private ImageView iv_cal;
    private RelativeLayout action_bar;
    /**
     * 状态：正在接收数据
     */
    private boolean isRecording;
    /**
     * 状态：已停止接收数据，可以重置
     */
    private boolean isRecordStopped;
    /**
     * 保存监测数据的异步任务
     */
    private AsyncTask<Void, Void, Void> mSaveTask;
    /**
     * 当前选中的日期，默认当天
     */
    private Date mSelectedDate = new Date();
    /**
     * 检测电池电量的intent:<br/>
     * 在{@link #setListener}方法中注册{@link Intent#ACTION_BATTERY_CHANGED}的粘性广播获取<br/>
     * 用于在每次开始监测时{@link #startRecord()}检查电量状态
     */
    private Intent mBatteryStatus;

    /**
     * 每次监测获取的有效数据的最低个数限制，在保存数据时判断
     */
    @SuppressWarnings("all")
    private final int VALID_NUMBER_MIN = 300;

    /**
     * 读取监测数据的异步任务
     */
    private AsyncTask<Void, Void, ArrayList<Entry>> mReadTask;
    /**
     * 缓存所选日期对应的历史记录数据的集合
     */
    private HashMap<String, ArrayList<Entry>> mHistoryMap = new HashMap<>();
    /**
     * {@link FHRService}服务的连接对象，用于获取设备的胎心数据
     */
    private ServiceConnection mFHRServiceConn;
    /**
     * 用于接收到胎心率数据后处理post页面刷新逻辑到主线程的handler
     */
    private Handler mFHRHandler;
    /**
     * 开始记录胎心的时间
     */
    private long mStartTime;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_health_monitor;
    }

    @Override
    protected void initView() {
        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        mLineChart = (LineChart) findViewById(R.id.lineChart);
        tv_beat_cur = (TextView) findViewById(R.id.tv_beat_cur);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_save_img = (Button) findViewById(R.id.bt_save_img);
        bt_save_data = (Button) findViewById(R.id.bt_save_data);
        iv_cal = (ImageView) findViewById(R.id.iv);
        action_bar = (RelativeLayout) findViewById(R.id.action_bar);
        mLineChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.SCREEN_HEIGHT / 3));
        ChartUtils.initChartView(mLineChart);
    }

    @Override
    protected void setListener() {
        bt_submit.setOnClickListener(this);
        bt_save_img.setOnClickListener(this);
        bt_save_data.setOnClickListener(this);
        iv_cal.setOnClickListener(this);
        setCalendarListener();
        setChartListener();
        mBatteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        mFHRHandler = new Handler(Looper.getMainLooper());
        registerHeadsetPlugReceiver();
    }

    /**
     * 设置图表点击事件
     *
     * @see #onValueSelected(Entry, Highlight)
     */
    private void setChartListener() {
        mLineChart.setOnChartValueSelectedListener(this);
    }


    /**
     * 设置日历默认值、点击事件
     *
     * @see #onDateSelected(MaterialCalendarView, CalendarDay, boolean)
     */
    private void setCalendarListener() {
        mCalendarView.setDateSelected(new Date(), true);
        mCalendarView.setDynamicHeightEnabled(true);
        mCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return day.getYear() + "年" + (day.getMonth() + 1) + "月";
            }
        });
        mCalendarView.setOnDateChangedListener(this);
    }

    @Override
    protected void initData() {
        readChartData();
        setFHRSource();
    }

    /**
     * 设置胎心数据的数据源（{@link FHRService}服务提供）
     */
    private void setFHRSource() {
        mFHRServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                FHRService.FHRBinder binder = (FHRService.FHRBinder) service;
                binder.addFHRReceiver(HealthMonitorActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, FHRService.class), mFHRServiceConn, BIND_AUTO_CREATE);
    }

    @Override
    protected void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.bt_submit:// 三种状态：正在记录、停止记录、重置（初始状态）
                if (isRecording) {
                    stopRecord();
                } else if (isRecordStopped) {
                    resetChart();
                } else {
                    startRecord();
                }
                break;
            case R.id.bt_save_img:
                saveImg();
                break;
            case R.id.bt_save_data:
                saveData();
                break;
            case R.id.iv:
                displayCalendar();
                break;
        }
    }

    /**
     * 展示或者隐藏日历控件
     */
    private void displayCalendar() {
        // 根据日历控件的位置和它上部的控件的位置，计算日历目前可见区域的高度
        int[] lo = new int[2];
        mCalendarView.getLocationInWindow(lo);
        int yCal = lo[1];// 日历的位置
        action_bar.getLocationInWindow(lo);// 获取上部控件的位置
        int visibleHeight = yCal + mCalendarView.getHeight() - lo[1] - action_bar.getHeight();
        // 如果大于0表示可见，则向上动画隐藏可见区域的距离，否则还原
        if (visibleHeight > 0) {
            ObjectAnimator.ofFloat(mCalendarView.getParent(), "translationY", -(visibleHeight)).setDuration(400).start();
        } else {
            ObjectAnimator.ofFloat(mCalendarView.getParent(), "translationY", 0).setDuration(400).start();
        }

    }

    /**
     * 开始记录检测数据
     */
    private void startRecord() {
        // 判断电量，如果不足，则提示充电
        if (null != mBatteryStatus) {
            int level = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            int status = mBatteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isChargingOrGood = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL || 1.0f * level / scale > 0.2f;
            if (!isChargingOrGood) {
                showToast("手机电量不足，请及时充电");
            }
        }
        bt_submit.setText(getString(R.string.stop));
        isRecording = true;
        mStartTime = System.currentTimeMillis();
    }


    /**
     * 停止记录监测数据
     */
    private void stopRecord() {
        isRecordStopped = true;
        bt_submit.setText(getString(R.string.reset));
        mLineChart.setVisibleXRangeMaximum(Math.max(mLineChart.getXChartMax(), 10));
        mLineChart.moveViewToX(0);
        isRecording = false;
    }

    /**
     * 重置图表
     */
    private void resetChart() {
        mLineChart.clear();
        mLineChart.getXAxis().setAxisMaximum(ChartUtils.PAGE_SIZE);
        bt_submit.setText(getString(R.string.start));
        isRecordStopped = false;
    }

    /**
     * 保存图表图片到本地
     */
    private void saveImg() {
        String imgName = ChartUtils.getFileName(mSelectedDate) + "-" + System.currentTimeMillis();
        if (mLineChart.saveToGallery(imgName, 100)) {
            showToast("保存图片成功");
        } else {
            showToast("保存图片失败");
        }
    }

    /**
     * 保存图表数据到本地，只可能是当天数据
     */
    private void saveData() {
        // 判断是否正在记录
        if (isRecording) {
            showToast("正在胎心监测，请先停止再保存数据");
            return;
        }
        // 判断有无数据
        if (null == mLineChart.getLineData()) {
            showToast("无数据");
            return;
        }
        final ArrayList<Entry> values = (ArrayList<Entry>) ((LineDataSet) mLineChart.getLineData().getDataSetByIndex(0)).getValues();
        // 判断数据量
        if (values.size() < VALID_NUMBER_MIN) {
            showToast("监测时间过短，数据无意义");
            return;
        }
        mSaveTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                showLoadingView("正在保存今天的数据");
            }

            @SuppressLint("ApplySharedPref")
            @Override
            protected Void doInBackground(Void... params) {
                String fileName = ChartUtils.getFileName(new Date());
                mHistoryMap.put(fileName, values);
                SharedPreferences prefs = ChartUtils.getPrefs(fileName);
                prefs.edit().clear().commit();
                for (Entry e : values) {
                    ChartUtils.putEntry(prefs, e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismissLoadingView();
                showToast("保存今天的数据成功");
            }
        }.execute();
    }

    /**
     * 从本地读取图表数据
     */
    private void readChartData() {
        if (null != mReadTask && mReadTask.getStatus() == AsyncTask.Status.RUNNING) {
            mReadTask.cancel(true);
        }
        mReadTask = new AsyncTask<Void, Void, ArrayList<Entry>>() {

            @Override
            protected void onPreExecute() {
                showLoadingView("正在读取记录，请稍等");
            }

            @Override
            protected ArrayList<Entry> doInBackground(Void... params) {
                String fileName = ChartUtils.getFileName(mSelectedDate);
                ArrayList<Entry> result = mHistoryMap.get(fileName);
                if (null == result) {
                    result = ChartUtils.getAllEntry(ChartUtils.getPrefs(fileName));
                    mHistoryMap.put(fileName, result);
                }
                return result;
            }

            @Override
            protected void onPostExecute(ArrayList<Entry> entries) {
                if (entries.size() != 0) {
                    bt_submit.setText(getString(R.string.reset));
                    isRecordStopped = true;
                    mLineChart.getXAxis().resetAxisMaximum();
                    ChartUtils.initLineData(mLineChart, entries);
                    mLineChart.invalidate();
                }
                dismissLoadingView();
            }
        }.execute();
    }

    public boolean inSameDay(Date date1, Date Date2) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(Date2);
        int year2 = calendar.get(Calendar.YEAR);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);
        return (year1 == year2) && (day1 == day2);
    }

    /**
     * 日历控件上的日期被选中时显示对应日期的胎心监测记录：<br/>
     * <li>如果点击的时期就是当前选择的日期，则不做处理。此日历控件{@link MaterialCalendarView}默认未做重复选中处理</li>
     * <li>如果当前正在胎心检测，则提示用户此操作无效，并恢复日历上的选中指示到今天。</li>
     * <li>非以上两种情况，则响应用户意图，切换日期，读取对应记录并展示到图表控件</li>
     */
    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        // 判断是否与当前选择的日期一致，一致不处理
        Date temp = date.getDate();
        if (inSameDay(temp, mSelectedDate)) {
            return;
        }
        // 判断是否正在监测
        if (isRecording) {
            showToast("正在胎心监测，请先停止再切换日期");
            mCalendarView.setDateSelected(date, false);
            mCalendarView.setDateSelected(new Date(), true);
            return;
        }
        // 更新日期
        mSelectedDate = temp;
        // 判断是否是今天，不是今天则只能看图表，保存图片，但是不能进行其它相关操作
        int visible = inSameDay(mSelectedDate, new Date()) ? View.VISIBLE : View.GONE;
        bt_save_data.setVisibility(visible);
        bt_submit.setVisibility(visible);
        tv_beat_cur.setVisibility(visible);
        // 重置图表
        resetChart();
        // 读取选择日期对应的数据
        readChartData();
    }

    @Override
    protected void onDestroy() {
        mCalendarView = null;
        if (null != mSaveTask) {
            mSaveTask.cancel(false);
        }
        if (null != mReadTask) {
            mReadTask.cancel(true);
        }
        if (null != mFHRHandler) {
            mFHRHandler.removeCallbacksAndMessages(null);
        }
        if (null != mFHRServiceConn) {
            unbindService(mFHRServiceConn);
        }
        if (null != mLineChart) {
            mLineChart.setOnChartValueSelectedListener(null);
        }
        if (null != mHeadsetPlugReceiver) {
            unregisterReceiver(mHeadsetPlugReceiver);
        }
        super.onDestroy();
    }

    /**
     * MPAndroidChart 图表上的点被选中时，吐司当前点坐标时间和胎心率
     */
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        // 获取时间
        int val = (int) e.getX();
        // 整理时间格式
        int sec = val % ChartUtils.PAGE_SIZE;
        int min = val / ChartUtils.PAGE_SIZE;
        StringBuffer time = new StringBuffer();
        if (sec == 0) {
            time.append(min).append("分");
        } else if (min == 0) {
            time.append(sec).append("秒");
        } else {
            time.append(min).append("分").append(sec).append("秒");
        }
        showToast(String.format("时间：%s，胎心率：%s", time, (int) e.getY()));
    }

    @Override
    public void onNothingSelected() {

    }

    /**
     * 接收到胎心率数据展示，如果正在监测则添加到图表上
     *
     * @param fhrInfo 数据
     */
    @Override
    public void onReceived(final FHRInfo fhrInfo) {
        // 接收数据，主线程刷新UI
        mFHRHandler.post(new Runnable() {
            @Override
            public void run() {
                int y = fhrInfo.getFhr();
                if (tv_beat_cur.getVisibility() == View.VISIBLE) {
                    tv_beat_cur.setText(String.format("%s:%s", getString(R.string.heart_now), y));
                }
                if (isRecording) {
                    if (y > 185) {
                        y = 185;
                    } else if (y < 85) {
                        y = 85;
                    }
                    ChartUtils.addLineData(mLineChart, new Entry((System.currentTimeMillis() - mStartTime) / 1000f, y));
                }
            }
        });
    }

    //***********************************以下为监听耳机插拔的广播部分*********************************************
    /**
     * 耳机拔插的广播
     */
    private HeadsetPlugReceiver mHeadsetPlugReceiver;

    /**
     * 延迟5s注册耳机拔插的广播
     */
    private void registerHeadsetPlugReceiver() {
        mFHRHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mHeadsetPlugReceiver = new HeadsetPlugReceiver();
                IntentFilter intentFilter = new IntentFilter(Intent.ACTION_HEADSET_PLUG);
                registerReceiver(mHeadsetPlugReceiver, intentFilter);
            }
        }, 5000);
    }

    /**
     * 耳机拔插的广播
     */
    public class HeadsetPlugReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (!intent.hasExtra("state") || intent.getIntExtra("state", 0) != 1) {
                showToast("连接已断开");
                startActivity(new Intent(HealthMonitorActivity.this, HealthMonitorIntroduceActivity.class));
                finish();
            }
        }
    }

}
