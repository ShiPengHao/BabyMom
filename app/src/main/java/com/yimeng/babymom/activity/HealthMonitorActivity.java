package com.yimeng.babymom.activity;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.ChartUtils;
import com.yimeng.babymom.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.yimeng.babymom.R.id.lineChart;


/**
 * 健康监测页面
 */

public class HealthMonitorActivity extends BaseActivity implements OnDateSelectedListener, OnChartValueSelectedListener {

    private MaterialCalendarView mCalendarView;
    private LineChart mLineChart;
    private Handler mChartDataHandler;
    private int mPointCount = 0;
    private TextView tv_beat_cur;
    private Button bt_submit;
    private Button bt_save_img;
    private Button bt_save_data;
    /**
     * 状态：正在接收数据
     */
    private boolean isMeasure;
    /**
     * 状态：已停止接收数据，可以重置
     */
    private boolean isStop;
    private AsyncTask<Void, Void, Void> mSaveTask;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_health_monitor;
    }

    @Override
    protected void initView() {
        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        mLineChart = (LineChart) findViewById(lineChart);
        tv_beat_cur = (TextView) findViewById(R.id.tv_beat_cur);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_save_img = (Button) findViewById(R.id.bt_save_img);
        bt_save_data = (Button) findViewById(R.id.bt_save_data);
        mLineChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.SCREEN_HEIGHT / 3));
        ChartUtils.initChartView(mLineChart);
    }

    @Override
    protected void setListener() {
        bt_submit.setOnClickListener(this);
        bt_save_img.setOnClickListener(this);
        bt_save_data.setOnClickListener(this);
        setCalendarListener();
        setChartListener();
    }

    /**
     * 设置图表
     */
    private void setChartListener() {
        mLineChart.setOnChartValueSelectedListener(this);
        mChartDataHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Entry entry = (Entry) msg.obj;
                ChartUtils.addLineData(mLineChart, entry);
                tv_beat_cur.setText(String.format("%s：%s", getString(R.string.heart_now), (int) entry.getY()));
                lineDataHeartBeat();
                return false;
            }
        });
    }


    /**
     * 设置日历
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
        readData();
    }

    /**
     * 波形振幅
     */
    final int amplitude = 10;
    /**
     * 波形基准
     */
    final int base = 130;
    /**
     * 波形周期
     */
    final int cycle = 100;
    /**
     * 胎动周期
     */
    final int quickenCycle = 100;
    /**
     * 胎动振幅
     */
    final int quickenAmplitude = 30;
    /*
     * 胎动时间长度
     */
    final int quickenLength = 4;

    /**
     * 模拟胎心图表数据
     */
    private void lineDataHeartBeat() {
        Message message = Message.obtain();
        int x = mPointCount++ * ChartUtils.POINT_SPACE;
        //波形随机因子
        float random = new Random().nextFloat();
        float y = (float) (base + (random + Math.sin(2 * (x % cycle) * Math.PI / cycle) / 2) * amplitude);
        if (x > quickenCycle && x % quickenCycle < quickenLength) {
            y += quickenAmplitude;
        }
        message.obj = new Entry(x, y);
        mChartDataHandler.sendMessageDelayed(message, 1000);
    }

    @Override
    protected void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.bt_submit:
                if (isMeasure) {
                    if (mChartDataHandler != null) {
                        mChartDataHandler.removeCallbacksAndMessages(null);
                        isStop = true;
                        bt_submit.setText(getString(R.string.reset));
                        mLineChart.setVisibleXRangeMaximum(mPointCount * ChartUtils.POINT_SPACE);
                        mLineChart.moveViewToX(0);
                    }
                    isMeasure = false;
                } else if (isStop) {
                    mPointCount = 0;
                    mLineChart.clear();
                    mLineChart.getXAxis().setAxisMaximum(ChartUtils.PAGE_SIZE);
                    tv_beat_cur.setText("");
                    bt_submit.setText(getString(R.string.start));
                    isStop = false;
                } else {
                    lineDataHeartBeat();
                    bt_submit.setText(getString(R.string.stop));
                    isMeasure = true;
                }
                break;
            case R.id.bt_save_img:
                saveImg();
                break;
            case R.id.bt_save_data:
                saveData();
                break;
        }
    }

    /**
     * 保存图表图片到本地
     */
    private void saveImg() {
        String imgName = String.valueOf(System.currentTimeMillis());
        if (mLineChart.saveToGallery(imgName, 100)) {
            showToast("保存图片成功");
        } else {
            showToast("保存图片失败");
        }
    }

    /**
     * 保存图表数据到本地
     */
    private void saveData() {
        final List<Entry> values = ((LineDataSet) mLineChart.getLineData().getDataSetByIndex(0)).getValues();
        mSaveTask = new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                bt_save_data.setEnabled(false);
                showLoadingView("保存中。。。");
            }

            @Override
            protected Void doInBackground(Void... params) {
                SharedPreferences prefs = ChartUtils.getPrefs(new Date());
                prefs.edit().clear().commit();
                for (Entry e : values) {
                    ChartUtils.putEntry(prefs, e);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                dismissLoadingView();
                showToast("保存数据成功");
                bt_save_data.setEnabled(true);
            }
        }.execute();
    }

    /**
     * 从本地读取图表数据
     */
    private void readData() {
        ArrayList<Entry> entries = ChartUtils.getAllEntry(ChartUtils.getPrefs(new Date()));
        if (entries.size() != 0) {
            bt_submit.setText(getString(R.string.reset));
            isStop = true;
            mLineChart.getXAxis().resetAxisMaximum();
            ChartUtils.initLineData(mLineChart, entries);
        }
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        showToast(date.getDate().toLocaleString());
    }

    @Override
    protected void onDestroy() {
        mCalendarView = null;
        if (null != mChartDataHandler) {
            mChartDataHandler.removeCallbacksAndMessages(null);
        }
        if (null != mSaveTask) {
            mSaveTask.cancel(false);
        }
        super.onDestroy();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        int val = (int) e.getX();
        int sec = val % ChartUtils.PAGE_SIZE;
        int min = val / ChartUtils.PAGE_SIZE;
        String time;
        if (sec == 0) {
            time = min + "分";
        } else if (min == 0) {
            time = sec + "秒";
        } else {
            time = min + "分" + sec + "秒";
        }
        showToast(String.format("时间：%s，值：%s", time, (int) e.getY()));
    }

    @Override
    public void onNothingSelected() {

    }

}
