package movi.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.ctvit.p2cloud.R;
import java.util.ArrayList;
import movi.base.MyBaseAdapter;
import movi.ui.bean.ChannelListBean;

/**
 * Created by chjj on 2016/6/22.
 */
public class ChannelAdapter extends MyBaseAdapter<ChannelListBean.ChannelBean> {

    public ChannelAdapter(Context context, ArrayList<ChannelListBean.ChannelBean> list) {
        super(context,  list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.channel_item, null);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //绑定数据
        ChannelListBean.ChannelBean channelBean = list.get(position);
        viewHolder.txtTitle.setText(channelBean.getTitle());
        return convertView;
    }

    private class ViewHolder {
        private TextView txtTitle;
    }
}