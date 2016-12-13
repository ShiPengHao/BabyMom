package com.yimeng.babymom.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.yimeng.babymom.R;
import com.yimeng.babymom.interFace.PwdResetUIInterface;
import com.yimeng.babymom.task.PwdResetTask;
import com.yimeng.babymom.utils.KeyBoardUtils;

import java.util.HashMap;


/**
 * 密码重置界面
 */

public class PwdResetActivity extends BaseActivity implements PwdResetUIInterface, CompoundButton.OnCheckedChangeListener {
    private Button bt_submit;
    private EditText et_new_pwd;
    private EditText et_phone;
    private CheckBox cb_eye;

    private static final int REQUEST_CODE_SMS_RESET = 101;
    private String mPhone;
    private String mPassword;
    private AsyncTask<Object, Object, String> mPwdRestTask;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_pwd_reset;
    }

    @Override
    protected void initView() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_new_pwd = (EditText) findViewById(R.id.et_new_pwd);
        bt_submit = (Button) findViewById(R.id.bt_submit);
        cb_eye = (CheckBox) findViewById(R.id.cb_eye);
    }

    @Override
    protected void setListener() {
        bt_submit.setOnClickListener(this);
        cb_eye.setOnCheckedChangeListener(this);
    }

    @Override
    protected void initData() {
        try {
            mPhone = getIntent().getStringExtra("phone");
            if (mPhone == null)
                mPhone = mPrefManager.getAccountUsername();
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
        KeyBoardUtils.closeKeybord(et_phone, this);
        switch (viewId) {
            case R.id.bt_submit:
                checkInput();
                break;
        }
    }

    @Override
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

        mPassword = et_new_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(mPassword)) {
            showToast(String.format("%s%s", getString(R.string.password), getString(R.string.can_not_be_null)));
            return;
        }
        if (mPassword.length() < 6 || mPassword.length() > 11) {
            showToast(getString(R.string.password_bad));
            return;
        }
        goToCheckSms();
    }

    @Override
    public void resetPwd() {
        HashMap<String, Object> params = new HashMap<>();
        params.put("phone", mPhone);
        params.put("password", mPassword);
        mPwdRestTask = new PwdResetTask(this, bt_submit).execute("pwdReset", params);
    }

    @Override
    public void goToCheckSms() {
        Intent intent = new Intent(activity, SMSVerifyActivity.class).putExtra("phone", mPhone);
        startActivityForResult(intent, REQUEST_CODE_SMS_RESET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null)
            return;
        switch (requestCode) {
            case REQUEST_CODE_SMS_RESET:
                resetPwd();
                break;
        }
    }

    @Override
    public void feedbackPre() {
        setResult(RESULT_OK, new Intent()
                .putExtra("username", mPhone)
                .putExtra("pwd", mPassword)
        );
        finish();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            et_new_pwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else
            et_new_pwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_new_pwd.setSelection(et_new_pwd.getText().toString().length());
    }

    @Override
    protected void onDestroy() {
        if (mPwdRestTask != null)
            mPwdRestTask.cancel(true);
        super.onDestroy();
    }
}
