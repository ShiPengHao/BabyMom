package com.yimeng.babymom.fragment;

import android.app.DatePickerDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.KeyBoardUtils;
import com.yimeng.babymom.utils.Lunar;
import com.yimeng.babymom.view.ClearEditText;

import java.util.Calendar;
import java.util.Locale;

/**
 * 预产期计算
 */

public class BirthDayFragment extends BaseFragment {
    private EditText et_woman_month_short;
    private EditText et_woman_month_long;
    private TextView tv_woman_month_last;
    private TextView tv;
    private Button bt_submit;
    private DatePickerDialog mDatePickerDialog;
    private long mLongChooseDayMills;
    private static final String[] DAYS_OF_WEEK = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private String mMonthShort;
    private String mMonthLong;
    private ClearEditText.SimpleTextChangedListener mTextWatcher1;
    private ClearEditText.SimpleTextChangedListener mTextWatcher2;

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_birth_day;
    }

    @Override
    protected void initView(View view) {
        et_woman_month_short = (EditText) view.findViewById(R.id.et_woman_month_short);
        et_woman_month_short.requestFocus();
        et_woman_month_long = (EditText) view.findViewById(R.id.et_woman_month_long);
        tv_woman_month_last = (TextView) view.findViewById(R.id.tv_woman_month_last);
        tv = (TextView) view.findViewById(R.id.tv);
        bt_submit = (Button) view.findViewById(R.id.bt_submit);
    }

    @Override
    protected void setListener() {
        tv_woman_month_last.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        mTextWatcher1 = new ClearEditText.SimpleTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equalsIgnoreCase(mMonthShort))
                    tv.setText("");
            }
        };
        mTextWatcher2 = new ClearEditText.SimpleTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equalsIgnoreCase(mMonthLong))
                    tv.setText("");
            }
        };
        et_woman_month_short.addTextChangedListener(mTextWatcher1);
        et_woman_month_long.addTextChangedListener(mTextWatcher2);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        KeyBoardUtils.closeKeybord(et_woman_month_long, activity);
        switch (v.getId()) {
            case R.id.tv_woman_month_last:
                showDatePickDialog();
                break;
            case R.id.bt_submit:
                checkInput();
                break;
        }
    }

    private void checkInput() {
        mMonthShort = et_woman_month_short.getText().toString().trim();
        mMonthLong = et_woman_month_long.getText().toString().trim();
        if (TextUtils.isEmpty(mMonthShort) || TextUtils.isEmpty(mMonthLong) || mLongChooseDayMills == 0) {
            showToast("您的输入有误，请输入正确再试");
            return;
        }
        int small = Integer.parseInt(mMonthShort);
        int big = Integer.parseInt(mMonthLong);

        if (small < 15 || small > 45 || big < 15 || big > 45 || small > big) {
            showToast("您的月经周期太神奇，小的晕了");
            return;
        }
        calculateBirthDay((big + small) / 2 - 28);
    }

    /**
     * 计算预产期
     *
     * @param offset 修正天数
     */
    private void calculateBirthDay(int offset) {
        long birthDayMillis = mLongChooseDayMills + (280 + offset) * 24 * 60 * 60 * 1000L;
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.setTimeInMillis(birthDayMillis);
        String dayOfWeek = DAYS_OF_WEEK[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        String sb = "您的预产期：\n" + calendar.get(Calendar.YEAR) + "年" +
                (calendar.get(Calendar.MONTH) + 1) + "月" +
                calendar.get(Calendar.DAY_OF_MONTH) + "日，" + dayOfWeek + "\n" + new Lunar(calendar).toString();
        tv.setText(sb);
    }

    /**
     * 选择预约日期
     */
    private void showDatePickDialog() {
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        int year = calendar.get(Calendar.YEAR);
        int monthOfYear = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        mDatePickerDialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            private boolean isFirst = true;

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                if (isFirst) {
                    Calendar tempCalendar = Calendar.getInstance();
                    tempCalendar.clear();
                    tempCalendar.set(year, monthOfYear, dayOfMonth);
                    long timeInMillis = tempCalendar.getTimeInMillis();
                    if (mLongChooseDayMills != timeInMillis) {
                        mLongChooseDayMills = timeInMillis;
                        tv.setText("");
                        String date = String.valueOf(year) + "年" + (monthOfYear + 1) + "月" + dayOfMonth + "日";
                        tv_woman_month_last.setText(String.format("%s：%s", getString(R.string.woman_month_last), date));
                        isFirst = false;
                    }
                }
            }
        }, year, monthOfYear, dayOfMonth);

        mDatePickerDialog.getDatePicker().init(year, monthOfYear, dayOfMonth, null);
        mDatePickerDialog.show();
    }

    @Override
    public void onDestroy() {
        if (mDatePickerDialog != null && mDatePickerDialog.isShowing())
            mDatePickerDialog.dismiss();
        if (et_woman_month_short != null && mTextWatcher1 != null)
            et_woman_month_short.removeTextChangedListener(mTextWatcher1);
        if (et_woman_month_long != null && mTextWatcher2 != null)
            et_woman_month_long.removeTextChangedListener(mTextWatcher2);
        super.onDestroy();
    }
}
