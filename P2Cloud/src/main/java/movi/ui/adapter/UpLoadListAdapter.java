package movi.ui.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ctvit.p2cloud.R;
import com.jcraft.jsch.SftpProgressMonitor;

import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;

import java.util.List;

import movi.MainApplication;
import movi.base.BaseActivity;
import movi.base.Response;
import movi.net.biz.NewsUpLoadNotifyBiz;
import movi.net.config.RemoteServerPath;
import movi.net.config.ServerConfig;
import movi.net.request.UpLoadUtils;
import movi.ui.db.VideoInfo;
import movi.net.request.NetCallBack;
import movi.utils.Bitmap2Bmp;
import movi.utils.BitmapUtils;
import movi.utils.PreferencesUtil;
import movi.utils.ToastUtils;

/**
 * Created by chjj on 2016/7/27.
 */
public class UpLoadListAdapter extends BaseAdapter implements UpLoadUtils.OnFileMaxListener {
    private OnDeleteChangeListenner listenner;
    private Context context;
    public List<VideoInfo> videoInfoList;
    public int mTaskPosition;
    private long fileMaxSize;
    private UpLoadUtils upLoadUtils;

    private ViewHolder mViewHolder;
    //删除标志位
    private static final int ACTION_DELETE_STATUS = 1;
    //更新进度标志位
    private static final int ACTION_PROGRESS_STATUS = 2;
    private static int DEFAULT_UPLOAD;
    //MP4
    private static final int DEFAULT_UPLOAD_MP4 = 3;
    //图片
    private static final int DEFAULT_UPLOAD_ICON = 5;
    //xml
    private static final int DEFAULT_UPLOAD_XML = 4;
    private static final int DEFAULT_UPLOAD_NOTIFY = 6;

    //标志位是否正在上传
    private boolean isUploading = false;


    public UpLoadListAdapter(Context context, List<VideoInfo> list) {
        this.context = context;
        this.videoInfoList = list;
        upLoadUtils = new UpLoadUtils();
        upLoadUtils.setOnFileMaxListener(this);
        /**
         * 初始化远程服务器用户名
         * 密码
         */
        BaseActivity.REMOTE_SERVER_PATH = "123.56.6.126";
        BaseActivity.USER_NAME = MainApplication.getUser().getUsername();
        BaseActivity.USER_PASSWORD = MainApplication.getUser().getPassword();
        LogUtil.d("当前的用户名密码为" + MainApplication.getUser().getUsername() + MainApplication.getUser().getPassword());
    }


    @Override
    public int getCount() {
        return videoInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return videoInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    VideoInfo tempVideoInfo;

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.upload_list_item, null);
            viewHolder.txtDelete = (TextView) convertView.findViewById(R.id.txtDelete);
            viewHolder.txtLeanth = (TextView) convertView.findViewById(R.id.txtLeanth);
            viewHolder.txtStatus = (TextView) convertView.findViewById(R.id.txtStatus);
            viewHolder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
            viewHolder.imgVideoPic = (ImageView) convertView.findViewById(R.id.imgVideoPic);
            viewHolder.txtUpLoadOrPause = (TextView) convertView.findViewById(R.id.txtUpLoadOrPause);
            viewHolder.mProgressBar = (ProgressBar) convertView.findViewById(R.id.pgUpload);
            viewHolder.ll_status = (LinearLayout) convertView.findViewById(R.id.ll_status);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final VideoInfo videoInfo = videoInfoList.get(position);
        if (videoInfo != null) {
            viewHolder.txtLeanth.setText(videoInfo.getTimeLeanth());
            viewHolder.txtStatus.setText(videoInfo.getStatus());
            viewHolder.txtTitle.setText(videoInfo.getTitle());
            viewHolder.txtStatus.setText(videoInfo.getStatus());
            viewHolder.txtUpLoadOrPause.setText(videoInfo.getTxtUploadText());
            if (null != tempVideoInfo && tempVideoInfo.getId() == videoInfo.getId()) {
                viewHolder.mProgressBar.setMax(tempVideoInfo.getVideoFileLeanth());
                viewHolder.mProgressBar.setProgress(tempVideoInfo.getProgress());
                if (tempVideoInfo.getTxtUploadText().equals("取消")) {
                    viewHolder.ll_status.setVisibility(View.GONE);
                    viewHolder.mProgressBar.setVisibility(View.VISIBLE);
                } else {
                    //处理取消的情况
                    viewHolder.mProgressBar.setProgress(0);
                    viewHolder.ll_status.setVisibility(View.VISIBLE);
                    viewHolder.mProgressBar.setVisibility(View.GONE);
                    viewHolder.txtUpLoadOrPause.setText(videoInfo.getTxtUploadText());
                }
            } else {
                viewHolder.mProgressBar.setProgress(0);
                viewHolder.ll_status.setVisibility(View.VISIBLE);
                viewHolder.mProgressBar.setVisibility(View.GONE);
                viewHolder.txtUpLoadOrPause.setText(videoInfo.getTxtUploadText());

            }
            if (videoInfo.getPicPath() != null) {
                Glide.with(context).load(videoInfo.getPicPath()).asBitmap().into(viewHolder.imgVideoPic);
            }
            /**
             * 删除监听
             */
            viewHolder.txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenner != null) {
                        listenner.onDeleteChange(position, viewHolder);
                    }
                }
            });
            /**
             * 上传取消监听
             */
            viewHolder.txtUpLoadOrPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DEFAULT_UPLOAD = DEFAULT_UPLOAD_MP4;
                    upLoadFile(videoInfo, new MyOnProgressListenner(videoInfo));
                }
            });

        }
        /**
         * 意味着自动上传
         */
        LogUtil.d("3333333333" + PreferencesUtil.getInt("UL")+"----------"+videoInfo.isUploadType()+position);
        if (videoInfo.isUploadType() && !isUploading) {
            LogUtil.d("3333333333" + PreferencesUtil.getInt("UL")+"----------"+videoInfo.isUploadType()+position);
            LogUtil.d("----------当前的postion为" + position);
            DEFAULT_UPLOAD = DEFAULT_UPLOAD_MP4;
            viewHolder.txtUpLoadOrPause.performClick();
        }
        return convertView;
    }


    /**
     * 换从类
     */

    public class ViewHolder {
        public TextView txtDelete;
        public TextView txtLeanth;
        public TextView txtStatus;
        public TextView txtTitle;
        public ImageView imgVideoPic;
        public TextView txtUpLoadOrPause;
        public ProgressBar mProgressBar;
        public LinearLayout ll_status;
    }

    /**
     * 上传文件
     */
    private void upLoadFile(VideoInfo videoInfo, SftpProgressMonitor listener) {
        tempVideoInfo = videoInfo;
        if (videoInfo.getTxtUploadText().equalsIgnoreCase("上载") || videoInfo.getTxtUploadText().equalsIgnoreCase("重载")) {
            isUploading = true;
            tempVideoInfo.setTxtUploadText("取消");
            String videoPath = videoInfo.getVideoPath();
            String  newFileVideoPath=videoInfo.getCompressFileName();
            upLoadUtils.initSftp(newFileVideoPath);
            upLoadUtils.upLoad(RemoteServerPath.REMOTE_MP4_PATH, videoInfo.getCurrentTime() + ".mp4",
                    newFileVideoPath, newFileVideoPath.substring(newFileVideoPath.lastIndexOf("/") + 1), listener);
        } else if (videoInfo.getTxtUploadText().equalsIgnoreCase("取消")) {
            upLoadUtils.sftp.disconnect();
            tempVideoInfo.setTxtUploadText("重载");
            try {
                MainApplication.getInstance().getDbManager().saveOrUpdate(tempVideoInfo);
            } catch (DbException e) {
                e.printStackTrace();
            }
            notifyDataSetChanged();
        }

    }


    private class MyOnProgressListenner implements SftpProgressMonitor {
        private long transfer = 0;

        public MyOnProgressListenner(VideoInfo videoInfo) {
            tempVideoInfo = videoInfo;
        }

        @Override
        public boolean count(long count) {
            // TODO Auto-generated method stub
            if (DEFAULT_UPLOAD == DEFAULT_UPLOAD_MP4) {
                transfer = (transfer + count);
                LogUtil.d("ssssssssssss" + transfer);
                Message message = Message.obtain();
                message.what = ACTION_PROGRESS_STATUS;
                tempVideoInfo.setProgress((int) transfer);
                message.obj = tempVideoInfo;
                handler.sendMessage(message);
            }

            return true;
        }

        @Override
        public void end() {
            if (DEFAULT_UPLOAD == DEFAULT_UPLOAD_MP4) {
                if (fileMaxSize == transfer) {
                    //删除上传完成的
                    try {
                        ToastUtils.showToast(context, "上传成功");
                        /**
                         * 发送广播
                         */
                        Message message = Message.obtain();
                        message.what = ACTION_DELETE_STATUS;
                        message.obj = tempVideoInfo;
                        handler.sendMessage(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    /**
                     * 无论哪一种失败
                     */
                    VideoInfo mVideoInfo = videoInfoList.get(mTaskPosition);
                    mVideoInfo.setProgress(0);
                    mVideoInfo.setStatus("等待");
                    try {
                        MainApplication.getInstance().getDbManager().saveOrUpdate(mVideoInfo);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    OnDataSetChangeListener.onDataSetChange(mTaskPosition, mViewHolder);


                }
            } else if (DEFAULT_UPLOAD == DEFAULT_UPLOAD_XML) {
                //说明上传xml已经结束
                handler.sendEmptyMessage(DEFAULT_UPLOAD_ICON);
            } else if (DEFAULT_UPLOAD == DEFAULT_UPLOAD_ICON) {
                //并且进行调用一次后台接口
                handler.sendEmptyMessage(DEFAULT_UPLOAD_NOTIFY);
            }
        }

        @Override
        public void init(int op, String src, String dest, long max) {
        }
    }

    /**
     * 提供删除监听
     */
    public interface OnDeleteChangeListenner {
        void onDeleteChange(int position, ViewHolder viewHolder);
    }

    /**
     * 设置监听
     */
    public void setDeleteChangeListener(OnDeleteChangeListenner listener) {
        this.listenner = listener;
    }

    /**
     * 提供一个接口更新
     * listview数据
     */
    public interface OnDataSetChangeListener {
        void onDataSetChange(int postion, ViewHolder viewHolder);
    }

    private OnDataSetChangeListener OnDataSetChangeListener;

    public void setOnDataSetChangeListener(OnDataSetChangeListener listener) {
        this.OnDataSetChangeListener = listener;

    }

    /**
     * 更新ui
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_DELETE_STATUS:
                    tempVideoInfo = (VideoInfo) msg.obj;
                    //上传xml文件
                    DEFAULT_UPLOAD = DEFAULT_UPLOAD_XML;
                    String xmlFilePath = tempVideoInfo.getXmlFilePath();
                    upLoadUtils.initSftp(tempVideoInfo.getXmlFilePath());
                    upLoadUtils.upLoad(RemoteServerPath.REMOTE_XML_PATH, tempVideoInfo.getCurrentTime() + ".xml",
                            xmlFilePath, xmlFilePath.substring(xmlFilePath.lastIndexOf("/") + 1), new MyOnProgressListenner(tempVideoInfo));
                    break;

                case ACTION_PROGRESS_STATUS:
                    tempVideoInfo = (VideoInfo) msg.obj;
                    notifyDataSetChanged();
                    break;
                case DEFAULT_UPLOAD_ICON:
                    DEFAULT_UPLOAD = DEFAULT_UPLOAD_ICON;
                    //首先压缩bitmap
                    Bitmap bitmap = BitmapUtils.getCompressBitmap(BitmapFactory.decodeFile(tempVideoInfo.getPicPath()), 400, 225);
                    String bmpFilePath = Bitmap2Bmp.saveBmpBitmap(bitmap, tempVideoInfo.getFileIconXmlName());
                    upLoadUtils.initSftp(bmpFilePath);
                    upLoadUtils.upLoad(RemoteServerPath.REMOTE_ICON_PATH, tempVideoInfo.getCurrentTime() + ".bmp",
                            bmpFilePath, bmpFilePath.substring(bmpFilePath.lastIndexOf("/") + 1), new MyOnProgressListenner(tempVideoInfo));
                    break;
                case DEFAULT_UPLOAD_NOTIFY:
                    try {
                        tempVideoInfo.setStatus("上载成功");
                        tempVideoInfo.setTxtUploadText("上载成功");
                        tempVideoInfo.setUploadType(false);
                        //修改数据库数据
                        MainApplication.getInstance().getDbManager().saveOrUpdate(tempVideoInfo);

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    OnDataSetChangeListener.onDataSetChange(mTaskPosition, mViewHolder);
                    NewsUpLoadNotifyBiz notifyBiz = new NewsUpLoadNotifyBiz();
                    notifyBiz.setNetCallBack(new UploadNotifyListener());
                    notifyBiz.requestUpLoadNotify(ServerConfig.POST, tempVideoInfo.getCurrentTime(), MainApplication.getInstance().getUser().getUsername());
                    break;

            }

        }
    };

    /**
     * 上传成功后的监听
     */
    private final class UploadNotifyListener implements NetCallBack {

        @Override
        public void onSuccess(Response response) {
            LogUtil.d("-------------通知后台成功");
            tempVideoInfo = null;
            isUploading = false;
            notifyDataSetChanged();
        }


        @Override
        public void onFailure(Response error) {
            LogUtil.d("-------------通知后台失败");
            tempVideoInfo = null;
            isUploading = false;
            notifyDataSetChanged();
        }

    }

    /**
     * 文件大小监听
     *
     * @param filesize
     */
    @Override
    public void onFileMax(long filesize) {
        this.fileMaxSize = filesize;
        tempVideoInfo.setVideoFileLeanth((int) fileMaxSize);
    }

    /**
     * 通知adapter更新
     */
    public void notifyMDataSetChanged() {
        this.notifyDataSetChanged();
    }

}

