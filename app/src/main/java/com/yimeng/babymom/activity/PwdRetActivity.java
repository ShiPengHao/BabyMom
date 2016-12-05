package com.yimeng.babymom.activity;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.MyToast;

import org.json.JSONObject;

import java.util.HashMap;


/**
 * 忘记密码，提交信息界面
 */

public class PwdRetActivity extends BaseActivity implements View.OnClickListener {

    private ImageView iv_back;
    private EditText et_username;
    private Button bt_submit;
    private EditText et_phone;
    private EditText et_remark;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_pwd_ret;
    }

    @Override
    protected void initView() {
        iv_back = (ImageView) findViewById(R.id.iv_back);
        et_username = (EditText) findViewById(R.id.et_username);
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_remark = (EditText) findViewById(R.id.et_remark);
        bt_submit = (Button) findViewById(R.id.bt_submit);
    }

    @Override
    protected void setListener() {
        iv_back.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        Intent intent = getIntent();
        if (intent == null)
            return;

        String phone = intent.getStringExtra("phone");
        if (phone == null)
            phone = getSharedPreferences(MyConstant.PREFS_ACCOUNT, MODE_PRIVATE).getString(MyConstant.KEY_ACCOUNT_LAST_USERNAME, "");
        et_username.setText(phone);
        if (Integer.getInteger(phone) != null)
            et_phone.setText(phone);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_submit:
                submit();
                break;
        }
    }

    private void submit() {
        String username = et_username.getText().toString().trim();
        if (TextUtils.isEmpty(username)) {
            MyToast.show("用户名不能为空");
            ObjectAnimator.ofFloat(et_username, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        String phoneNumber = et_phone.getText().toString().trim();
        if (!TextUtils.isEmpty(phoneNumber) && !phoneNumber.matches("[1][358]\\d{9}")) {
            MyToast.show("手机号格式不正确");
            ObjectAnimator.ofFloat(et_phone, "translationX", 15f, -15f, 20f, -20f, 0).setDuration(300).start();
            return;
        }

        HashMap<String, Object> values = new HashMap<>();
        values.clear();
        values.put("apply_account", username);
        values.put("apply_phone", phoneNumber);
        values.put("apply_remark", et_remark.getText().toString().trim());
        bt_submit.setEnabled(false);
        new SoapAsyncTask() {
            @Override
            protected void onPostExecute(String s) {
                bt_submit.setEnabled(true);
                if (s == null) {
                    MyToast.show(getString(R.string.connect_error));
                    return;
                }
                try {
                    JSONObject object = new JSONObject(s);
                    MyToast.show(object.optString("msg"));
                    if ("ok".equalsIgnoreCase(object.optString("status")))
                        finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    MyToast.show(getString(R.string.connect_error));
                }
            }
        }.execute("ApplyPwdRet", values);
    }
}
