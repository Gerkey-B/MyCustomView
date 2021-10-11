package com.gb.mycustomview;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.gb.mycustomview.utils.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * @author Gerkey
 * Created on 2021/9/22
 */
public class MyDatePicker extends LinearLayout {

    /**
     * 每一个滑动区块能够显示的item数目
     */
    private static final int DEFAULT_DISPLAY_ITEM_COUNT = 5;

    /**
     * 能否点击(能否聚焦)
     */
    private static final boolean DEFAULT_ENABLED = false;
    public static final int MAX_YEAR = 20;
    public static final int START_MONTH = 1;
    public static final int END_MONTH = 12;
    public static final int START_DAY = 1;
    public static final int END_DAY = 31;
    public static final int START_HOUR = 0;
    public static final int END_HOUR = 24;
    public static final int START_MINUTE = 0;
    public static final int END_MINUTE = 60;
    private List<String> yearList = new ArrayList<>();
    private List<String> monthList = new ArrayList<>();
    private List<String> dayList = new ArrayList<>();
    private List<String> hourList = new ArrayList<>();
    private List<String> minuteList = new ArrayList<>();
    private int mDisplayItemCount;
    private boolean mEnabled;
    private int mOffset;
    private MyWheelView mYearWheel;
    private MyWheelView mMonthWheel;
    private MyWheelView mHourWheel;
    private MyWheelView mDayWheel;
    private MyWheelView mMinuteWheel;
    private int mCurYear;
    private int mCurMonth;
    private int mCurDay;
    private int mCurHour;
    private int mCurMinute;
    private TextView mTitle;
    private Button mCancelBtn;
    private Button mConfirmBtn;

    public MyDatePicker(Context context) {
        this(context, null);
    }

    public MyDatePicker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyDatePicker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initTypeArray(context, attrs);
        initView();
        initData();
        initListener();
    }

    private void initListener() {
        mYearWheel.setOnMyWheelViewListener((index, item) -> {
            mSelectedYear = StringUtils.get(item);
            isSelectedYearLeapYear = isLeapYear(mSelectedYear);
            updateTitle();
        });

        mMonthWheel.setOnMyWheelViewListener((index, item) -> {
            mSelectedMonth = StringUtils.get(item);
            isBigMonth = isBigMonth(mSelectedMonth);
            isFebruary = mSelectedMonth == 2;
            mDayWheel.updateDays(mSelectedMonth, isSelectedYearLeapYear);
            updateTitle();

        });

        mDayWheel.setOnMyWheelViewListener((index, item) -> {
            mSelectedDay = StringUtils.get(item);
            updateTitle();
        });

        mHourWheel.setOnMyWheelViewListener((index, item) -> mSelectedHour = StringUtils.get(item));

        mMinuteWheel.setOnMyWheelViewListener((index, item) -> mSelectedMinute = StringUtils.get(item));
    }

    private void initData() {

        yearList.clear();
        monthList.clear();
        dayList.clear();
        hourList.clear();
        minuteList.clear();


        // 拿到系统当前的时间
        TimeZone.setDefault(TimeZone.getTimeZone("GMT + 8"));
        Calendar calendar = Calendar.getInstance();

        mCurYear = calendar.get(Calendar.YEAR);
        // 因为 月份和日期没有第 0 天,所以要 - 1 来适配下
        mCurMonth = calendar.get(Calendar.MONTH);
        mCurDay = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        mCurHour = calendar.get(Calendar.HOUR_OF_DAY);
        mCurMinute = calendar.get(Calendar.MINUTE);


        for (int i = mCurYear; i < mCurYear + MAX_YEAR; ++i) {
            yearList.add(i + "年");
        }
        for (int i = START_MONTH; i <= END_MONTH; ++i) {
            monthList.add(i + "月");
        }
        for (int i = START_DAY; i <= END_DAY; ++i) {
            dayList.add(i + "日");
        }
        for (int i = START_HOUR; i < END_HOUR; ++i) {
            hourList.add(i + "时");
        }
        for (int i = START_MINUTE; i < END_MINUTE; ++i) {
            minuteList.add(i + "分");
        }

        settingsForWheelViews(mYearWheel, yearList, 0);
        settingsForWheelViews(mMonthWheel, monthList, mCurMonth);
        settingsForWheelViews(mDayWheel, dayList, mCurDay);
        settingsForWheelViews(mHourWheel, hourList, mCurHour);
        settingsForWheelViews(mMinuteWheel, minuteList, mCurMinute);

    }

    private void initView() {
        mYearWheel = findViewById(R.id.date_picker_year);
        mMonthWheel = findViewById(R.id.date_picker_month);
        mDayWheel = findViewById(R.id.date_picker_day);
        mHourWheel = findViewById(R.id.date_picker_hour);
        mMinuteWheel = findViewById(R.id.date_picker_minute);
        mTitle = findViewById(R.id.date_picker_title);
        mCancelBtn = findViewById(R.id.date_picker_cancel_button);
        mConfirmBtn = findViewById(R.id.date_picker_confirm_button);
    }

    private void initTypeArray(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.getResources().obtainAttributes(attrs, R.styleable.MyDatePicker);

        mDisplayItemCount = typedArray.getInteger(R.styleable.MyDatePicker_DisplayItemsCount, DEFAULT_DISPLAY_ITEM_COUNT);
        mEnabled = typedArray.getBoolean(R.styleable.MyDatePicker_Enabled, DEFAULT_ENABLED);

        // 处理 offSet 和 displayItemCount
        // 如果 displayItemCount 为偶数,则让它进一,保证上下对齐

        mOffset = mDisplayItemCount / 2;

        //TODO: 先默认 enabled 都是 false,防止复杂化,后面再优化为 EditText
        typedArray.recycle();


        setOrientation(VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.item_date_picker_title, this, true);
        LayoutInflater.from(context).inflate(R.layout.item_date_picker_content, this, true);
        LayoutInflater.from(context).inflate(R.layout.item_date_picker_button, this, true);
    }

    private int mSelectedYear;
    private int mSelectedMonth;
    private int mSelectedDay;
    private int mSelectedHour;

    private int mSelectedMinute;

    private boolean isSelectedYearLeapYear;

    private boolean isBigMonth;

    private boolean isFebruary;

    private void updateTitle() {
        if (mSelectedYear == 0) {
            mTitle.setText(String.format("%d年%d月%d日", mCurYear, mSelectedMonth, mSelectedDay));
        } else {
            mTitle.setText(String.format("%d年%d月%d日", mSelectedYear, mSelectedMonth, mSelectedDay));
        }
    }

    private void settingsForWheelViews(MyWheelView wheelView, List<String> list, int selectedIndex) {
        wheelView.setOffset(mOffset);
        wheelView.setItems(list);
        wheelView.setSelection(selectedIndex);
        LayoutParams layoutParams = (LayoutParams) wheelView.getLayoutParams();
        layoutParams.weight = 1;
        wheelView.setLayoutParams(layoutParams);
    }

    public static final int HUNDRED = 100;

    private boolean isLeapYear(int year) {
        if (year % HUNDRED == 0) {
            return year % HUNDRED * 4 == 0;
        } else {
            return year % 4 == 0;
        }
    }

    private boolean isBigMonth(int month) {
        return month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12;
    }
}

