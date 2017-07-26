package com.yimeng.babymom.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.nineoldandroids.animation.ObjectAnimator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.ChartUtils;
import com.yimeng.babymom.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 胎心监测历史页面，使用{@link MaterialCalendarView}展示日历，充当查看历史纪录的入口，通过{@link AsyncTask}从本地SharedPreference读取胎心率数据
 */
public class FHRHistoryActivity extends BaseActivity implements OnDateSelectedListener, OnChartValueSelectedListener {

    private MaterialCalendarView mCalendarView;
    private LineChart mLineChart;
    private ImageView iv_cal;
    private RelativeLayout action_bar;
    /**
     * 日历控件是否显示
     */
    private boolean isCalendarShow;
    /**
     * 当前选中的日期，默认当天
     */
    private Date mSelectedDate = new Date();

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


    @Override
    protected int setLayoutResId() {
        return R.layout.activity_fhr_history;
    }

    @Override
    protected void initView() {
        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        mLineChart = (LineChart) findViewById(R.id.lineChart);
        iv_cal = (ImageView) findViewById(R.id.iv);
        action_bar = (RelativeLayout) findViewById(R.id.action_bar);
        isCalendarShow = true;
        // 日历控件今天标记
        DayViewDecorator todayDecorator = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return CalendarDay.today().getYear() == day.getYear() && CalendarDay.today().getDay() == day.getDay();
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_green_33));
            }
        };
        // 日历控件事件标记
        DayViewDecorator eventDecorator = new DayViewDecorator() {

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                // 读取当月对应的一个“年-月”的sp文件，核对当天事件标记
                String fileName = day.getYear() + "-" + day.getMonth();
                SharedPreferences sharedPreferences = getSharedPreferences(fileName, Context.MODE_PRIVATE);
                return sharedPreferences.contains("day" + day.getDay());
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new DotSpan(5, getResources().getColor(R.color.colorAccent)));
            }
        };
        // 日历控件未来时间标记
        DayViewDecorator afterDecorator = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.isAfter(CalendarDay.today());
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setDaysDisabled(true);
            }
        };
        mCalendarView.addDecorator(todayDecorator);
        mCalendarView.addDecorator(eventDecorator);
        mCalendarView.addDecorator(afterDecorator);
        mLineChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.SCREEN_HEIGHT / 3));
        ChartUtils.initChartView(mLineChart);
    }

    @Override
    protected void setListener() {
        iv_cal.setOnClickListener(this);
        setCalendarListener();
        setChartListener();
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
    }

    @Override
    protected void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.iv:
                displayCalendar();
                break;
        }
    }

    /**
     * 展示或者隐藏日历控件
     */
    private void displayCalendar() {
        iv_cal.setImageResource(isCalendarShow ? R.drawable.day : R.drawable.day_hs);
        isCalendarShow = !isCalendarShow;
        // 根据日历控件的位置和它上部的控件的位置，计算日历目前可见区域的高度
        int[] lo = new int[2];
        mCalendarView.getLocationInWindow(lo);
        int yCal = lo[1];// 日历的位置
        action_bar.getLocationInWindow(lo);// 获取上部控件的位置
        int visibleHeight = yCal + mCalendarView.getHeight() - lo[1] - action_bar.getHeight();
        // 如果大于0表示可见，则向上动画隐藏可见区域的距离，否则还原
        if (visibleHeight > 0) {
            ObjectAnimator.ofFloat(mCalendarView.getParent(), "translationY", -(visibleHeight)).setDuration(300).start();
        } else {
            ObjectAnimator.ofFloat(mCalendarView.getParent(), "translationY", 0).setDuration(300).start();
        }

    }

    /**
     * 重置图表
     */
    private void resetChart() {
        mLineChart.clear();
        mLineChart.getXAxis().setAxisMaximum(ChartUtils.PAGE_SIZE);
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
        // 更新日期
        mSelectedDate = temp;
        // 重置图表
        resetChart();
        // 读取选择日期对应的数据
        readChartData();
    }

    @Override
    protected void onDestroy() {
        mCalendarView = null;
        if (null != mReadTask) {
            mReadTask.cancel(true);
        }
        if (null != mLineChart) {
            mLineChart.setOnChartValueSelectedListener(null);
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

}
