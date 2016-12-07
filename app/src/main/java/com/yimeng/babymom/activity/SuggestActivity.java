package com.yimeng.babymom.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.yimeng.babymom.task.SoapAsyncTask;
import com.yimeng.babymom.utils.MyToast;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * 意见与反馈界面
 */

public class SuggestActivity extends BaseActivity implements AutoLinkOnClickListener {

    private ImageView iv_back;
    private EditText et_suggest;
    private TextView tv_count;
    private Button bt_submit;
    private AutoLinkTextView autoLinkTextView;
    private EditText et_phone;

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
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_submit:
                submitSuggest();
                break;
        }
    }

    /**
     * 提交建议
     */
    private void submitSuggest() {
        String suggest = et_suggest.getText().toString();
        if (TextUtils.isEmpty(suggest)) {
            MyToast.show(activity, String.format("%s%s", getString(R.string.suggest), getString(R.string.can_not_be_null)));
            return;
        }
        String phoneNumber = et_phone.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNumber) && !phoneNumber.matches("[1][358]\\d{9}")) {
            MyToast.show(activity, "手机号格式不正确");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }
        HashMap<String, Object> params = new HashMap<>();
        params.put("msg", suggest);
        params.put("phone", phoneNumber);
//        new SoapAsyncTask() {
//            @Override
//            protected void onPostExecute(String s) {
//                if (s == null) {
//                    MyToast.show(activity, getString(R.string.connect_error));
//                    return;
//                }
//                try {
//                    JSONObject object = new JSONObject(s);
//                    MyToast.show(activity, object.optString("msg"));
//                    if ("ok".equalsIgnoreCase(object.optString("status")))
//                        finish();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    MyToast.show(activity, getString(R.string.connect_error));
//                }
//            }
//        }.execute("AddGuestbook", params);
    }

    @Override
    public void onAutoLinkTextClick(AutoLinkMode autoLinkMode, String matchedText) {
        if (autoLinkMode == AutoLinkMode.MODE_PHONE && ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            startActivity(new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + matchedText)));
        }
    }
}
