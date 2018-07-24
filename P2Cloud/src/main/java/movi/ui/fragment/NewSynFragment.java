package movi.ui.fragment;

import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ctvit.p2cloud.R;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

import java.util.ArrayList;

import movi.net.biz.NewsClipStatusBiz;
import movi.ui.activity.NewVideoPlayerActivity;
import movi.ui.activity.VideoFragmentActivity;
import movi.ui.adapter.PicListAdapter;
import movi.base.BaseActivity;
import movi.base.BaseFragment;
import movi.base.Response;
import movi.ui.bean.NewsPicListBean;
import movi.net.config.ServerConfig;
import movi.net.request.NetCallBack;
import movi.utils.ToastUtils;
import movi.ui.view.XListView;

/**
 * Created by chjj on 2016/5/11.
 * 合成发布
 */
@ContentView(R.layout.news_sysn)
public class NewSynFragment extends BaseFragment implements PicListAdapter.OnClickStepListener, NetCallBack, XListView.IXListViewListener {
    @ViewInject(R.id.lv_synthesis)
    private XListView lv_synthesis;
    @ViewInject(R.id.ll_layout)
    private LinearLayout ll_layout;
    @ViewInject(R.id.syn_contaner)
    private FrameLayout syn_contaner;
    private PicListAdapter adapter;
    private static final int REFRESH_CODE = 1;
    private static final int LOADER_CODE = 2;
    private static int DEFAULT = 1;
    private int pager = 1;
    private static final int DEFAULT_PIC = 1;
//    private ArrayList<NewsPicListBean.NewsPicBean> mList;
//    private ArrayList<NewsPicListBean.NewsPicBean> list;
private ArrayList<NewsPicListBean.NewsPicBean> mList;
    private ArrayList<NewsPicListBean.NewsPicBean> list;

    @Override
    protected int getResource() {
        return R.layout.news_sysn;
    }

    @Override
    protected void afterViews() {
        super.afterViews();
        initData();
        sendPicTogetRequest();
    }

    //完成初始化View
    private void initData() {
        mList = new ArrayList<>();
        list = new ArrayList<>();
        lv_synthesis.setPullRefreshEnable(true);
        lv_synthesis.setPullLoadEnable(false);
        lv_synthesis.setXListViewListener(this);
        adapter = new PicListAdapter(context, mList);
        adapter.setOnClickStepLitener(this);
        lv_synthesis.setAdapter(adapter);
    }


    //监听adapter中点击预览事件
    @Override
    public void onStep(Object object) {
        //需要传递参数
        NewsPicListBean.NewsPicBean picBean = (NewsPicListBean.NewsPicBean) object;
        replaceVideoFragment(picBean);

    }


    /**
     * 发送合成列表请求
     */
    public void sendPicTogetRequest() {
        DEFAULT = DEFAULT_PIC;
        BaseActivity.showProgressDialog(context);
//        NewsPicTogetBiz biz = new NewsPicTogetBiz();
//        biz.setNetCallBack(this);
//        biz.requestPicToGet(ServerConfig.GET);

        NewsClipStatusBiz biz = new NewsClipStatusBiz();
        biz.setNetCallBack(this);
        biz.requestClipStatus(ServerConfig.GET);
    }

    @Override
    public void onSuccess(Response response) {
        onLoad();
        BaseActivity.dismissProgressDialog();

       /* if (response instanceof NewsPicListBean) {
            NewsPicListBean cutlist = (NewsPicListBean) response;
            list = cutlist.getData();
            mList.clear();
            mList.addAll(list);
            adapter.notifyDataSetChanged();

        }*/
        if (response instanceof NewsPicListBean) {
            NewsPicListBean picListBean = (NewsPicListBean) response;
            list =  picListBean.getData();
            mList.clear();
            mList.addAll(list);
            adapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onFailure(Response error) {
        onLoad();
        BaseActivity.dismissProgressDialog();
        ToastUtils.showToast(context, "数据请求失败");

    }


    public void replaceVideoFragment(final NewsPicListBean.NewsPicBean picBean) {
        lv_synthesis.setVisibility(View.GONE);
        Intent intent = new Intent(context, NewVideoPlayerActivity.class);
        intent.putExtra("bean", picBean);
        context.startActivity(intent);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                lv_synthesis.setVisibility(View.VISIBLE);
            }
        }, 500);


    }


    /**
     * 刷新
     */
    public void onLoad() {
        lv_synthesis.stopRefresh();
    }

    @Override
    public void onRefresh() {
        sendPicTogetRequest();
    }

    @Override
    public void onLoadMore() {

    }


}
