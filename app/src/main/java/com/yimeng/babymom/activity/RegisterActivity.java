package com.yimeng.babymom.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.interFace.RegisterInterface;
import com.yimeng.babymom.task.RegisterTask;

import java.util.HashMap;


/**
 * 注册页面
 */

public class RegisterActivity extends BaseActivity implements RegisterInterface {
    private EditText et_phone;
    private EditText et_pwd;
    private EditText et_pwd_confirm;
    private CheckBox cb;
    private TextView tv_protocol;
    private Button bt_submit;

    public static final int REQUEST_CODE_SMS_REGISTER = 100;

    private String mPhone;
    private String mPassword;
    private AsyncTask mRegisterTask;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        et_pwd_confirm = (EditText) findViewById(R.id.et_pwd_confirm);

        cb = (CheckBox) findViewById(R.id.cb);

        tv_protocol = (TextView) findViewById(R.id.tv_protocol);

        bt_submit = (Button) findViewById(R.id.bt_submit);
    }

    @Override
    protected void setListener() {
        tv_protocol.setOnClickListener(this);
        bt_submit.setOnClickListener(this);
    }


    @Override
    protected void initData() {
        try {
            mPhone = getIntent().getStringExtra("phone");
            if (mPhone != null) {
                et_phone.setText(mPhone);
                et_phone.setSelection(mPhone.length());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.tv_protocol:
                goToProtocol();
                break;
            case R.id.bt_submit:
                checkInput();
                break;
        }
    }

    public void goToProtocol() {
        startActivity(new Intent(this, UserProtocolActivity.class));
    }

    public void checkInput() {
        mPhone = et_phone.getText().toString().trim();
        if (isEmpty(mPhone)) {
            showToast(String.format("%s%s", getString(R.string.phone), getString(R.string.can_not_be_null)));
            return;
        }
        if (!mPhone.matches("[1][358]\\d{9}")) {
            showToast(getString(R.string.phone_bad));
            return;
        }

        mPassword = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(mPassword)) {
            showToast(String.format("%s%s", getString(R.string.password), getString(R.string.can_not_be_null)));
            return;
        }
        if (mPassword.length() < 6 || mPassword.length() > 11) {
            showToast(getString(R.string.password_bad));
            return;
        }

        String confirm = et_pwd_confirm.getText().toString().trim();
        if (!mPassword.equals(confirm)) {
            showToast(getString(R.string.password_different));
            return;
        }

        if (!cb.isChecked()) {
            showToast(getString(R.string.protocol_need_agree));
            return;
        }
        goToCheckSms();
    }


    public void goToCheckSms() {
        Intent intent = new Intent(mActivity, SMSVerifyActivity.class).putExtra("phone", mPhone);
        startActivityForResult(intent, REQUEST_CODE_SMS_REGISTER);
    }

    public void feedbackPre() {
        setResult(RESULT_OK, new Intent()
                .putExtra("username", mPhone)
                .putExtra("pwd", mPassword)
        );
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case REQUEST_CODE_SMS_REGISTER:
                register();
                break;
        }
    }

    public void register() {
        HashMap<String, Object> params = new HashMap<>();
        params.put(RegisterTask.PHONE, mPhone);
        params.put(RegisterTask.PASSWORD, mPassword);
        mRegisterTask = new RegisterTask(this, bt_submit).execute(RegisterTask.METHOD, params);
    }

    @Override
    protected void onDestroy() {
        if (mRegisterTask != null)
            mRegisterTask.cancel(true);
        super.onDestroy();
    }

}
