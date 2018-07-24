package movi.ui.fragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.widget.VideoView;
import movi.ui.adapter.CutBottomAdapter;
import movi.ui.adapter.CutListAdapter;
import movi.base.BaseFragment;
import movi.base.Response;
import movi.ui.bean.CloudListBean;
import movi.ui.bean.HeadPicList;
import movi.ui.bean.NewsCutBottomBean;
import movi.ui.bean.NewsCutBottomPicListBean;
import movi.net.biz.NewsCutBottomSaveBiz;
import movi.net.biz.NewsCutDeleteAllBiz;
import movi.net.biz.NewsCutDeleteBiz;
import movi.net.biz.NewsHeadPicBiz;
import movi.net.biz.NewsHeadPicInfoListBiz;
import movi.net.config.ServerConfig;
import movi.ui.fragment.main.HomeFragment;
import movi.net.request.NetCallBack;
import movi.ui.view.RangeSeekBar;
import movi.utils.CommonUtils;
import movi.utils.MyTextUtils;
import movi.utils.TimeUtils;
import movi.utils.ToastUtils;
import movi.utils.VideoUtils;
import movi.ui.view.CustomDialog;
import movi.ui.view.DragGridView;
import movi.ui.view.MyAlertDialog;
import movi.ui.view.lib.RefreshCallBack;
import movi.ui.view.lib.SimpleRefreshHeader;
import movi.view.lib.HorizontalRefreshLayout;


/**
 * Created by chjj on 2016/5/11.
 * 剪辑列表
 */
@ContentView(value = R.layout.news_cut)
public class NewsCutFragmentSheet extends BaseFragment implements CutListAdapter.OnItemClickListener, NetCallBack, AdapterView.OnItemClickListener, CutBottomAdapter.OnListSizeChangeListener, RefreshCallBack {
    @ViewInject(value = R.id.rv_view)
    private RecyclerView rv_view;
    @ViewInject(R.id.gr_cut)
    private DragGridView gr_cut;
    @ViewInject(R.id.layout)
    private LinearLayout layout;
    @ViewInject(R.id.mVideoView)
    private VideoView videoView;
    @ViewInject(R.id.pg_iv)
    private ProgressBar pg_iv;
    @ViewInject(R.id.txtTimeLenth)
    private TextView txtTimeLenth;
    @ViewInject(R.id.imgPlay)
    private ImageView imgPlay;
    @ViewInject(R.id.refresh)
    private HorizontalRefreshLayout refreshLayout;
    @ViewInject(R.id.img_bac)
    private ImageView img_bac;
    @ViewInject(R.id.txtSave)
    private TextView txtSave;
    private ArrayList<HeadPicList.HeadPic> list;
    private CutListAdapter adapter;
    private RangeSeekBar<Integer> seekBar;
    private CustomDialog dialog;
    //标志位 用来设置红框背景 默认是第一个
    @ViewInject(R.id.txtTitle)
    private TextView txtTitle;
    private int[] thumbs = new int[]{R.mipmap.ic_normal, R.mipmap.ic_press};

    //    http://7rfkz6.com1.z0.glb.clouddn.com/480p_20160229_T2.mp4
    private static final String TAG = NewsCutFragmentSheet.class.getSimpleName();
    private android.media.MediaPlayer mediaPlayer;
    private int TIME_MILLS = 40;
    //当前一个thumb拖动时 记录当前中间的位置 记录最小位置
    private Integer currentNormal = 0;
    //成员state变量 默认是最小的thumb
    private static int STATE = 0;
    //记录大小值
    public Integer minValue = 0;
    public Integer maxValue = 0;
    private boolean reset = false;
    private ArrayList<NewsCutBottomBean> data;
    private ArrayList<NewsCutBottomBean> sList;
    private CutBottomAdapter bottomAdapter;
    //标志位 用来设置红框背景 默认是第一个
    public static int backPosition = 0;
    private NewsCutBottomPicListBean picListBean;
    public static int currentPosition = -1;
    private int DEFAULT_DELETE = -1;
    private String minText = "00:00:00:00";
    private String maxText = "00:00:00:00";
    private boolean isSame = true;
    private static String SAVE = "0";
    private static String SUBMIT = "1";
    private static String SAVE_SUBMIT_DEFAULT = "-1";
    private CloudListBean.CloudBean cloudBean;
    private String path = "";
    //记录相关的标题
    private String title = "";

    //标志是否新建状态保存过
    private boolean isSaved = false;
    //声明变量保存当前的最小出点位置
    private int minCurrentValue = 0;
    private int maxCurrentValue = 0;

    private static final int STATE_DELAY = 500;
    private long[] timeMills;

    private boolean isBuffed = false;
    //抽图
    public static final int STATE_SHEET_PIC = 0x001;
    public static final int STATE_UPDATE_PROGRESS = 0x002;

    @Override
    protected void afterViews() {
        super.afterViews();
        initReLayout();
        initBundle();
        initView();
        initGridView();
        sendGetHeadPic();
    }

    @Override
    protected int getResource() {
        return R.layout.news_cut;
    }

    private void initReLayout() {
        refreshLayout.setEnable(true);
        refreshLayout.setRefreshHeader(new SimpleRefreshHeader(context), HorizontalRefreshLayout.START);
        refreshLayout.setRefreshCallback(this);

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

    /**
     * 重置背景
     */
    public void resetBac() {
        BitmapDrawable drawable = (BitmapDrawable) img_bac.getBackground();
        if (drawable != null) {
            drawable = null;
            img_bac.setBackgroundDrawable(null);
        }
    }

    /**
     * 初始化
     */
    private void initView() {
        if (removeSeekBar()) return;
        if (cloudBean != null) {
            String imgUrl = cloudBean.getJpg_url();
            if (imgUrl != null) {
                x.image().bind(img_bac, imgUrl);
            }
        }

//        pg_iv.setVisibility(View.VISIBLE);
        playVideo();
    }

    private boolean removeSeekBar() {
        if (layout == null) {
            return true;
        }
        layout.removeAllViews();
        seekBar = new RangeSeekBar<Integer>(context, 0, 0, (int) videoView.getDuration());
        layout.addView(seekBar);
        seekBar.setInvisibleThumb(false);
        if (path == null || path.equals("")) {
            return true;
        }

        img_bac.setVisibility(View.VISIBLE);
        resetBac();

        return false;

    }

    private void playVideo() {
//        pg_iv.setVisibility(View.VISIBLE);
        videoView.setVideoPath(path);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
//                mp.setPlaybackSpeed(1.0f);
                int duration = maxValue = (int) videoView.getDuration();
                seekBar.setRangeSeekBarParam(minValue, (int) currentNormal, duration);
                LogUtil.d("当前最大的duration" + duration);
                addListener();
                if (isNewCut) {
                    setSeekBarPosition(duration);
                }
                isBuffed = true;
                LogUtil.d("------------" + isBuffed);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (!Thread.currentThread().isInterrupted()) {
                            if (videoView.getCurrentPosition() == videoView.getDuration()) {
                                return;
                            }
                            Message message = new Message();
                            message.what = STATE_UPDATE_PROGRESS;
                            message.obj = (int) videoView.getCurrentPosition();
                            videoHandler.sendMessage(message);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                }).start();


            }
        });

        videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(MediaPlayer mp, int what, int extra) {
                switch (what) {
                    //开始缓冲
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        mp.pause();
                        break;
                    //缓冲结束
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        mp.pause();
                        break;

                }
                return true;
            }
        });

        //监听事件使之处于空闲状态
        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.reset();
                return false;
            }
        });

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                //定位到当前出点
                imgPlay.setImageResource(R.mipmap.imb_play_normal);
                seekBar.setProgress(minCurrentValue);
                seekBar.setInvisibleThumb(false);
            }
        });

    }


    private void addListener() {
        seekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar<?> bar,
                                                    Integer minValue, Integer normalValue, Integer maxValue, int state) {
                Log.d("当前拖动的seekbar为STATE为", state + "");
                img_bac.setVisibility(View.GONE);
                minCurrentValue = minValue;
                maxCurrentValue = maxValue;
                bar.minText = RangeSeekBar.formatDuration(minValue / 40);
                bar.maxText = RangeSeekBar.formatDuration(maxValue / 40);
                minText = bar.minText;
                maxText = bar.maxText;
                switch (state) {
                    case RangeSeekBar.STATE_MIN:
                        seekBar.setMinThumbRes(R.mipmap.ic_normal);
                        seekBar.setMaxThumbRes(R.mipmap.ic_press);
                        seekBar.setInvisibleThumb(true);
                        currentNormal = minValue;

                        break;
                    case RangeSeekBar.STATE_MAX:
                        seekBar.setMinThumbRes(R.mipmap.ic_press);
                        seekBar.setMaxThumbRes(R.mipmap.ic_normal);
                        currentNormal = normalValue;
                        reset = true;
                        break;
                    case RangeSeekBar.STATE_NORMAL:

                        break;
                }
                STATE = state;
                NewsCutFragmentSheet.this.minValue = minValue;
                NewsCutFragmentSheet.this.maxValue = maxValue;
                //同时隐藏中间进度条
                seekBar.setInvisibleThumb(false);
                pause();
                sheetBitmap(data.get(backPosition).getMp4url(), currentNormal);

            }
        });

    }

    /**
     * 抽图
     */
    public void sheetBitmap(String url, int mill) {
        pg_iv.setVisibility(View.VISIBLE);
        new SheetThread(url, mill).start();

    }

    private class SheetThread extends Thread {
        private String url;
        private int mill;

        public SheetThread(String url, int mill) {
            this.url = url;
            this.mill = mill;
        }

        @Override
        public void run() {
            VideoUtils videoUtils = new VideoUtils(context);
            Bitmap bitmap = videoUtils.getFrams(data.get(backPosition).getMp4url(), mill);
            Message msg = new Message();
            msg.what = STATE_SHEET_PIC;
            msg.obj = bitmap;
            videoHandler.sendMessage(msg);

        }
    }


    Handler videoHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STATE_SHEET_PIC:
                    Bitmap bitmap = (Bitmap) msg.obj;
                    if (bitmap != null) {
                        img_bac.setVisibility(View.VISIBLE);
                        img_bac.setImageBitmap(bitmap);
                        pg_iv.setVisibility(View.GONE);
                    }
                    break;
                case STATE_UPDATE_PROGRESS:
                    seekBar.setProgress((Integer) msg.obj);
                    break;
            }
        }
    };

    private void start() {
        imgPlay.setImageResource(R.mipmap.imb_play);
        videoView.start();


    }

    public void pause() {
        imgPlay.setImageResource(R.mipmap.imb_play_normal);
        videoView.pause();
    }


    @Override
    public void onItemClickListener(View view, int posision) {
        //更改hashMap的状态
        adapter.notifyDataSetChanged();
        txtTitle.setText(list.get(posision).getTitle());
        sendGetBtPicRequest(list.get(posision).getPlaylist_id());
    }

    @Event(value = {R.id.imgTo, R.id.imgBack, R.id.imgPlay, R.id.txtAdd, R.id.txtSubmit, R.id.txtDelete, R.id.txtClear, R.id.txtSave})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtSubmit:
                if (!CommonUtils.isFastDoubleClick()) {
                    if (currentPosition >= 0) {
                        if (data == null || data.size() <= 0) {
                            ToastUtils.showToast(context, "当前列表为空，请选择图片");
                            return;
                        }
                        SAVE_SUBMIT_DEFAULT = SUBMIT;
                        submit(SAVE_SUBMIT_DEFAULT);
                    }
                }
                break;
            case R.id.txtDelete:
                if (!CommonUtils.isFastDoubleClick()) {
                    if (currentPosition >= 0) {
                        this.SAVE_SUBMIT_DEFAULT = SAVE_SUBMIT_DEFAULT;
                        sendDeleteHttpRequest();
                    }
                }
                break;
            case R.id.txtClear:
                if (currentPosition >= 0) {
                    SAVE_SUBMIT_DEFAULT = SAVE_SUBMIT_DEFAULT;
                    if (list.get(currentPosition).getPlayliststatus().equals("1")) {
                        data.clear();
                        bottomAdapter.notifyDataSetChanged();
                        return;
                    }
                    sendDeleteAllHttpRequest();
                }

                break;
            case R.id.txtSave:
                if (!CommonUtils.isFastDoubleClick()) {
//                if (currentPosition >= 0) {
                    if (data == null || data.size() <= 0) {
                        ToastUtils.showToast(context, "当前列表为空，请选择图片");
                        return;
                    }
                    SAVE_SUBMIT_DEFAULT = SAVE;
                    submit(SAVE_SUBMIT_DEFAULT);
//                }
                }
                break;
            case R.id.txtAdd:
                //添加
                insertIntoGridView();
                break;
            case R.id.imgPlay:

                if (!isBuffed) {
                    ToastUtils.showToast(context, "视频正在缓存中，请稍后");
                    return;
                }
                img_bac.setVisibility(View.GONE);
                if (videoView.isPlaying()) {
                    pause();
                } else {
                    start();
                    //同时显示中间的thumb
                    seekBar.setInvisibleThumb(true);


                }
                break;
            case R.id.imgBack:
                //往后跳一帧
                if (STATE == 0) {
                    if (minValue - TIME_MILLS <= 0) {
                        minValue = 0;
                    } else {
                        minValue = minValue - TIME_MILLS;
                    }
                    seekBar.setMinWard(minValue);
//                    setMediaPosition(minValue);
                } else if (STATE == 2) {
                    //往前跳一帧
                    if (maxValue - TIME_MILLS <= minValue) {
                        maxValue = minValue;
                    } else {
                        maxValue = maxValue - TIME_MILLS;

                    }
                    seekBar.setMaxWard(maxValue);
//                    setMediaPosition(maxValue);
                }
                break;
            case R.id.imgTo:
                if (STATE == 0) {
                    if (minValue + TIME_MILLS >= maxValue) {
                        minValue = maxValue;
                    } else {
                        minValue = minValue + TIME_MILLS;
                    }
                    seekBar.setMinWard(minValue);
//                    setMediaPosition(minValue);
                } else if (STATE == 2) {
                    //往前跳一帧
                    if (maxValue + TIME_MILLS >= videoView.getDuration()) {
                        maxValue = (int) videoView.getDuration();
                    } else {
                        maxValue = maxValue + TIME_MILLS;
                    }
                    seekBar.setMaxWard(maxValue);
//                    setMediaPosition(maxValue);
                }
                break;
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

            if (currentPosition >= 0) {
                bottomBean.setPlaylist_id(list.get(currentPosition).getPlaylist_id());
                bottomBean.setMedia_id(list.get(currentPosition).getMedia_id());
                bottomBean.setAuthor_id(list.get(currentPosition).getAuthor_id());

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
            bottomBean.setMp4url("0");
            bottomBean.setDuration("0");
            bottomBean.setItem_id("0");
            data.add(bottomBean);
            bottomAdapter.notifyDataSetChanged();

        }
    }


    //删除全部
    private void sendDeleteAllHttpRequest() {
        if (currentPosition >= 0) {
            DEFAULT_DELETE = 1;
            NewsCutDeleteAllBiz biz = new NewsCutDeleteAllBiz();
            biz.setNetCallBack(this);
            biz.requestCutDeleteAll(ServerConfig.GET, list.get(currentPosition).getPlaylist_id());
        }


    }

    //删除指定的项
    private void sendDeleteHttpRequest() {

        if (backPosition >= 0 && data != null && data.size() > 0) {
            if (list.get(currentPosition).getPlayliststatus().equals("1")) {
                data.remove(backPosition);
                bottomAdapter.notifyDataSetChanged();
            } else {
                if (data.get(backPosition).getIsNet().equals("0")) {
                    //代表本地
                    data.remove(backPosition);
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

    private void sendGetHeadPic() {
        NewsHeadPicBiz biz = new NewsHeadPicBiz();
        biz.setNetCallBack(this);
        biz.requestHeadPic(ServerConfig.GET);

    }

    private void sendGetBtPicRequest(String id) {
        data.clear();
        bottomAdapter.notifyDataSetChanged();
        NewsHeadPicInfoListBiz picInfoListBiz = new NewsHeadPicInfoListBiz();
        picInfoListBiz.setNetCallBack(this);
        picInfoListBiz.requestHeadPicInfoList(ServerConfig.GET, id);
    }

    //初始化
    private void initRecycleView() {
        LinearLayoutManager manager = new LinearLayoutManager(baseActivity);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        rv_view.setLayoutManager(manager);
        adapter = new CutListAdapter(context, list);
        rv_view.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    /**
     * 请求成功返回值
     *
     * @param response
     */
    @Override
    public void onSuccess(Response response) {
        if (response instanceof HeadPicList) {
            refreshLayout.onRefreshComplete();
            HeadPicList picList = (HeadPicList) response;
            if (picList != null) {
                list = picList.getData();
                if (list != null && list.size() > 0) {
                    initRecycleView();
                    if (currentPosition < 0) {
                        return;
                    }
                    sendGetBtPicRequest(list.get(currentPosition).getPlaylist_id());
                    txtTitle.setText(list.get(currentPosition).getTitle());
                    ToastUtils.showToast(context, "列表刷新成功");
                }
            }
        } else if (response instanceof NewsCutBottomPicListBean) {
            //底部图片接口
            backPosition = 0;
            picListBean = (NewsCutBottomPicListBean) response;
//            txtTitle.setText(picListBean.getTitle());
            sList.clear();
            data.clear();
            sList = picListBean.getData();
            if (sList == null) {
                sList = new ArrayList<>();
            }
            data.addAll(sList);
            bottomAdapter.notifyDataSetChanged();

        } else {
            if (DEFAULT_DELETE == 0) {
                ToastUtils.showToast(context, "删除成功");
                sendGetBtPicRequest(list.get(currentPosition).getPlaylist_id());
                if (data.size() <= 0) {
                    sendGetHeadPic();
                }
            } else if (DEFAULT_DELETE == 1) {
                ToastUtils.showToast(context, "删除全部成功");
                //清楚数据
                data.clear();
                backPosition = -1;
                bottomAdapter.notifyDataSetChanged();
                sendGetHeadPic();

            }
            if (SAVE_SUBMIT_DEFAULT.equals(SAVE)) {
                ToastUtils.showToast(context, "保存成功");
                isSaved = true;
                txtTitle.setText(title);
                sendGetHeadPic();
            } else if (SAVE_SUBMIT_DEFAULT.equals(SUBMIT)) {
                ToastUtils.showToast(context, "提交发布成功");
                //清空底部列表
                data.clear();
                currentPosition = -1;
                bottomAdapter.notifyDataSetChanged();
                sendGetHeadPic();
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
                                           dialog.dismiss();
                                           LogUtil.d("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + data.toString() + data.size());
                                           if (data != null && data.size() > 0) {
                                               if (sList != null) {
                                                   //开始循环遍历判断是不是有状态
                                                   if (MyTextUtils.isEmpty(etTitle, "剪辑标题不能为空")) {
                                                       title = etTitle.getText().toString().trim();
                                                       if (SAVE_SUBMIT_DEFAULT.equals(SUBMIT)) {
                                                           for (int i = 0; i < list.size(); i++) {
                                                               if (list.get(i).getTitle().equals(etTitle.getText().toString())) {
                                                                   if (list.get(i).getPlayliststatus().equals("1")) {
                                                                       ToastUtils.showToast(context, "剪辑列表已存在,请编辑名字重新提交");
                                                                       return;
                                                                   }
                                                               }
                                                           }
                                                       }
                                                       //开始调用保存接口
                                                       sendSaveBottomHttpRequest(etTitle, status);
                                                   }
                                               }

                                           } else {
                                               ToastUtils.showToast(context, "剪辑列表为空，请重新剪辑");
                                           }
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
        if (currentPosition != -1) {
            //说明是提交不是新建
            paramMap.put("playlist_id", currentPosition >= 0 ? list.get(currentPosition).getPlaylist_id() : "0");
            paramMap.put("playliststatus", currentPosition >= 0 ? list.get(currentPosition).getPlayliststatus() : "0");
            paramMap.put("media_id", currentPosition >= 0 ? list.get(currentPosition).getMedia_id() : "0");
            paramMap.put("author_id", currentPosition >= 0 ? list.get(currentPosition).getAuthor_id() : "0");
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
            paramMap.put("item_order[" + i + "]", data.get(i).getItem_order());
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
        this.path = data.get(position).getMp4url();
        playVideo();
        timeMills = TimeUtils.getTimeMill(data.get(position).getIn_position_time_code(), data.get(position).getOut_position_time_code());
        isNewCut = true;
    }

    /**
     * 设置出入点
     *
     * @param duration
     */
    public void setSeekBarPosition(int duration) {
        seekBar.setRangeSeekBarParam(0, 0, duration);
        seekBar.minText = data.get(backPosition).getIn_position_time_code();
        seekBar.maxText = data.get(backPosition).getOut_position_time_code();
        LogUtil.d("---------" + seekBar.minText + "----------" + seekBar.maxText);
        seekBar.setMinAndMaxValue((int) timeMills[0], (int) timeMills[1]);
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
            LogUtil.d("第二次----------------------------------------------------------" + path);
            isSame = false;
            initView();
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


    @Override
    public void onLeftRefreshing() {
        sendGetHeadPic();
    }

    @Override
    public void onRightRefreshing() {

    }


    /**
     * 对外公开方法
     * 判断当前列表是否为
     * 保存  提交状态
     * 如果不是保存状态   则提示保存
     */
    public void checkIsSaveCut() {

        if (currentPosition < 0) {
            if (data.size() > 0) {
                if (!isSaved) {
                    //有数据
                    final MyAlertDialog dialog = new MyAlertDialog(context).builder();
                    dialog.setMsg("是否保存");
                    dialog.setPositiveButton("是", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isSaved = true;
                            txtSave.performClick();
                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton("否", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            isSaved = false;
                            dialog.dismiss();
                            data.clear();
                            bottomAdapter.notifyDataSetChanged();
                        }
                    });
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }
        }
    }


    /**
     * 三项联动
     */
    public void setCurrentPosition(int position) {
        currentPosition = position;
        adapter.notifyDataSetChanged();
        sendGetBtPicRequest(list.get(currentPosition).getPlaylist_id());
    }


}
