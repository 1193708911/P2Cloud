package movi.ui.adapter;


import android.content.Context;
import android.graphics.Color;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.ArrayList;

import movi.MainApplication;
import movi.base.MyBaseAdapter;
import movi.ui.bean.NewsPicListBean;
import movi.utils.CommonUtils;

/**
 * Created by chjj on 2016/5/12.
 * 合成发布列表
 * 包括剪辑和素材两大模块
 */
public class PicListAdapter1 extends MyBaseAdapter<NewsPicListBean.NewsPicBean> {
private OnClickStepListener  listener;
    public PicListAdapter1(Context context, ArrayList<NewsPicListBean.NewsPicBean> list) {
        super(context,list);
    }

    @Override
    public View getView(final int po, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView==null){
            convertView= LayoutInflater.from(MainApplication.getContext()).inflate(R.layout.piclist_item,null);
            viewHolder=new ViewHolder();
            convertView.setTag(viewHolder);
            viewHolder.imgPic= (ImageView) convertView.findViewById(R.id.imgPic);
            viewHolder.txtStatus= (TextView) convertView.findViewById(R.id.txtStatus);
            viewHolder.txtTitle= (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.ll_preLook= (LinearLayout) convertView.findViewById(R.id.ll_prelook);
            viewHolder.txtTimeLeanth= (TextView) convertView.findViewById(R.id.txtTimeLeanth);
            viewHolder.imgPreLook= (ImageView) convertView.findViewById(R.id.imgPreLook);
            viewHolder.txtPreLook= (TextView) convertView.findViewById(R.id.txtPreLook);

        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.imgPreLook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    //进行跳转传递参数
                    listener.onStep(list.get(po));
                }
            }
        });
        NewsPicListBean.NewsPicBean  picBean=list.get(po);
        viewHolder.txtTitle.setText(picBean.getClip_name());
        x.image().bind(viewHolder.imgPic, picBean.getPhoto());
        viewHolder.txtTimeLeanth.setText(picBean.getDuration() + " /" + picBean.getMp4().size());

        //绑定数据
        StringBuilder  builder=new StringBuilder();
        Spanned[] spanneds=new Spanned[]{};
        viewHolder.txtStatus.clearComposingText();
        viewHolder.txtStatus.setText("");
        for(int  i=0; picBean.getClip_status() != null && i<picBean.getClip_status().size();i++){
            Spanned  spanned= CommonUtils.checkStateColor(String.valueOf(picBean.getClip_status().get(i)));
            viewHolder.txtStatus.append(spanned);
            viewHolder.txtStatus.append("-");
        }
//        viewHolder.txtStatus.append(CommonUtils.checkStateColor(picBean.getClip_status_end()));

        if(picBean.getMp4().equals("0")){
            viewHolder.imgPreLook.setClickable(false);
            viewHolder.imgPreLook.setEnabled(false);
            viewHolder.imgPreLook.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_unprelook));
            viewHolder.txtPreLook.setTextColor(context.getResources().getColor(R.color.dark_white));
        }else{
            viewHolder.imgPreLook.setClickable(true);
            viewHolder.imgPreLook.setEnabled(true);
            viewHolder.imgPreLook.setImageDrawable(context.getResources().getDrawable(R.mipmap.ic_prelook));
            viewHolder.txtPreLook.setTextColor(Color.WHITE);
        }
        return convertView;
    }


    public class ViewHolder{
        private ImageView imgPic;
        private TextView txtStatus,txtTitle;
        private LinearLayout ll_preLook;
        private  TextView txtTimeLeanth;
        private TextView txtPreLook;
        private ImageView imgPreLook;
    }

    //对外提供一个接口用于界面之间的跳转
    public interface  OnClickStepListener{
        public void onStep(Object object);
    }
    public void setOnClickStepLitener(OnClickStepListener  listener){
      this.listener=listener;
    }
}