package movi.ui.fragment;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import movi.base.BaseFragment;
import movi.base.Response;
import movi.net.biz.NewsCutBottomSaveBiz;
import movi.net.biz.NewsCutDeleteBiz;
import movi.net.biz.NewsHeadPicInfoListBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.ui.adapter.CutBottomAdapter;
import movi.ui.bean.CloudListBean;
import movi.ui.bean.HeadPicList;
import movi.ui.bean.NewsCutBottomBean;
import movi.ui.bean.NewsCutBottomPicListBean;
import movi.ui.fragment.main.HomeFragment;
import movi.ui.inter.OnItemClickListenner;
import movi.ui.view.CustomDialog;
import movi.ui.view.CustomVideoView;
import movi.ui.view.DragGridView;
import movi.ui.view.MyAlertDialog;
import movi.ui.view.RangeSeekBar;
import movi.utils.CommonUtils;
import movi.utils.MyTextUtils;
import movi.utils.TimeUtils;
import movi.utils.ToastUtils;
import movi.view.HorirontalHeaderLayout;
import movi.view.lib.HorizontalRefreshLayout;

import static android.R.id.list;
import static android.R.id.message;
import static com.ctvit.p2cloud.R.id.layoutHeader;
import static com.ctvit.p2cloud.R.id.txtsave;


/**
 * Created by chjj on 2016/5/11.
 * 剪辑列表
 */
@ContentView(value = R.layout.news_cut)
public class NewsCutFragment extends BaseFragment implements NetCallBack,
        CutBottomAdapter.OnListSizeChangeListener,
        AdapterView.OnItemClickListener, RangeSeekBar.OnRangeSeekBarChangeListener<Integer> {
    @ViewInject(R.id.gr_cut)
    private DragGridView gr_cut;
    @ViewInject(R.id.mVideoView)
    private CustomVideoView videoView;
    @ViewInject(R.id.pg_iv)
    private ProgressBar pg_iv;
    @ViewInject(R.id.txtTimeLenth)
    private TextView txtTimeLenth;
    @ViewInject(R.id.imgPlay)
    private ImageView imgPlay;
    private HorizontalRefreshLayout refreshLayout;
    @ViewInject(R.id.txtSave)
    private TextView txtSave;
    @ViewInject(R.id.contanner)
    private FrameLayout contanner;

    private CustomDialog dialog;
    //标志位 用来设置红框背景 默认是第一个
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    private int[] thumbs = new int[]{R.mipmap.ic_normal, R.mipmap.ic_press};
    private static int STATE;
    public Integer minValue = 0;
    public Integer maxValue = 0;
    public ArrayList<NewsCutBottomBean> data;
    private ArrayList<NewsCutBottomBean> sList;
    private List<HeadPicList.HeadPic> list;
    public CutBottomAdapter bottomAdapter;
    public static int backPosition = -1;
    private NewsCutBottomPicListBean picListBean;
    private int DEFAULT_DELETE = -1;
    private String minText = "00:00:00:00";
    private String maxText = "00:00:00:00";
    private static String SAVE = "0";
    private static String SUBMIT = "1";
    private static String SAVE_SUBMIT_DEFAULT = "-1";
    private CloudListBean.CloudBean cloudBean;
    private String path = "";
    //记录相关的标题
    private String title = "";
    private long[] timeMills;
    private HorirontalHeaderLayout layoutHeader;
    private RangeSeekBar<Integer> seekBar;
    private boolean isPlaying = false;
    private boolean isStart = false;
    private int currentMin;
    private int currentMax;
    private int currentNormal;
    private boolean isMaxSeeked = false;
    private boolean isHuanChongComplet;
    private boolean isNewPic, isNoNewCutSaved;
    private static final int STATE_PROGRESS = 0X001;
    private static final int STATE_UPDATE_MIN = 0X002;
    private static final int STATE_UPDATE_MAX = 0x003;
    private static final int TIME_MILLS = 40;
    private static final int STATE_ON_SEEK_COMPLET = 0X004;

    private static final int STATE_CAN_TOUCH = 0X005;


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case STATE_PROGRESS:
                    int progress = (Integer) msg.obj;
                    seekBar.setProgress(progress);
                    LogUtil.d("---------------progress");
                    break;

                case STATE_UPDATE_MIN:
                    setInvisibleProgress(false);
                    sendIsNoCanTouch();
                    videoView.seekTo(currentMin);
                    seekBar.setProgress(currentMin);
                    LogUtil.d("---------------currentMin");
                    break;
                case STATE_UPDATE_MAX:
                    setInvisibleProgress(false);
                    sendIsNoCanTouch();
                    videoView.seekTo(currentMax);
                    seekBar.setProgress(currentMax);
                    LogUtil.d("---------------currentMax");
                    break;
                case STATE_ON_SEEK_COMPLET:
                    videoView.pause();
                    break;
                case STATE_CAN_TOUCH:
                    boolean isNoCanTouch = (boolean) msg.obj;
                    if (seekBar != null) {
                        seekBar.setIsNoCanTouch(isNoCanTouch);
                    }
                    break;
            }

        }

        ;
    };


    private void initMovie() {
        imgPlay.setImageResource(R.mipmap.imb_play_normal);
        seekBar = new RangeSeekBar<Integer>(context, 0, 0, 100);
        removeSeekBar();
        contanner.addView(seekBar);
        seekBar.setOnRangeSeekBarChangeListener(this);
        if (TextUtils.isEmpty(path)) {
            return;
        }
        videoView.setVideoPath(path);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                int duration = (int) mediaPlayer.getDuration();
                isPlaying = true;
                seekBar.setRangeSeekBarParam(0, 0, duration);
                if (isNewCut) {
                    setSeekBarPosition();
                }

                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        while (isPlaying) {
                            int progress = (int) videoView.getCurrentPosition();
                            Message message = new Message();
                            message.what = STATE_PROGRESS;
                            message.obj = progress;
                            handler.sendMessage(message);
                            LogUtil.d("---------------myprogress" + progress + "----" + currentMin);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }

                    }
                }).start();
                mediaPlayer.setOnSeekCompleteListener(new MediaPlayer.OnSeekCompleteListener() {
                    @Override
                    public void onSeekComplete(MediaPlayer mp) {
                        if (!isStart) {
                            LogUtil.d("-----------start" + isStart);
                            videoView.pause();
                        }
                        dissmissProgressBar();
                        isStart = false;
                        Message msg = new Message();
                        msg.what = STATE_CAN_TOUCH;
                        msg.obj = false;
                        handler.sendMessage(msg);
                    }
                });
            }
        });
        isStart = false;
        videoView.requestFocus();
        videoView.start();
        showProgressBar();
        addListenner();
        if (!isNewCut) {
            sendIsNoCanTouch();
            videoView.seekTo(40);
        }

    }

    /**
     * 发送消息不能拖动
     */
    public void sendIsNoCanTouch() {
        Message msg = new Message();
        msg.what = STATE_CAN_TOUCH;
        msg.obj = true;
        handler.sendMessage(msg);
    }

    private void addListenner() {
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer arg0) {
                isStart = false;
//                removeSeekBar();
                seekBar.setInvisibleThumb(false);
                imgPlay.setImageResource(R.mipmap.imb_play_normal);
//                initMovie();
                videoView.seekTo(currentMin);
                seekBar.setProgress(currentMin);
                setNormalRes();

            }
        });
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
                isStart = false;
                return false;
            }
        });

    }


    public void removeSeekBar() {
        if (contanner.getChildCount() > 0) {
            contanner.removeAllViews();
        }
    }

    public void setInvisibleProgress(boolean isShow) {
        seekBar.setInvisibleThumb(isShow);
    }

    /**
     * 回复默认值
     */
    public void setNormalRes() {
        seekBar.setMinThumbRes(R.mipmap.ic_normal);
        seekBar.setMaxThumbRes(R.mipmap.ic_normal);
    }

    /**
     * 设置seekbar的图标
     *
     * @param isMin
     */
    public void setSeekImgIcon(boolean isMin) {
        if (isMin) {
            seekBar.setMinThumbRes(R.mipmap.ic_normal);
            seekBar.setMaxThumbRes(R.mipmap.ic_press);
        } else {
            seekBar.setMinThumbRes(R.mipmap.ic_press);
            seekBar.setMaxThumbRes(R.mipmap.ic_normal);
        }
    }


    public void setLayoutHeader(HorirontalHeaderLayout layoutHeader) {
        this.layoutHeader = layoutHeader;
        this.refreshLayout = (HorizontalRefreshLayout) layoutHeader.findViewById(R.id.refresh);
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        initGridView();
        initListener();
        initBundle();
        initMovie();
    }


    private void initListener() {
        layoutHeader.setOnCutListenner(new OnItemClickListenner() {
            @Override
            public void onCutItemClickListenner(View view, int position, List<HeadPicList.HeadPic> data, int state) {
                if (HorirontalHeaderLayout.STATE_NEWS_CUT_FRAGMENT == state) {
                    list = data;
                    txtTitle.setText(list.get(position).getTitle());
                    sendGetBtPicRequest(list.get(position).getPlaylist_id());
                    LogUtil.d("当前第几个选中NewCut");
                }
            }

            @Override
            public void onCutItemSuccess(List<HeadPicList.HeadPic> l) {
                list = l;

            }
        });
    }


    //初始化一些参数
    private void initBundle() {
        if (cloudBean == null) {
            return;
        }
        this.path = cloudBean.getMp4_url();
        txtTitle.setText(cloudBean.getClip_name());
    }

    private void initGridView() {
        sList = new ArrayList<>();
        data = new ArrayList<>();
        bottomAdapter = new CutBottomAdapter(context, data);
        bottomAdapter.setOnSizeChangeListener(this);
        gr_cut.setAdapter(bottomAdapter);
        gr_cut.setOnItemClickListener(this);
    }


    @Override
    protected int getResource() {
        return R.layout.news_cut;
    }


    @Event(value = {R.id.imgTo, R.id.imgBack, R.id.imgPlay, R.id.txtAdd, R.id.txtSubmit, R.id.txtDelete, R.id.txtSave})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSubmit:
                sendSubmit();
                break;
            case R.id.txtDelete:
                if (isNewCut) {
                    ToastUtils.showToast(context, "视频资源正在加载，请加载完后重试");
                    return;
                }
                if (!CommonUtils.isFastDoubleClick()) {
                    if (layoutHeader.currentPosition >= 0) {
                        SAVE_SUBMIT_DEFAULT = "-1";
                        sendDeleteHttpRequest();
                    }
                }
                break;

            case R.id.txtSave:
                if (!CommonUtils.isFastDoubleClick()) {
                    if (data == null || data.size() <= 0) {
                        ToastUtils.showToast(context, "当前列表为空，请剪辑片段");
                        return;
                    }
                    SAVE_SUBMIT_DEFAULT = SAVE;
                    submit(SAVE_SUBMIT_DEFAULT);
                }
                break;
            case R.id.txtAdd:
                //添加
                insertIntoGridView();
                break;
            case R.id.imgPlay:
                if (isNewCut) {
                    ToastUtils.showToast(context, "视频资源正在加载，请加载完后重试");
                    return;
                }
                if (videoView.isPlaying()) {
                    videoView.pause();
                    imgPlay.setImageResource(R.mipmap.imb_play_normal);
                } else {
                    if (!isHuanChongComplet) {
                        ToastUtils.showToast(context, "视频资源正在缓存，请稍后");
                        return;
                    }
                    //同时显示中间的thumb
                    isStart = true;
                    videoView.start();
                    imgPlay.setImageResource(R.mipmap.imb_play);
                    if (isMaxSeeked) {
                        videoView.seekTo(currentNormal);
                        isMaxSeeked = false;
                    }
                    setInvisibleProgress(true);
                }

                break;
            case R.id.imgBack:
                //往后跳一帧
                seekBar.setInvisibleThumb(false);
                pause();
                if (STATE == RangeSeekBar.STATE_MIN) {
                    currentMin = currentMin - TIME_MILLS <= 0 ? 0 : currentMin - TIME_MILLS;
                    seekBar.setMinWard(currentMin);
                    videoView.seekTo(currentMin);
                } else if (STATE == RangeSeekBar.STATE_MAX) {
                    //往前跳一帧
                    currentMax = currentMax - TIME_MILLS <= currentMin ? currentMin : currentMax - TIME_MILLS;
                    seekBar.setMaxWard(currentMax);
                    videoView.seekTo(currentMax);
                }
                break;
            case R.id.imgTo:
                seekBar.setInvisibleThumb(false);
                pause();
                if (STATE == RangeSeekBar.STATE_MIN) {
                    currentMin = currentMin + TIME_MILLS >= currentMax ? currentMax : currentMin + TIME_MILLS;
                    seekBar.setMinWard(currentMin);
                    videoView.seekTo(currentMin);
                    LogUtil.d("--------当前最值" + currentMin);
                } else if (STATE == RangeSeekBar.STATE_MAX) {
                    //往前跳一帧
                    currentMax = (currentMax + TIME_MILLS) >= videoView.getDuration() ? videoView.getDuration() : currentMax + TIME_MILLS;
                    seekBar.setMaxWard(currentMax);
                    videoView.seekTo(currentMax);
                }
                break;
        }
    }

    public void sendSubmit() {
        if (!CommonUtils.isFastDoubleClick()) {
            if (layoutHeader.currentPosition >= 0) {
                if (data == null || data.size() <= 0) {
                    ToastUtils.showToast(context, "请先选择一个片段");
                    return;
                }
                SAVE_SUBMIT_DEFAULT = SUBMIT;
                submit(SAVE_SUBMIT_DEFAULT);
            } else {
                if (data != null && data.size() > 0) {
                    showAlertDilaog();
                } else {
                    ToastUtils.showToast(context, "请先选择一个片段");
                }

            }
        }
    }

    public void showAlertDilaog() {
        final MyAlertDialog dialog = new MyAlertDialog(context).builder();
        dialog.setMsg("请先保存剪辑列表");
        dialog.setPositiveButton("是", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtSave.performClick();
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton("否", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public void pause() {
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            imgPlay.setImageResource(R.mipmap.imb_play_normal);
        }
    }

    //向griview中插入数据并且进行初始化
    private void insertIntoGridView() {
        if (cloudBean != null) {
            /**
             * 初始化操作
             */
            NewsCutBottomBean bottomBean = new NewsCutBottomBean();
            bottomBean.setIsNet("0");
            bottomBean.setId("0");

            if (layoutHeader.currentPosition >= 0) {
                bottomBean.setPlaylist_id(list.get(layoutHeader.currentPosition).getPlaylist_id());
                bottomBean.setMedia_id(list.get(layoutHeader.currentPosition).getMedia_id());
                bottomBean.setAuthor_id(list.get(layoutHeader.currentPosition).getAuthor_id());

            } else {
                bottomBean.setPlaylist_id("0");
                bottomBean.setMedia_id(cloudBean.getMedia_id());
                bottomBean.setAuthor_id(cloudBean.getAuthor_id());
            }
            bottomBean.setThumbnail_src(cloudBean.getJpg_url());
            bottomBean.setCreation_date(cloudBean.getCreation_date());
            bottomBean.setStart_timecode(cloudBean.getStart_timecode());
            bottomBean.setShot_id(cloudBean.getShot_id());
            bottomBean.setIn_position(TimeUtils.getZhenDuration(seekBar.minText) + "");
            bottomBean.setIn_position_time_code(seekBar.minText);
            bottomBean.setOut_position(TimeUtils.getZhenDuration(seekBar.maxText) + "");
            bottomBean.setOut_position_time_code(seekBar.maxText);
            //总时长
            long max = TimeUtils.getZhenDuration(seekBar.maxText);
            long min = TimeUtils.getZhenDuration(seekBar.minText);
            String allTimes = TimeUtils.formatDuration(TimeUtils.getZhenDuration(seekBar.maxText) - TimeUtils.getZhenDuration(seekBar.minText));
            bottomBean.setDuration_time_code(TimeUtils.formatDuration(TimeUtils.getZhenDuration(seekBar.maxText) - TimeUtils.getZhenDuration(seekBar.minText)));
            bottomBean.setItem_order("0");
            bottomBean.setCreated("0");
            bottomBean.setUpdated("0");
            bottomBean.setMp4url(path);
            bottomBean.setDuration("0");
            bottomBean.setItem_id("0");
            data.add(bottomBean);
            bottomAdapter.notifyDataSetChanged();
            isNoNewCutSaved = false;
        } else {
            ToastUtils.showToast(context, "请选择素材后进行剪辑");
        }
    }


    //删除指定的项
    private void sendDeleteHttpRequest() {

        if (backPosition >= 0 && data != null && data.size() > 0) {
            if (list.get(layoutHeader.currentPosition).getPlayliststatus().equals("1")) {
                data.remove(backPosition);
                backPosition = -1;
                bottomAdapter.notifyDataSetChanged();

            } else {
                if (data.get(backPosition).getIsNet().equals("0")) {
                    //代表本地
                    data.remove(backPosition);
                    backPosition = -1;
                    bottomAdapter.notifyDataSetChanged();

                } else {
                    DEFAULT_DELETE = 0;
                    NewsCutDeleteBiz biz = new NewsCutDeleteBiz();
                    biz.setNetCallBack(this);
                    biz.requestCutDelete(ServerConfig.GET, data.get(backPosition).getItem_id());
                }
            }
        }


    }


    private void sendGetBtPicRequest(String id) {
        data.clear();
        bottomAdapter.notifyDataSetChanged();
        NewsHeadPicInfoListBiz picInfoListBiz = new NewsHeadPicInfoListBiz();
        picInfoListBiz.setNetCallBack(this);
        picInfoListBiz.requestHeadPicInfoList(ServerConfig.GET, id);
    }

    //隐藏进度条
    public void dissmissProgressBar() {
        isHuanChongComplet = true;
        pg_iv.setVisibility(View.GONE);
    }

    //展示缓冲进度条
    public void showProgressBar() {
        isHuanChongComplet = false;
        pg_iv.setVisibility(View.VISIBLE);
    }

    @Override
    public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar, Integer minValue, Integer normalValue, Integer maxValue, int state) {
        currentMin = minValue;
        currentMax = maxValue;
        LogUtil.d("-------------" + currentMin);
        bar.minText = RangeSeekBar.formatDuration(minValue / 40);
        bar.maxText = RangeSeekBar.formatDuration(maxValue / 40);
        minText = bar.minText;
        maxText = bar.maxText;
        switch (state) {
            case RangeSeekBar.STATE_START:
                currentNormal = normalValue;
                break;
            case RangeSeekBar.STATE_MIN:
                //拖动完成最小值时  seekto  指定位置
                STATE = RangeSeekBar.STATE_MIN;
                isMaxSeeked = false;
                isStart = false;
                setSeekImgIcon(true);
                showProgressBar();
                Message min = new Message();
                min.what = STATE_UPDATE_MIN;
                handler.sendMessage(min);
                imgPlay.setImageResource(R.mipmap.imb_play_normal);
                LogUtil.d("----------------min" + currentMin);
                break;
            case RangeSeekBar.STATE_MAX:
                isStart = false;
                isMaxSeeked = true;
                STATE = RangeSeekBar.STATE_MAX;
                showProgressBar();
                setSeekImgIcon(false);
                imgPlay.setImageResource(R.mipmap.imb_play_normal);
                Message max = new Message();
                max.what = STATE_UPDATE_MAX;
                handler.sendMessage(max);

                break;
            case RangeSeekBar.STATE_NORMAL:
                isMaxSeeked = false;
                break;

        }
    }

    /**
     * 请求成功返回值
     *
     * @param response
     */
    @Override
    public void onSuccess(Response response) {
        if (response instanceof NewsCutBottomPicListBean) {
            picListBean = (NewsCutBottomPicListBean) response;
            if (null == sList) {
                sList = new ArrayList<>();
            }
            if (null == data) {
                data = new ArrayList<>();
            }
            sList.clear();
            data.clear();
            sList = picListBean.getData();
            if (null == sList || sList.size() <= 0) {
                return;
            }
//            for (int i = 0; i < sList.size(); i++) {
//                sList.get(i).setIsNet("1");
//            }
            if (sList == null) {
                sList = new ArrayList<>();
            }
            data.addAll(sList);
            bottomAdapter.notifyDataSetChanged();

        } else {
            if (DEFAULT_DELETE == 0) {
                backPosition = -1;
                ToastUtils.showToast(context, "删除成功");
                sendGetBtPicRequest(list.get(layoutHeader.currentPosition).getPlaylist_id());
            }
            if (SAVE_SUBMIT_DEFAULT.equals(SAVE)) {
                ToastUtils.showToast(context, "保存成功");
                txtTitle.setText(title);
                for (int i = 0; i < data.size(); i++) {
                    data.get(i).setIsNet("1");
                }
                bottomAdapter.notifyDataSetChanged();
                if (layoutHeader.currentPosition == -1) {
                    layoutHeader.setIsNew(true);
                }
                layoutHeader.sendGetHeadPic();
            } else if (SAVE_SUBMIT_DEFAULT.equals(SUBMIT)) {
                ToastUtils.showToast(context, "提交发布成功");
                //清空底部列表
                data.clear();
                txtTitle.setText("");
                bottomAdapter.notifyDataSetChanged();
                //清空抽图

                HomeFragment.homeFragment.clearStatus();
                layoutHeader.sendGetHeadPic();

            } else if (SAVE_SUBMIT_DEFAULT.equals(SAVE_SUBMIT_DEFAULT)) {

            }
        }

    }

    @Override
    public void onFailure(Response error) {
        ToastUtils.showToast(context, "请求失败");
        if (refreshLayout != null) {
            refreshLayout.onRefreshComplete();
        }
    }

    //提交数据
    private void submit(final String status) {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        View rootView = LayoutInflater.from(context).inflate(R.layout.alert_dialog, null);
        builder.setContentView(rootView);
        dialog = builder.create();
        dialog.show();
        final EditText etTitle = (EditText) rootView.findViewById(R.id.etTitle);
        etTitle.setText(txtTitle.getText().toString());
        TextView txtSave = (TextView) rootView.findViewById(R.id.txtSave);
        TextView txtCancel = (TextView) rootView.findViewById(R.id.txtCancel);
        TextView txtSaveOrSubmit = (TextView) rootView.findViewById(R.id.txtSaveOrSubmit);
        if (status.equals("0")) {
            txtSave.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_save));
            txtSaveOrSubmit.setText("保存");

        } else if (status.equals("1")) {
            txtSave.setBackgroundDrawable(getResources().getDrawable(R.mipmap.ic_commit));
            txtSaveOrSubmit.setText("提交");
        }
        txtSave.setOnClickListener(new View.OnClickListener() {
                                       @Override
                                       public void onClick(View v) {

                                           LogUtil.d("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + data.toString() + data.size());
                                           if (data != null && data.size() > 0) {
                                               //开始循环遍历判断是不是有状态
                                               if (MyTextUtils.isEmpty(etTitle, "剪辑标题不能为空")) {
                                                   title = etTitle.getText().toString().trim();
                                                   if (status.equals(SUBMIT)) {
                                                       for (int i = 0; i < list.size(); i++) {
                                                           if (list.get(i).getTitle().equals(etTitle.getText().toString())) {
                                                               if (list.get(i).getPlayliststatus().equals("1")) {
                                                                   ToastUtils.showToast(context, "剪辑列表已存在,请编辑名字");
                                                                   return;
                                                               }
                                                           }
                                                       }


                                                   }
                                               }

                                           } else {
                                               ToastUtils.showToast(context, "剪辑列表为空，请重新剪辑");
                                           }
                                           dialog.dismiss();
                                           //开始调用保存接口
                                           sendSaveBottomHttpRequest(etTitle, status);
                                       }

                                   }

        );
        txtCancel.setOnClickListener(new View.OnClickListener() {
                                         @Override
                                         public void onClick(View v) {
                                             dialog.dismiss();
                                         }
                                     }
        );

    }

    /**
     * 发送保存接口调用
     */
    private void sendSaveBottomHttpRequest(EditText etTitle, String playstaus) {
        //组织参数
        Map<String, String> paramMap = new HashMap<>();
        if (layoutHeader.currentPosition != -1) {
            //说明是提交不是新建
            paramMap.put("playlist_id", layoutHeader.currentPosition >= 0 ? list.get(layoutHeader.currentPosition).getPlaylist_id() : "0");
            paramMap.put("playliststatus", layoutHeader.currentPosition >= 0 ? list.get(layoutHeader.currentPosition).getPlayliststatus() : "0");
            paramMap.put("media_id", layoutHeader.currentPosition >= 0 ? list.get(layoutHeader.currentPosition).getMedia_id() : "0");
            paramMap.put("author_id", layoutHeader.currentPosition >= 0 ? list.get(layoutHeader.currentPosition).getAuthor_id() : "0");
        } else {
            paramMap.put("playlist_id", "0");
            paramMap.put("playliststatus", "0");
            paramMap.put("media_id", cloudBean.getMedia_id());
            paramMap.put("author_id", cloudBean.getAuthor_id());
        }
        paramMap.put("title", etTitle.getText().toString());
        paramMap.put("nums", data.size() + "");
        paramMap.put("isstatus", playstaus);

        for (int i = 0; i < data.size(); i++) {
            paramMap.put("start_timecode[" + i + "]", data.get(i).getStart_timecode());
            paramMap.put("shot_id[" + i + "]", data.get(i).getShot_id());
            paramMap.put("in_position_time_code[" + i + "]", data.get(i).getIn_position_time_code());
            paramMap.put("out_position_time_code[" + i + "]", data.get(i).getOut_position_time_code());
            paramMap.put("thumbnail_src[" + i + "]", data.get(i).getThumbnail_src());
            paramMap.put("creation_date[" + i + "]", data.get(i).getCreation_date());
//            paramMap.put("item_order[" + i + "]", data.get(i).getItem_order());
            paramMap.put("item_order[" + i + "]", i + "");
        }

        NewsCutBottomSaveBiz biz = new NewsCutBottomSaveBiz();
        biz.setNetCallBack(this);
        biz.requestCutBottomSave(ServerConfig.POST, paramMap);

    }


    //gr_view监听
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        backPosition = position;
        for (int i = 0; i < data.size(); i++) {
            if (i == position) {
                data.get(i).setChecked(true);
            } else {
                data.get(i).setChecked(false);
            }
        }
        bottomAdapter.notifyDataSetChanged();
        setCurrentStartPosition(position);
    }

    /**
     * 出入点处理
     */
    boolean isNewCut = false;

    public void setCurrentStartPosition(int position) {
        if (videoView.isPlaying()) {
            videoView.pause();
        }
        this.path = data.get(position).getMp4url();
        timeMills = TimeUtils.getTimeMill(data.get(position).getIn_position_time_code(), data.get(position).getOut_position_time_code());
        currentMin = currentNormal = (int) timeMills[0];
        //同时定点
        initMovie();

        isNewCut = true;
    }

    /**
     * 设置出入点
     */
    public void setSeekBarPosition() {
        if (data.size() <= 0) {
            return;
        }
        seekBar.minText = data.get(backPosition).getIn_position_time_code();
        seekBar.maxText = data.get(backPosition).getOut_position_time_code();
        seekBar.setProgress(currentNormal);
        seekBar.setMinAndMaxValue((int) timeMills[0], (int) timeMills[1]);
        videoView.seekTo((int) timeMills[0]);
        isNewCut = false;
    }

    /**
     * 该方法用于初始化数据
     *
     * @param cloudBean
     */
    public void setCloudPicBean(CloudListBean.CloudBean cloudBean) {
        if (cloudBean != null) {
            this.cloudBean = cloudBean;
            this.path = cloudBean.getMp4_url();
            initMovie();
        }
    }

    /**
     * 接口实现计算相应的集合数据
     */
    @Override
    public void onSizeChangeListener(String timeAllLeanth, String all) {
        if (timeAllLeanth != null) {
            txtTimeLenth.setText(timeAllLeanth + "/" + all);
        }
    }


    /**
     * 对外公开方法
     * 判断当前列表是否为
     * 保存  提交状态
     * 如果不是保存状态   则提示保存
     */
    public void checkIsSaveCut() {
        if (data.size() > 0) {
            //有数据
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).getIsNet().equals("0")) {
                    isNewPic = true;
                }
            }
            if (isNewPic && !isNoNewCutSaved) {
                final MyAlertDialog dialog = new MyAlertDialog(context).builder();
                dialog.setMsg("是否保存剪辑列表");
                dialog.setPositiveButton("是", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        txtSave.performClick();
                        dialog.dismiss();
                        isNewPic = false;
                        isNoNewCutSaved = true;
                    }
                });
                dialog.setNegativeButton("否", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        isNewPic = false;
                        isNoNewCutSaved = true;
                        clearLocalCut();
                    }
                });
                dialog.setCancelable(false);
                dialog.show();
            }

        }
    }

    /**
     * 点击否的情况下清空本地剪辑列表
     */
    public void clearLocalCut() {
        LogUtil.d("-----------------------" + data.size());
        if (layoutHeader.currentPosition < 0) {
            data.clear();
            bottomAdapter.notifyDataSetChanged();
        } else {
            sendGetBtPicRequest(list.get(layoutHeader.currentPosition).getPlaylist_id());
        }


    }

    /**
     * 重置title
     */
    public void resetTitle() {
        txtTitle.setText("");
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        if(hidden){
            if(videoView!=null&&videoView.isPlaying()){
                videoView.pause();
            }
        }
    }
}
