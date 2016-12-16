package com.yimeng.babymom.task;

import android.os.AsyncTask;
import android.view.View;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.BaseActivity;
import com.yimeng.babymom.fragment.BaseFragment;
import com.yimeng.babymom.utils.MyConstant;
import com.yimeng.babymom.utils.WebServiceUtils;

import java.lang.ref.WeakReference;
import java.util.Map;

/**
 * 使用ksoap框架执行WebService请求的异步任务基类，实现如下功能：
 * 1.自动进行了弱引用判断，以防止异步任务持有activity实例造成activity无法回收以至于oom
 * 2.自动处理联网时过渡view的显示和隐藏
 * 3.对服务器返回的数据进行了非空判断，子类需要重写parseResult方法
 * 4.提供了异常回调onError方法
 */
public abstract class BaseTask<T> extends AsyncTask<Object, Object, String> {

    private WeakReference<T> activityWR;
    private WeakReference<View> viewWR;

    public BaseTask(T activity, View view) {
        activityWR = new WeakReference<>(activity);
        if (view != null)
            viewWR = new WeakReference<>(view);
    }

    @Override
    protected void onPreExecute() {
        T activity = activityWR.get();
        checkReference(activity, true);
    }

    /**
     * 检查引用状态，根据当前状态来选择对应的操作（显示进度或者隐藏）
     *
     * @param state    需要显示true，否则false
     * @param activity activity
     * @return 如果activity引用不存在返回true，否则false
     */
    private boolean checkReference(T activity, boolean state) {
        // 处理过渡view状态
        if (activity == null)
            return true;
        else if (state) {
            if (activity instanceof BaseActivity)
                ((BaseActivity) activity).showLoadingView();
            else
                ((BaseFragment) activity).showLoadingView(null);
        } else if (activity instanceof BaseActivity)
            ((BaseActivity) activity).dismissLoadingView();
        else
            ((BaseFragment) activity).dismissLoadingView();
        // 处理按钮状态
        if (viewWR != null) {
            View view = viewWR.get();
            if (view != null)
                view.setEnabled(!state);
        }
        return false;
    }

    /**
     * 子类必须实现的方法，用来处理服务器返回的数据
     *
     * @param activity 子类引用的activity实例
     * @param result   服务器返回的数据，非null
     */
    public abstract void parseResult(T activity, String result);

    @Override
    protected void onPostExecute(String s) {
        T activity = activityWR.get();
        if (checkReference(activity, false))
            return;
        if (s == null)
            onError(activity);
        else
            parseResult(activity, s);
    }

    /**
     * 获取数据过程发生异常回调
     *
     * @param activity activity
     */
    protected void onError(T activity) {
        if (activity instanceof BaseActivity)
            ((BaseActivity) activity).showToast(((BaseActivity) activity).getString(R.string.connect_error));
        else
            ((BaseFragment) activity).showToast(((BaseFragment) activity).getString(R.string.connect_error));
    }

    @Override
    protected String doInBackground(Object... params) {
        try {
            if (params != null && params.length >= 2) {
                return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                        (Map<String, Object>) params[1]);
            } else if (params != null && params.length == 1) {
                return WebServiceUtils.callWebService(MyConstant.WEB_SERVICE_URL, MyConstant.NAMESPACE, (String) params[0],
                        null);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
