package com.yimeng.babymom.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.interFace.SMSVerifyUIInterface;
import com.yimeng.babymom.utils.MyToast;

import org.json.JSONObject;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * 短信验证界面
 */

public class SMSVerifyActivity extends BaseActivity implements SMSVerifyUIInterface {
    private TextView tv_phone;
    private EditText et_msg_verify;
    private Button bt_verify;
    private Button bt_submit;

    private EventHandler mSmsEventHandler;
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_DELAY:
                    if (mTimeCount > 0) {
                        bt_verify.setText(String.format("%s秒后再次点击", mTimeCount--));
                        handler.sendEmptyMessageDelayed(TIME_DELAY, 1000);
                    } else {
                        bt_verify.setEnabled(true);
                        bt_verify.setText(getString(R.string.get_msg_verify));
                    }
                    break;
            }
            return true;
        }

    });
    private static final int TIME_DELAY = 60;
    private int mTimeCount = TIME_DELAY;
    private String mPhone;
    private String mVerifyCode;

    private class MyEventHandler extends EventHandler implements SMSVerifyUIInterface.SMSListener {
        @Override
        public void afterEvent(int event, int result, Object data) {
            switch (result) {
                case SMSSDK.RESULT_COMPLETE:
                    dispatchOkEvent(event, data);
                    break;
                case SMSSDK.RESULT_ERROR:
                    showErrorTip((Throwable) data);
                    break;
            }
        }

        public void showErrorTip(Throwable data) {
            dismissLoadingView();
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    bt_verify.setEnabled(true);
                    bt_submit.setEnabled(true);
                }
            });
            try {
                data.printStackTrace();
                JSONObject object = new JSONObject(data.getMessage());
                String des = object.optString("detail");//错误描述
                int status = object.optInt("status");//错误代码
                if (status > 0 && !TextUtils.isEmpty(des)) {
                    showToast(des);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void dispatchOkEvent(int event, Object data) {
            switch (event) {
                case SMSSDK.EVENT_GET_VERIFICATION_CODE:
                    updateTextIndicator();
                    break;
                case SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE:
                    HashMap<String, Object> map = (HashMap<String, Object>) data;
                    if (mPhone.equalsIgnoreCase((String) map.get("phone"))) {// 手机号匹配，验证成功
                        feedbackPre();
                    } else {
                        dismissLoadingView();
                        showToast(getString(R.string.sms_check_error));
                    }
                    break;
            }
        }
    }

    public void feedbackPre() {
        setResult(RESULT_OK, new Intent());
        finish();
    }

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_sms_verify;
    }

    @Override
    protected void initView() {
        tv_phone = (TextView) findViewById(R.id.tv_phone);
        et_msg_verify = (EditText) findViewById(R.id.et_msg_verify);

        bt_submit = (Button) findViewById(R.id.bt_submit);
        bt_verify = (Button) findViewById(R.id.bt_verify);
    }

    @Override
    protected void setListener() {
        bt_submit.setOnClickListener(this);
        bt_verify.setOnClickListener(this);
        registerSmsListener();
    }

    @Override
    protected void initData() {
        try {
            Intent intent = getIntent();
            mPhone = intent.getStringExtra("phone");
            tv_phone.setText(mPhone);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onInnerClick(int viewId) {
        switch (viewId) {
            case R.id.bt_verify:
                getVerifyCode();
                break;
            case R.id.bt_submit:
                checkInput();
                break;
        }
    }

    /**
     * 获取短信验证码
     */
    private void getVerifyCode() {
        if (isEmpty(mPhone)) {
            MyToast.show(this, String.format("%s%s", getString(R.string.phone), getString(R.string.can_not_be_null)));
            return;
        }
        if (!mPhone.matches("[1][358]\\d{9}")) {
            MyToast.show(this, "手机号格式不正确");
            return;
        }

        bt_verify.setEnabled(false);
        requestSmsCode();
    }

    public void requestSmsCode() {
        SMSSDK.getVerificationCode("86", mPhone, null);
    }

    /**
     * 检验短信验证码
     */
    public void checkInput() {
        mVerifyCode = et_msg_verify.getText().toString().trim();
        if (isEmpty(mVerifyCode)) {
            MyToast.show(this, String.format("%s%s", getString(R.string.verification), getString(R.string.can_not_be_null)));
            return;
        }

        bt_submit.setEnabled(false);
        showLoadingView();
        checkSmsCode();
    }


    @Override
    public void checkSmsCode() {
        SMSSDK.submitVerificationCode("86", mPhone, mVerifyCode);
    }


    public void updateTextIndicator() {
        handler.removeMessages(TIME_DELAY);
        handler.sendEmptyMessageDelayed(TIME_DELAY, 0);
    }

    public void registerSmsListener() {
        mSmsEventHandler = new MyEventHandler();
        SMSSDK.registerEventHandler(mSmsEventHandler); //注册短信回调
//        SMSSDK.getSupportedCountries();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        unregisterSmsListener();
        super.onDestroy();
    }

    public void unregisterSmsListener() {
        if (mSmsEventHandler != null)
            SMSSDK.unregisterEventHandler(mSmsEventHandler);
    }


}
