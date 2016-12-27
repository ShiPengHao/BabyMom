package com.yimeng.babymom.fragment;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.activity.WebViewActivity;
import com.yimeng.babymom.adapter.DefaultAdapter;
import com.yimeng.babymom.bean.PromotionBean;
import com.yimeng.babymom.holder.BaseHolder;
import com.yimeng.babymom.holder.PromotionHolder;
import com.yimeng.babymom.task.PromotionTask;
import com.yimeng.babymom.utils.BitmapUtils;
import com.yimeng.babymom.utils.JsonUtils;
import com.yimeng.babymom.utils.MyConstant;

import java.util.ArrayList;

/**
 * 活动页面
 */

public class PromotionFragment extends BaseFragment implements AdapterView.OnItemClickListener {
    private ListView lv;
    private ArrayList<PromotionBean> mPromotionList = new ArrayList<>();
    private DefaultAdapter<PromotionBean> mAdapter;
    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            refreshPromotionList();
            return true;
        }
    });

    @Override
    protected int setLayoutResId() {
        return R.layout.fragment_promotion;
    }

    @Override
    protected void initView(View view) {
        lv = (ListView) view.findViewById(R.id.lv);
        lv.setBackgroundDrawable(BitmapUtils.bitmapToDrawable(activity,
                BitmapUtils.getResImg(activity, R.drawable.promotion_place)));
    }

    @Override
    protected void setListener() {
        lv.setOnItemClickListener(this);
        mAdapter = new DefaultAdapter<PromotionBean>(mPromotionList) {
            @Override
            protected BaseHolder getHolder() {
                return new PromotionHolder();
            }
        };
        lv.setAdapter(mAdapter);
    }

    /**
     * 刷新页面，每秒刷新1次
     */
    private void refreshPromotionList() {
        mAdapter.notifyDataSetChanged();
        mHandler.sendEmptyMessageDelayed(0, 800);
    }

    @Override
    protected void initData() {
        new PromotionTask(this, null).execute(PromotionTask.METHOD);
    }

    public void onPromotionResult(String result) {
        try {
            JsonUtils.parseListResult(mPromotionList, PromotionBean.class, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        refreshPromotionList();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String url = mPromotionList.get(position).url;
        if (!TextUtils.isEmpty(url) && url.matches(MyConstant.URL_PATTERN)) {
            startActivity(new Intent(activity, WebViewActivity.class).putExtra("url", url));
        }
    }

}
