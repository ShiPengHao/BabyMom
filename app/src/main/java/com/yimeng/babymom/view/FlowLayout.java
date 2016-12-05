package com.yimeng.babymom.activity.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.yimeng.hyzchbczhwq.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * 可以实现对子View自动排版，自动换行的ViewGroup
 */
public class FlowLayout extends ViewGroup {
    // 多行
    private List<Line> lines = new ArrayList<>();
    private Line currentLine; //当前行
    private int useWidth;// 当前行使用的宽度
    private int HORIZONTAL_SPACING = DensityUtil.dip2px(8);//横向的间隔
    private int VERTICAL_SPACING = DensityUtil.dip2px(8); //垂直方向的间隔


    private FlowLayout(Context context) {
        super(context,null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs,0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*测量  父view  有义务 测量所有的子view*/
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        lines.clear();
        currentLine = null;
        useWidth = 0;


//  widthMeasureSpec  mode+size  模式+ 大小

//        MeasureSpec.EXACTLY  精确模式 :  match_parent   100dp
//        MeasureSpec.AT_MOST  包裹模式 :  wrap_content
//        MeasureSpec.UNSPECIFIED 未指定

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);//  ---->宽的mode
        int width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();//  ---->宽


        int heightMode = MeasureSpec.getMode(heightMeasureSpec);//  ---->高的mode
        int height = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();//  ---->高


//        1. 只有父view 的mode EXACTLY  子view  肯定是 AT_MOST
//        2. 如果父view 不是 EXACTLY 子view  一定和和父view  一样
        //子view 的mode
        int childWidthMode = widthMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : widthMode;
//        if (widthMode == MeasureSpec.EXACTLY){
//            childWidthMode = MeasureSpec.AT_MOST;
//        }else{
//            childWidthMode = widthMode;
//        }
        // 子view 的高的mode
        int childHeightMode = heightMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : heightMode;


        // 指定子view 的宽测量规则
        int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, childWidthMode);
        /*子view 的高的规则 */
        int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, childHeightMode);


        /*测量子view*/

        currentLine = new Line();//创建当前行
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            view.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            // 测量完成
            int childWidth = view.getMeasuredWidth();
            useWidth += childWidth;
            if (useWidth <= width) {
                currentLine.addView(view);//将view 放在当前行
                useWidth += HORIZONTAL_SPACING;// 横向的间隔
                if (useWidth > width) {
                    // 间隔放不进去了
                    //换行
                    newLine();
                }
            } else {
                //换行
                newLine();
                useWidth += childWidth;
                currentLine.addView(view);//将view 放在当前行
            }
        }

        if (!lines.contains(currentLine)) {
            lines.add(currentLine);
        }


        int totalHeight = 0;

        for (int i = 0; i < lines.size(); i++) {
            Line line = lines.get(i);
            totalHeight += line.getHeight();
        }


        totalHeight += VERTICAL_SPACING * (lines.size() - 1);
        // 存储  宽高 flowLayout 自身的宽高
        setMeasuredDimension(width+getPaddingRight() + getPaddingLeft()
                , resolveSize(totalHeight + getPaddingTop() + getPaddingBottom(), heightMeasureSpec));

    }

    /**
     * 换行
     */
    private void newLine() {
        lines.add(currentLine);
        currentLine = new Line();// 创建新行
        useWidth = 0;

    }

    /*放置所有的子view*/
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        l += getPaddingLeft();
        t += getPaddingTop();
        /* 交给 行放置*/
        for (Line line : lines) {
            line.layout(l, t);
            t += VERTICAL_SPACING;
            t += line.getHeight();
        }
    }


    /*放置子view*/

    /**
     * 行对象
     */
    class Line {


        private int height;
        private List<View> children = new ArrayList<>();
        private int lineWidth = 0;


        public void addView(View view) {
            children.add(view);

            if (height < view.getMeasuredHeight())
                height = view.getMeasuredHeight();//  控件的高
            lineWidth += view.getMeasuredWidth();

        }

        /**
         * 获取当前行高
         *
         * @return
         */
        public int getHeight() {
            return height;
        }

        /**
         * 当作行    measure   layout
         *
         * @param left 左边的坐标
         * @param top  上边的坐标
         */
        public void layout(int left, int top) {
            //计算剩余的空间
            int ramaind = getMeasuredWidth() -getPaddingLeft() - getPaddingRight()
                    - lineWidth - HORIZONTAL_SPACING * (children.size() - 1);
            //每个 增加的宽度
            int leave =0;
            if (children.size() != 0)
                leave = ramaind / children.size();


            // 遍历当前行 所有的子view
            for (int i = 0; i < children.size(); i++) {
                View child = children.get(i);
                //放置view 的位置
                child.layout(left, top, left + child.getMeasuredWidth()+leave, top + child.getMeasuredHeight());
                left += HORIZONTAL_SPACING;// 横向的间隔
                left += child.getMeasuredWidth();
                left+= leave;
            }
        }
    }

}
