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
import com.yimeng.babymom.utils.KeyBoardUtils;

import java.util.HashMap;
import java.util.Map;


/**
 * 登陆界面
 */
public class LoginActivity extends BaseActivity implements LoginInterface,CompoundButton.OnCheckedChangeListener {

    private EditText et_username;
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
        et_username = (EditText) findViewById(R.id.et_username);
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
        et_username.setText(mPrefManager.getAccountUsername());
        et_username.setSelection(et_username.getText().length());
        // 回显密码
        et_pwd.setText(mPrefManager.getAccountPassword());
        // 回显记住密码
        cb_remember.setChecked(mPrefManager.getAccountLastRemember());
    }


    @Override
    public void onInnerClick(int viewId) {
        KeyBoardUtils.closeKeybord(et_pwd, this);
        switch (viewId) {
            case R.id.bt_register:
                goToRegister();
                break;
            case R.id.bt_login:
                checkInput();
                break;
            case R.id.tv_forget_pwd:
                goToPwdReset();
                break;
        }
    }

    public void goToHome() {
//        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }


    public void goToPwdReset() {
        startActivityForResult(new Intent(this, PwdResetActivity.class)
                .putExtra("phone", et_username.getText().toString().trim()), 101
        );
    }

    public void goToRegister() {
        startActivityForResult(new Intent(this, RegisterActivity.class), 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (null == data) {
            return;
        }
        switch (requestCode) {
            case 100:
            case 101:
                et_username.setText(data.getStringExtra("phone"));
                et_pwd.setText(data.getStringExtra("pwd"));
                break;
        }
    }


    public void checkInput() {
        mPhone = et_username.getText().toString().trim();
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
        mParams.put("phone", mPhone);
        mParams.put("password", mPassword);
        mLoginTask = new LoginTask(this, bt_login).execute("login", mParams);
    }

    public void saveAccountInfo() {
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
