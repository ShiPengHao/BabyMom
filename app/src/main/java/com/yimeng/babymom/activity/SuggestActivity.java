package com.yimeng.babymom.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.luseen.autolinklibrary.AutoLinkMode;
import com.luseen.autolinklibrary.AutoLinkOnClickListener;
import com.luseen.autolinklibrary.AutoLinkTextView;
import com.yimeng.babymom.R;
import com.yimeng.babymom.interFace.SuggestInterface;
import com.yimeng.babymom.task.SuggestTask;
import com.yimeng.babymom.utils.MyToast;

import java.util.HashMap;


/**
 * 意见与反馈界面
 */

public class SuggestActivity extends BaseActivity implements AutoLinkOnClickListener, SuggestInterface {

    private ImageView iv_back;
    private EditText et_suggest;
    private TextView tv_count;
    private Button bt_submit;
    private AutoLinkTextView autoLinkTextView;
    private EditText et_phone;
    private String mSuggest;
    private String mPhone;
    private AsyncTask<Object, Object, String> mSuggestTask;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_suggest;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_suggest = (EditText) findViewById(R.id.et_suggest);
        et_phone = (EditText) findViewById(R.id.et_phone);
        tv_count = (TextView) findViewById(R.id.tv_count);
        tv_count.setText(String.format("%s%s%s", getString(R.string.can_input), 100, getString(R.string.charactor)));
        bt_submit = (Button) findViewById(R.id.bt_submit);
        autoLinkTextView = (AutoLinkTextView) findViewById(R.id.autoLinkTextView);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
        autoLinkTextView.setAutoLinkOnClickListener(this);
        et_suggest.addTextChangedListener(new com.yimeng.babymom.view.ClearEditText.SimpleTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String toString = et_suggest.getText().toString();
                tv_count.setText(String.format("%s%s%s", getString(R.string.can_input), 100 - toString.length(), getString(R.string.charactor)));
            }
        });
    }

    @Override
    protected void initData() {
        try {
            String phone = getIntent().getStringExtra("phone");
            if (phone == null)
                phone = mPrefManager.getAccountUsername();
            et_phone.setText(phone);
        } catch (Exception e) {
            e.printStackTrace();
        }
        autoLinkTextView.addAutoLinkMode(
                AutoLinkMode.MODE_PHONE
                , AutoLinkMode.MODE_EMAIL
        );
//        autoLinkTextView.setCustomRegex("[1-9][0-9]{4,14}");//QQ号码
        autoLinkTextView.setPhoneModeColor(ContextCompat.getColor(this, R.color.colorAccent));
        autoLinkTextView.setEmailModeColor(ContextCompat.getColor(this, R.color.bt_yellow));
        autoLinkTextView.setAutoLinkText(getString(R.string.contact_way));
    }

    @Override
    public void onInnerClick(int id) {
        switch (id) {
            case R.id.bt_submit:
                checkInput();
                break;
        }
    }

    @Override
    public void checkInput() {
        mSuggest = et_suggest.getText().toString();
        if (TextUtils.isEmpty(mSuggest)) {
            MyToast.show(activity, String.format("%s%s", getString(R.string.suggest), getString(R.string.can_not_be_null)));
            return;
        }
        mPhone = et_phone.getText().toString().trim();
        if (!TextUtils.isEmpty(mPhone) && !mPhone.matches("[1][358]\\d{9}")) {
            MyToast.show(activity, "手机号格式不正确");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        suggest();
    }

    public void suggest() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("msg", mSuggest);
        params.put("phone", mPhone);
        mSuggestTask = new SuggestTask(this, bt_submit).execute("AddGuestbook", params);
    }

    @Override
    public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
        if (autoLinkMode == AutoLinkMode.MODE_PHONE && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + matchedText)));
        }
    }

    @Override
    protected void onDestroy() {
        if (mSuggestTask != null)
            mSuggestTask.cancel(true);
        super.onDestroy();
    }
}
