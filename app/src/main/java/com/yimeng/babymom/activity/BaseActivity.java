package com.yimeng.babymom.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.KeyBoardUtils;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.MyToast;
import com.yimeng.babymom.utils.PreferenceManager;
import com.yimeng.babymom.utils.WebServiceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * 自定义activity基类，实现以下功能：
 * 1.设置状态栏透明及颜色，子类可以重写
 * 2.提供过渡view的显示和隐藏方法mLoadingDialog
 * 3.通过一系列抽象方法规范视图创建时的逻辑，initView，initData等
 * 4.处理点击事件响应：返回键finish，其它控件子类处理
 * 5.增加activity的进入和退出动画
 * 6.提供一个通用的提示对话框mOkDialog
 * 7.简化toast
 */
public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    protected View mStatusBarView;
    protected Activity mActivity;
    public PreferenceManager mPrefManager = PreferenceManager.getInstance();
    private Dialog mLoadingDialog;
    private AlertDialog mOkDialog;
    private DialogInterface.OnClickListener mOnclickListener;
    private Handler mShowLoadingHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case WHAT_SHOW_LOADING:
                    mLoadingDialog.show();
                    return true;
            }
            return false;
        }
    });
    private static final int WHAT_SHOW_LOADING = 100;

    /**
     * 使用ksoap框架执行WebService请求的异步任务类
     */
    public static class SoapAsyncTask extends AsyncTask<Object, Object, String> {
        @Override
        protected String doInBackground(Object... params) {
            if (params != null && params.length >= 2) {
                return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                        (Map<String, Object>) params[1]);
            } else if (params != null && params.length == 1) {
                return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                        null);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getAppContext().addActivity(this);
        if (mActivity == null)
            mActivity = this;
        setContentView(setLayoutResId());
        setStatusBar();
        initView();
        setBackListener();
        setListener();
        initData();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    /**
     * 为返回控件设置点击事件
     */
    protected void setBackListener() {
        View backView = findViewById(R.id.iv_back);
        if (backView != null)
            backView.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        dismissLoadingView();
        if (mOkDialog != null && mOkDialog.isShowing())
            mOkDialog.dismiss();
        MyApp.getAppContext().removeActivity(this);
        super.onDestroy();
    }

    /**
     * 解析包含JsonArray结构的数据的json对象字符串，将结果存入集合中
     *
     * @param result    json对象字符串，键data对应一个JsonArray
     * @param arrayList 存入数据的集合
     * @param clazz     javabean的字节码文件
     */
    public <T> void parseListResult(ArrayList<T> arrayList, Class<T> clazz, String result) {
        arrayList.clear();
        try {
            JSONArray array = new JSONObject(result).optJSONArray("data");
            Gson gson = new Gson();
            for (int i = 0; i < array.length(); i++) {
                arrayList.add(gson.fromJson(array.optString(i), clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        try {
//            arrayList = new Gson().fromJson(new JSONObject(result).optString("data"),new TypeToken<List<T>>(){}.getType());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * 判断字符串是否为空
     *
     * @param string 字符串
     * @return 空true，否则false
     */
    public boolean isEmpty(String string) {
        return null == string || TextUtils.isEmpty(string.trim());
    }

    /**
     * 判断两个日期是否是同一天
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 同true
     */
    public boolean inSameDay(Date date1, Date date2) {
        if (null == date1 || null == date2) {
            return false;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date1);
        int year1 = calendar.get(Calendar.YEAR);
        int day1 = calendar.get(Calendar.DAY_OF_YEAR);
        calendar.setTime(date2);
        int year2 = calendar.get(Calendar.YEAR);
        int day2 = calendar.get(Calendar.DAY_OF_YEAR);
        return (year1 == year2) && (day1 == day2);
    }

    /**
     * 处理状态栏
     */
    protected void setStatusBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            // 设置透明状态栏
            if ((params.flags & bits) == 0) {
                params.flags |= bits;
                window.setAttributes(params);
            }

            // 设置状态栏颜色
            ViewGroup contentLayout = (ViewGroup) findViewById(android.R.id.content);
            setupStatusBarView(contentLayout);

            // 设置Activity layout的fitsSystemWindows
            View contentChild = contentLayout.getChildAt(0);
            contentChild.setFitsSystemWindows(true);
        }
    }

    /**
     * 创建一个背景为指定颜色，大小为状态栏大小的view，并且添加到屏幕的根view中
     *
     * @param contentLayout 屏幕的内容视图
     */
    private void setupStatusBarView(ViewGroup contentLayout) {
        if (mStatusBarView == null) {
            View statusBarView = new View(this);
            statusBarView.setId(R.id.iv_back);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight());
            contentLayout.addView(statusBarView, lp);

            mStatusBarView = statusBarView;
        }
        mStatusBarView.setBackgroundColor(setStatusBarColor());
    }

    /**
     * 设置状态栏颜色
     *
     * @return 颜色
     */
    public int setStatusBarColor() {
        return MyApp.getAppContext().getResources().getColor(R.color.colorStatusBar);
    }

    /**
     * 获得状态栏高度
     */
    private static int getStatusBarHeight() {
        int resourceId = MyApp.getAppContext().getResources().getIdentifier("status_bar_height", "dimen", "android");
        return MyApp.getAppContext().getResources().getDimensionPixelSize(resourceId);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        JPushInterface.onPause(this);
    }

    /**
     * 获得布局资源id
     *
     * @return 资源id
     */
    protected abstract int setLayoutResId();

    /**
     * 初始化控件，获得控件引用
     */
    protected abstract void initView();

    /**
     * 为控件设置监听和适配器
     */
    protected abstract void setListener();

    /**
     * 为控件绑定数据
     */
    protected abstract void initData();

    /**
     * 让子类处理"返回"以外的控件的点击事件
     *
     * @param viewId 控件id
     */
    protected abstract void onInnerClick(int viewId);

    @Override
    public void onClick(View v) {
        KeyBoardUtils.closeKeyboard(v);
        int id = v.getId();
        if (id == R.id.iv_back) {
            finish();
        } else {
            onInnerClick(id);
        }
    }

    public void showLoadingView() {
        showLoadingView(null);
    }

    /**
     * 显示过渡view
     *
     * @param stringId 文字指示资源id
     */
    public void showLoadingView(int stringId) {
        showLoadingView(getString(stringId));
    }

    /**
     * 显示过渡view
     *
     * @param message 加载视图文字指示
     */
    public void showLoadingView(@Nullable String message) {
        if (mLoadingDialog == null) {
            mLoadingDialog = new Dialog(this, R.style.MyDialogStyle);
            mLoadingDialog.setContentView(R.layout.layout_loading);
            Window window = mLoadingDialog.getWindow();
            WindowManager.LayoutParams params = window.getAttributes();
            params.dimAmount = 0f;
            window.setAttributes(params);
            mLoadingDialog.setCanceledOnTouchOutside(false);
        }
        TextView tv = (TextView) mLoadingDialog.findViewById(R.id.tv);
        tv.setText(isEmpty(message) ? getString(R.string.loading) : message);
        mShowLoadingHandler.removeMessages(WHAT_SHOW_LOADING);
        mShowLoadingHandler.sendEmptyMessageDelayed(WHAT_SHOW_LOADING, 500);
    }

    /**
     * 消失过渡view
     */
    public void dismissLoadingView() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
        mShowLoadingHandler.removeMessages(WHAT_SHOW_LOADING);
    }

    /**
     * 显示提示对话框
     *
     * @param message         显示消息
     * @param onClickListener 确定按钮的点击事件监听
     */
    public void showOkTips(String message, @Nullable DialogInterface.OnClickListener onClickListener) {
        dismissLoadingView();
        if (null == mOkDialog) {
            mOkDialog = new AlertDialog.Builder(mActivity)
                    .setTitle(getString(R.string.tip))
                    .setCancelable(false)
                    .create();

            mOnclickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        mOkDialog.setMessage(message);
        mOkDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(android.R.string.ok), onClickListener == null ? mOnclickListener : onClickListener);
        mOkDialog.show();
    }

    /**
     * 吐司
     *
     * @param message 内容
     */
    public void showToast(String message) {
        MyToast.show(mActivity, message);
    }

}
