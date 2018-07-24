package movi.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import movi.MainApplication;
import movi.ui.fragment.CloudFragment;
import movi.utils.Constants;
import movi.utils.ToastUtils;

public class MonthDateView extends View {
    private OnCalanderItemClickListener listener;
    private boolean isFirst = true;
    private static final int NUM_COLUMNS = 7;
    private static final int NUM_ROWS = 6;
    private Paint mPaint;
    private int mDayColor = Color.parseColor("#000000");
    private int mSelectDayColor = Color.parseColor("#ffffff");
    private int mSelectBGColor = Color.parseColor("#1FC2F3");
    private int mCurrentColor = Color.parseColor("#ff0000");
    private int mCurrYear, mCurrMonth, mCurrDay;
    private int mSelYear, mSelMonth, mSelDay;
    private int mColumnSize, mRowSize;
    private DisplayMetrics mDisplayMetrics;
    private int mDaySize = 18;
    private TextView tv_date, tv_week;
    private int weekRow;
    private int[][] daysString;
    private int mCircleRadius = 6;
    private DateClick dateClick;
    private int mCircleColor = Color.parseColor("#ff0000");
    private List<Integer> daysHasThingList;

    private boolean isOnclick=false;

    private List<String> clickList = new ArrayList<String>();
    //缓存当前点击的哪一个条目
    private  int onclickYear;
    private int onclickMonth;
    private int onclickDay;

    private  boolean  isOnClick=true;

    public MonthDateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDisplayMetrics = getResources().getDisplayMetrics();
        Calendar calendar = Calendar.getInstance();
        mPaint = new Paint();
        mCurrYear = calendar.get(Calendar.YEAR);
        mCurrMonth = calendar.get(Calendar.MONTH);
        mCurrDay = calendar.get(Calendar.DATE);
        setSelectYearMonth(mCurrYear, mCurrMonth, mCurrDay);
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        daysString = new int[6][7];
        mPaint.setTextSize(mDaySize * mDisplayMetrics.scaledDensity);
        String dayString;
        int mMonthDays = DateUtils.getMonthDays(mSelYear, mSelMonth);
        int weekNumber = DateUtils.getFirstDayWeek(mSelYear, mSelMonth);
        Log.d("DateView", "DateView:" + mSelMonth + "月1号周" + weekNumber);
        for (int day = 0; day < mMonthDays; day++) {
            dayString = (day + 1) + "";
            int column = (day + weekNumber - 1) % 7;
            int row = (day + weekNumber - 1) / 7;
            daysString[row][column] = day + 1;
            int startX = (int) (mColumnSize * column + (mColumnSize - mPaint.measureText(dayString)) / 2);
            int startY = (int) (mRowSize * row + mRowSize / 2 - (mPaint.ascent() + mPaint.descent()) / 2);
            if (dayString.equals(mSelDay + "")) {
                //绘制背景色矩形
                int startRecX = mColumnSize * column;
                int startRecY = mRowSize * row;
                int endRecX = startRecX + mColumnSize;
                int endRecY = startRecY + mRowSize;
                mPaint.setColor(mSelectBGColor);
                //测试 如果时间小于当前时间能点击
                int myMonth= mSelMonth+1;
                String dateString = mSelYear + "年" + myMonth + "月" + mSelDay + "日";
                //进行判断
                 String str=Constants.DATE_FORMAT_CN.format(Calendar.getInstance().getTime().getTime());
                try {
                    if (Constants.DATE_FORMAT_CN.parse(dateString).getTime() <Calendar.getInstance().getTime().getTime()) {

                        if (listener != null) {
                            if(isOnClick){
                            listener.onClickOnDate(String.valueOf(onclickYear + "年" + onclickMonth + "月" + onclickDay + "日"));
                            }
                              //每一次都会调用一次
                            if(isOnClick){
                                //如果是点击的时候才进行赋值否则不处理
                                setOnclickTimer(mSelYear, mSelMonth, mSelDay);
                            }

                        }
                        //上一次的点击的时间
                        if(mSelYear==onclickYear&&mSelMonth==onclickMonth&&mSelDay==onclickDay){
                            canvas.drawRect(startRecX, startRecY, endRecX, endRecY, mPaint);
                        }
//                        if(mSelYear== CloudFragment.onClickYear&&mSelMonth==CloudFragment.onClickMonth&&mSelDay==CloudFragment.onClickDay){
//                            canvas.drawRect(startRecX, startRecY, endRecX, endRecY, mPaint);
//                        }

                    } else {
                        if (!isFirst) {
                            ToastUtils.showToast(MainApplication.getContext(), "选择日期不能大于当前日期");
                        }
                        isFirst = false;

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            String timer=Constants.DATE_FORMAT_CN.format(new Date());
            String  ColorTime=mSelYear+"年"+(mSelMonth+1)+"月"+mSelDay+"日";
            //测试
            if (dayString.equals(mCurrDay + "")  && mCurrMonth == mSelMonth) {
                //正常月，选中其他日期，则今日为红色
                //&& mCurrDay != mSelDay
                if(isFirst){
                    mPaint.setColor(mCurrentColor);
                    isFirst=false;
                }else if(mCurrDay != mSelDay){
                    mPaint.setColor(mCurrentColor);
                    isFirst=false;
                }else{
                    mPaint.setColor(Color.BLACK);
                    isFirst=false;
                }

            } else {
                mPaint.setColor(Color.BLACK);
            }

            canvas.drawText(dayString, startX, startY, mPaint);

            if (tv_date != null) {
                tv_date.setText(mSelYear + "年" + (mSelMonth + 1) + "月");
            }


        }
    }
    //设置点击的事件
    public void setOnclickTimer(int year,int month,int day){
        this.onclickYear=year;
        this.onclickMonth=month;
        this.onclickDay=day;

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    private int downX = 0, downY = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventCode = event.getAction();
        switch (eventCode) {
            case MotionEvent.ACTION_DOWN:
                downX = (int) event.getX();
                downY = (int) event.getY();
                isOnClick=true;
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int upX = (int) event.getX();
                int upY = (int) event.getY();
                if (Math.abs(upX - downX) < 10 && Math.abs(upY - downY) < 10) {//点击事件
                    performClick();
                    doClickAction((upX + downX) / 2, (upY + downY) / 2);

                }
                break;
        }
        return true;
    }

    /**
     * 初始化列宽行高
     */
    private void initSize() {
        mColumnSize = getWidth() / NUM_COLUMNS;
        mRowSize = getHeight() / NUM_ROWS;
    }

    /**
     * 设置年月
     *
     * @param year
     * @param month
     */
    private void setSelectYearMonth(int year, int month, int day) {
            mSelYear = year;
            mSelMonth = month ;
            mSelDay=day;
    }

    /**
     * 执行点击事件
     *
     * @param x
     * @param y
     */
    private void doClickAction(int x, int y) {
        int row = y / mRowSize;
        int column = x / mColumnSize;
        //		clickList.add(arg0)
        setSelectYearMonth(mSelYear, mSelMonth, daysString[row][column]);
        clickList.add(String.valueOf(daysString[row][column]));
        isFirst = false;
        invalidate();
        //执行activity发送过来的点击处理事件
        if (dateClick != null) {
            dateClick.onClickOnDate();
        }
    }

    /**
     * 左点击，日历向后翻页
     */
    public void onLeftClick() {
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        isOnClick=false;
        if (month == 0) {//若果是1月份，则变成12月份
            year = mSelYear - 1;
            month = 11;
        } else if (DateUtils.getMonthDays(year, month) == day) {
            //如果当前日期为该月最后一点，当向前推的时候，就需要改变选中的日期
            month = month - 1;
            day = DateUtils.getMonthDays(year, month);
        } else {
            month = month - 1;
        }
        isFirst = true;
        setSelectYearMonth(year, month, day);

        invalidate();
    }

    /**
     * 右点击，日历向前翻页
     */
    public void onRightClick() {
        int year = mSelYear;
        int month = mSelMonth;
        int day = mSelDay;
        isOnClick=false;
        if (month == 11) {//若果是12月份，则变成1月份
            year = mSelYear + 1;
            month = 0;
        } else if (DateUtils.getMonthDays(year, month) == day) {
            //如果当前日期为该月最后一点，当向前推的时候，就需要改变选中的日期
            month = month + 1;
            day = DateUtils.getMonthDays(year, month);
        } else {
            month = month + 1;
        }
        isFirst = true;
        setSelectYearMonth(year, month, day);
        invalidate();
    }

    /**
     * 获取选择的年份
     *
     * @return
     */
    public int getmSelYear() {
        return mSelYear;
    }

    /**
     * 获取选择的月份
     *
     * @return
     */
    public int getmSelMonth() {
        return mSelMonth + 1;
    }

    /**
     * 获取选择的日期
     *
     * @param
     */
    public int getmSelDay() {
        return this.mSelDay;
    }

    /**
     * 普通日期的字体颜色，默认黑色
     *
     * @param mDayColor
     */
    public void setmDayColor(int mDayColor) {
        this.mDayColor = mDayColor;
    }

    /**
     * 选择日期的颜色，默认为白色
     *
     * @param mSelectDayColor
     */
    public void setmSelectDayColor(int mSelectDayColor) {
        this.mSelectDayColor = mSelectDayColor;
    }

    /**
     * 选中日期的背景颜色，默认蓝色
     *
     * @param mSelectBGColor
     */
    public void setmSelectBGColor(int mSelectBGColor) {
        this.mSelectBGColor = mSelectBGColor;
    }

    /**
     * 当前日期不是选中的颜色，默认红色
     *
     * @param mCurrentColor
     */
    public void setmCurrentColor(int mCurrentColor) {
        this.mCurrentColor = mCurrentColor;
    }

    /**
     * 日期的大小，默认18sp
     *
     * @param mDaySize
     */
    public void setmDaySize(int mDaySize) {
        this.mDaySize = mDaySize;
    }

    /**
     * 设置显示当前日期的控件
     *
     * @param tv_date 显示日期
     * @param
     */
    public void setTextView(TextView tv_date) {
        this.tv_date = tv_date;
        invalidate();
    }



    /***
     * 设置圆圈的半径，默认为6
     *
     * @param mCircleRadius
     */
    public void setmCircleRadius(int mCircleRadius) {
        this.mCircleRadius = mCircleRadius;
    }

    /**
     * 设置圆圈的半径
     *
     * @param mCircleColor
     */
    public void setmCircleColor(int mCircleColor) {
        this.mCircleColor = mCircleColor;
    }

    /**
     * 设置日期的点击回调事件
     *
     * @author shiwei.deng
     */
    public interface DateClick {
        public void onClickOnDate();
    }

    /**
     * 设置日期点击事件
     *
     * @param dateClick
     */
    public void setDateClick(DateClick dateClick) {
        this.dateClick = dateClick;
    }

    /**
     * 跳转至今天
     */
    public void setTodayToView() {
        setSelectYearMonth(mCurrYear, mCurrMonth, mCurrDay);
        invalidate();
    }

    //回调监听
    public interface OnCalanderItemClickListener {
        public void onClickOnDate(String date);
    }

    public void setOnCalanderItemClickListener(OnCalanderItemClickListener lisener) {
        this.listener = lisener;
    }
}
