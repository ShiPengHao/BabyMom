package com.yimeng.babymom.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yimeng.babymom.activity.BaseActivity;
import com.yimeng.babymom.utils.KeyBoardUtils;
import com.yimeng.babymom.utils.UiUtils;


/**
 * fragment抽象基类
 */
public abstract class BaseFragment extends Fragment implements View.OnClickListener {
    protected BaseActivity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (activity == null) {
            activity = (BaseActivity) getActivity();
        }
        View view = getView();
        if (null == view) {
            view = UiUtils.inflate(setLayoutResId());
            initView(view);
            setListener();
            initData();
        }
        return view;
    }


    /**
     * 获得布局id
     *
     * @return layoutId
     */
    protected abstract int setLayoutResId();

    /**
     * 初始化控件
     *
     * @param view rootView
     */
    protected abstract void initView(View view);

    /**
     * 为控件设置监听
     */
    protected abstract void setListener();

    /**
     * 加载数据，绑定到控件
     */
    protected abstract void initData();

    /**
     * 吐司
     *
     * @param message 内容
     */
    public void showToast(String message) {
        activity.showToast(message);
    }

    /**
     * 显示提示对话框
     *
     * @param message         显示消息
     * @param onClickListener 确定按钮的点击事件监听
     */
    public void showOkTips(String message, DialogInterface.OnClickListener onClickListener) {
        activity.showOkTips(message, onClickListener);
    }

    /**
     * 显示过渡view
     *
     * @param message 加载视图文字指示
     */
    public void showLoadingView(String message) {
        activity.showLoadingView(message);
    }

    /**
     * 消失过渡view
     */
    public void dismissLoadingView() {
        activity.dismissLoadingView();
    }

    @Override
    public void onClick(View v) {
        KeyBoardUtils.closeKeybord(v);
    }
}
