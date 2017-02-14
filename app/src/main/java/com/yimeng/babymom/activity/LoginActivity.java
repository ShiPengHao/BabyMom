package com.yimeng.babymom.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.interFace.LoginInterface;
import com.yimeng.babymom.task.LoginTask;

import java.util.HashMap;
import java.util.Map;


/**
 * 登陆界面
 */
public class LoginActivity extends BaseActivity implements LoginInterface, CompoundButton.OnCheckedChangeListener {

    private EditText et_phone;
    private EditText et_pwd;
    private CheckBox cb_remember;
    private CheckBox cb_auto;
    private Button bt_register;
    private Button bt_login;
    private TextView tv_forget_pwd;

    private String mPhone;
    private String mPassword;
    private Map<String, Object> mParams = new HashMap<>();
    private AsyncTask mLoginTask;


    @Override
    protected int setLayoutResId() {
        return R.layout.activity_login;
    }

    protected void initView() {
        et_phone = (EditText) findViewById(R.id.et_phone);
        et_pwd = (EditText) findViewById(R.id.et_pwd);
        cb_remember = (CheckBox) findViewById(R.id.cb_remeber);
        cb_auto = (CheckBox) findViewById(R.id.cb_auto);
        bt_register = (Button) findViewById(R.id.bt_register);
        bt_login = (Button) findViewById(R.id.bt_login);
        tv_forget_pwd = (TextView) findViewById(R.id.tv_forget_pwd);
    }

    @Override
    protected void setListener() {
        cb_auto.setOnCheckedChangeListener(this);
        bt_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);
        tv_forget_pwd.setOnClickListener(this);
    }

    @Override
    protected void initData() {
        // 回显账号
        et_phone.setText(mPrefManager.getAccountUsername());
        et_phone.setSelection(et_phone.getText().length());
        // 回显密码
        et_pwd.setText(mPrefManager.getAccountPassword());
        // 回显记住密码
        cb_remember.setChecked(mPrefManager.getAccountLastRemember());
    }


    @Override
    public void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.bt_register:
                goToRegister();
                break;
            case R.id.bt_login:
//                checkInput();//TODO 登录未测试
                goToHome();
                break;
            case R.id.tv_forget_pwd:
                goToPwdReset();
                break;
        }
    }

    public void goToHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }


    public void goToPwdReset() {
        startActivityForResult(new Intent(this, PwdResetActivity.class)
                .putExtra("phone", et_phone.getText().toString().trim()), 101
        );
    }

    public void goToRegister() {
        startActivityForResult(new Intent(this, RegisterActivity.class)
                .putExtra("phone", et_phone.getText().toString().trim()), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data) {
            return;
        }
        switch (requestCode) {
            case 100:
            case 101:
                mPhone = data.getStringExtra("phone");
                if (mPhone != null) {
                    et_phone.setText(mPhone);
                    et_phone.setSelection(mPhone.length());
                }

                mPassword = data.getStringExtra("pwd");
                if (mPassword != null) {
                    et_pwd.setText(mPassword);
                    et_pwd.setSelection(mPassword.length());
                }
                break;
        }
    }


    public void checkInput() {
        mPhone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(mPhone)) {
            showToast(String.format("%s%s", getString(R.string.phone), getString(R.string.can_not_be_null)));
            return;
        }

        mPassword = et_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(mPassword)) {
            showToast(String.format("%s%s", getString(R.string.password), getString(R.string.can_not_be_null)));
            return;
        }

        login();
    }

    public void login() {
        if (mLoginTask != null)
            mLoginTask.cancel(true);
        mParams.clear();
        mParams.put(LoginTask.PHONE, mPhone);
        mParams.put(LoginTask.PASSWORD, mPassword);
        mLoginTask = new LoginTask(this, bt_login).execute(LoginTask.METHOD, mParams);
    }

    @Override
    public void onLoginOk() {
        saveAccountInfo();
        goToHome();
    }

    @Override
    public void onLoginError() {
        showToast(getString(R.string.connect_error));
    }

    private void saveAccountInfo() {
        mPrefManager.setAccountAutoLogin(cb_auto.isChecked())
                .setAccountLastRemember(cb_remember.isChecked())
                .setAccountUsername(mPhone)
                .setAccountPassword(cb_remember.isChecked() ? mPassword : "")
        ;
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == cb_auto.getId() && cb_auto.isChecked()) {
            cb_remember.setChecked(true);
        }
    }

    @Override
    protected void onDestroy() {
        if (mLoginTask != null)
            mLoginTask.cancel(true);
        super.onDestroy();
    }

}
