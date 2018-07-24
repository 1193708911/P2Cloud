package movi.lib.camera;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;

public class MyRecyclerView extends RecyclerView {
	/**
	 * 记录当前第一个View
	 */
	private View mCurrentView;

	private OnItemScrollChangeListener mItemScrollChangeListener;

	public void setOnItemScrollChangeListener(OnItemScrollChangeListener mItemScrollChangeListener) {
		this.mItemScrollChangeListener = mItemScrollChangeListener;
	}

	public interface OnItemScrollChangeListener {
		void onChange(View view, int position);
		void onChangeState(int state);
	}

	public MyRecyclerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.addOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
				if (mItemScrollChangeListener != null) {
					mItemScrollChangeListener.onChangeState(newState);
				}
			}

			@Override
			public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
				super.onScrolled(recyclerView, dx, dy);
				mCurrentView = getChildAt(0);
				if (mItemScrollChangeListener != null && mCurrentView != null) {
					mItemScrollChangeListener.onChange(mCurrentView, getChildPosition(mCurrentView));
				}
			}
		});
	}


	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}



}
