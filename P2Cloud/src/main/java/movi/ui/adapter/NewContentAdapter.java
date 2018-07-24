package movi.ui.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import movi.base.MyBaseAdapter;

/**
 * Created by chjj on 2016/5/11.
 */
public class NewContentAdapter extends MyBaseAdapter<String> {
    public NewContentAdapter(Context context,ArrayList<String> list) {
        super(context,list);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
