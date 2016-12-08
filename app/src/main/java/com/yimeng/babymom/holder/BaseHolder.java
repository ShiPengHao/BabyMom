package com.yimeng.babymom.holder;

import android.view.View;

/**
 * holder的基类
 */
public abstract class BaseHolder<T> {

    private final View contentView;

    public BaseHolder(){
        contentView = initView();
        contentView.setTag(this);
    }

    /**
     * 获得此holder绑定的view
     * @return view
     */
    public View getView(){
        return contentView;
    }

    /**
     * 为此holder绑定view
     * @return view
     */
    protected abstract View initView();

    /**
     * 为此holder绑定的view设置数据
     * @param data 对应的数据源
     */
    public abstract void bindData(T data);
}
