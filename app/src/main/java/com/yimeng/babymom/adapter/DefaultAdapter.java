package com.yimeng.babymom.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;


import com.yimeng.babymom.holder.BaseHolder;

import java.util.List;

/**
 * 适配器的模板
 */
public abstract class DefaultAdapter<T> extends BaseAdapter {
    protected List<T> data;

    private DefaultAdapter() {
    }

    public DefaultAdapter(List<T> data) {
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        BaseHolder holder;
        if (convertView == null) {
            holder = getHolder();
        } else {
            holder = (BaseHolder) convertView.getTag();
        }
        holder.bindData(getItem(position));
        return holder.getView();
    }

    /**
     * 获得此适配器对应的holder对象
     * @return holder对象
     */
    protected abstract BaseHolder getHolder();
}
