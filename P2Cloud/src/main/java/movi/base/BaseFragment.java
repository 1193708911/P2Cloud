package movi.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.DbManager;
import org.xutils.common.util.LogUtil;
import org.xutils.x;

import java.lang.reflect.Field;

import movi.MainApplication;

/**
 * 基类用户用于让其他的fragment进行继承
 * 基fragment
 * Created by chjj on 2016/5/9.
 */
public abstract class BaseFragment extends Fragment {
    protected FragmentManager manager;
    protected DbManager dbManager;
    protected   BaseActivity baseActivity;
    protected Context context;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context=context;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       if(getActivity() instanceof  BaseActivity){
            baseActivity= (BaseActivity) getActivity();
        }
        manager=baseActivity.getSupportFragmentManager();
        dbManager= MainApplication.getDbManager();
        LogUtil.d("当前已经执行");
        return x.view().inject(this,inflater,container);
    }
    //初始化当前控件
    protected  void afterViews(){};
    //返回资源的id
    protected  abstract  int getResource();
    //根据资源的id返回相应的View
   /* @Override
      public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        afterViews();
    }*/

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        afterViews();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override

    public void onDetach() {

        super.onDetach();

        try {

            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");

            childFragmentManager.setAccessible(true);

            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {

            throw new RuntimeException(e);

        } catch (IllegalAccessException e) {

            throw new RuntimeException(e);

        }

    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
    }


}
