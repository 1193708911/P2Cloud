package movi.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.x;

import java.util.ArrayList;

import movi.base.MyBaseAdapter;
import movi.ui.bean.NewsCutBottomBean;
import movi.utils.TimeUtils;

/**
 * Created by chjj on 2016/6/22.
 */
public class CutBottomAdapter extends MyBaseAdapter<NewsCutBottomBean> {
    private OnListSizeChangeListener listener;
    private int hidePosition = AdapterView.INVALID_POSITION;
    public CutBottomAdapter(Context context, ArrayList<NewsCutBottomBean> list) {
        super(context, list);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.news_cutbtm_lyout, null);
            viewHolder.txtTimeStart = (TextView) convertView.findViewById(R.id.txtTimeStart);
            viewHolder.iv_cut = (ImageView) convertView.findViewById(R.id.iv_cut);
            viewHolder.layout = (RelativeLayout) convertView.findViewById(R.id.ll_layout);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //绑定数据
        NewsCutBottomBean bottomBean = list.get(position);
        x.image().bind(viewHolder.iv_cut, bottomBean.getThumbnail_src());
        viewHolder.txtTimeStart.setText(bottomBean.getDuration_time_code());
       /* if (position == NewsCutFragment.backPosition) {
            viewHolder.layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rec_redshap));
        } else {
            viewHolder.layout.setBackgroundDrawable(null);
        }*/

        if(bottomBean.isChecked()){
            viewHolder.layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rec_redshap));
        }else {
            viewHolder.layout.setBackgroundDrawable(null);
        }
        /**
         * 循环遍历
         */
        if (listener != null) {
            long allTimeLeanth = 0;
            int j = 0;
            for (int i = 0; i < list.size(); i++) {
                allTimeLeanth = allTimeLeanth + Long.parseLong(list.get(i).getOut_position()) - Long.parseLong(list.get(i).getIn_position());
                j = i + 1;
            }
            //开始计算相关的帧
            String allTime = TimeUtils.formatDuration(allTimeLeanth);
            listener.onSizeChangeListener(allTime, list.size() + "");
        }


        //hide时隐藏Text
        if (position != hidePosition) {
            viewHolder.txtTimeStart.setText(bottomBean.getDuration_time_code());
        } else {
            viewHolder.txtTimeStart.setText("");
        }
        viewHolder.txtTimeStart.setId(position);
        return convertView;
    }

    private class ViewHolder {
        private TextView txtTimeStart;
        private ImageView iv_cut;
        private RelativeLayout layout;
    }

    /**
     * 提供一个接口计算当前adapter中的集合数量
     */
    public interface OnListSizeChangeListener {
        public void onSizeChangeListener(String timeAllLeanth, String all);
    }

    public void setOnSizeChangeListener(OnListSizeChangeListener listener) {
        this.listener = listener;
    }
    public void hideView(int pos) {
        hidePosition = pos;
        notifyDataSetChanged();
    }

    public void showHideView() {
        hidePosition = AdapterView.INVALID_POSITION;
        notifyDataSetChanged();
    }

    public void removeView(int pos) {
        list.remove(pos);
        notifyDataSetChanged();
    }

    //更新拖动时的gridView
    public void swapView(int draggedPos, int destPos) {
        //从前向后拖动，其他item依次前移
        if (draggedPos < destPos) {
            list.add(destPos + 1, (NewsCutBottomBean) getItem(draggedPos));
            list.remove(draggedPos);
        }
        //从后向前拖动，其他item依次后移
        else if (draggedPos > destPos) {
            list.add(destPos, (NewsCutBottomBean) getItem(draggedPos));
            list.remove(draggedPos + 1);
        }
        hidePosition = destPos;
        notifyDataSetChanged();
    }
}