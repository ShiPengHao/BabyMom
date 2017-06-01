package com.yimeng.babymom.activity;

import android.graphics.Color;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.yimeng.babymom.R;
import com.yimeng.babymom.utils.Cheeses;
import com.yimeng.babymom.utils.UiUtils;

import java.util.Locale;
import java.util.Random;


/**
 * 测试
 * 1.ListView嵌套水平滑动条目，实现ListView+scrollview，解决卡顿问题
 */

public class TestActivity extends BaseActivity {

    private ListView listView;

    @Override
    protected int setLayoutResId() {
        return R.layout.activity_test;
    }

    @Override
    protected void initView() {
        listView = (ListView) findViewById(R.id.lv);
    }

    @Override
    protected void setListener() {
        // 设置ListView的焦点模式，让子条目先处理事件，以实现条目内水平滑动
        listView.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        listView.setAdapter(new ListViewHorizontalItemAdapter());
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void onInnerClick(int viewId) {

    }

    /**
     * ListView嵌套水平滑动条目adapter
     */
    private class ListViewHorizontalItemAdapter extends BaseAdapter {
        /**
         * 资源文件，产生图片，实际应用中从服务器获取的url
         */
        private final int[] res = new int[]{R.drawable.bannermask1, R.drawable.bannermask2, R.drawable.bannermask3};
        /**
         * 模拟条目横向滑动模块里边的子条目个数的数组，值为随机3-7个，数组长度大于条目总数/3，实际应用中从服务器获取
         */
        private final int[] PIC_NUMBERS_ARRAY = new int[]{
                new Random().nextInt(5) + 3,
                new Random().nextInt(5) + 3,
                new Random().nextInt(5) + 3,
                new Random().nextInt(5) + 3,
                new Random().nextInt(5) + 3,
                new Random().nextInt(5) + 3,
                new Random().nextInt(5) + 3,
                new Random().nextInt(5) + 3
        };
        //  模拟22个条目
        private static final int ITEM_COUNT = 22;
        private final int ITEM_TYPE_TEXT = 0;
        private final int ITEM_TYPE_PIC = 1;

        @Override
        public int getCount() {
            return ITEM_COUNT;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        // 模拟2种条目类型，1/3的条目为纯文本，2/3图文+横向滑动，实际应用通常有头布局等
        public int getItemViewType(int position) {
            return position % 3 == 0 ? ITEM_TYPE_PIC : ITEM_TYPE_TEXT;
        }

        @Override
        public int getViewTypeCount() {
            return super.getViewTypeCount() + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch (getItemViewType(position)) {
                case ITEM_TYPE_TEXT:// 纯文本，不需要holder
                    if (convertView == null) {
                        convertView = UiUtils.inflate(android.R.layout.simple_list_item_1);
                    }
                    ((TextView) convertView).setText(Cheeses.NAMES[position]);
                    ((TextView) convertView).setTextColor(Color.BLACK);
                    break;
                case ITEM_TYPE_PIC:// 图文+横向滑动模块
                    if (convertView == null) {
                        convertView = UiUtils.inflate(R.layout.layout_scroll_item);
                        convertView.setTag(new ViewHolder(convertView));
                    }
                    ViewHolder holder = (ViewHolder) convertView.getTag();
                    // 因为设置1/3的条目为图文，position必然为3的倍数，所以这里/3获得一个索引，用于获取每个滑动模块内部子条目的模拟个数
                    holder.bindView(position / 3);
                    break;
            }
            return convertView;
        }

        /**
         * 子条目holder，左图右多行文字的形式
         */
        private class ItemViewHolder implements View.OnClickListener {
            private ImageView imageView;
            private TextView textView;
            private View view;

            private ItemViewHolder() {
                view = UiUtils.inflate(R.layout.item_doctor);
                imageView = (ImageView) view.findViewById(R.id.item_icon);
                textView = (TextView) view.findViewById(R.id.item_name);
                imageView.setOnClickListener(this);
                textView.setOnClickListener(this);
            }

            @Override
            // 点击控件时吐司哈希码，检查复用性
            public void onClick(View v) {
                showToast(String.format("class:%s\nhashcode:%s", v.getClass().toString(), v.hashCode()));
            }
        }

        /**
         * 条目holder，图文简单设定为固定，没有处理，滑动模块实现方式为scrollview+LinearLayout+子条目
         */
        private class ViewHolder implements View.OnClickListener {
            /**
             * 管理滑动模块的子条目的map，index:holder的形式，map的长度就是所有滑动模块最大的子条目个数，
             * 这样滑动模块内的条目互相不复用，相当于增加了内存，但是实现了模块间复用，效果还行。
             */
            private SparseArray<ItemViewHolder> itemHolders = new SparseArray<>();
            private final LinearLayout ll;
            private final HorizontalScrollView sl;
            private final ImageView imageView;
            private final TextView textView;


            private ViewHolder(View view) {
                ll = (LinearLayout) view.findViewById(R.id.ll);
                sl = (HorizontalScrollView) view.findViewById(R.id.sl);
                textView = (TextView) view.findViewById(R.id.tv);
                imageView = (ImageView) view.findViewById(R.id.iv);
                imageView.setOnClickListener(this);
                textView.setOnClickListener(this);
            }

            /**
             * 图文简化不处理，只处理滑动模块：先移除所有子条目，再添加指定个数的子条目
             *
             * @param picItemPosition 含有滑动模块的条目索引
             */
            private void bindView(int picItemPosition) {
                // 移除所有子条目
                ll.removeAllViews();
                ItemViewHolder itemViewHolder;
                // 根据获得的子条目个数，添加子条目
                for (int i = 0; i < PIC_NUMBERS_ARRAY[picItemPosition]; i++) {
                    // 存取子条目holder
                    itemViewHolder = itemHolders.valueAt(i);
                    if (itemViewHolder == null) {
                        itemViewHolder = new ItemViewHolder();
                        itemHolders.setValueAt(i, itemViewHolder);
                    }
                    // 直接在这里设置子条目的图文，并没有用bind，当然也可以抽取方法到holder里边，个人习惯不同自由设置
                    // 图片与条目位置、子条目位置均相关，使相邻条目的滑动模块必然不同，方便观察条目复用性
                    itemViewHolder.imageView.setImageResource(res[(i + picItemPosition) % 3]);
                    // 文字与子条目位置相关
                    itemViewHolder.textView.setText(String.format(Locale.CHINA, "num:%d", i));
                    // 添加子条目
                    ll.addView(itemViewHolder.view);
                }
                // 滑动模块滚动到最左端
                sl.scrollTo(0, 0);
            }

            @Override
            // 点击控件时吐司哈希码，检查复用性
            public void onClick(View v) {
                showToast(String.format("class:%s\nhashcode:%s", v.getClass().toString(), v.hashCode()));
            }

        }
    }
}
