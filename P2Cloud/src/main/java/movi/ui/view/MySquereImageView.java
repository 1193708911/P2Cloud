package movi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by admin on 2017/3/8.
 */

public class MySquereImageView extends ImageView {
    Context mContext;
    int mWidth;
    int mHeight;
    public MySquereImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        int screenWidth = movi.view.photopicker.utils.OtherUtils.getWidthInPx(mContext);
        mWidth = (screenWidth - movi.view.photopicker.utils.OtherUtils.dip2px(mContext, 4))/4;
    }




    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mWidth, mWidth/2);
    }
}
