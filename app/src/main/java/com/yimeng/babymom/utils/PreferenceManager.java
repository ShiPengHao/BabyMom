package com.yimeng.babymom.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    /**
     * name of preference
     */
    private static final String PREFERENCE_NAME = "PREFS_ACCOUNT";
    private static SharedPreferences mSharedPreferences;
    private static PreferenceManager mPreferencemManager;
    private static SharedPreferences.Editor mEditor;
    /**
     * 设置声音提醒的键
     */
    private String KEY_SETTING_SOUND = "KEY_SETTING_SOUND";
    /**
     * 设置震动提醒的键
     */
    private String KEY_SETTING_VIBRATE = "KEY_SETTING_VIBRATE";
    /**
     * 首次运行的键
     */
    private static final String KEY_ACCOUNT_FIRST_RUNNING = "KEY_ACCOUNT_FIRST_RUNNING";
    /**
     * 自动更新的键
     */
    private static final String KEY_ACCOUNT_AUTO_UPDATE = "KEY_ACCOUNT_AUTO_UPDATE";
    /**
     * 自动登陆的键
     */
    private static final String KEY_ACCOUNT_AUTO_LOGIN = "KEY_ACCOUNT_AUTO_LOGIN";
    /**
     * 上次登录成功的用户名的键
     */
    private static final String KEY_ACCOUNT_LAST_USERNAME = "KEY_ACCOUNT_LAST_USERNAME";
    /**
     * 上次登录成功的用户的密码的键
     */
    private static final String KEY_ACCOUNT_LAST_PASSWORD = "KEY_ACCOUNT_LAST_PASSWORD";
    /**
     * 上次登录成功的用户名的是否记住密码的键
     */
    private static final String KEY_ACCOUNT_LAST_REMEMBER = "KEY_ACCOUNT_LAST_REMEMBER";
    /**
     * 上次登录成功的用户的id的键
     */
    private static final String KEY_ACCOUNT_LAST_ID = "KEY_ACCOUNT_LAST_ID";


    private PreferenceManager(Context cxt) {
        mSharedPreferences = cxt.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static synchronized void init(Context cxt) {
        if (mPreferencemManager == null) {
            mPreferencemManager = new PreferenceManager(cxt);
        }
    }

    /**
     * get instance of PreferenceManager
     *
     * @return instance
     */
    public synchronized static PreferenceManager getInstance() {
        if (mPreferencemManager == null) {
            throw new RuntimeException("please init first!");
        }
        return mPreferencemManager;
    }

    public PreferenceManager setSettingMsgSound(boolean paramBoolean) {
        mEditor.putBoolean(KEY_SETTING_SOUND, paramBoolean).apply();
        return this;
    }

    public boolean getSettingMsgSound() {
        return mSharedPreferences.getBoolean(KEY_SETTING_SOUND, true);
    }

    public PreferenceManager setSettingMsgVibrate(boolean paramBoolean) {
        mEditor.putBoolean(KEY_SETTING_VIBRATE, paramBoolean).apply();
        return this;
    }

    public boolean getSettingMsgVibrate() {
        return mSharedPreferences.getBoolean(KEY_SETTING_VIBRATE, true);
    }

    public PreferenceManager setAccountFirstRunning(boolean paramBoolean) {
        mEditor.putBoolean(KEY_ACCOUNT_FIRST_RUNNING, paramBoolean).apply();
        return this;
    }

    public boolean getAccountFirstRunning() {
        return mSharedPreferences.getBoolean(KEY_ACCOUNT_FIRST_RUNNING, true);
    }

    public PreferenceManager setAccountAutoUpdate(boolean paramBoolean) {
        mEditor.putBoolean(KEY_ACCOUNT_AUTO_UPDATE, paramBoolean).apply();
        return this;
    }

    public boolean getAccountAutoUpdate() {
        return mSharedPreferences.getBoolean(KEY_ACCOUNT_AUTO_UPDATE, true);
    }

    public PreferenceManager setAccountAutoLogin(boolean paramBoolean) {
        mEditor.putBoolean(KEY_ACCOUNT_AUTO_LOGIN, paramBoolean).apply();
        return this;
    }

    public boolean getAccountAutoLogin() {
        return mSharedPreferences.getBoolean(KEY_ACCOUNT_AUTO_LOGIN, false);
    }

    public PreferenceManager setAccountLastRemember(boolean paramBoolean) {
        mEditor.putBoolean(KEY_ACCOUNT_LAST_REMEMBER, paramBoolean).apply();
        return this;
    }

    public boolean getAccountLastRemember() {
        return mSharedPreferences.getBoolean(KEY_ACCOUNT_LAST_REMEMBER, false);
    }

    public PreferenceManager setAccountUsername(String paramString) {
        mEditor.putString(KEY_ACCOUNT_LAST_USERNAME, paramString).apply();
        return this;
    }

    public String getAccountUsername() {
        return mSharedPreferences.getString(KEY_ACCOUNT_LAST_USERNAME, "");
    }

    public PreferenceManager setAccountPassword(String paramString) {
        mEditor.putString(KEY_ACCOUNT_LAST_PASSWORD, paramString).apply();
        return this;
    }

    public String getAccountPassword() {
        return mSharedPreferences.getString(KEY_ACCOUNT_LAST_PASSWORD, "");
    }


}
