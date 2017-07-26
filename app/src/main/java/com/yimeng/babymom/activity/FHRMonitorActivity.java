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
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jumper.data.FHRInfo;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.yimeng.babymom.R;
import com.yimeng.babymom.service.FHRService;
import com.yimeng.babymom.utils.ChartUtils;
import com.yimeng.babymom.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Date;

/**
 * 健康监测页面，主要功能有：<br/>
 * <li>通过绑定{@link FHRService}服务获取胎心率
 * <li>使用第三方图表控件库MPAndroidChart的{@link LineChart}展示胎心率波形，这里封装了一个图表有关的工具类{@link ChartUtils}
 * <li>通过{@link AsyncTask}存储胎心率数据到本地SharedPreference
 * <li>使用{@link Intent#ACTION_BATTERY_CHANGED}粘性广播在每次开始记录胎心率时查看电量，并及时提醒用户充电但不拒绝操作
 * <li>使用{@link Intent#ACTION_HEADSET_PLUG}广播实时检测耳机插孔状态，如果拔出则跳转到{@link FHRIntroduceActivity}页面
 */
public class FHRMonitorActivity extends BaseActivity implements OnChartValueSelectedListener, FHRService.FHRReceiver {

    private LineChart mLineChart;
    private TextView tv_beat_cur;
    private Button bt_submit;
    private Button bt_save_img;
    private Button bt_save_data;
    private ImageView iv_cal;

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
        return R.layout.activity_fhr_monitor;
    }

    @Override
    protected void initView() {
        mLineChart = (LineChart) findViewById(R.id.lineChart);
        tv_beat_cur = (TextView) findViewById(R.id.tv_beat_cur);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_save_img = (Button) findViewById(R.id.bt_save_img);
        bt_save_data = (Button) findViewById(R.id.bt_save_data);
        iv_cal = (ImageView) findViewById(R.id.iv);
        mLineChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.SCREEN_HEIGHT / 3));
        ChartUtils.initChartView(mLineChart);
        mLineChart.setNoDataText("请在胎心率数据稳定后，点击下方开始按钮开始记录");
    }

    @Override
    protected void setListener() {
        bt_submit.setOnClickListener(this);
        bt_save_img.setOnClickListener(this);
        bt_save_data.setOnClickListener(this);
        iv_cal.setOnClickListener(this);
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

    @Override
    protected void initData() {
        setFHRSource();
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
                goHistory();
                break;
        }
    }

    /**
     * 设置胎心数据的数据源（{@link FHRService}服务提供）
     */
    private void setFHRSource() {
        mFHRServiceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                FHRService.FHRBinder binder = (FHRService.FHRBinder) service;
                binder.addFHRReceiver(FHRMonitorActivity.this);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(new Intent(this, FHRService.class), mFHRServiceConn, BIND_AUTO_CREATE);
    }

    /**
     * 去历史记录页面
     */
    private void goHistory() {
        startActivity(new Intent(this, FHRHistoryActivity.class));
    }

    @Override
    protected void onDestroy() {
        if (null != mSaveTask) {
            mSaveTask.cancel(false);
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
        String imgName = ChartUtils.getFileName(new Date()) + "-" + System.currentTimeMillis();
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
                // 保存当天日期到一个“年-月”的sp文件中，作为历史记录中日历上当天的一个事件标记，便于日历控件在UI上做标记
                String fileName = CalendarDay.today().getYear() + "-" + CalendarDay.today().getMonth();
                SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                sharedPreferences.edit().putString("day" + CalendarDay.today().getDay(), "").apply();
            }
        }.execute();
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
                startActivity(new Intent(FHRMonitorActivity.this, FHRIntroduceActivity.class));
                finish();
            }
        }
    }
}
