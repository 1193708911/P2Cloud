package movi.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import movi.view.photopicker.utils.OtherUtils;


/**
 * Created by chjj on 2016/6/20.
 */
public class MyImageView extends ImageView {
    Context mContext;
    int mWidth;
    int mHeight;
    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        int screenWidth = OtherUtils.getWidthInPx(mContext);
        mWidth = (screenWidth - OtherUtils.dip2px(mContext, 4))/4;
        mHeight = mWidth/2;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mHeight);
    }

}
