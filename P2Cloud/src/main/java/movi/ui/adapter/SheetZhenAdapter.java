package movi.ui.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.ctvit.p2cloud.R;
import java.util.ArrayList;
import movi.base.MyBaseAdapter;


/**
 * Created by chjj on 2016/5/12.
 * 处理帧图片列表数据
 */
public  class SheetZhenAdapter extends MyBaseAdapter<String> {

    public SheetZhenAdapter(Context context, ArrayList<String> list) {
        super(context, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder  viewHolder;
        if(convertView==null){
            //暂时持平
            convertView= LayoutInflater.from(context).inflate(R.layout.alert_dialog,null);
        }
        return convertView;
    }
    //缓存
    class ViewHolder{
        private ImageView  iv_zhenBit;
        //图片名称
        private TextView tv_title;
    }
}
