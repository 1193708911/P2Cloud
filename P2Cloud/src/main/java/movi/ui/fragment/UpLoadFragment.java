package movi.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import movi.MainApplication;
import movi.ui.activity.NewsLocalCutActivity;
import movi.ui.adapter.UpLoadListAdapter;
import movi.base.BaseFragment;
import movi.ui.bean.ProgressBean;
import movi.ui.db.VideoInfo;
import movi.utils.PreferencesUtil;
import movi.utils.ToastUtils;
import movi.ui.view.MyAlertDialog;

/**
 * Created by chjj on 2016/7/27.
 * 上载界面
 *
 * @author xufuqiang
 */
@ContentView(R.layout.fragment_upload)
public class UpLoadFragment extends BaseFragment implements View.OnClickListener, UpLoadListAdapter.OnDeleteChangeListenner, UpLoadListAdapter.OnDataSetChangeListener, AdapterView.OnItemClickListener {
    @ViewInject(R.id.txtUpLoad)
    private TextView txtUpLoad;
    @ViewInject(R.id.lvUpLoad)
    private ListView lvUpLoad;
    private UpLoadListAdapter mLoadListAdapter;
    private List<VideoInfo> videoBeanList;
    private List<VideoInfo> mVideoBeans;
    private ProgressBean mProgressBean;
    /**
     * 记录当前是自动上载还是手动上载
     * 默认是自动上载
     */
    private boolean isZUpLoad = true;

    /**
     * 注册更新广播
     *
     * @return
     */
    private BroadcastReceiver receiver;
    private IntentFilter intentFilter;
    public static final String ACTION_SELECT = "SELECT";

    /**
     * 由于进程之间
     * 数据库只能通过contentprovide公用
     * 因此这里通过广播进行返回插入，防止mainApplication不能够使用
     * 解决死机状态
     */
    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_SELECT)) {
                try {
                    VideoInfo info = (VideoInfo) intent.getSerializableExtra("bean");
                    info.setUserName(MainApplication.getInstance().getUser().getUsername());
                    if(PreferencesUtil.getInt("UL")==1){
                        info.setUploadType(true);
                    }else{
                        info.setUploadType(false);
                    }
                    MainApplication.getInstance().getDbManager().saveBindingId(info);
                    selectSQL();
                } catch (DbException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected int getResource() {
        return R.layout.fragment_upload;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        initReceiver();
        initData();
    }

    private void initReceiver() {
        receiver = new MyReceiver();
        intentFilter = new IntentFilter(ACTION_SELECT);
        context.registerReceiver(receiver, intentFilter);
    }

    /**
     * 初始化相关集合
     */
    private void initData() {
        mProgressBean = new ProgressBean();
        mVideoBeans = new ArrayList<>();
        videoBeanList = new ArrayList<>();
        mLoadListAdapter = new UpLoadListAdapter(context, mVideoBeans);
        mLoadListAdapter.setDeleteChangeListener(this);
        mLoadListAdapter.setOnDataSetChangeListener(this);
        lvUpLoad.setAdapter(mLoadListAdapter);
        lvUpLoad.setOnItemClickListener(this);
        selectSQL();


    }


    /**
     * 提供一个方法用于更新ui
     * 重新获取数据库
     */
    public void selectSQL() {
        try {
            videoBeanList = MainApplication.getInstance().getDbManager().selector(VideoInfo.class).where("userName", "=", MainApplication.getInstance().getUser().getUsername()).orderBy("id", true).findAll();
            mVideoBeans.clear();
            if (videoBeanList != null && videoBeanList.size() > 0) {
                mVideoBeans.addAll(videoBeanList);
            }
            mLoadListAdapter.notifyMDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtZUpload:
                isZUpLoad = true;
                break;
            case R.id.txtSUpload:
                isZUpLoad = false;
                break;
        }
    }

    @Override
    public void onDestroy() {
        //销毁之前改变状态
        try {
            List<VideoInfo> infosList = MainApplication.getInstance().getDbManager().findAll(VideoInfo.class);
            for (VideoInfo info : infosList) {
                info.setUploadType(false);
                MainApplication.getInstance().getDbManager().saveOrUpdate(info);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }
        context.unregisterReceiver(receiver);
        super.onDestroy();
    }

    /**
     * 删除监听
     *
     * @param position
     */
    @Override
    public void onDeleteChange(final int position, final UpLoadListAdapter.ViewHolder viewHolder) {
        try {
            final MyAlertDialog dialog = new MyAlertDialog(context).builder();
            dialog.setTitle("提示");
            dialog.setMsg("是否删除该项上传");
            dialog.setNegativeButton("否", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.setPositiveButton("是", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        dialog.dismiss();
                        VideoInfo mVideoInfo = mVideoBeans.get(position);
                        deleteFile(mVideoInfo);
                        //同时更新图库
                        MainApplication.getInstance().getDbManager().delete(mVideoInfo);
                        ToastUtils.showToast(context, "删除成功");
                        selectSQL();

                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除文件
     *
     * @param mVideoInfo
     */
    private void deleteFile(VideoInfo mVideoInfo) {
        File file;
        file = new File(mVideoInfo.getVideoPath());
        if (file.exists()) {
            file.delete();
        }
        file = new File(mVideoInfo.getXmlFilePath());
        if (file.exists()) {
            file.delete();
        }
        file = new File(mVideoInfo.getPhotoPath());
        if (file.exists()) {
            file.delete();
        }

        file=new File(mVideoInfo.getCompressFileName());
        if(file.exists()){
            file.delete();
        }
    }


    /**
     * 上传完成之后删除
     *
     * @param position
     */
    public void deleteUploadPosition(int position, UpLoadListAdapter.ViewHolder viewHolder) {
        try {
            if (mVideoBeans.size() <= 0) {
                return;
            }
            VideoInfo mVideoInfo = mVideoBeans.get(position);
            selectSQL();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据变化的时候回调
     */
    @Override
    public void onDataSetChange(int postion, UpLoadListAdapter.ViewHolder viewHolder) {
        deleteUploadPosition(postion, viewHolder);
    }

    /**
     * 点击之后处理本地视频剪辑功能
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(context, NewsLocalCutActivity.class);
        intent.putExtra("bean", mVideoBeans.get(position));
        context.startActivity(intent);
    }


}
