package com.yimeng.babymom.fragment;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.KeyBoardUtils;
import com.yimeng.babymom.view.ClearEditText;

/**
 * 身高预测
 */

public class BabyHeightFragment extends BaseFragment {
    private EditText et_head_width;
    private EditText et_body_width;
    private TextView tv;
    private Button bt_submit;
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
        et_head_width.addTextChangedListener(mTextWatcher1);
        et_body_width.addTextChangedListener(mTextWatcher2);
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

        if (TextUtils.isEmpty(mHeadWidth) || TextUtils.isEmpty(mBodyWidth)) {
            showToast("您的输入有误，请输入正确再试");
            return;
        }

        int pa = Integer.parseInt(mHeadWidth);
        int ma = Integer.parseInt(mBodyWidth);
        tv.setText("男宝宝的身高大概为" + (pa + ma + 14) / 2 + "cm\n" + "女宝宝的身高大概为" + (pa + ma - 12) / 2 + "cm\n");
    }

    @Override
    public void onDestroy() {
        if (et_head_width != null && mTextWatcher1 != null)
            et_head_width.removeTextChangedListener(mTextWatcher1);
        if (et_body_width != null && mTextWatcher2 != null)
            et_body_width.removeTextChangedListener(mTextWatcher2);
        super.onDestroy();
    }
}
