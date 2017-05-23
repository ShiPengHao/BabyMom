package com.yimeng.babymom.activity;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.ChartUtils;
import com.yimeng.babymom.utils.DensityUtil;

import java.util.Date;
import java.util.Random;

/**
 * 健康监测页面
 */

public class HealthMonitorActivity extends BaseActivity implements OnDateSelectedListener {

    private MaterialCalendarView mCalendarView;
    private LineChart mLineChart;
    private Handler mChartDataHandler;
    private int mPointCount = 0;
    private TextView tv_beat_cur;
    private TextView tv_beat_sel;
    private Button bt_submit;
    private Button bt_save;
    /**
     * 状态：正在接收数据
     */
    private boolean isMeasure;
    /**
     * 状态：已停止接收数据，可以重置
     */
    private boolean isStop;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_health_monitor;
    }

    @Override
    protected void initView() {
        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        mLineChart = (LineChart) findViewById(R.id.lineChart);
        tv_beat_cur = (TextView) findViewById(R.id.tv_beat_cur);
        tv_beat_sel = (TextView) findViewById(R.id.tv_beat_sel);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_save = (Button) findViewById(R.id.bt_save);
        mLineChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.SCREEN_HEIGHT / 3));
        ChartUtils.initChartView(mLineChart);
    }

    @Override
    protected void setListener() {
        setCalendarListener();
        bt_submit.setOnClickListener(this);
        bt_save.setOnClickListener(this);
        mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                tv_beat_sel.setText(String.format("选择点的值：%s", (int) e.getY()));
            }

            @Override
            public void onNothingSelected() {
                tv_beat_sel.setText("选择点的值：无");
            }
        });
        mChartDataHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                Entry entry = (Entry) msg.obj;
                ChartUtils.addLineData(mLineChart, entry);
                tv_beat_cur.setText(String.format("瞬时值：%s", (int) entry.getY()));
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

    }

    /**
     * 模拟胎心图标数据
     */
    private void lineDataHeartBeat() {
        Message message = Message.obtain();
        int x = mPointCount++ * ChartUtils.POINT_SPACE;
        float y = (float) Math.sin(2 * Math.PI * x / 40) * 24 + 135 + new Random().nextInt(3);
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
                    }
                    isMeasure = false;
                } else if (isStop) {
                    mLineChart.clear();
                    mPointCount = 0;
                    tv_beat_cur.setText("");
                    bt_submit.setText(getString(R.string.start));
                    isStop = false;
                } else {
                    lineDataHeartBeat();
                    bt_submit.setText(getString(R.string.stop));
                    isMeasure = true;
                }
                break;
            case R.id.bt_save:
                saveChart();
                break;
        }
    }

    /**
     * 保存图表到本地文件
     */
    private void saveChart() {
        String fileName = new Date().toLocaleString();
        if (mLineChart.saveToGallery(fileName, 100)) {
            showToast("保存成功");
        } else {
            showToast("保存失败");
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
        super.onDestroy();
    }
}
