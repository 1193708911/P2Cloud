package movi.view;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.ctvit.p2cloud.R;

import org.xutils.common.util.LogUtil;

import java.util.ArrayList;

import movi.base.Response;
import movi.net.biz.NewsHeadPicBiz;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.ui.adapter.CutListAdapter;
import movi.ui.bean.HeadPicList;
import movi.ui.inter.OnItemClickListenner;
import movi.ui.view.lib.RefreshCallBack;
import movi.ui.view.lib.SimpleRefreshHeader;
import movi.utils.ToastUtils;
import movi.view.lib.HorizontalRefreshLayout;

/**
 * Created by admin on 2017/3/20.
 */

public class HorirontalHeaderLayout extends LinearLayout implements NetCallBack, CutListAdapter.OnItemClickListener, RefreshCallBack {
    private ArrayList<HeadPicList.HeadPic> list;
    public CutListAdapter adapter;
    public Context context;
    private RecyclerView rv_view;
    public boolean isNew;
    public static int currentPosition = -1;
    private HorizontalRefreshLayout refreshLayout;
    private OnItemClickListenner OnCutListenner;
    private OnItemClickListenner onManuListenner;
    private OnItemClickListenner onSheetListenner;
    public static final int STATE_NEWS_CUT_FRAGMENT = 0X001;
    public static final int STATE_NEWS_SHEET_FRAGMENT = 0X002;
    public static final int STATE_NEWS_MANU_FRAGMENT = 0X003;
    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    public void setOnSheetListenner(OnItemClickListenner onSheetListenner) {
        this.onSheetListenner = onSheetListenner;
    }

    public void setOnManuListenner(OnItemClickListenner onManuListenner) {
        this.onManuListenner = onManuListenner;
    }

    public void setOnCutListenner(OnItemClickListenner onCutListenner) {
        OnCutListenner = onCutListenner;
    }

    public HorirontalHeaderLayout(Context context) {
        super(context);
        this.context = context;
        initLayout();
    }


    public HorirontalHeaderLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initLayout();
    }

    private void initLayout() {
        inflate(context, R.layout.layout_head, this);
        rv_view = (RecyclerView) findViewById(R.id.rv_view);
        refreshLayout = (HorizontalRefreshLayout) findViewById(R.id.refresh);
        initReLayout();
        sendGetHeadPic();
        LogUtil.d("当前重新加载了布局");
    }

    private void initReLayout() {
        refreshLayout.setEnable(true);
        refreshLayout.setRefreshHeader(new SimpleRefreshHeader(context), HorizontalRefreshLayout.START);
        refreshLayout.setRefreshCallback(this);

    }

    //初始化
    private void initRecycleView() {
        LinearLayoutManager manager = new LinearLayoutManager(context);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        rv_view.setLayoutManager(manager);
        adapter = new CutListAdapter(context, list);
        rv_view.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
    }

    public void sendGetHeadPic() {
        NewsHeadPicBiz biz = new NewsHeadPicBiz();
        biz.setNetCallBack(this);
        biz.requestHeadPic(ServerConfig.GET);

    }

    @Override
    public void onSuccess(Response response) {
        refreshLayout.onRefreshComplete();
        HeadPicList picList = (HeadPicList) response;
        if (picList != null) {
            list = picList.getData();
            if (list != null && list.size() > 0) {
                initRecycleView();
                if (currentPosition == -1 && isNew) {
                    currentPosition = 0;
                    isNew = false;
                    onItemClickListener(rv_view, 0);
                }
                if(OnCutListenner!=null){
                    OnCutListenner.onCutItemSuccess(list);
                }
                if(onSheetListenner!=null){
                    onSheetListenner.onSheetItemSuccess(list);
                }
                if(onManuListenner!=null){
                    onManuListenner.onManuItemSucess(list);
                }
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

    /**
     * 点击事件监听
     *
     * @param view     选中当前的并且
     * @param posision
     */
    @Override
    public void onItemClickListener(View view, int posision) {
        this.currentPosition = posision;
        for (int i = 0; i < list.size(); i++) {
            if (i == posision) {
                list.get(i).setChecked(true);
            } else {
                list.get(i).setChecked(false);
            }
            adapter.notifyDataSetChanged();
            LogUtil.d("当前第几个选中" + posision);
        }
//同时更新三个界面
        if (OnCutListenner != null) {
            OnCutListenner.onCutItemClickListenner(view, posision, list, STATE_NEWS_CUT_FRAGMENT);
        }
        if (onSheetListenner != null) {
            onSheetListenner.onSheetItemClickListenner(view, posision, list, STATE_NEWS_SHEET_FRAGMENT);
        }
        if (onManuListenner != null) {
            onManuListenner.onManuItemClickListenner(view, posision, list, STATE_NEWS_MANU_FRAGMENT);
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
     * 在这里判断是不是提交状态
     */

    public boolean isCheckSubmit() {
        if (this.currentPosition >= 0) {
            if (list.get(currentPosition).getPlayliststatus().equals("1")) {
                return true;
            }
        }
        return false;
    }

}
