/*
package movi.adapter;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import movi.File.XmlUtils;
import movi.MainApplication;
import movi.base.Response;
import movi.bean.ProgressBean;
import movi.net.biz.NewsUpLoadNotifyBiz;
import movi.net.config.RemotePathConfig;
import movi.net.config.ServerConfig;
import movi.ui.db.VideoInfo;
import movi.net.request.NetCallBack;
import movi.sevice.UpLoadUtils;
import movi.utils.Bitmap2Bmp;
import movi.utils.BitmapUtils;
import movi.utils.PreferencesUtil;
import movi.utils.ToastUtils;

*/
/**
 * Created by chjj on 2016/7/27.
 *//*

public class UpLoadListAdapter1 extends BaseAdapter implements UpLoadUtils.OnFileMaxListener {
    private OnDeleteChangeListenner listenner;
    private Context context;
    public List<VideoInfo> videoInfoList;
    public boolean isUploading = false;
    public int mTaskPosition, mCurrentTaskPosition;
    private long fileMaxSize;
    private UpLoadUtils upLoadUtils;

    private ViewHolder mViewHolder;
    //删除标志位
    private static final int ACTION_DELETE_STATUS = 1;
    //更新进度标志位
    private static final int ACTION_PROGRESS_STATUS = 2;
    private ProgressBar mProgress;
    private static int DEFAULT_UPLOAD;
    //MP4
    private static final int DEFAULT_UPLOAD_MP4 = 3;
    //图片
    private static final int DEFAULT_UPLOAD_ICON = 5;
    //xml
    private static final int DEFAULT_UPLOAD_XML = 4;
    private static final int DEFAULT_UPLOAD_NOTIFY = 6;
    //创建一个hashMap用来判断是否有任务
    private static Map<Integer, Integer> taskMap = new HashMap<>();

    */
/**
     * 创建一个集合用于更新ui
     *
     * @param context
     * @param list
     *//*

    private ProgressBean mProgressBean;
    private File file;
    private FileOutputStream outputStream;
    private String photoPath;
    private String fileIconXmlName;
    private String videoPath;
    private String mCurrentTime;

    public UpLoadListAdapter1(Context context, List<VideoInfo> list, ProgressBean mProgressBean) {
        this.context = context;
        this.videoInfoList = list;
        upLoadUtils = new UpLoadUtils();
        upLoadUtils.setOnFileMaxListener(this);
        this.mProgressBean = mProgressBean;
        */
/**
         * 初始化远程服务器用户名
         * 密码
         *//*

        RemotePathConfigs.REMOTE_SERVER_PATH = "123.56.6.126";
        RemotePathConfigs.USER_NAME = MainApplication.getUser().getUsername();
        RemotePathConfigs.USER_PASSWORD = MainApplication.getUser().getPassword();
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
            mProgress = viewHolder.mProgressBar;
            viewHolder.txtStatus.setText(videoInfo.getStatus());
            viewHolder.mProgressBar.setMax(mProgressBean.getFileMaxLeanth());
            viewHolder.mProgressBar.setProgress(mProgressBean.getProgress());
            viewHolder.txtUpLoadOrPause.setText(videoInfo.getReUpLoad());
            LogUtil.d("+++++++++++++++++++" + mProgressBean.getProgress() + "======" + mProgressBean.getFileMaxLeanth());
            if (videoInfo.getPicPath() != null) {
                Glide.with(context).load(videoInfo.getPicPath()).asBitmap().into(viewHolder.imgVideoPic);
            }
            //设置监听
            */
/**
             * 删除监听
             *//*

            viewHolder.txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listenner != null) {
                        listenner.onDeleteChange(position, viewHolder);
                    }
                }
            });
            */
/**
             * 上传取消监听
             *//*

            viewHolder.txtUpLoadOrPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (videoInfo.getReUpLoad().equals("上载完成")) {
                        viewHolder.txtUpLoadOrPause.setClickable(false);
                        LogUtil.d("--------"+videoInfo.getReUpLoad());
                        viewHolder.txtUpLoadOrPause.setEnabled(false);
                    } else {
                        if (!isUploading) {
                            DEFAULT_UPLOAD = DEFAULT_UPLOAD_MP4;
                            mViewHolder = viewHolder;
                            upLoadFile(position, viewHolder.txtUpLoadOrPause, viewHolder.ll_status, viewHolder.mProgressBar, new MyOnProgressListenner());
                        } else {
                            if (videoInfoList.get(position).getUploadStatus().equals("1")) {
                                if (viewHolder.txtUpLoadOrPause.getText().toString().equals("取消")) {
                                    isUploading = false;
                                    viewHolder.txtUpLoadOrPause.setText("上载");
                                    viewHolder.ll_status.setVisibility(View.VISIBLE);
                                    viewHolder.mProgressBar.setVisibility(View.GONE);
                                    viewHolder.mProgressBar.setProgress(0);
                                    videoInfoList.get(position).setUploadStatus("2");
                                    upLoadUtils.sftp.disconnect();
                                    LogUtil.d("-----------" + "已经取消");
                                }
                            }
                        }
                    }
                }
            });
        }
        */
/**
         * 意味着自动上传
         *//*

        if (PreferencesUtil.getInt("UL") == 1&&position==0) {
            if (videoInfo.isUploadType() ) {
                LogUtil.d("----------" + PreferencesUtil.getInt("UL"));
                LogUtil.d("----------当前的postion为"+position);
                DEFAULT_UPLOAD = DEFAULT_UPLOAD_MP4;
                viewHolder.txtUpLoadOrPause.performClick();
            }
        }
            return convertView;
        }

    */
/**
     * 换从类
     *//*

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

    */
/**
     * 上传文件
     *
     * @param position
     * @param txtUpLoadOrPause
     * @param ll_status
     *//*

    private void upLoadFile(int position, TextView txtUpLoadOrPause, LinearLayout ll_status, ProgressBar mProgressBar, SftpProgressMonitor listener) {
        getXmlMetaData(position);

        */
/**
         * 首先进行判断
         * 一下当前是否有上载任务
         * 如果有等待上载任务完成之后
         * 再上载其他任务
         * 如果没有执行当前任务
         *//*

        if (txtUpLoadOrPause.getText().toString().equals("上载")) {
            txtUpLoadOrPause.setText("取消");
            isUploading = true;
            mTaskPosition = position;
            videoInfoList.get(position).setUploadStatus("1");
            ll_status.setVisibility(View.GONE);
            mProgressBar.setProgress(0);
            mProgressBar.setVisibility(View.VISIBLE);
            LogUtil.d("+++++++++++" + videoInfoList.get(position).getVideoPath());
            upLoadUtils.initSftp(videoPath);
            LogUtil.d("++++++++++" + RemotePathConfigs.REMOTE_SERVER_PATH);
            LogUtil.d("++++++++++" + RemotePathConfigs.REMOTE_MP4_PATH);
            LogUtil.d("++++++++++" + videoPath);
            LogUtil.d("++++++++++" + videoPath.substring(videoPath.lastIndexOf("/") + 1));
            upLoadUtils.upLoad(RemotePathConfigs.REMOTE_MP4_PATH, mCurrentTime + ".mp4",
                    videoPath, videoPath.substring(videoPath.lastIndexOf("/") + 1), listener);
        }

    }

    */
/**
     * 获取xml
     *
     * @param position
     * @return
     *//*

    private boolean getXmlMetaData(int position) {
        */
/**
         * 在这里进行上传完成之后的
         * 数据的组装
         *//*

        String fbl = "16:9";
        Map<String, String> params = new HashMap<>();
        if (PreferencesUtil.getInt("FBL") == 720) {
            fbl = "16:9";
        } else if (PreferencesUtil.getInt("FBL") == 480) {
            fbl = "3:2";
        } else if (PreferencesUtil.getInt("FBL") == 560) {
            fbl = "16:9";
        }

        videoPath = videoInfoList.get(position).getVideoPath();
        mCurrentTime = videoPath.substring(videoPath.lastIndexOf("/") + 1).split("\\.")[0];
        LogUtil.d("----------" + mCurrentTime);
        //素材名称
        params.put("ClipName", mCurrentTime);
        //分辨率
        params.put("AspectRatio", fbl);
        //视频开始时间
        params.put("StartTimecode", "00:00:00:00");
        //视频大小
        File videFile = new File(videoPath);
        if (videFile.exists()) {
            params.put("DataSize", String.valueOf(videFile.length()));
        }

        */
/**
         * 获取视频的开始时间
         * 创建时间以及更新时间
         * 视频帧数  组装数据重新赋值
         *//*

        params = XmlUtils.getAfterUploadVideoInfo(context, params, videoInfoList.get(position).getVideoPath());
        photoPath = videoInfoList.get(position).getPicPath();
        videoPath = videoInfoList.get(position).getVideoPath();
        String xml = XmlUtils.getXmlFile(params);
        LogUtil.i("------------" + xml);

        */
/**
         * 获取photopath文件名称
         * 同时切割取时间戳
         *//*

        if (videoPath == null) {
            return true;
        }
        String indexPath = videoPath.substring(videoPath.lastIndexOf("/") + 1);
        fileIconXmlName = indexPath.split("\\.")[0];
        file = new File(Environment.getExternalStorageDirectory(), fileIconXmlName + ".xml");
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            outputStream.write(xml.getBytes("utf-8"));
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    private final class MyOnProgressListenner implements SftpProgressMonitor {
        private long transfer = 0;
        private long max;

        @Override
        public boolean count(long count) {
            // TODO Auto-generated method stub
            if (DEFAULT_UPLOAD == DEFAULT_UPLOAD_MP4) {
                transfer = (transfer + count);
                LogUtil.d("ssssssssssss" + transfer);
                Message message = Message.obtain();
                message.what = ACTION_PROGRESS_STATUS;
                message.obj = transfer;
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
                        */
/**
                         * 发送广播
                         *//*

                        if (isUploading) {
                            Message message = Message.obtain();
                            message.what = ACTION_DELETE_STATUS;
                            message.obj = mTaskPosition;
                            handler.sendMessage(message);
                            isUploading = false;
                            mTaskPosition = 0;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    */
/**
                     * 无论哪一种失败
                     *//*

                    VideoInfo mVideoInfo = videoInfoList.get(mTaskPosition);
                    mVideoInfo.setReUpLoad("重新上载");
                    mVideoInfo.setProgress(String.valueOf(0));
                    mVideoInfo.setUploadStatus(String.valueOf(2));
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
                LogUtil.d("++++++++" + "上传xml成功" + file.getAbsolutePath());
            } else if (DEFAULT_UPLOAD == DEFAULT_UPLOAD_ICON) {

                //并且进行调用一次后台接口
                handler.sendEmptyMessage(DEFAULT_UPLOAD_NOTIFY);
            }
        }

        @Override
        public void init(int op, String src, String dest, long max) {
            this.max = max;
        }
    }

    */
/**
     * 提供删除监听
     *//*

    public interface OnDeleteChangeListenner {
        void onDeleteChange(int position, ViewHolder viewHolder);
    }

    */
/**
     * 设置监听
     *//*

    public void setDeleteChangeListener(OnDeleteChangeListenner listener) {
        this.listenner = listener;
    }

    */
/**
     * 提供一个接口更新
     * listview数据
     *//*

    public interface OnDataSetChangeListener {
        void onDataSetChange(int postion, ViewHolder viewHolder);
    }

    private OnDataSetChangeListener OnDataSetChangeListener;

    public void setOnDataSetChangeListener(OnDataSetChangeListener listener) {
        this.OnDataSetChangeListener = listener;

    }

    */
/**
     * 更新ui
     *//*

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case ACTION_DELETE_STATUS:
                    //清除任务
                    int position = (int) msg.obj;
                    mProgressBean.setFileMaxLeanth(100);
                    mProgressBean.setProgress(0);
                    LogUtil.d("-----------------" + "上传mp4成功");
                    //上传xml文件
                    DEFAULT_UPLOAD = DEFAULT_UPLOAD_XML;
                    upLoadUtils.initSftp(file.getAbsolutePath());
                    upLoadUtils.upLoad(RemotePathConfigs.REMOTE_XML_PATH, mCurrentTime + ".xml",
                            file.getAbsolutePath(), file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/") + 1), new MyOnProgressListenner());
                    break;

                case ACTION_PROGRESS_STATUS:
                    long progress = (long) msg.obj;
                    LogUtil.e("--------------progress" + progress);
                    mProgressBean.setProgress((int) progress);
                    notifyDataSetChanged();
                    break;
                case DEFAULT_UPLOAD_ICON:
                    DEFAULT_UPLOAD = DEFAULT_UPLOAD_ICON;
                    */
/**
                     * 这里提供bitmap转换为
                     * bmp文件的方法
                     *//*

                    LogUtil.d("00000000" + "上传xml成功");
                    //首先压缩bitmap
                    Bitmap bitmap = BitmapUtils.getCompressBitmap(BitmapFactory.decodeFile(photoPath), 80, 45);
//                    String bmpFilePath = Bitmap2Bmp.saveBmp(Bitmap2Bmp.convertToBlackWhite(bitmap), fileIconXmlName);
                    String bmpFilePath = Bitmap2Bmp.saveBmpBitmap(bitmap, fileIconXmlName);
                    LogUtil.d("-------------当前的bmpFilePath为" + bmpFilePath);
                    upLoadUtils.initSftp(bmpFilePath);
                    upLoadUtils.upLoad(RemotePathConfigs.REMOTE_ICON_PATH, mCurrentTime + ".bmp",
                            bmpFilePath, bmpFilePath.substring(bmpFilePath.lastIndexOf("/") + 1), new MyOnProgressListenner());
                    break;
                case DEFAULT_UPLOAD_NOTIFY:
                    LogUtil.d("00000000" + "上传icon成功");
                    */
/**
                     * 首先保存相应的
                     * 上传完整的数据
                     *//*

                    VideoInfo videoInfo = videoInfoList.get(mTaskPosition);
                    videoInfo.setReUpLoad("上载成功");
                    videoInfo.setStatus("上载成功");
                    //意味着手动上载
                    videoInfo.setUploadType(false);
                    //重置背景
                    mViewHolder.mProgressBar.setProgress(0);
                    mViewHolder.mProgressBar.setMax(100);
                    mViewHolder.ll_status.setVisibility(View.VISIBLE);
                    mViewHolder.mProgressBar.setVisibility(View.GONE);
                    //修改数据库数据
                    try {
                        MainApplication.getInstance().getDbManager().saveOrUpdate(videoInfo);
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                    OnDataSetChangeListener.onDataSetChange(mTaskPosition, mViewHolder);
                    NewsUpLoadNotifyBiz notifyBiz = new NewsUpLoadNotifyBiz();
                    notifyBiz.setNetCallBack(new UploadNotifyListener());
                    notifyBiz.requestUpLoadNotify(ServerConfig.POST, mCurrentTime, MainApplication.getInstance().getUser().getUsername());
                    break;

            }

        }
    };

    */
/**
     * 上传成功后的监听
     *//*

    private final class UploadNotifyListener implements NetCallBack {

        @Override
        public void onSuccess(Response response) {
            LogUtil.d("-------------通知后台成功");
        }


        @Override
        public void onFailure(Response error) {
            LogUtil.d("-------------通知后台失败");
        }
    }

    */
/**
     * 文件大小监听
     *
     * @param filesize
     *//*

    @Override
    public void onFileMax(long filesize) {
        this.fileMaxSize = filesize;
        mProgressBean.setFileMaxLeanth((int) fileMaxSize);
        LogUtil.d("ssssssssssss" + fileMaxSize);
    }

    */
/**
     * 通知adapter更新
     *//*

    public void notifyMDataSetChanged() {
        this.notifyDataSetChanged();
    }

}

*/
