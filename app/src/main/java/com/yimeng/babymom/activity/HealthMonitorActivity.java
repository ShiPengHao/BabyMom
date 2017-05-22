package com.yimeng.babymom.activity;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.yimeng.babymom.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

/**
 * 健康监测页面
 */

public class HealthMonitorActivity extends BaseActivity implements OnDateSelectedListener {

    private MaterialCalendarView mCalendarView;
    private LineChart mLineChart;
    private Handler mDataHandler;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_health_monitor;
    }

    @Override
    protected void initView() {
        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        mLineChart = (LineChart) findViewById(R.id.lineChart);
        initChartView();
    }

    /**
     * 设置图标控件的样式
     */
    private void initChartView() {
        //chart
//        //设置手势滑动事件
//        mLineChart.setOnChartGestureListener(this);
//        //设置数值选择监听
//        mLineChart.setOnChartValueSelectedListener(this);
        //后台绘制
        mLineChart.setDrawGridBackground(false);
        //设置描述文本
        mLineChart.getDescription().setEnabled(false);
        //设置支持触控手势
        mLineChart.setTouchEnabled(true);
        //设置缩放
        mLineChart.setDragEnabled(true);
        //设置推动
        mLineChart.setScaleEnabled(true);
        //如果禁用,扩展可以在x轴和y轴分别完成
        mLineChart.setPinchZoom(true);

        //x轴
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setAxisMaximum(100f);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.enableGridDashedLine(10f, 10f, 0f);

        //y轴
        YAxis yAxis = mLineChart.getAxisLeft();
        yAxis.setAxisMaximum(200f);
        yAxis.setAxisMinimum(30f);
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setDrawZeroLine(false);
        // y轴限制数据(而不是背后的线条勾勒出了上面)
        yAxis.setDrawLimitLinesBehindData(true);
        // y轴范围上限
        LimitLine limitTop = new LimitLine(160f, "160");
        limitTop.setLineWidth(1f);
        limitTop.setTextColor(Color.GREEN);
        limitTop.setLineColor(Color.LTGRAY);
        limitTop.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        // y轴范围下限
        LimitLine limitBellow = new LimitLine(110f, "110");
        limitBellow.setLineWidth(1f);
        limitBellow.setTextColor(Color.GREEN);
        limitBellow.setLineColor(Color.LTGRAY);
        limitBellow.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);

        // y轴添加上下范围线
        yAxis.removeAllLimitLines();
        yAxis.addLimitLine(limitTop);
        yAxis.addLimitLine(limitBellow);

        mLineChart.getAxisRight().setEnabled(false);
        // 隐藏图标标题
        mLineChart.getLegend().setEnabled(false);
    }

    /**
     * 设置数据线的样式和数据，或者刷新
     */
    private void initLineData() {
        // 创建一个数据集
        ArrayList<Entry> mLineValues = new ArrayList<>();
        mLineValues.add(new Entry(0, 80 + new Random().nextInt(90)));
        LineDataSet mLineDataSet = new LineDataSet(mLineValues, "");
        // 设置线
        mLineDataSet.enableDashedLine(10f, 5f, 0f);
        mLineDataSet.enableDashedHighlightLine(10f, 5f, 0f);
        mLineDataSet.setColor(Color.BLACK); // 数据连接线颜色
        mLineDataSet.setLineWidth(1f);
        mLineDataSet.setDrawCircles(false);
//        mLineDataSet.setCircleColor(Color.BLACK);// 数据点颜色
//        mLineDataSet.setCircleRadius(3f);
//        mLineDataSet.setDrawCircleHole(true);
        mLineDataSet.setDrawValues(false);
//        mLineDataSet.setValueFormatter(new IValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
//                return String.valueOf((int) value);
//            }
//        });
//        mLineDataSet.setValueTextSize(9f);
        mLineDataSet.setDrawFilled(false);
        mLineDataSet.setMode(mLineDataSet.getMode() == LineDataSet.Mode.HORIZONTAL_BEZIER
                ? LineDataSet.Mode.LINEAR
                : LineDataSet.Mode.HORIZONTAL_BEZIER);

        //添加数据集
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(mLineDataSet);
        //图标添加数据集
        mLineChart.setData(new LineData(dataSets));
//        mLineChart.invalidate();
        //默认动画
//        mLineChart.animateX(2500);
    }

    @Override
    protected void setListener() {
        setCalendarListener();
        mDataHandler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                addLineData(msg);
                lineDataHeartBeat();
                return true;
            }
        });
    }

    private void addLineData(Message msg) {
        Entry entry = (Entry) msg.obj;
        mLineChart.getData().addEntry(entry, 0);
        mLineChart.notifyDataSetChanged();
        XAxis xAxis = mLineChart.getXAxis();
        float axisMaximum = xAxis.getAxisMaximum();
        float currentX = entry.getX();
        if (currentX > axisMaximum) {
            xAxis.setAxisMinimum(axisMaximum + POINT_PER_PAGE * POINT_SPACE);
            xAxis.setAxisMaximum(axisMaximum);
            mLineChart.moveViewToX(mPointCount);
        }else {
            mLineChart.invalidate();
        }
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
        //设置数据
        initLineData();
        //模拟数据
        lineDataHeartBeat();
    }

    private int mPointCount = 1;
    private static final int POINT_PER_PAGE = 10;
    private static final int POINT_SPACE = 10;

    private void lineDataHeartBeat() {
        mPointCount++;
        Message message = Message.obtain();
        message.obj = new Entry(mPointCount * POINT_SPACE, 90 + new Random().nextInt(90));
        mDataHandler.sendMessageDelayed(message, 2000);
    }

    @Override
    protected void onInnerClick(int viewId) {

    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        showToast(date.getDate().toLocaleString());
    }

    @Override
    protected void onDestroy() {
        mCalendarView = null;
        if (null != mDataHandler) {
            mDataHandler.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }
}
