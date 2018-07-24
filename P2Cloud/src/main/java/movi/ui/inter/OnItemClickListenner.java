package movi.ui.inter;

import android.view.View;

import java.util.List;

import movi.ui.bean.HeadPicList;

/**
 * Created by admin on 2017/3/20.
 */

public abstract  class OnItemClickListenner {
    public void onCutItemClickListenner(View view, int position, List<HeadPicList.HeadPic> list, int state) {
    }
    public   void  onCutItemSuccess( List<HeadPicList.HeadPic> list){};
    public void onSheetItemClickListenner(View view, int position, List<HeadPicList.HeadPic> list, int state) {
    }
    public  void  onSheetItemSuccess(List<HeadPicList.HeadPic> list){};
    public void onManuItemClickListenner(View view, int position, List<HeadPicList.HeadPic> list, int state) {
    }
    public   void  onManuItemSucess(List<HeadPicList.HeadPic> list){};
}
