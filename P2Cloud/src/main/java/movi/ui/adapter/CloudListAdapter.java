package movi.ui.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

import movi.MainApplication;
import movi.base.MyBaseAdapter;
import movi.ui.bean.CloudListBean;
import movi.ui.view.InfoPopWindow;

import static android.R.attr.button;
import static com.ctvit.p2cloud.R.id.txtSuCaiName;


/**
 * Created by chjj on 2016/5/12.
 * 云端服务列表
 */
public class CloudListAdapter extends MyBaseAdapter<CloudListBean.CloudBean> {

    private OnMediaNameClick listenner;

    public CloudListAdapter(Context context, ArrayList<CloudListBean.CloudBean> list) {
        super(context, list);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(MainApplication.getContext()).inflate(R.layout.cloud_item, null);
            viewHolder = new ViewHolder();
            convertView.setTag(viewHolder);
            viewHolder.imgInfo = (ImageView) convertView.findViewById(R.id.imgInfo);
            viewHolder.iv_pic = (ImageView) convertView.findViewById(R.id.iv_pic);
            viewHolder.txtSucaiName = (TextView) convertView.findViewById(R.id.txtSucaiName);
            viewHolder.txtFormat = (TextView) convertView.findViewById(R.id.txtFormat);
            viewHolder.txtTimeLeanth = (TextView) convertView.findViewById(R.id.txtTimeLeanth);
            viewHolder.txtDevice = (TextView) convertView.findViewById(R.id.txtDevice);
            viewHolder.txtOwner = (TextView) convertView.findViewById(R.id.txtOwner);
            viewHolder.txtTime = (TextView) convertView.findViewById(R.id.txtTime);
            viewHolder.llSuCai = (LinearLayout) convertView.findViewById(R.id.llSuCai);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CloudListBean.CloudBean bean = list.get(position);
        if (!TextUtils.isEmpty(bean.getProgram_name())) {
            viewHolder.txtSucaiName.setText(bean.getProgram_name());
        } else {
            viewHolder.txtSucaiName.setText(bean.getClip_name());
        }

        viewHolder.txtFormat.setText(bean.getCodec());
        viewHolder.txtTimeLeanth.setText(bean.getDuration());
        viewHolder.txtDevice.setText(bean.getModel_name());
        viewHolder.txtOwner.setText(bean.getUid());
        viewHolder.txtTime.setText(bean.getCreated());
        viewHolder.llSuCai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listenner != null) {
                    listenner.onMediaName(bean);
                }

            }
        });
        Glide.with(context).load(bean.getJpg_url()).into(viewHolder.iv_pic);
        viewHolder.imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View rootView = LayoutInflater.from(context).inflate(R.layout.cloud_info, null);
                int[] locations = new int[2];
                viewHolder.imgInfo.getLocationOnScreen(locations);
                LogUtil.d("当前imgInfo在屏幕的位置为" + locations[1]);
                //获取屏幕的宽高
                DisplayMetrics metrics = context.getResources().getDisplayMetrics();
                setRootView(rootView, position, locations, metrics, viewHolder.imgInfo);


            }
        });
        return convertView;
    }

    //设置弹出框数据
    private void setRootView(View rootView, int position, int[] locations, DisplayMetrics metrics, View view) {

        TextView txtSuCai = (TextView) rootView.findViewById(R.id.txtSuCai);
        TextView txtSuCaiName = (TextView) rootView.findViewById(R.id.txtSuCaiName);
        TextView txtUserId = (TextView) rootView.findViewById(R.id.txtUserId);
        TextView txtMediaFormat = (TextView) rootView.findViewById(R.id.txtMediaFormat);
        TextView txtTimeLeanth = (TextView) rootView.findViewById(R.id.txtTimeLeanth);
        TextView txtStartTime = (TextView) rootView.findViewById(R.id.txtStartTime);
        TextView txtZhen = (TextView) rootView.findViewById(R.id.txtZhen);
        TextView txtBiLi = (TextView) rootView.findViewById(R.id.txtBiLi);
        TextView txtCreteTime = (TextView) rootView.findViewById(R.id.txtCreteTime);
        TextView txtDeviceName = (TextView) rootView.findViewById(R.id.txtDeviceName);
        TextView txtDeviceFactory = (TextView) rootView.findViewById(R.id.txtDeviceFactory);
        TextView txtOwner = (TextView) rootView.findViewById(R.id.txtOwner);
        TextView txtTakeNum = (TextView) rootView.findViewById(R.id.txtTakeNum);
//绑定数据
        final CloudListBean.CloudBean bean = list.get(position);
        if (!TextUtils.isEmpty(bean.getProgram_name())) {
            txtSuCaiName.setText(" " + bean.getProgram_name());
        } else {
            txtSuCaiName.setText(" " + bean.getClip_name());
        }

        txtUserId.setText(bean.getAuthor_id());
        txtMediaFormat.setText(bean.getCodec());
        txtTimeLeanth.setText(bean.getDuration());
        txtStartTime.setText(bean.getStart_timecode());
        txtZhen.setText(bean.getFrame_rate());
        txtCreteTime.setText(bean.getCreated());
        txtDeviceName.setText(bean.getModel_name());
        txtDeviceFactory.setText(bean.getManufacturer());
        txtOwner.setText(bean.getUid());
        //待沟通
        txtTakeNum.setText(bean.getTake_number());

        InfoPopWindow popWindow = new InfoPopWindow((Activity) context, rootView);
        if (!popWindow.isShowing()) {
            if (locations[1] <= metrics.heightPixels / 2) {
                popWindow.setAnimationStyle(R.style.Popuwindow_style);
                popWindow.showAtLocation(view, Gravity.NO_GRAVITY, locations[0] - popWindow.getWidth(), locations[1] + view.getHeight());
            } else {
                popWindow.setAnimationStyle(R.style.Popuwindow_style_bottom);
                popWindow.showAtLocation(view, Gravity.NO_GRAVITY, locations[0] - popWindow.getWidth(), locations[1] - popWindow.getHeight());
            }
        } else {
            popWindow.dismiss();
        }

    }

    public interface OnMediaNameClick {
        public abstract void onMediaName(CloudListBean.CloudBean bean);
    }

    public void setOnSuCaiNameClick(OnMediaNameClick click) {
        this.listenner = click;

    }

    public class ViewHolder {
        private LinearLayout llSuCai;
        private ImageView iv_pic, imgInfo;
        private TextView txtSucaiName, txtFormat, txtTimeLeanth, txtDevice, txtOwner, txtTime;
    }
}