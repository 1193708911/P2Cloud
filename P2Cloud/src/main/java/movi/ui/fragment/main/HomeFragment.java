package movi.ui.fragment.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import movi.base.BaseFragment;
import movi.ui.bean.CloudListBean;
import movi.ui.fragment.CloudFragment;
import movi.ui.fragment.NewManuFragment;
import movi.ui.fragment.NewSynFragment;
import movi.ui.fragment.NewsCutFragment;
import movi.ui.fragment.NewsSheetFragment;
import movi.view.HorirontalHeaderLayout;


@ContentView(R.layout.fragment_home)
public class HomeFragment extends BaseFragment {
    @ViewInject(R.id.txt_cloud)
    private TextView txt_cloud;
    @ViewInject(R.id.txt_cut)
    private TextView txt_cut;
    @ViewInject(R.id.txt_sheet)
    private TextView txt_sheet;
    @ViewInject(R.id.txt_pic)
    private TextView txt_pic;
    @ViewInject(R.id.txt_wengao)
    private TextView txt_wengao;
    private static CloudFragment cloudFragment;
    private static NewsCutFragment cutFragment;
    private static NewsSheetFragment sheetFragment;
    private static NewManuFragment manuFragment;
    private static NewSynFragment synFragment;
    private static TextView[] titles;
    public static HomeFragment homeFragment;
    private IntentFilter intentFilter;
    public static final String ACTION_PRGRESS = "ACTION_PRGRESS";
    private MyProgressReceiver receiver;

    @ViewInject(R.id.layoutHeader)
    private HorirontalHeaderLayout layoutHeader;

    @Override
    protected void afterViews() {
        super.afterViews();
        homeFragment = this;
        initReceiver();
        initList();
    }


    private void initReceiver() {
        intentFilter = new IntentFilter(ACTION_PRGRESS);
        receiver = new MyProgressReceiver();
        context.registerReceiver(receiver, intentFilter);
    }


    private void initList() {
        titles = new TextView[]{txt_cloud, txt_cut, txt_sheet, txt_wengao, txt_pic};
        cloudFragment = new CloudFragment();
        cutFragment = new NewsCutFragment();
        cutFragment.setLayoutHeader(layoutHeader);
        sheetFragment = new NewsSheetFragment();
        sheetFragment.setLayoutHeader(layoutHeader);
        manuFragment = new NewManuFragment();
        manuFragment.setLayoutHeader(layoutHeader);
        synFragment = new NewSynFragment();
        layoutHeader.setVisibility(View.GONE);
        replaceNewsSynFragment();
        replaceNewsManuFragment();
        replaceNewsSheetFragment();
        replaceNewsCutFragment();
        replaceNewsCloudFragment();


    }

    @Override
    protected int getResource() {
        return R.layout.fragment_home;
    }


    @Event(value = {R.id.txt_cloud, R.id.txt_cut, R.id.txt_sheet, R.id.txt_wengao, R.id.txt_pic})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.txt_cloud:
                layoutHeader.setVisibility(View.GONE);
                replaceNewsCloudFragment();
//                checkSheetIsSave();
//                checkManuIsSave();
                break;
            case R.id.txt_cut:
                layoutHeader.setVisibility(View.VISIBLE);
                replaceNewsCutFragment();
                checkSheetIsSave();
                checkManuIsSave();
                break;
            case R.id.txt_sheet:
                layoutHeader.setVisibility(View.VISIBLE);
                replaceNewsSheetFragment();
                checkCutIsSave();
                checkManuIsSave();
                break;
            case R.id.txt_wengao:
                layoutHeader.setVisibility(View.VISIBLE);
                checkCutIsSave();
                checkSheetIsSave();
                replaceNewsManuFragment();
                break;
            case R.id.txt_pic:
                replaceNewsSynFragment();
                layoutHeader.setVisibility(View.GONE);
                break;
        }
    }

    /**
     * 判断剪辑列表中是否
     * 有需要保存的新建
     * 剪辑
     */
    public void checkCutIsSave() {
        cutFragment.checkIsSaveCut();
    }

    public   void  checkSheetIsSave(){
        sheetFragment.checkIsSaveSheet();
    }

    public  void  checkManuIsSave(){
        manuFragment.checkSaveManu();
    }
    /**
     * 初始化newsCutFragment
     * 点击云端列表切换到
     * 我的剪辑列表中
     */
    public void setNewsCutFragment(CloudListBean.CloudBean cloudBean) {
        cutFragment.setCloudPicBean(cloudBean);
        layoutHeader.setVisibility(View.VISIBLE);
        replaceNewsCutFragment();
    }

    /**
     * 重置背景
     */
    public void clearStatus() {
            layoutHeader.currentPosition = -1;
            layoutHeader.adapter.notifyDataSetChanged();
            cutFragment.data.clear();
            cutFragment.resetTitle();
            cutFragment.bottomAdapter.notifyDataSetChanged();
            sheetFragment.grList.clear();
            sheetFragment.adapter.notifyDataSetChanged();
            manuFragment.resetTextView();


    }

    /**
     * 切换fragment
     */
    private class MyProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_PRGRESS)) {
                setNewsCutFragment((CloudListBean.CloudBean) intent.getSerializableExtra("bean"));
            }
        }
    }


    /**
     * 重置背景
     * @param position
     */
    public static void resetBack(int position) {
        for (int i = 0; i < titles.length; i++) {
            if (i == position) {
                titles[i].setTextColor(Color.RED);
                titles[i].setTextSize(15);
            } else {
                titles[i].setTextSize(14);
                titles[i].setTextColor(Color.BLACK);
            }

        }
    }


    /**
     * 切换cutFragment
     */
    public void replaceNewsCloudFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideFragments(transaction);
        if (cloudFragment.isAdded()) {
            transaction.show(cloudFragment);
        } else {
            transaction.add(R.id.containner, cloudFragment);
        }
        transaction.commit();
        resetBack(0);

    }

    public void replaceNewsCutFragment() {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideFragments(transaction);
        if (cutFragment.isAdded()) {
            transaction.show(cutFragment);
        } else {
            transaction.add(R.id.containner, cutFragment);
        }
        transaction.commit();
        resetBack(1);

    }

    public void replaceNewsSheetFragment() {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideFragments(transaction);

        if (sheetFragment.isAdded()) {
            transaction.show(sheetFragment);
        } else {
            transaction.add(R.id.containner, sheetFragment);
        }
        transaction.commit();
        resetBack(2);

    }


    public void replaceNewsManuFragment() {

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideFragments(transaction);

        if (manuFragment.isAdded()) {
            transaction.show(manuFragment);
        } else {
            transaction.add(R.id.containner, manuFragment);
        }
        transaction.commit();
        resetBack(3);

    }

    public void replaceNewsSynFragment() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideFragments(transaction);
        if (synFragment.isAdded()) {
            transaction.show(synFragment);
        } else {
            transaction.add(R.id.containner, synFragment);
        }
        transaction.commit();
        resetBack(4);

    }

    /**
     * 吟唱所有的
     */
    private void hideFragments(FragmentTransaction transaction) {
        if (cloudFragment.isAdded()) {
            transaction.hide(cloudFragment);
        }
        if (cutFragment.isAdded()) {
            transaction.hide(cutFragment);
        }
        if (sheetFragment.isAdded()) {
            transaction.hide(sheetFragment);
        }
        if (manuFragment.isAdded()) {
            transaction.hide(manuFragment);
        }
        if (synFragment.isAdded()) {
            transaction.hide(synFragment);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        context.unregisterReceiver(receiver);
    }




    /**
     * 提交剪辑列表
     */
    public void sendSubmitCut() {
        cutFragment.sendSubmit();
    }



}
