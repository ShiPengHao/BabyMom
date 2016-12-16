package com.yimeng.babymom.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.KeyBoardUtils;
import com.yimeng.babymom.view.ClearEditText;

import java.text.DecimalFormat;

/**
 * 胎儿体重计算
 */

public class BabyWeightFragment extends BaseFragment {
    private EditText et_head_width;
    private EditText et_body_width;
    private EditText et_ass_width;
    private TextView tv;
    private Button bt_submit;
    private ClearEditText.SimpleTextChangedListener mTextWatcher1;
    private ClearEditText.SimpleTextChangedListener mTextWatcher2;
    private String mHeadWidth;
    private String mBodyWidth;
    private String mAssWidth;
    private ClearEditText.SimpleTextChangedListener mTextWatcher3;

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_baby_weight;
    }

    @Override
    protected void initView(View view) {
        et_head_width = (EditText) view.findViewById(R.id.et_head_width);
        et_body_width = (EditText) view.findViewById(R.id.et_body_width);
        et_ass_width = (EditText) view.findViewById(R.id.et_ass_width);
        bt_submit = (Button) view.findViewById(R.id.bt_submit);
        tv = (TextView) view.findViewById(R.id.tv);
    }

    @Override
    protected void setListener() {
        bt_submit.setOnClickListener(this);
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
        mTextWatcher3 = new ClearEditText.SimpleTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equalsIgnoreCase(mAssWidth))
                    tv.setText("");
            }
        };
        et_head_width.addTextChangedListener(mTextWatcher1);
        et_body_width.addTextChangedListener(mTextWatcher2);
        et_ass_width.addTextChangedListener(mTextWatcher3);
    }

    @Override
    protected void initData() {

    }

    @Override
    public void onClick(View v) {
        KeyBoardUtils.closeKeybord(et_body_width, activity);
        switch (v.getId()) {
            case R.id.bt_submit:
                checkInput();
                break;
        }
    }

    private void checkInput() {
        mHeadWidth = et_head_width.getText().toString().trim();
        mBodyWidth = et_body_width.getText().toString().trim();
        mAssWidth = et_ass_width.getText().toString().trim();

        if (TextUtils.isEmpty(mHeadWidth) || TextUtils.isEmpty(mBodyWidth) || TextUtils.isEmpty(mAssWidth)) {
            showToast("您的输入有误，请输入正确再试");
            return;
        }

        double head = Double.parseDouble(mHeadWidth);
        double body = Double.parseDouble(mBodyWidth);
        double ass = Double.parseDouble(mAssWidth);

        double weight = 1.07 * Math.pow(head, 3) + 0.3 * Math.pow(body, 2) * ass;

        tv.setText("宝宝的体重大概为" + new DecimalFormat("#.00").format(weight / 1000) + "克");
    }

    @Override
    public void onDestroy() {
        if (et_head_width != null && mTextWatcher1 != null)
            et_head_width.removeTextChangedListener(mTextWatcher1);
        if (et_body_width != null && mTextWatcher2 != null)
            et_body_width.removeTextChangedListener(mTextWatcher2);
        if (et_ass_width != null && mTextWatcher3 != null)
            et_ass_width.removeTextChangedListener(mTextWatcher3);
        super.onDestroy();
    }
}
