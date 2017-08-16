package com.yimeng.babymom.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.MeasureActivity;
import com.yimeng.babymom.view.ClearEditText;

/**
 * 身高预测
 */

public class BabyHeightFragment extends BaseFragment implements MeasureActivity.MeasureSubmitListener {
    private EditText et_head_width;
    private EditText et_body_width;
    private TextView tv;
    private String mHeadWidth;
    private String mBodyWidth;
    private ClearEditText.SimpleTextChangedListener mTextWatcher1;
    private ClearEditText.SimpleTextChangedListener mTextWatcher2;

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_baby_height;
    }

    @Override
    protected void initView(View view) {
        et_head_width = (EditText) view.findViewById(R.id.et_head_width);
        et_body_width = (EditText) view.findViewById(R.id.et_body_width);
        tv = (TextView) view.findViewById(R.id.tv);
    }

    @Override
    protected void setListener() {
        mTextWatcher1 = new ClearEditText.SimpleTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equalsIgnoreCase(mHeadWidth))
                    tv.setText("");
            }
        };
        mTextWatcher2 = new ClearEditText.SimpleTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equalsIgnoreCase(mBodyWidth))
                    tv.setText("");
            }
        };
        et_head_width.addTextChangedListener(mTextWatcher1);
        et_body_width.addTextChangedListener(mTextWatcher2);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
    }

    private void checkInput() {
        mHeadWidth = et_head_width.getText().toString().trim();
        mBodyWidth = et_body_width.getText().toString().trim();

        if (TextUtils.isEmpty(mHeadWidth) || TextUtils.isEmpty(mBodyWidth)) {
            showToast("您的输入有误，请输入正确再试");
            return;
        }

        int pa = Integer.parseInt(mHeadWidth);
        int ma = Integer.parseInt(mBodyWidth);
        String result;
        int color;
        if (pa < 250 && pa > 120 && ma < 250 && ma > 120) {
            result = "男宝宝的身高大概为" + (pa + ma + 14) / 2 + "cm\n" + "女宝宝的身高大概为" + (pa + ma - 12) / 2 + "cm\n";
            color = getResources().getColor(R.color.colorAccent);
        } else {
            result = "呵呵~";
            color = getResources().getColor(R.color.bg_grey_aaa);
        }
        tv.setText(result);
        tv.setTextColor(color);
    }

    @Override
    public void onDestroy() {
        if (et_head_width != null && mTextWatcher1 != null)
            et_head_width.removeTextChangedListener(mTextWatcher1);
        if (et_body_width != null && mTextWatcher2 != null)
            et_body_width.removeTextChangedListener(mTextWatcher2);
        super.onDestroy();
    }

    @Override
    public void onSubmit() {
        checkInput();
    }
}
