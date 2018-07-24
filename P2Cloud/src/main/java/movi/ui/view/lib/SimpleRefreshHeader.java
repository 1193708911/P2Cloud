package movi.ui.view.lib;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;


/**
 * Created by wangqi on 2015/12/24.
 */
public class SimpleRefreshHeader implements RefreshHeader {

    private final Context context;
    public   TextView textView;

    public SimpleRefreshHeader(Context context) {
        this.context = context;
    }

    @Override
    public View getView(ViewGroup container) {
        View view = LayoutInflater.from(context).inflate(R.layout.widget_refresh_header, container, false);
        textView = (TextView) view.findViewById(R.id.text);
        return view;
    }

    @Override
    public void onStart(int dragPosition, View refreshHead) {
        textView.setText("滑动刷新");


    }

    @Override
    public void onDragging(float distance, View refreshHead) {
        textView.setText("滑动刷新");
    }

    @Override
    public void onReadyToRelease(View refreshHead) {
        textView.setText("释放刷新");
        LogUtil.d("-----------"+"释放刷新");
    }

    @Override
    public void onRefreshing(View refreshHead) {
        textView.setText("正在刷新");

    }


}
