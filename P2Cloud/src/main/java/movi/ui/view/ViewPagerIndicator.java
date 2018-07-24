package movi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

/**
 * 自定义viewPagerIndicator
 * 计算移动的位置
 * 底部滑动条
 * position  *  tabWidth+positionOffset * tabWidth;
 * @author Administrator
 */
public class ViewPagerIndicator extends View implements OnPageChangeListener {
    private Paint mPaint;
    private int mTabWidht;
    private ViewPager mViewPager;
    private Context mContext;
    private int mTabCount = 2;
    //开始绘制的位置
    private int mStartPosition;
    //开始绘制的结束位置
    private int mEndPositon;
    private OnPagerChangeListener listener;

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initView();
    }

    /**
     * 初始化
     */
    private void initView() {
        mPaint = new Paint();
        mPaint.setStrokeWidth(3);
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.RED);
        mTabWidht = getDisplayMetrics() / mTabCount;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        //开始绘制
        canvas.drawLine(mStartPosition, 0, mEndPositon, 0, mPaint);


    }

    /**
     * 设置viewpager
     *
     * @param viewPager
     */
    public void setViewPager(ViewPager viewPager) {
        this.mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(this);
    }

    /**
     * 设置属性当前的位置
     * 用于切换tab
     *
     * @return
     */
    public void setCurrentPosition(int position) {
        mStartPosition = position * mTabWidht;
        mEndPositon = position * mTabWidht + mTabWidht;
        invalidate();

    }

    public int getDisplayMetrics() {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels;
    }

    @Override
    public void onPageScrollStateChanged(int position) {
        listener.onPageScrollStateChanged(position);
    }

    @Override
    public void onPageScrolled(int position, float positionOffSet, int arg2) {
        mStartPosition = (int) (position * mTabWidht + positionOffSet * mTabWidht);
        mEndPositon = mStartPosition + mTabWidht;
        invalidate();
        listener.onPageScrolled(position, positionOffSet, arg2);


    }

    @Override
    public void onPageSelected(int position) {
        listener.onPageSelected(position);
    }

    /**
     * 对外提供一个接口
     */
    public interface OnPagerChangeListener {
        void onPageScrollStateChanged(int position);

        void onPageScrolled(int position, float positionOffSet, int arg2);

        void onPageSelected(int position);
    }

    /**
     * 设置监听
     */
    public void setOnPagerChangeListener(OnPagerChangeListener listener) {
        this.listener = listener;
    }
}
