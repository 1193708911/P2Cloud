package movi.base;

import android.content.Context;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/3/8.
 * 基Adapter
 */
public abstract class MyBaseAdapter<T> extends BaseAdapter {
    protected List<T> list;//由于不知道需要传入什么参数那么就用泛型替换
    protected  Context context;
    //通过构造把数据传递下去
    public MyBaseAdapter(Context context,ArrayList<T> list) {
        this.list = list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
