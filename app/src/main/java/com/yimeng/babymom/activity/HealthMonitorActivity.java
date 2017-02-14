package com.yimeng.babymom.activity;

import android.support.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.format.TitleFormatter;
import com.yimeng.babymom.R;

import java.util.Date;

/**
 * 健康监测页面
 */

public class HealthMonitorActivity extends BaseActivity implements OnDateSelectedListener {

    private MaterialCalendarView calendarView;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_health_monitor;
    }

    @Override
    protected void initView() {
        calendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
    }

    @Override
    protected void setListener() {
        setCalendarView();
    }

    /**
     * 设置日历
     */
    private void setCalendarView() {
        calendarView.setDateSelected(new Date(), true);
        calendarView.setDynamicHeightEnabled(true);
        calendarView.setTitleFormatter(new TitleFormatter() {
            @Override
            public CharSequence format(CalendarDay day) {
                return day.getYear() + "年" + (day.getMonth() + 1) + "月";
            }
        });
        calendarView.setOnDateChangedListener(this);
    }

    @Override
    protected void initData() {

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
        calendarView = null;
        super.onDestroy();
    }
}
