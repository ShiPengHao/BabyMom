package com.yimeng.babymom.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yimeng.babymom.utils.MyToast;
import com.yimeng.babymom.utils.UiUtils;


/**
 * fragment抽象基类
 */
public abstract class BaseFragment extends Fragment {
    protected Activity activity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (activity == null) {
            activity = getActivity();
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
     * @param message 内容
     */
    protected void showToast(String message) {
        MyToast.show(activity, message);
    }
}
