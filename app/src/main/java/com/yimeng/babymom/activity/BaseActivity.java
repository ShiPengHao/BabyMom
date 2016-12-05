package com.yimeng.babymom.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.MyApp;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.WebServiceUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public abstract class BaseActivity extends AppCompatActivity {
    private View mStatusBarView;
    protected Context context;

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
        if (context == null) {
            context = this;
        }
        setContentView(setLayoutResId());
        setStatusBar();
        initView();
        setListener();
        initData();
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
//        LocationUtils.setUpdateLocationListener(null);
    }

    @Override
    protected void onDestroy() {
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
    protected boolean isEmpty(String string) {
        if (null == string) {
            return true;
        }
        return TextUtils.isEmpty(string.trim());
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
    protected int setStatusBarColor() {
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


}
