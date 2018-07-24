package movi.ui.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;

import movi.ui.adapter.CutBottomAdapter;

public class DragGridView extends GridView {
    private static final int DRAG_IMG_SHOW = 1;
    private static final int DRAG_IMG_NOT_SHOW = 0;
    private static final String LOG_TAG = "DragGridView";
    private static final float AMP_FACTOR = 1.2f;

    private ImageView dragImageView;
    private WindowManager.LayoutParams dragImageViewParams;
    private WindowManager windowManager;
    private boolean isViewOnDrag = false;

    /**previous dragged over position*/
    private int preDraggedOverPositon = AdapterView.INVALID_POSITION;
    private int downRawX;
    private int downRawY;
    private OnItemLongClickListener onLongClickListener = new OnItemLongClickListener(){

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            preDraggedOverPositon = position;

            view.destroyDrawingCache();
            view.setDrawingCacheEnabled(true);
            Bitmap dragBitmap = Bitmap.createBitmap(view.getDrawingCache());

            dragImageViewParams.gravity = Gravity.TOP | Gravity.LEFT;
            dragImageViewParams.width = (int)(AMP_FACTOR*dragBitmap.getWidth());
            dragImageViewParams.height = (int)(AMP_FACTOR*dragBitmap.getHeight());
            dragImageViewParams.x = (downRawX - dragImageViewParams.width/2);
            dragImageViewParams.y = (downRawY - dragImageViewParams.height/2);
            dragImageViewParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                                        | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                                        | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
            dragImageViewParams.format = PixelFormat.TRANSLUCENT;
            dragImageViewParams.windowAnimations = 0;

            if((int)dragImageView.getTag() == DRAG_IMG_SHOW) {
                windowManager.removeView(dragImageView);
                dragImageView.setTag(DRAG_IMG_NOT_SHOW);
            }

            dragImageView.setImageBitmap(dragBitmap);

            windowManager.addView(dragImageView, dragImageViewParams);
            dragImageView.setTag(DRAG_IMG_SHOW);
            isViewOnDrag = true;

            ((CutBottomAdapter)getAdapter()).hideView(position);
            return true;
        }
    };

    public DragGridView(Context context) {
        super(context);
        initView();
    }

    public DragGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public DragGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    public void initView() {
        setOnItemLongClickListener(onLongClickListener);
        dragImageView = new ImageView(getContext());
        dragImageView.setTag(DRAG_IMG_NOT_SHOW);
        dragImageViewParams = new WindowManager.LayoutParams();
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }


    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            downRawX = (int)ev.getRawX();
            downRawY = (int)ev.getRawY();
        }
        else if((ev.getAction() == MotionEvent.ACTION_MOVE) && (isViewOnDrag == true)) {
            Log.i(LOG_TAG, "" + ev.getRawX() + " " + ev.getRawY());
            dragImageViewParams.x = (int)(ev.getRawX() - dragImageView.getWidth()/2);
            dragImageViewParams.y = (int)(ev.getRawY() - dragImageView.getHeight()/2);
            windowManager.updateViewLayout(dragImageView, dragImageViewParams);
            int currDraggedPosition = pointToPosition((int)ev.getX(), (int)ev.getY());
            if((currDraggedPosition != AdapterView.INVALID_POSITION) && (currDraggedPosition != preDraggedOverPositon)) {
                ((CutBottomAdapter)getAdapter()).swapView(preDraggedOverPositon, currDraggedPosition);
                preDraggedOverPositon = currDraggedPosition;
            }
        }
        else if((ev.getAction() == MotionEvent.ACTION_UP) && (isViewOnDrag == true)) {
            ((CutBottomAdapter)getAdapter()).showHideView();
            if((int)dragImageView.getTag() == DRAG_IMG_SHOW) {
                windowManager.removeView(dragImageView);
                dragImageView.setTag(DRAG_IMG_NOT_SHOW);
            }
            isViewOnDrag = false;
        }
        return super.onTouchEvent(ev);
    }
}