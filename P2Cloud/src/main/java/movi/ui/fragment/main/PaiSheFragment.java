package movi.ui.fragment.main;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.ex.DbException;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;
import movi.MainApplication;
import movi.ui.adapter.ViewPagerAdapter;
import movi.base.BaseFragment;
import movi.lib.camera.VideoNewActivity1;
import movi.net.config.PermissionCodes;
import movi.ui.db.VideoInfo;
import movi.ui.fragment.UpLoadFragment;
import movi.utils.MyTextUtils;
import movi.utils.PreferencesUtil;
import movi.utils.ToastUtils;
import movi.lib.videocompress.FFMPEGUtils;
import movi.ui.view.CustomDialog;
import movi.ui.view.MyAlertDialog;
import movi.utils.XmlUtils;

/**
 * Created by chjj on 2016/7/22.
 */
@ContentView(R.layout.fragment_paishe)
public class PaiSheFragment extends BaseFragment implements FFMPEGUtils.OnVideoCompressCompletListener {
    @ViewInject(R.id.viewPager)
    private ViewPager viewPager;
    @ViewInject(R.id.txtUpLoad)
    private TextView txtUpLoad;
    @ViewInject(R.id.txtSetting)
    private TextView txtSetting;
    private UpLoadSettingFragment mUpLoadSettingFragment;
    private UpLoadFragment mUpLoadFragment;
    private List<Fragment> fragmentsList;
    /**
     * 调用摄像头成功后的返回值
     */
    private static final int REQUEST_OK = 0;
    //时长
    private String timeLeanth;
    //默认状态
    private static final String STATUS = "等待";
    //默认进度
    private static final int PROGRESS = 0;
    private String mCurrentTime;
    private String fileIconXmlName;
    private String newFilePath;
    private CustomDialog dialog;

    @Override
    protected int getResource() {
        return R.layout.fragment_paishe;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        initFragment();
    }

    /**
     * 初始化fragment
     */
    private void initFragment() {
        fragmentsList = new ArrayList<>();
        mUpLoadFragment = new UpLoadFragment();
        mUpLoadSettingFragment = new UpLoadSettingFragment();
        fragmentsList.add(mUpLoadFragment);
        fragmentsList.add(mUpLoadSettingFragment);
        ViewPagerAdapter adapter = new ViewPagerAdapter(manager, fragmentsList);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(new MyOnChangeClickListener());
        viewPager.setCurrentItem(0);

    }


    private final class MyOnChangeClickListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {

            if (position == 0) {
                resetBac(txtUpLoad);
                txtUpLoad.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.segment_left_enter));
            } else if (position == 1) {
                resetBac(txtSetting);
                txtSetting.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.segment_right_enter));
            }
            viewPager.setCurrentItem(position);
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    @Event(value = {R.id.txtUpLoad, R.id.txtSetting, R.id.img_camera})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.txtUpLoad:
                resetBac(txtUpLoad);
                viewPager.setCurrentItem(0);
                txtUpLoad.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.segment_left_enter));
                break;
            case R.id.txtSetting:
                resetBac(txtSetting);
                viewPager.setCurrentItem(1);
                txtSetting.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.segment_right_enter));
                break;
            case R.id.img_camera:
                //启动录像机
                PermissionGen.needPermission(this, PermissionCodes.PERMISSION_CAMERAL_WRITE, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO});
//                startCamera();
                break;
        }
    }

    /**
     * 权限回掉
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @PermissionSuccess(requestCode = PermissionCodes.PERMISSION_CAMERAL_WRITE)
    public void onStartCameral() {
        startCamera();
    }

    @PermissionFail(requestCode = PermissionCodes.PERMISSION_CAMERAL_WRITE)
    public void onUnStartCameral() {
        ToastUtils.showToast(getActivity(), "permissison  is  not  granted");
    }

    private void startCamera() {
        Intent intent = new Intent(getActivity(), VideoNewActivity1.class);
        intent.putExtra("FBL", PreferencesUtil.getInt("FBL"));
        startActivityForResult(intent, REQUEST_OK);
    }

    /**
     * 重置背景
     */
    private void resetBac(TextView textView) {
        txtUpLoad.setTextColor(context.getResources().getColor(R.color.white));
        txtSetting.setTextColor(context.getResources().getColor(R.color.white));
        textView.setTextColor(context.getResources().getColor(R.color.red));
        resetTextBack();


    }

    private void resetTextBack() {
        txtUpLoad.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.segment_left_normal));
        txtSetting.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.segment_right_normal));
    }

    /**
     * 录制视频成功之后的回调
     *
     * @param requestCode
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OK) {
                String path = data.getExtras().getString("path");
                timeLeanth = data.getExtras().getString("timeLeanth");
                //通知图库更新  测试
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
                //搞一个对话框提示用户输入名称
                showTitleDialog(path);

            }
        }
    }

    /**
     * 呈现对话框使用户添加名称
     */
    private void showTitleDialog(final String path) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.title_dialog, null);
        final EditText et_title = (EditText) rootView.findViewById(R.id.et_title);
        final MyAlertDialog dialog = new MyAlertDialog(context).builder();
        dialog.setView(rootView);
        dialog.setCancelable(false);
        dialog.setPositiveButton("确定", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyTextUtils.isEmpty(et_title, "视频名称不能为空，请填写")) {
                    dialog.dismiss();
                    String title = et_title.getText().toString();
                    newFilePath = compressVideo(path);
                    insertIntoSql(path, title);
                }
            }
        });
        dialog.show();
    }

    /**
     * 压缩后的回调
     * 因为压缩过程中开启了线程因此不能保证
     * 是否压缩完所以这里使用了接口回调的方式
     */
    @Override
    public void videoComplet() {
//        insertIntoSql(path);
    }

    /**
     * 调用此方法进行视频压缩
     *
     * @param path
     */
    private String compressVideo(String path) {
        FFMPEGUtils fmu = new FFMPEGUtils(getActivity());
        fmu.setOnVideoCompletLitener(this);
        String newFilePath = creteNewFile(path);
        fmu.cropVideo(path, newFilePath);
        return newFilePath;
    }

    /**
     * 插入到sql中
     * 1首先获取视频缩略图保存在本地并返回保存本地的路径
     * 2其次通过数据库保存   1视频  时长   2视频缩略图路径 3视频标题 4视频状态
     * 5视频进度
     */
    private void insertIntoSql(String path, String movieTitle) {
        try {
            String picPath = XmlUtils.saveBitMap(path);
            if (picPath == null) {
                ToastUtils.showToast(context, "视频必须录制两秒后使用");
                return;
            }

            //改为动态title
//            String title = CommonUtils.getRandomStr();
            String Status = STATUS;
            int progress = PROGRESS;
            /**
             * 开始存入数据库
             */
            VideoInfo videoInfo = new VideoInfo();
            videoInfo.setProgress(progress);
            videoInfo.setPicPath(picPath);
            videoInfo.setStatus(Status);
            videoInfo.setTimeLeanth(timeLeanth);
            videoInfo.setTitle(movieTitle);
            videoInfo.setVideoPath(path);
            videoInfo.setCompressFileName(newFilePath);
            videoInfo.setUserName(MainApplication.getInstance().getUser().getUsername());
            videoInfo.setTxtUploadText("上载");
            if (PreferencesUtil.getInt("UL") == 1) {
                videoInfo.setUploadType(true);
            } else {
                videoInfo.setUploadType(false);
            }
            VideoInfo mVideoInfo = XmlUtils.getXmlMetaData(context, videoInfo, path, picPath);
            MainApplication.getInstance().getDbManager().saveBindingId(mVideoInfo);
            //同时通知uploadFragment更新
            resetBac(txtUpLoad);
            viewPager.setCurrentItem(0);
            txtUpLoad.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.segment_left_enter));
            if (mUpLoadFragment != null) {
                mUpLoadFragment.selectSQL();
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    /**
     * 创建新的文件路径
     */
    String fileName = "";

    public String creteNewFile(String path) {
        if (path != null) {

            fileName = path.substring(path.lastIndexOf("/") + 1);
        }
        File dirFile = new File(Environment.getExternalStorageDirectory(), "/video/video");
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }
        File videoFile = new File(dirFile, fileName);
        if (videoFile.exists()) {
            videoFile.delete();
        }
        try {
            videoFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        LogUtil.d("--------------新的文件路径" + videoFile.getAbsolutePath());
        return videoFile.getAbsolutePath();
    }
}
