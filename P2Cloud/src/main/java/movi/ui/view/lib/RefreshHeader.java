package movi.ui.view.lib;

import android.view.View;
import android.view.ViewGroup;

/**
 * Created by wangqi on 2015/12/24.
 */
public interface RefreshHeader{

    /**
     * @param dragPosition  HorizontalRefreshLayout.START or HorizontalRefreshLayout.END
     */
    void onStart(int dragPosition, View refreshHead);

    /**
     * @param distance
     */
    void onDragging(float distance, View refreshHead);

    void onReadyToRelease(View refreshHead);

    View getView(ViewGroup container);

    void onRefreshing(View refreshHead);
}