package movi.ui.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
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
import java.util.HashMap;
import java.util.Map;

import movi.ui.bean.HeadPicList;
import movi.ui.fragment.NewManuFragment;
import movi.ui.fragment.NewsCutFragment;
import movi.ui.fragment.NewsSheetFragment;
import movi.view.HorirontalHeaderLayout;

import static android.R.attr.fragment;


/**
 * Created by chjj on 2016/5/12.
 */
public class CutListAdapter extends RecyclerView.Adapter<CutListAdapter.ViewHolder> implements View.OnClickListener {
    private Context context;
    private OnItemClickListener listener;
    private ArrayList<HeadPicList.HeadPic> data;
    public static Map<Integer, Boolean> paramMap = new HashMap<>();

    public static Map<Integer, Boolean> getParamMap() {
        return paramMap;
    }


    public CutListAdapter(Context context, ArrayList<HeadPicList.HeadPic> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cutlist_item, null);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        HeadPicList.HeadPic pic = data.get(position);
        if (pic.getPlaylistsrc() != null) {
            x.image().bind(holder.iv_cut_bac, pic.getPlaylistsrc());
        }
        if (position==HorirontalHeaderLayout.currentPosition) {
            holder.ll_layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rec_read_bac));
        } else {
            holder.ll_layout.setBackgroundDrawable(null);
        }

        holder.tv_title.setText(pic.getTitle());
        holder.tv_time.setText(pic.getDuration_time_code_all());
        holder.itemView.setTag(position);
        if (pic.getPlayliststatus().equals("0")) {
            holder.iv_savesubmit.setImageResource(R.mipmap.ic_savestatus);
        } else if (pic.getPlayliststatus().equals("1")) {
            holder.iv_savesubmit.setImageResource(R.mipmap.ic_submitstatus);
        }
        if (pic.getTypephoto().equals("1")) {
            holder.iv_photo.setVisibility(View.VISIBLE);
        } else if (pic.getTypephoto().equals("0")) {
            holder.iv_photo.setVisibility(View.GONE);
        }
        if (pic.getTypewg().equals("1")) {
            holder.iv_wengao.setVisibility(View.VISIBLE);
        } else if (pic.getTypewg().equals("0")) {
            holder.iv_wengao.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView iv_cut_bac;
        private View itemView;
        private TextView tv_title;
        private TextView tv_time;
        private LinearLayout ll_layout;
        private ImageView iv_photo, iv_wengao, iv_savesubmit;

        public ViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            iv_cut_bac = (ImageView) itemView.findViewById(R.id.iv_cut_bac);
            tv_time = (TextView) itemView.findViewById(R.id.tv_time);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);
            iv_savesubmit = (ImageView) itemView.findViewById(R.id.iv_savesubmit);
            iv_wengao = (ImageView) itemView.findViewById(R.id.iv_wengao);
            iv_photo = (ImageView) itemView.findViewById(R.id.iv_photo);
            ll_layout = (LinearLayout) itemView.findViewById(R.id.ll_layout);

        }
    }

    @Override
    public void onClick(View v) {
        //获取保存的数据同时回调
        if (listener != null) {
            listener.onItemClickListener(v, (int) (v.getTag()));
        }
    }

    //对外提供的接口
    public interface OnItemClickListener {
        public void onItemClickListener(View view, int posision);
    }

    //对外暴露接口
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
