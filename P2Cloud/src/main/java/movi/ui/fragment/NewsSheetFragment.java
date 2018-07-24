package movi.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import movi.MainApplication;
import movi.base.BaseActivity;
import movi.base.BaseFragment;
import movi.base.Response;
import movi.net.biz.NewsDeleteSheetBiz;
import movi.net.biz.NewsFileUploadBiz;
import movi.net.biz.NewsSheetMovieBiz;
import movi.net.biz.NewsSheetPicBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.ui.adapter.CutListAdapter;
import movi.ui.adapter.MobileBottomPicAdapter;
import movi.ui.bean.HeadPicList;
import movi.ui.bean.NewsSheetBtPicListBean;
import movi.ui.bean.NewsSheetMovieListBean;
import movi.ui.bean.NewsSheetPicBean;
import movi.ui.fragment.main.HomeFragment;
import movi.ui.inter.OnItemClickListenner;
import movi.ui.view.ActionSheetDialog;
import movi.ui.view.MyAlertDialog;
import movi.ui.view.photopicker.PhotoPickerActivity;
import movi.utils.CachePathUtils;
import movi.utils.CommonUtils;
import movi.utils.LogUtils;
import movi.utils.MyTextUtils;
import movi.utils.TimeUtils;
import movi.utils.ToastUtils;
import movi.utils.VideoUtils;
import movi.view.HorirontalHeaderLayout;
import movi.view.lib.HorizontalRefreshLayout;
import movi.view.photopicker.utils.OtherUtils;

import static android.R.attr.path;
import static com.ctvit.p2cloud.R.id.txtSave;


/**
 * 抽图Fragment
 */
@ContentView(value = R.layout.news_sheet)
public class NewsSheetFragment extends BaseFragment implements
        NewsFileUploadBiz.MyCallBack,
        NetCallBack, SeekBar.OnSeekBarChangeListener,
        MobileBottomPicAdapter.PhotoClickCallBack,
        AdapterView.OnItemClickListener {
    @ViewInject(R.id.gr_galary)
    private GridView gr_galary;
    @ViewInject(R.id.sb)
    private SeekBar seekBar;
    @ViewInject(R.id.img_sheet)
    private ImageView img_sheet;
    @ViewInject(R.id.txtNewsCut)
    private TextView txtNewsCut;
    @ViewInject(R.id.txtTimeLeanth)
    private TextView txtTimeLeanth;
    @ViewInject(R.id.pg_iv)
    private ProgressBar pg_iv;
    @ViewInject(R.id.txtsave)
    private TextView txtsave;
    private HorizontalRefreshLayout refreshLayout;
    //调用系统相机拍照
    private static final int PHOTO_CHOSE = 3;
    private File mTmpFile;
    public ArrayList<NewsSheetPicBean> grList;
    public MobileBottomPicAdapter adapter;
    private CutListAdapter cutAdapter;
    private ArrayList<NewsSheetMovieListBean.NewsMovieBean> newsMovieBeans;
    private String bitmapPath = "";
    private Bitmap bitmap;
    private int TIME_MILLS = 40;
    private int currentPostion = -1;
    //指定当前的第一段视频的
    private int i;
    //记录gridview点击的哪一个
    public static int grindex = 0;
    //记录当前位置所在视频段的位置
    public static int movieCurrentPostion = 0;
    private static final int TAKE_PHOTO = 1;
    private static final int CROP_PHOTO = 2;
    private NewsSheetBtPicListBean sheetBtPicListBean;
    private ArrayList<NewsSheetPicBean> picList;
    private int DEFAULT;
    //封面id
    private String fengMianId;
    public static boolean isSubmit = false;
    private List<HeadPicList.HeadPic> data;

    private HorirontalHeaderLayout layoutHeader;
    private static final int DEFAULT_SUBMIT = 2;
    private static final int DEFAULT_SAVE = 1;
    private static final int DEFAULT_DELETE = 3;
    private int DEFAULT_FENGMIAN = 5;
    private boolean isNewPic;

    //判断是否保存过抽图
    private boolean isNullSaveSheetPic;


    public void setLayoutHeader(HorirontalHeaderLayout layoutHeader) {
        this.layoutHeader = layoutHeader;
        this.refreshLayout = (HorizontalRefreshLayout) layoutHeader.findViewById(R.id.refresh);
    }

    @Override
    protected int getResource() {
        return R.layout.news_sheet;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        initListenner();
        initGridAdapter();
    }

    private void initListenner() {
        layoutHeader.setOnSheetListenner(new OnItemClickListenner() {
                                             @Override
                                             public void onSheetItemClickListenner(View view, int position, List<HeadPicList.HeadPic> list, int state) {
                                                 if (HorirontalHeaderLayout.STATE_NEWS_SHEET_FRAGMENT == state) {
                                                     data = list;
                                                     HeadPicList.HeadPic headPic = list.get(position);
                                                     sendGetMovie(headPic.getPlaylist_id());
                                                     sendGetBottomPic(headPic.getPlaylist_id());
                                                     LogUtil.d("当前第几个选中NewSheet");
                                                 }
                                             }

                                             @Override
                                             public void onSheetItemSuccess(List<HeadPicList.HeadPic> l) {
                                                 data = l;
                                             }
                                         }

        );
    }


    //初始化下面图片展示gridview
    private void initGridAdapter() {
        grList = new ArrayList<NewsSheetPicBean>();
        grList.add(new NewsSheetPicBean());
        adapter = new MobileBottomPicAdapter(context, grList);
        gr_galary.setAdapter(adapter);
        adapter.setPhotoClickCallBack(this);
        gr_galary.setOnItemClickListener(this);


    }


    /**
     * 发送请求视频
     */
    private void sendGetMovie(String playlistid) {
        BaseActivity.showProgressDialog(context);
        NewsSheetMovieBiz biz = new NewsSheetMovieBiz();
        biz.setNetCallBack(this);
        biz.requestSheetMovie(ServerConfig.GET, playlistid);
    }

    /**
     * 发送请求地步图片
     */
    private void sendGetBottomPic(String playlistid) {
//        BaseActivity.showProgressDialog(context);
        NewsSheetPicBiz biz = new NewsSheetPicBiz();
        biz.setNetCallBack(this);
        biz.requestSheetPic(ServerConfig.GET, playlistid);
    }

    /**
     * 重置背景同时释放bitmap
     */
    public void resetBitmapBac() {
        img_sheet.setImageBitmap(null);
        if (bitmap != null) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 判断是否当前的
     * 按钮能够点击
     */

    public boolean canOnclick(String playlistStatus) {
        if (playlistStatus.equals("1")) {
            return false;
        }
        return true;
    }

    /**
     * 点击事件的监听
     *
     * @param view
     */
    @Event(value = {R.id.imgTo, R.id.imgReturn, R.id.txtAdd, R.id.txtsave, R.id.txtSubmit, R.id.txtDelete, R.id.txtsetFengMian})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.imgTo:
                //抽图进行展示
                if (newsMovieBeans != null && newsMovieBeans.size() > 0) {
                    pg_iv.setVisibility(View.VISIBLE);
                    currentPostion = currentPostion + TIME_MILLS;
                    if (currentPostion >= seekBar.getMax()) {
                        currentPostion = seekBar.getMax();
                    }
                    seekBar.setProgress(currentPostion);
                    resetBitmapBac();
                    new Thread(new SheetPicThread(i, currentPostion)).start();
                }
                break;
            case R.id.imgReturn:
                if (newsMovieBeans != null && newsMovieBeans.size() > 0) {
                    pg_iv.setVisibility(View.VISIBLE);
                    currentPostion = currentPostion - TIME_MILLS;
                    if (currentPostion <= 0) {
                        currentPostion = 0;
                    }
                    seekBar.setProgress(currentPostion);
                    resetBitmapBac();
                    new Thread(new SheetPicThread(i, currentPostion)).start();
                }
                break;
            case R.id.txtAdd:
                /**
                 * 抽图保存 图片
                 * 同时放在下边的gridViewreturn
                 */
                if (layoutHeader.isCheckSubmit()) {
                    ToastUtils.showToast(context, "当前是提交状态，不可修改");
                    return;
                }
                if (bitmap == null) {
                    return;
                }
                if (grList.size() > 10) {
                    ToastUtils.showToast(context, "当前列表图片数目不能大于10");
                    return;
                }
                bitmapPath = CachePathUtils.compressBit(bitmap, context);
                addPic(bitmapPath);
                break;

            case R.id.txtsave:
                sendSaveSheet();
                break;
            case R.id.txtSubmit:
                sendCutSheetSubmit();
                break;

            case R.id.txtsetFengMian:
                DEFAULT = DEFAULT_FENGMIAN;
                if (layoutHeader.currentPosition >= 0) {
                    if (canOnclick(data.get(layoutHeader.currentPosition).getPlayliststatus())) {
                        if (grList.size() <= 1) {
                            ToastUtils.showToast(context, "当前列表为空，不能进行此操作");
                            return;
                        }
                        setFengMian();
                    } else {
                        ToastUtils.showToast(context, "当前已是提交状态");
                    }
                }

                break;
            case R.id.txtDelete:
                /**
                 * 判断是不是网络上的图片
                 */
                if (!CommonUtils.isFastDoubleClick()) {
                    DEFAULT = DEFAULT_DELETE;
                    if (layoutHeader.currentPosition >= 0) {
                        if (canOnclick(data.get(layoutHeader.currentPosition).getPlayliststatus())) {
                            if (grList.size() <= 2) {
                                ToastUtils.showToast(context, "请保留最后一张设置封面");
                                return;
                            }
                            delete();
                        } else {
                            ToastUtils.showToast(context, "当前已是提交状态");

                        }
                    } else {
                        ToastUtils.showToast(context, "请先选择一个片段");
                    }
                }
                break;

        }
    }

    private void sendSaveSheet() {
        DEFAULT = DEFAULT_SAVE;
        saveAndSubmitSheetInfo(DEFAULT);
    }


    private void sendCutSheetSubmit() {
        if (!layoutHeader.isCheckSubmit()) {
            if (layoutHeader.currentPosition >= 0) {
                if (isSaved()) {
                    HomeFragment.homeFragment.sendSubmitCut();
                } else {
                    checkIsSaveSheet();
                }
            } else {
                ToastUtils.showToast(context, "请先选择一个片段");
            }
        } else {
            ToastUtils.showToast(context, "当前为提交状态");
        }


    }


    /**
     * 保存或者提交抽图信息
     *
     * @param tag
     */
    private void saveAndSubmitSheetInfo(int tag) {
        if (!layoutHeader.isCheckSubmit()) {
            if (layoutHeader.currentPosition >= 0) {
                BaseActivity.showProgressDialog(context);
//                HomeFragment.homeFragment.sendSubmitCut();
                sendSaveOrSubmitRequest(tag);
                isSubmit = true;
            } else {
                ToastUtils.showToast(context, "请先选择一个片段");
            }
        } else {
            ToastUtils.showToast(context, "当前为提交状态");
        }

    }

    private void addPic(String path) {
        isNullSaveSheetPic = true;
        NewsSheetPicBean picBean = new NewsSheetPicBean();
        picBean.setId("0");
        picBean.setIs_cover("0");
//        picBean.setLogo("0");
        picBean.setPlaylist_id("0");
        picBean.setTitle("0");
        picBean.setType("0");
        picBean.setLogo(path);
        picBean.setIsNet("0");
        grList.add(1, picBean);
        adapter.notifyDataSetChanged();
        LogUtil.d(picBean.toString());

    }

    /**
     * 删除数据
     */
    private void delete() {
        if (grindex > 0) {

            if (grList.get(grindex).getIsNet().equals("1")) {
                if (!grList.get(grindex).getId().equalsIgnoreCase(fengMianId)) {
                    //发送删除请求
                    NewsDeleteSheetBiz biz = new NewsDeleteSheetBiz();
                    biz.setNetCallBack(this);
                    biz.requestDeleteSheet(ServerConfig.GET, grList.get(grindex).getId());
                } else {
                    ToastUtils.showToast(context, "当前封面不能删除");
                }
            } else {
                grList.remove(grindex);
                adapter.notifyDataSetChanged();
            }
        } else {
            if (grList.size() >= 1) {
                ToastUtils.showToast(context, "请选择图片");
            } else {
                ToastUtils.showToast(context, "当前图片列表为空");
            }
        }

    }

    /**
     * 设置封面
     */
    private void setFengMian() {
        for (int i = 1; i < grList.size(); i++) {
            if (grindex == i) {
                grList.get(grindex).setIs_cover("1");
            } else {
                grList.get(i).setIs_cover("0");
            }
        }
        adapter.setList(grList);
        adapter.notifyDataSetChanged();
    }

    /**
     * 保存提交处理
     *
     * @param index
     */
    public void sendSaveOrSubmitRequest(int index) {
        //判断一下是否当前无帧图
        if (grList.size() <= 1) {
            ToastUtils.showToast(context, "当前列表为空，不能进行此操作");
            return;
        }
        DEFAULT = index;
        sendFileUpload();
    }

    /**
     * 弹出提交保存的window
     */
    private void sendFileUpload() {
        /**
         * 判断
         */
        if (layoutHeader.currentPosition >= 0) {
            String title = "抽图列表";
            String is_cover_id = "0";
            String is_cover_id_end = "0";
            String is_cover = "0";
            for (int i = 1; i < grList.size(); i++) {
                if (grList.get(i).getIs_cover().equals("1")) {
                    NewsSheetPicBean picBean = grList.get(i);
                    is_cover = i + "";
                    is_cover_id = picBean.getId();
                }
            }
            //判断is_cover_id_end
            if (sheetBtPicListBean != null) {
                is_cover_id_end = sheetBtPicListBean.getIs_cover_id_end();
                if (is_cover_id_end == null) {
                    is_cover_id_end = "0";
                }
            } else {
                is_cover_id_end = "0";
            }
            if (grList.size() <= 1) {
                ToastUtils.showToast(context, "图帧列表不能为空");
                return;
            }
            String playlistid = "0";
            if (layoutHeader.currentPosition >= 0) {
                playlistid = data.get(layoutHeader.currentPosition).getPlaylist_id();
            }
            NewsFileUploadBiz biz = new NewsFileUploadBiz();
            biz.setMyCallBack(this);
            biz.requestFileUpload(ServerConfig.POST, is_cover, title, is_cover_id, is_cover_id_end, playlistid, grList);
        } else {
            ToastUtils.showToast(context, "请选择一个片段");
        }


    }

    /**
     * 下方gridView的点击监听
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        this.grindex = position;
        adapter.notifyDataSetChanged();
        showCurrentPicAtVideo();
    }

    //点击gridView显示相应的数据
    public void showCurrentPicAtVideo() {
        NewsSheetPicBean picBean = grList.get(grindex);
        Glide.with(context).load(picBean.getLogo()).into(img_sheet);


    }

    @Override
    public void onMyTakePhotoClick() {
        new ActionSheetDialog(context).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                .addSheetItem("照相机", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        showCamera(TAKE_PHOTO);
                        LogUtil.d("---------------------------------------------" + "当前的");

                    }
                }).addSheetItem("图库", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
            @Override
            public void onClick(int which) {
                Intent intent = new Intent(MainApplication.getContext(), PhotoPickerActivity.class);
                intent.putExtra(PhotoPickerActivity.EXTRA_SHOW_CAMERA, false);
                intent.putExtra(PhotoPickerActivity.EXTRA_SELECT_MODE, 1);
                intent.putExtra(PhotoPickerActivity.EXTRA_MAX_MUN, 12);
                startActivityForResult(intent, PHOTO_CHOSE);
            }
        }).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == PHOTO_CHOSE && resultCode == Activity.RESULT_OK) {// 直接从相册获取
            //获取图库
            ArrayList<String> StringList = data.getStringArrayListExtra(PhotoPickerActivity.KEY_RESULT);

            setBaoZhuangBean(StringList);
        } else if (requestCode == TAKE_PHOTO && resultCode == Activity.RESULT_OK) {// 调用相机拍照
            if (mTmpFile != null) {
                startPhotoZoom(Uri.fromFile(mTmpFile), CROP_PHOTO);
            }
        } else if (requestCode == CROP_PHOTO && resultCode == Activity.RESULT_OK) {
            Bitmap bm = data.getParcelableExtra("data");
            String path = CachePathUtils.compressBit(bm, context);
            setPicToView(path);
            LogUtil.d("----------------" + path);

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 包装bean
     *
     * @param stringList
     */
    private void setBaoZhuangBean(ArrayList<String> stringList) {
        if (stringList != null && stringList.size() > 0) {
            ArrayList<NewsSheetPicBean> sheetPicBean = new ArrayList<>();
            for (String s : stringList) {
                NewsSheetPicBean picBean = new NewsSheetPicBean();
                picBean.setId("0");
                picBean.setIs_cover("0");
                picBean.setLogo("0");
                picBean.setPlaylist_id("0");
                picBean.setTitle("0");
                picBean.setType("0");
                picBean.setLogo(s);
                picBean.setIsNet("0");
                sheetPicBean.add(picBean);
            }
            showResult(sheetPicBean);
        }
    }

    //展示数据
    private void showResult(ArrayList<NewsSheetPicBean> galeryList) {
        grList.addAll(1, galeryList);
        adapter.setList(grList);
        adapter.notifyDataSetChanged();

    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param
     */
    private void setPicToView(String path) {
        if (path != null) {
            addPic(path);
            LogUtil.d("当前文件路径为" + path);
           context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
        }
    }

    /**
     * 数据获取处理
     *
     * @param response
     */
    @Override
    public void onSuccess(Response response) {
        BaseActivity.dismissProgressDialog();
        if (response instanceof NewsSheetBtPicListBean) {
            sheetBtPicListBean = (NewsSheetBtPicListBean) response;
            picList = sheetBtPicListBean.getData();
            grList.clear();
            grList.add(new NewsSheetPicBean());
            if (picList != null) {
                grList.addAll(1, picList);
                for (int i = 1; i < grList.size(); i++) {
                    if (grList.get(i).getIs_cover().equals("1")) {
                        fengMianId = grList.get(i).getId();
                    }
                }
                adapter.notifyDataSetChanged();
            }
        } else if (response instanceof NewsSheetMovieListBean) {
            NewsSheetMovieListBean sheetMovieListBean = (NewsSheetMovieListBean) response;
            newsMovieBeans = sheetMovieListBean.getData();
            //获取总时长
            if (newsMovieBeans != null && newsMovieBeans.size() > 0) {
                long duration = TimeUtils.getDuration(newsMovieBeans);
                seekBar.setMax((int) duration * 40);
                seekBar.setProgress(0);
                currentPostion = 0;
                seekBar.setOnSeekBarChangeListener(this);
                txtTimeLeanth.setText("总时长为" + data.get(layoutHeader.currentPosition).getDuration_time_code_all() + "/" + newsMovieBeans.size() + "镜头");
                x.image().bind(img_sheet, data.get(layoutHeader.currentPosition).getPlaylistsrc());
                new Thread(new SheetPicThread(0, 0)).start();
            }
        } else {
            //删除抽图
            ToastUtils.showToast(context, "删除抽图成功");
            //重新请求
            if (grindex > 0) {
                grList.remove(grindex);
                adapter.notifyDataSetChanged();
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onFailure(Response error) {
        BaseActivity.dismissProgressDialog();
        if (refreshLayout != null) {
            refreshLayout.onRefreshComplete();
        }

    }

    /**
     * seekbar监听
     *
     * @param seekBar
     * @param progress
     * @param fromUser
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (newsMovieBeans != null && newsMovieBeans.size() > 0) {
            //获取当前第几段的视频
            i = TimeUtils.getVideoIndex(newsMovieBeans, seekBar.getProgress());
            LogUtil.e("当前的第几段视频" + i);
            LogUtil.e("当前的视频地址为" + newsMovieBeans.get(i).getMp4url());
            int progress = seekBar.getProgress();
            currentPostion = progress;
            int start = Integer.parseInt(newsMovieBeans.get(i).getIn_position());
//            int currentPostion=0;
            //进行判断获取开始帧
            if (i == 0) {
                //代表第一段
                movieCurrentPostion = progress + start;
                LogUtil.d("当前所在的位置是" + movieCurrentPostion);
            } else {
                int prePosition = 0;
                for (int j = 0; j < i; j++) {
                    //如果当前所在段大于第一段 减去之前的段 首先计算之前有多少
                    prePosition = prePosition + Integer.parseInt(newsMovieBeans.get(j).getOut_position()) - Integer.parseInt(newsMovieBeans.get(j).getIn_position());
                    LogUtil.d("当前的preposition为" + prePosition);
                }
                //在原视频地址中的位置
                movieCurrentPostion = progress - prePosition + start;
                LogUtil.d("当前所在的位置是" + currentPostion + "当前的进度是" + progress + "当前的之前位置是" + prePosition + "当前的开始位置是" + start);
                prePosition = 0;
            }
            pg_iv.setVisibility(View.VISIBLE);
            //开始抽帧
            resetBitmapBac();
            new Thread(new SheetPicThread(i, movieCurrentPostion)).start();


        }
    }

    /**
     * 保存图片
     *
     * @param result
     */
    @Override
    public void onSucces(String result) {
        Response  response= JSON.parseObject(result,Response.class);
        LogUtil.d("后台返回数据" + response);
        if (DEFAULT == DEFAULT_SAVE) {
            ToastUtils.showToast(context, "数据保存成功");

        } else if (DEFAULT == DEFAULT_SUBMIT) {
            ToastUtils.showToast(context, "提交合成成功");
        }

        isSubmit = false;
//        BaseActivity.dismissProgressDialog();
        txtNewsCut.setText(data.get(layoutHeader.currentPosition).getTitle());
        layoutHeader.sendGetHeadPic();
        sendGetBottomPic(data.get(layoutHeader.currentPosition).getPlaylist_id());

        if (refreshLayout != null) {
            refreshLayout.onRefreshComplete();
        }

    }

    @Override
    public void onFailure(String error) {
        isSubmit = false;
        if (DEFAULT == DEFAULT_SAVE) {
            ToastUtils.showToast(context, "数据保存失败");
        } else if (DEFAULT == DEFAULT_SUBMIT) {
            ToastUtils.showToast(context, "提交合成失败");
        }
//
        BaseActivity.dismissProgressDialog();
    }


    private class SheetPicThread implements Runnable {
        private int i;
        private int position;

        public SheetPicThread(int index, int position) {
            this.i = index;
            this.position = position;
        }

        @Override
        public void run() {
            //开始抽图
            VideoUtils videoUtils = new VideoUtils(context);
            //显示进度条
            if (null != newsMovieBeans && newsMovieBeans.size() > 0) {
                if (null != newsMovieBeans.get(i).getMp4url() && !newsMovieBeans.get(i).getMp4url().equals("0")) {
                    LogUtil.d("当前的抽帧的位置为" + position);
                    bitmap = videoUtils.getFrams(newsMovieBeans.get(i).getMp4url(), position);
                    if (bitmap == null) {
                        return;
                    }
                    Message message = Message.obtain();
                    message.what = 1;
                    message.obj = bitmap;
                    handler.sendMessage(message);
                } else {
//                    ToastUtils.showToast(context, "当前视频地址不存在");
                    pg_iv.setVisibility(View.GONE);
                }
            }
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    pg_iv.setVisibility(View.GONE);
                    bitmap = (Bitmap) msg.obj;
                    if (bitmap != null) {
                        img_sheet.setImageBitmap(bitmap);
                        LogUtil.d("抽帧成功");
                    } else {
                        ToastUtils.showToast(context, "抽图失败");
                    }
                    break;
            }

        }
    };

    /**
     * 选择相机
     */
    public void showCamera(int TAKE_PHOTO) {
        // 跳转到系统照相机
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(context.getPackageManager()) != null) {
            // 设置系统相机拍照后的输出路径
            // 创建临时文件
            mTmpFile = OtherUtils.createFile(context);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mTmpFile));
            startActivityForResult(cameraIntent, TAKE_PHOTO);
        } else {
            Toast.makeText(context,
                    R.string.msg_no_camera, Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri, int code) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, code);
    }

    /**
     * 判断是否抽图已经保存
     * 没保存提示保存
     */

    public boolean isSaved() {
        if (grList.size() > 0) {
            //有数据
            for (int i = 1; i < grList.size(); i++) {
                if (grList.get(i).getIsNet().equals("0")) {
                    return false;
                }
            }

        }
        return true;
    }

    public void checkIsSaveSheet() {
        if (!isSaved() && isNullSaveSheetPic) {
            final MyAlertDialog dialog = new MyAlertDialog(context).builder();
            dialog.setMsg("是否保存抽图");
            dialog.setPositiveButton("是", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    txtsave.performClick();
                    dialog.dismiss();
                    isNewPic = false;
                    isNullSaveSheetPic = false;
                }
            });
            dialog.setNegativeButton("否", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                    isNewPic = false;
                    isNullSaveSheetPic = false;
                }
            });
            dialog.setCancelable(false);
            dialog.show();

        }
    }

}
