package com.yimeng.babymom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

/**
 * 图表相关工具类
 */
public class ChartUtils {
    /**
     * 每一页的数据个数，设置为60个，1分钟，即要保证1s一个数据点
     */
    public static final int PAGE_SIZE = 60;
    /**
     * 限制线的颜色
     */
    private static final int COLOR_LINE_LIMIT = MyApp.getAppContext().getResources().getColor(R.color.colorAccent);
    /**
     * 数据线的颜色
     */
    private static final int COLOR_LINE_DATA = MyApp.getAppContext().getResources().getColor(R.color.bg_light_green);
    /**
     * 日期转为对应文件名的格式
     */
    private static final SimpleDateFormat FILE_FORMAT = new SimpleDateFormat("yy-MM-dd", Locale.CHINA);

    /**
     * 设置图表控件的样式
     */
    public static void initChartView(LineChart lineChart) {
        //chart
//        //设置手势滑动事件
//        lineChart.setOnChartGestureListener(this);
        //拖拽
        lineChart.setDragEnabled(true);
        //缩放
        lineChart.setPinchZoom(true);
        // 标签
        lineChart.getDescription().setEnabled(false);
        lineChart.setNoDataTextColor(COLOR_LINE_DATA);

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
                int val = (int) value;
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
                return time;
            }
        });

        //y轴
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.enableGridDashedLine(10f, 10f, 0f);
        yAxis.setDrawZeroLine(false);
//        yAxis.setSpaceMin(1f);
        yAxis.setAxisMaximum(210f);
        yAxis.setAxisMinimum(50f);
//        yAxis.setDrawGridLines(false);
        yAxis.setDrawLimitLinesBehindData(true);
        // y轴范围上限
        LimitLine limitTop = new LimitLine(160f, "160");
        limitTop.setLineWidth(0.5f);
        limitTop.setTextColor(COLOR_LINE_LIMIT);
        limitTop.setLineColor(COLOR_LINE_LIMIT);
        limitTop.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        // y轴范围下限
        LimitLine limitBellow = new LimitLine(110f, "110");
        limitBellow.setLineWidth(0.5f);
        limitBellow.setTextColor(COLOR_LINE_LIMIT);
        limitBellow.setLineColor(COLOR_LINE_LIMIT);
        limitBellow.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);

        // y轴添加上下范围线
        yAxis.removeAllLimitLines();
        yAxis.addLimitLine(limitTop);
        yAxis.addLimitLine(limitBellow);

        lineChart.getAxisRight().setEnabled(false);
        // 隐藏图表标题
        lineChart.getLegend().setEnabled(false);
    }

    /**
     * 添加一个数据到图表中
     *
     * @param lineChart 图表
     * @param entry     数据
     */
    public static void appendLineEntry(LineChart lineChart, Entry entry) {
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
     * @param lineChart 图表控件
     * @param values    初始值集合
     */
    public static void initLineData(LineChart lineChart, ArrayList<Entry> values) {
        LineDataSet dataSet = new LineDataSet(values, "");
        // 设置线
//        dataSet.enableDashedLine(10f, 5f, 0f);
//        dataSet.enableDashedHighlightLine(10f, 5f, 0f);
        dataSet.setColor(COLOR_LINE_DATA); // 数据连接线颜色
        dataSet.setLineWidth(1.5f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        //添加数据集
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(dataSet);
        //图表添加数据集
        lineChart.setData(new LineData(dataSets));
    }

    /**
     * 根据日期绑定sp文件名，每天对应一个文件
     *
     * @param date 日期
     * @return 文件名
     */
    public static String getFileName(Date date) {
        return FILE_FORMAT.format(date);
    }

    /**
     * 根据文件名获取sp文件
     *
     * @param prefsName SharedPreference文件名：绑定的日期
     * @return SharedPreference单例
     */
    public static SharedPreferences getPrefs(String prefsName) {
        return MyApp.getAppContext().getSharedPreferences(prefsName, Context.MODE_PRIVATE);
    }

    /**
     * 根据文件名删除sp文件
     *
     * @param prefsName SharedPreference文件名：绑定的日期
     * @return 操作是否成功
     */
    public static boolean delPrefs(String prefsName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return MyApp.getAppContext().deleteSharedPreferences(prefsName);
        } else {
            getPrefs(prefsName).edit().clear().apply();
            return true;
        }
    }

    /**
     * 向文件中写入一个点
     *
     * @param prefs sp
     * @param entry 点信息
     */
    public static void saveEntry(SharedPreferences prefs, Entry entry) {
        prefs.edit().putFloat(String.valueOf(entry.getX()), entry.getY()).apply();
    }

    /**
     * 获取该文件下的所有键值对，返回包含所有点的信息的集合
     *
     * @param prefs sp
     * @return 包含所有点的信息的集合，不会为空
     */
    public static ArrayList<Entry> getOrderedEntryList(SharedPreferences prefs) {
        ArrayList<Entry> values = new ArrayList<>();
        for (String key : prefs.getAll().keySet()) {
            values.add(new Entry(Float.parseFloat(key), prefs.getFloat(key, 0)));
            Collections.sort(values, new Comparator<Entry>() {
                @Override
                public int compare(Entry o1, Entry o2) {
                    return (int) (1000 * (o1.getX() - o2.getX()));
                }
            });
        }
        return values;
    }
}
