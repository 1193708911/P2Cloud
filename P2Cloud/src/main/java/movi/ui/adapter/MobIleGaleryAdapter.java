package movi.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.ctvit.p2cloud.R;
import org.xutils.common.util.LogUtil;
import java.util.ArrayList;
import movi.base.MyBaseAdapter;
import movi.ui.bean.NewsSheetPicBean;
import movi.ui.fragment.NewsSheetFragment;
import movi.ui.view.photopicker.utils.ImageLoader;
import movi.view.photopicker.utils.OtherUtils;


/**
 * 用于拍照功能的实现
 */
public class MobIleGaleryAdapter extends MyBaseAdapter<NewsSheetPicBean> {
    private int screenWidth;
    private int mWidth;
    private OnTakePhotoClickListener listener;
    private ArrayList<NewsSheetPicBean> list;

    public ArrayList<NewsSheetPicBean> getList() {
        return list;
    }
    public void setList(ArrayList<NewsSheetPicBean> list) {
        this.list = list;
    }

    public MobIleGaleryAdapter(Context context, ArrayList<NewsSheetPicBean> list) {
        super(context, list);
        this.list = list;
        screenWidth = OtherUtils.getWidthInPx(context);
        mWidth = (screenWidth - OtherUtils.dip2px(context, 4)) / 4;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (position == 0) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_camera_layout, null);
            //设置高度等于宽度
            convertView.setTag(null);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LogUtil.d("当前点击了拍照功能");
                    if (listener != null) {
                        listener.onTakePhotoClick();
                    }
                }
            });
//            convertView.setTag(null);

        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.photo_layout, null);
            RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.ll_layout);
            ImageView iv_new_bac = (ImageView) convertView.findViewById(R.id.iv_new_pic);
            ImageView imageview_photo = (ImageView) convertView.findViewById(R.id.imageview_photo);
            ImageLoader.getInstance().display(list.get(position).getLogo(), imageview_photo,
                    mWidth, mWidth);
//            if (statusMap.get(position)) {
//                layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rec_redshap));
//            } else {
//                layout.setBackgroundDrawable(null);
//            }
            if (list.get(position).getIs_cover().equals("1")) {
                iv_new_bac.setVisibility(View.VISIBLE);
            } else {
                iv_new_bac.setVisibility(View.GONE);
            }
            if ( position ==NewsSheetFragment.grindex) {
                layout.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.rec_redshap));
            } else {
                layout.setBackgroundDrawable(null);
            }
        }
        return convertView;
    }

    public interface OnTakePhotoClickListener {
        public void onTakePhotoClick();
    }

    public void setPhotoClickCallBack(OnTakePhotoClickListener lisener) {
        this.listener = lisener;
    }

}
