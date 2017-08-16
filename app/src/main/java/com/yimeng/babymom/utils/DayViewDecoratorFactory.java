package com.yimeng.babymom.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

/**
 * {@link DayViewDecorator}的工厂类
 */
public class DayViewDecoratorFactory {

    /**
     * 获取今日标记
     *
     * @param context    context
     * @param drawableId 背景资源id
     * @return DayViewDecorator
     */
    public static DayViewDecorator today(@NonNull final Context context, final int drawableId) {
        return new DayViewDecorator() {
            private CalendarDay mToday = CalendarDay.today();

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.getYear() == mToday.getYear() && day.getMonth() == mToday.getMonth() && day.getDay() == mToday.getDay();
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setBackgroundDrawable(context.getResources().getDrawable(drawableId));
            }
        };
    }

    /**
     * 禁用今日以后的日期
     *
     * @return DayViewDecorator
     */
    public static DayViewDecorator after() {
        return new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return day.isAfter(CalendarDay.today());
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.setDaysDisabled(true);
            }
        };
    }

    /**
     * 事件标记，简单实用一个小圆点来作为标记。事件根据场景自定义。
     *
     * @param context context
     * @param colorId 小原点颜色资源id
     * @return DayViewDecorator
     */
    public static DayViewDecorator eventFromSharedPreference(@NonNull final Context context, final int colorId) {
        return new DayViewDecorator() {

            @Override
            public boolean shouldDecorate(CalendarDay day) {
                // 读取当月对应的一个“年-月”的sp文件，核对当天事件标记
                String fileName = day.getYear() + "-" + day.getMonth();
                SharedPreferences sharedPreferences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
                return sharedPreferences.contains("day" + day.getDay());
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new DotSpan(5, context.getResources().getColor(colorId)));
            }
        };
    }

}
