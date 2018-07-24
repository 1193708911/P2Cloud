package movi.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.util.ArrayList;

import movi.base.MyBaseAdapter;
import movi.ui.bean.NewsSheetPicBean;
import movi.ui.fragment.NewsSheetFragment;

/**
 * @Class: PhotoAdapter
 * @Description: 图片适配器
 * @author: lling(www.liuling123.com)
 * @Date: 2015/11/4
 */
public class MobileBottomPicAdapter1 extends MyBaseAdapter<NewsSheetPicBean> {
    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_PHOTO = 1;
    private ArrayList<NewsSheetPicBean> list;

    public ArrayList<NewsSheetPicBean> getList() {
        return list;
    }

    public void setList(ArrayList<NewsSheetPicBean> list) {
        this.list = list;
    }

    private Context mContext;
    private int mWidth;
    //是否显示相机，默认不显示
    private PhotoClickCallBack mCallBack;

    public MobileBottomPicAdapter1(Context context, ArrayList<NewsSheetPicBean> mDatas) {
        super(context, mDatas);
        this.list = mDatas;
        this.mContext = context;
        int screenWidth = movi.view.photopicker.utils.OtherUtils.getWidthInPx(mContext);
        mWidth = (screenWidth - movi.view.photopicker.utils.OtherUtils.dip2px(mContext, 4)) / 4;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_CAMERA;
        } else {
            return TYPE_PHOTO;
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {

//        if (position == 0) {
////            return null;
//        } else {
//            return list.get(position);
//        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == TYPE_CAMERA) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.item_camera_layout, null);
                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCallBack != null) {
                            mCallBack.onMyTakePhotoClick();
                            LogUtil.d("----------------------------------"+"走了");
                        }
                    }
                });


        } else {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.photo_layout, null);
                holder.layout = (RelativeLayout) convertView.findViewById(R.id.ll_layout);
                holder.iv_new_bac = (ImageView) convertView.findViewById(R.id.iv_new_pic);
                holder.imageview_photo = (ImageView) convertView.findViewById(R.id.imageview_photo);
                holder.txtTitle= (TextView) convertView.findViewById(R.id.txtTitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtTitle.setText(position+"");
            x.image().bind(holder.imageview_photo, list.get(position).getLogo());
            if (list.get(position).getIs_cover().equals("1")) {
                holder.iv_new_bac.setVisibility(View.VISIBLE);

            } else {
                holder.iv_new_bac.setVisibility(View.GONE);
            }
            if (position == NewsSheetFragment.grindex) {
                holder.layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rec_redshap));
            } else {
                holder.layout.setBackgroundDrawable(null);
            }
        }
        return convertView;
    }

    private class ViewHolder {
        private RelativeLayout layout;
        private ImageView iv_new_bac;
        private ImageView imageview_photo;
        private TextView txtTitle;
    }

    /**
     * 多选时，点击相片的回调接口
     */
    public interface PhotoClickCallBack {
        void onMyTakePhotoClick();
    }

    public void setPhotoClickCallBack(PhotoClickCallBack callback) {
        mCallBack = callback;
    }


}
