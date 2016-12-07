package com.yimeng.babymom.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.gson.Gson;
import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyToast;
import com.yimeng.babymom.utils.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public abstract class BaseActivity extends AppCompatActivity implements View.OnClickListener {
    private View mStatusBarView;
    protected Activity activity;
    protected PreferenceManager mPrefManager = PreferenceManager.getInstance();
    private Dialog mLoadingDialog;
    private AlertDialog mOkDialog;
    private DialogInterface.OnClickListener mOnclickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getAppContext().addActivity(this);
        if (activity == null) {
            activity = this;
        }
        setContentView(setLayoutResId());
        setStatusBar();
        initView();
        setBackListener();
        setListener();
        initData();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
//        LocationUtils.setUpdateLocationListener(null);
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
            for (int i = 0; i < array.length(); i++) {
                arrayList.add(new Gson().fromJson(array.optString(i), clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
     * 处理状态栏
     */
    protected void setStatusBar() {
        final int sdk = Build.VERSION.SDK_INT;

        if (sdk >= Build.VERSION_CODES.KITKAT) {
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
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, getStatusBarHeight(this));
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
        return getResources().getColor(R.color.colorStatusBar);
    }

    /**
     * 获得状态栏高度
     */
    private static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
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
        int id = v.getId();
        if (id == R.id.iv_back)
            finish();
        else
            onInnerClick(id);
    }

    public void showLoadingView() {
        showLoadingView(null);
    }

    /**
     * 显示过渡view
     *
     * @param message 加载视图文字指示
     */
    public void showLoadingView(String message) {
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
        tv.setText(message == null ? getString(R.string.loading) : message);
        mLoadingDialog.show();
    }

    /**
     * 显示提示对话框
     *
     * @param message         显示消息
     * @param onClickListener 确定按钮的点击事件监听
     */
    public void showOkTips(String message, DialogInterface.OnClickListener onClickListener) {
        dismissLoadingView();
        if (null == mOkDialog) {
            mOkDialog = new AlertDialog.Builder(activity)
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
     * 消失过渡view
     */
    public void dismissLoadingView() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing())
            mLoadingDialog.dismiss();
    }

    /**
     * 吐司
     *
     * @param message 内容
     */
    public void showToast(String message) {
        MyToast.show(activity, message);
    }

}
