package com.yimeng.babymom.view;

import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

/**
 * 无限轮播ViewPager，可以设置轮播间隔时间和每个条目的点击事件，可以手动控制自动轮播的开始和停止，默认设置完适配器之后开始自动轮播
 */
public class CycleViewPager extends ViewPager {

    private static final int WHAT_ROLL = 0;
    private static final int DURATION_DEFAULT = 2000;// 轮播间隔默认2s
    private int duration = DURATION_DEFAULT;
    /**
     * 包装外部adapter的adapter
     */
    private InnerPagerAdapter innerPagerAdapter;
    private int position;


    public CycleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CycleViewPager(Context context) {
        super(context);
    }

    @Override
    /**
     * 重写此方法，包装adapter，并且初始化
     */
    public void setAdapter(PagerAdapter adapter) {
        addOnPageChangeListener(null);
        innerPagerAdapter = new InnerPagerAdapter(adapter);
        super.setAdapter(innerPagerAdapter);
        setCurrentItem(1);
        if (adapter.getCount() > 1)
            startRoll();
    }

    /**
     * 设置图片切换间隔时长，单位毫秒
     *
     * @param duration 时长
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }

    /**
     * 开始自动轮播
     */
    public void startRoll() {
        stopRoll();
        handler.sendEmptyMessageDelayed(WHAT_ROLL, duration);
    }

    /**
     * 停止自动轮播
     */
    public void stopRoll() {
        handler.removeMessages(WHAT_ROLL);
    }

    /**
     * 设置条目点击监听
     *
     * @param onItemClickListener 监听器
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 主要处理定时轮播的handler
     */
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case WHAT_ROLL:
                    if (innerPagerAdapter != null && innerPagerAdapter.getCount() > 3) {
                        setCurrentItem(position + 1);
                        handler.sendEmptyMessageDelayed(WHAT_ROLL, duration);
                    }
            }
        }
    };

    private float downX;

    protected void onDetachedFromWindow() {
        stopRoll();
        super.onDetachedFromWindow();
    }

    private boolean isClick;

    private float downY;

    private long downTime;

    @Override
    /**
     * 一、点击此ViewPager区域时，在获得down事件时请求处理事件并停止滚动，默认为点击事件
     * 二、在move事件中，1.当当前点相对于点击的点移动的横或者竖方向上的距离大于5像素时，认为是滑动事件；
     *      2.当横向距离小于竖向距离时，将事件交与外层（可能是ViewPager的onTouchEvent中默认的处理方式）
     *      3.当滑动到Pager集合的两端，但仍然向外滑动时，交与外层
     *      4.当2和3的条件都不符合时（即用户要正常切换Pager或者点击），要求处理事件
     * 三、在up事件中，根据移动距离、事件时间间隔来执行点击事件监听中的事件点击方法，并继续开始滚动
     * 四、在cancel事件中，即此ViewPager区域失去move事件时，继续开始滚动
     */
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);
                downX = ev.getX();
                downY = ev.getY();
                downTime = SystemClock.uptimeMillis();
                stopRoll();
                isClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - downX) > 5 || Math.abs(ev.getY() - downY) > 5) {
                    isClick = false;
                }
                if (Math.abs(ev.getX() - downX) < Math.abs(ev.getY() - downY)) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (ev.getX() > downX && getCurrentItem() == 0) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else if (ev.getX() < downX && getCurrentItem() == innerPagerAdapter.getCount() - 1) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                startRoll();
                break;
            case MotionEvent.ACTION_UP:
                if (isClick && (SystemClock.uptimeMillis() - downTime) < 500 && onItemClickListener != null) {
                    int index = this.position;
                    if (index == 0) {
                        index = innerPagerAdapter.getCount() - 3;
                    } else if (index == innerPagerAdapter.getCount() - 1) {
                        index = 0;
                    } else {
                        index--;
                    }
                    onItemClickListener.onItemClick(index);
                }
                startRoll();
                break;

            default:
                break;
        }
        return super.onTouchEvent(ev);
    }

    private OnItemClickListener onItemClickListener;

    /**
     * 条目点击监听接口
     */
    public interface OnItemClickListener {
        void onItemClick(int index);
    }

    @Override
    /**
     * 重写此方法，将监听包装
     */
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        super.addOnPageChangeListener(new InnerOnPageChangeListener(listener));
    }

    /**
     * 监听包装类，主要处理轮播图的切换和索引修正
     */
    private class InnerOnPageChangeListener implements OnPageChangeListener {

        private OnPageChangeListener listener;

        public InnerOnPageChangeListener(OnPageChangeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            if (position == innerPagerAdapter.getCount() - 1) {
                // A 最后一个条目
                position = 1;
            } else if (position == 0) {
                //D A B C D A
                position = innerPagerAdapter.getCount() - 2;
            }
            if (listener != null)
                listener.onPageScrolled(position - 1, positionOffset, positionOffsetPixels);
        }

        @Override
        public void onPageSelected(int position) {
            CycleViewPager.this.position = position;
            if (position == innerPagerAdapter.getCount() - 1) {
                // A 最后一个条目
                position = 1;
            } else if (position == 0) {
                //D A B C D A
                position = innerPagerAdapter.getCount() - 2;
            }
            if (listener != null)
                listener.onPageSelected(position - 1);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            //  空闲 IDLE
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (position == innerPagerAdapter.getCount() - 1) {
                    // A 最后一个条目 切换到position = 1的 条目 悄悄的切换
                    setCurrentItem(1, false);
                } else if (position == 0) {
                    //D A B C D A
                    setCurrentItem(innerPagerAdapter.getCount() - 2, false);
                }
            }
            if (listener != null)
                listener.onPageScrollStateChanged(state);
        }
    }

    /**
     * 适配器包装类，主要处理轮播图的pager视图修正
     */
    private class InnerPagerAdapter extends PagerAdapter {

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            setCurrentItem(1);
            startRoll();
        }

        private PagerAdapter outerAdapter;

        public InnerPagerAdapter(PagerAdapter outerAdapter) {
            this.outerAdapter = outerAdapter;
        }

        @Override
        public int getCount() {
            return outerAdapter.getCount() + 2;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position == 0) {
                position = getCount() - 3;
            } else if (position == getCount() - 1) {
                position = 0;
            } else {
                position--;
            }
            return outerAdapter.instantiateItem(container, position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            outerAdapter.destroyItem(container, position, object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return outerAdapter.isViewFromObject(view, object);
        }

    }

}
