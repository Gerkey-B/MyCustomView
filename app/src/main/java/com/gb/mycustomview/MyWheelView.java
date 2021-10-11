package com.gb.mycustomview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


import com.gb.mycustomview.utils.LogUtils;
import com.gb.mycustomview.utils.SizeUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Gerkey
 * Created on 2021/9/22
 * 单个 WheelView ,用来组合的
 */
public class MyWheelView extends ScrollView {
    private static final int DEFAULT_OFF_SET = 1;
    private static final int DEFAULT_DISPLAY_ITEM_COUNT = 3;
    private static final int DEFAULT_SELECTED_INDEX = 1;
    private static final String TAG = "MyWheelView";
    private Context mContext;

    public List<String> getItems() {
        return items;
    }

    public void setItems(List<String> list) {
        if (null == items) {
            items = new ArrayList<String>();
        }
        items.clear();
        items.addAll(list);

        // 空出 offset 个 item,要记得,从这里开始,list 的 length 时原来的长度 + 2倍的 offset ,不这样做的话,
        // 是选不了最边界的值,目的就是做出空白效果
        for (int i = 0; i < offset; i++) {
            items.add(0, "");
            items.add("");
        }
        initData();
    }

    public int getItemSize() {
        return items.size();
    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public void setSelection(int position) {
        final int p = position;
        selectedIndex = p + offset;
        this.post(new Runnable() {
            @Override
            public void run() {
                MyWheelView.this.smoothScrollTo(0, p * itemHeight);
            }
        });
    }

    /**
     * 用来存放 item 的 View
     */
    private LinearLayout mViews;
    private int offset = DEFAULT_OFF_SET;
    private int displayItemCount = DEFAULT_DISPLAY_ITEM_COUNT;
    private int selectedIndex = DEFAULT_SELECTED_INDEX;
    private OnMyWheelViewListener mWheelViewListener;
    private List<String> items;

    public MyWheelView(Context context) {
        this(context, null);
    }

    public MyWheelView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyWheelView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
        initListener();

    }

    private void initListener() {

    }

    private void init(Context context) {
        this.mContext = context;
        setVerticalScrollBarEnabled(false);

        mViews = new LinearLayout(mContext);
        mViews.setOrientation(LinearLayout.VERTICAL);
        this.addView(mViews);

        scrollerTask = new Runnable() {
            @Override
            public void run() {
                int newY = getScrollY();
                if (initialY - newY == 0) {
                    // 用来判断滑动是否超过了 item 的一半,超过了就跳到下一个 item ,否则就保留在当前的 item
                    final int remainder = initialY % itemHeight;

                    //  用来记录已经滑过了多少个 item
                    final int divided = initialY / itemHeight;

                    if (remainder == 0) {
                        selectedIndex = divided + offset;
                        onSelectedCallBack();
                    } else {
                        if (remainder > itemHeight / 2) {
                            MyWheelView.this.post(() -> {
                                MyWheelView.this.smoothScrollTo(0, initialY - remainder + itemHeight);
                                selectedIndex = divided + offset + 1;
                                onSelectedCallBack();
                            });
                        } else {
                            MyWheelView.this.post(() -> {
                                MyWheelView.this.smoothScrollTo(0, initialY - remainder);
                                selectedIndex = divided + offset;
                                onSelectedCallBack();
                            });
                        }
                    }
                } else {
                    initialY = getScrollY();
                    MyWheelView.this.postDelayed(scrollerTask, newCheck);
                    onSelectedCallBack();
                }
            }
        };
        // 默认初始不滑动时执行一次回调
        onSelectedCallBack();
    }

    private void initData() {
        // 确保 displayItemCount 是奇数
        displayItemCount = offset * 2 + 1;

        mViews.removeAllViews();

        for (String item : items) {
            mViews.addView(createView(item));
        }

        refreshItemView(0);
    }

    /**
     * 现在有两种删除思路
     * 1.是遍历数据,将不用的数据的 visibility 设置为 GONE
     * 2.直接删除这个 view
     * ----------------------
     * 因为我是直接创建出数据 View 的,然后把它加入到 mViews 中,并且数据是复用的,如果因为某个月份不符合就盲目删去,那么
     * 在其他的月份当中会造成月份的消失,并且因为复用性的关系,很难得知删除的是哪个,要添加回来的又是哪个,
     * 相对应的解决办法:
     * 1.采用第一种的办法,就是当点击某个月的时候,进到里面来,判断大小月,在判断是否为二月,
     * 之后将需要的数据设置为 VISIBLE , 不需要的数据设置为 GONE.
     * 2.暂时想不出
     *
     * @param index
     */
    public void removeItemViewAt(int index) {
        items.remove(index + offset);
        mViews.removeViewAt(index + offset);
        invalidate();
    }


    int initialY;

    Runnable scrollerTask;
    int newCheck = 50;

    public void startScrollerTask() {
        initialY = getScrollY();
        this.postDelayed(scrollerTask, newCheck);
    }

    int itemHeight = 0;

    private TextView createView(String item) {
        TextView tv = new TextView(mContext);
        tv.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        tv.setSingleLine(true);
        // textSize 后期可能要进行适配操作
        tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        tv.setText(item);
        tv.setGravity(Gravity.CENTER);
        // padding 也应该进行适配操作,或者将选择权交给定制的时候
        int padding = SizeUtils.dp2px(mContext, 10f);
        tv.setPadding(padding, padding, padding, padding);
        if (itemHeight == 0) {
            itemHeight = getViewMeasuredHeight(tv);
            LogUtils.d(this, "itemHeight --- > " + itemHeight);
            mViews.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, itemHeight * displayItemCount, Gravity.CENTER_HORIZONTAL));
            mViews.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) this.getLayoutParams();
            this.setLayoutParams(new LinearLayout.LayoutParams(lp.width, itemHeight * displayItemCount));
        }
        return tv;
    }

    private void refreshItemView(int y) {
        int position = y / itemHeight + offset;
        int remainder = y % itemHeight;
        int divided = y / itemHeight;

        if (remainder == 0) {
            position = divided + offset;
        } else {
            if (remainder > itemHeight / 2) {
                position = divided + offset + 1;
            }
        }

        int childSize = mViews.getChildCount();
        for (int i = 0; i < childSize; i++) {
            TextView itemView = (TextView) mViews.getChildAt(i);
            if (itemView == null) {
                return;
            }
            if (position == i) {
                itemView.setTextColor(mViews.getResources().getColor(R.color.green));
            } else {
                itemView.setTextColor(mViews.getResources().getColor(R.color.color_999999));
            }
        }
        onSelectedCallBack();
    }

    /**
     * 获取选中区域的边界
     */
    int[] selectedAreaBorder;

    private int[] obtainSelectedAreaBorder() {
        if (null == selectedAreaBorder) {
            selectedAreaBorder = new int[2];
            selectedAreaBorder[0] = itemHeight * offset;
            selectedAreaBorder[1] = itemHeight * (offset + 1);
        }
        return selectedAreaBorder;
    }

    private int getViewMeasuredHeight(View view) {
        int width = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        view.measure(width, expandSpec);
        return view.getMeasuredHeight();
    }

    private void onSelectedCallBack() {
        if (mWheelViewListener != null) {
            mWheelViewListener.onSelected(selectedIndex, items.get(selectedIndex));
        }
    }

    int viewWidth;
    Paint paint;

    @Override
    public void setBackgroundDrawable(Drawable background) {

        if (viewWidth == 0) {
            viewWidth = mViews.getWidth();
            Log.d(TAG, "viewWidth: " + viewWidth);
        }

        if (null == paint) {
            paint = new Paint();
            paint.setColor(Color.parseColor("#83cde6"));
            paint.setStrokeWidth(SizeUtils.dp2px(mContext, 1f));
        }

        background = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawLine(0, obtainSelectedAreaBorder()[0], viewWidth, obtainSelectedAreaBorder()[0], paint);
                canvas.drawLine(0, obtainSelectedAreaBorder()[1], viewWidth, obtainSelectedAreaBorder()[1], paint);
            }

            @Override
            public void setAlpha(int alpha) {

            }

            @Override
            public void setColorFilter(ColorFilter cf) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.UNKNOWN;
            }
        };
        super.setBackgroundDrawable(background);

    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        refreshItemView(t);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        setBackgroundDrawable(null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            // 每次开始滑动的时候,就开始进行 Runnable
            startScrollerTask();
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public void fling(int velocityY) {
        super.fling(velocityY / 2);
    }


    public void setOnMyWheelViewListener(OnMyWheelViewListener listener) {
        this.mWheelViewListener = listener;
    }

    public static final int BIG_DAY = 31;
    public static final int SMALL_DAY = 30;
    public static final int LEAP_FEBRUARY = 29;
    public static final int NORMAL_FEBRUARY = 28;
    public static final int FEBRUARY = 2;

    private boolean isBigMonth(int month) {
        return month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12;
    }

    public void updateDays(int selectedMonth, boolean isLeapYear) {
        if (selectedMonth == FEBRUARY) {
            if (isLeapYear) {
                updateVisibilities(LEAP_FEBRUARY);
            } else {
                updateVisibilities(NORMAL_FEBRUARY);
            }
        } else {
            if (isBigMonth(selectedMonth)) {
                updateVisibilities(BIG_DAY);
            } else {
                updateVisibilities(SMALL_DAY);
            }
        }
    }

    /**
     * 对每年的每月进行适配,不需要的数据就将他设置为 GONE,需要的就又将他们设置为 VISIBLE
     *
     * @param days 当前月份拥有的天数
     */
    private void updateVisibilities(int days) {
        int itemSize = getItemSize();
        days += offset;
        if (days == itemSize - offset) {
            for (int i = 0; i < itemSize; ++i) {
                mViews.getChildAt(i).setVisibility(VISIBLE);
            }
        } else {
            for (int i = 0; i < days; i++) {
                mViews.getChildAt(i).setVisibility(VISIBLE);
            }
            for (int i = days; i < itemSize - offset; ++i) {
                mViews.getChildAt(i).setVisibility(GONE);
            }
        }
        invalidate();
    }

    public interface OnMyWheelViewListener {
        void onSelected(int index, String item);
    }
}

