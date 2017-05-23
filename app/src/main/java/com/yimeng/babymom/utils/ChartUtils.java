package com.yimeng.babymom.utils;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.yimeng.babymom.R;

import java.util.ArrayList;

/**
 * 图表相关工具类
 */
public class ChartUtils {


    public static final int POINT_PER_PAGE = 60;
    public static final int POINT_SPACE = 1;
    public static final int PAGE_SIZE = POINT_PER_PAGE * POINT_SPACE;
    public static final int COLOR_ACCENT = MyApp.getAppContext().getResources().getColor(R.color.colorAccent);
    public static final int BG_LIGHT_GREEN = MyApp.getAppContext().getResources().getColor(R.color.bg_light_green);

    /**
     * 设置图标控件的样式
     */
    public static void initChartView(LineChart lineChart) {
        //chart
//        //设置手势滑动事件
//        lineChart.setOnChartGestureListener(this);
//        //设置数值选择监听
//        lineChart.setOnChartValueSelectedListener(this);
        //拖拽
        lineChart.setDragEnabled(true);
        //缩放
        lineChart.setPinchZoom(true);
        // 标签
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataText("无当天数据");
        lineChart.setNoDataTextColor(BG_LIGHT_GREEN);

        //x轴
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMaximum(PAGE_SIZE);
        xAxis.setAxisMinimum(0f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.enableGridDashedLine(10f, 10f, 0f);
//        xAxis.setDrawAxisLine(false);
//        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if (value == 0)
                    return "0";
                int val = (int) value;
                return val % PAGE_SIZE == 0 ? val / PAGE_SIZE + "min" : val % PAGE_SIZE + "s";
            }
        });

        //y轴
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setDrawZeroLine(false);
//        yAxis.setDrawGridLines(false);
        yAxis.setDrawLimitLinesBehindData(true);
        // y轴范围上限
        LimitLine limitTop = new LimitLine(160f, "160");
        limitTop.setLineWidth(0.5f);
        limitTop.setTextColor(COLOR_ACCENT);
        limitTop.setLineColor(COLOR_ACCENT);
        limitTop.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        // y轴范围下限
        LimitLine limitBellow = new LimitLine(110f, "110");
        limitBellow.setLineWidth(0.5f);
        limitBellow.setTextColor(COLOR_ACCENT);
        limitBellow.setLineColor(COLOR_ACCENT);
        limitBellow.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);

        // y轴添加上下范围线
        yAxis.removeAllLimitLines();
        yAxis.addLimitLine(limitTop);
        yAxis.addLimitLine(limitBellow);

        lineChart.getAxisRight().setEnabled(false);
        // 隐藏图标标题
        lineChart.getLegend().setEnabled(false);
    }

    /**
     * 添加一个数据到图表中
     *
     * @param lineChart 图表
     * @param entry     数据
     */
    public static void addLineData(LineChart lineChart, Entry entry) {
        if (lineChart.getData() == null) {
            // 创建一个数据集
            ArrayList<Entry> mLineValues = new ArrayList<>();
            mLineValues.add(entry);
            initLineData(lineChart, mLineValues);
        } else {
            lineChart.getData().addEntry(entry, 0);
            lineChart.notifyDataSetChanged();
            XAxis xAxis = lineChart.getXAxis();
            float axisMaximum = xAxis.getAxisMaximum();
            float currentX = entry.getX();
            if (axisMaximum != 0 && currentX > axisMaximum) {
                xAxis.setAxisMaximum(axisMaximum + PAGE_SIZE);
                lineChart.setVisibleXRangeMaximum(PAGE_SIZE);
                lineChart.moveViewToX(axisMaximum);
            } else {
                lineChart.invalidate();
            }
        }
    }

    /**
     * 设置数据线的样式和数据，设置初始值
     *
     * @param lineChart   图表控件
     * @param values 初始值集合
     */
    public static void initLineData(LineChart lineChart, ArrayList<Entry> values) {
        LineDataSet mLineDataSet = new LineDataSet(values, "");
        // 设置线
//        mLineDataSet.enableDashedLine(10f, 5f, 0f);
//        mLineDataSet.enableDashedHighlightLine(10f, 5f, 0f);
        mLineDataSet.setColor(BG_LIGHT_GREEN); // 数据连接线颜色
        mLineDataSet.setLineWidth(2f);
        mLineDataSet.setDrawCircles(false);
        mLineDataSet.setDrawValues(false);
        mLineDataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        //添加数据集
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(mLineDataSet);
        //图标添加数据集
        lineChart.setData(new LineData(dataSets));
    }


}
