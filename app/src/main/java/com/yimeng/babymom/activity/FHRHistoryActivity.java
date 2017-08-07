package com.yimeng.babymom.activity;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;
import com.nineoldandroids.animation.ValueAnimator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.ChartUtils;
import com.yimeng.babymom.utils.DayViewDecoratorFactory;
import com.yimeng.babymom.utils.DensityUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static com.yimeng.babymom.R.id.lineChart;

/**
 * 胎心监测历史页面，使用{@link MaterialCalendarView}展示日历，充当查看历史纪录的入口<br/>
 * 通过{@link AsyncTask}从本地SharedPreference读取胎心率数据<br/>
 * 并将结果展示到{@link LineChart}图表上。默认在当前记录第一次加载时使用{@link ObjectAnimator}让数据线沿X轴正向描点，动画时长为记录时长的1/20
 */
public class FHRHistoryActivity extends BaseActivity implements OnDateSelectedListener, OnChartValueSelectedListener {

    private MaterialCalendarView mCalendarView;
    private LineChart mLineChart;
    private ImageView iv_cal;
    private RelativeLayout action_bar;
    private TextView tv;
    private TextView tv_tip;
    /**
     * 日历控件是否显示
     */
    private boolean isCalendarShow;
    /**
     * 当前选中的日期
     */
    private Date mSelectedDate;

    /**
     * 读取监测数据的异步任务
     */
    private AsyncTask<Void, Void, ArrayList<Entry>> mReadTask;
    /**
     * 缓存所选日期对应的历史记录数据的集合
     */
    private HashMap<String, ArrayList<Entry>> mHistoryMap = new HashMap<>();
    /**
     * 胎心图播放动画
     */
    private ObjectAnimator mChartAnimatorX;
    /**
     * 胎心图动画刷新监听
     */
    private ValueAnimator.AnimatorUpdateListener mChartAnimatorUpdateListener;
    /**
     * 胎心图动画事件监听
     */
    private Animator.AnimatorListener mChartAnimatorListener;


    @Override
    protected int setLayoutResId() {
        return R.layout.activity_fhr_history;
    }

    @Override
    protected void initView() {
        tv = (TextView) findViewById(R.id.tv);
        tv_tip = (TextView) findViewById(R.id.tv_tip);
        mCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        mLineChart = (LineChart) findViewById(lineChart);
        iv_cal = (ImageView) findViewById(R.id.iv);
        action_bar = (RelativeLayout) findViewById(R.id.action_bar);
        initCalendarView();
        initChartView();
    }

    /**
     * 初始化图表控件
     */
    private void initChartView() {
        mLineChart.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, DensityUtil.SCREEN_HEIGHT / 3));
        ChartUtils.initChartView(mLineChart);
        mLineChart.setNoDataText(getString(R.string.tip_record_no_today));
    }

    /**
     * 初始化日历控件
     */
    private void initCalendarView() {
        isCalendarShow = true;
        // 日历控件今天标记
        DayViewDecorator todayDecorator = DayViewDecoratorFactory.today(this, R.drawable.circle_green_333);
        mCalendarView.addDecorator(todayDecorator);
        // 日历控件未来时间标记
        DayViewDecorator afterDecorator = DayViewDecoratorFactory.after();
        mCalendarView.addDecorator(afterDecorator);
        // 日历控件事件标记
        DayViewDecorator eventDecorator = DayViewDecoratorFactory.eventFromSharedPreference(this, R.color.colorAccent);
        mCalendarView.addDecorator(eventDecorator);
        // 每月动态设置行数为4还是5
        mCalendarView.setDynamicHeightEnabled(true);
        mCalendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return day.getYear() + "年" + (day.getMonth() + 1) + "月";
            }
        });
    }

    @Override
    protected void setListener() {
        iv_cal.setOnClickListener(this);
        tv_tip.setOnClickListener(this);
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
        mCalendarView.setOnDateChangedListener(this);
        mCalendarView.setDateSelected(CalendarDay.today(), true);
    }

    @Override
    protected void initData() {
        onDateSelected(mCalendarView, CalendarDay.today(), true);
    }

    @Override
    protected void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.iv:
                displayCalendar();
                break;
            case R.id.tv_tip:
                if (null != mChartAnimatorX) {
                    mChartAnimatorX.cancel();
                }
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
     * 日历控件上的日期被选中时显示对应日期的胎心监测记录：<br/>
     * <li>如果点击的时期就是当前选择的日期，则不做处理。此日历控件{@link MaterialCalendarView}默认未做重复选中处理</li>
     * <li>否则切换日期，读取对应记录并展示到图表控件</li>
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
        // 读取选择日期对应的数据
        resetChart();
    }

    /**
     * 重置图表，可能要读取本地文件，使用异步操作
     */
    private void resetChart() {
        if (null != mReadTask && mReadTask.getStatus() == AsyncTask.Status.RUNNING) {
            mReadTask.cancel(true);
        }
        mReadTask = new AsyncTask<Void, Void, ArrayList<Entry>>() {

            private boolean isFirstRun;

            @Override
            protected void onPreExecute() {
                showLoadingView(R.string.tip_record_reading);
                if (null != mChartAnimatorX) {
                    mChartAnimatorX.cancel();
                }
            }

            @Override
            protected ArrayList<Entry> doInBackground(Void... params) {
                // 根据日期，计算记录文件名称，先在内存HashMap中找，没有的话再从本地SharedPreferences文件中读取并加入内存
                String fileName = ChartUtils.getFileName(mSelectedDate);
                ArrayList<Entry> result = mHistoryMap.get(fileName);
                if (null == result) {
                    isFirstRun = true;
                    result = ChartUtils.getASCEntryList(ChartUtils.getPrefs(fileName));
                    mHistoryMap.put(fileName, result);
                }
                return result;
            }

            @Override
            protected void onPostExecute(ArrayList<Entry> entries) {
                // 根据获取的数据集合进行展示
                // 如果有数据
                if (entries.size() != 0) {
                    // 重置坐标范围
                    mLineChart.getXAxis().resetAxisMaximum();
                    // 设置新数据
                    ChartUtils.initLineData(mLineChart, entries);
                    // 缩放到最大
                    while (!mLineChart.isFullyZoomedOut()) {
                        mLineChart.zoom(0.7f, 0.7f, 0f, 0f);
                    }
                    // 统计胎心率、记录时长等信息并展示
                    int count = entries.size();
                    int sum = 0;
                    for (int i = 0; i < count; i++) {
                        sum += entries.get(i).getY();
                    }
                    int minute = (int) Math.round(count / 60d);
                    tv.setText(String.format("胎心率:%sbpm    时长:%s分钟", sum / count, minute));
                    // 根据时长设置描点动画
                    if (isFirstRun) {
                        resetChartAnimator(count);
                    } else {
                        mLineChart.invalidate();
                    }
                } else {
                    mLineChart.clear();
                    tv.setText(R.string.fhr);
                    tv_tip.setVisibility(View.GONE);
                }
                dismissLoadingView();
            }
        }.execute();
    }

    /**
     * 重置动画播放
     *
     * @param second 胎心检测实际时长，秒
     */
    private void resetChartAnimator(int second) {
        mChartAnimatorX = ObjectAnimator.ofFloat(mLineChart.getAnimator(), "phaseX", 0f, 1f);
        // 秒数/20，使60s的数据播放时间设置为3s，20分钟60s，符合一般感觉
        mChartAnimatorX.setDuration(second * 1000 / 20);
        if (null == mChartAnimatorUpdateListener) {
            mChartAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mLineChart.invalidate();
                }
            };
        }
        if (null == mChartAnimatorListener) {
            mChartAnimatorListener = new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    tv_tip.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    tv_tip.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    animation.end();
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        }
        mChartAnimatorX.addUpdateListener(mChartAnimatorUpdateListener);
        mChartAnimatorX.addListener(mChartAnimatorListener);
        mChartAnimatorX.start();
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
        String time = mLineChart.getXAxis().getValueFormatter().getFormattedValue(e.getX(), mLineChart.getXAxis());
        showToast(String.format("时间：%s，胎心率：%s", time, (int) e.getY()));
    }

    @Override
    public void onNothingSelected() {

    }

}
