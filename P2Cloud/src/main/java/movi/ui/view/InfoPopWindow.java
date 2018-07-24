package movi.ui.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

import com.ctvit.p2cloud.R;

/**
 * @author xufuqiang
 * 自定义popwindow
 */
public class InfoPopWindow extends PopupWindow {

    private View conentView;

    public InfoPopWindow(final Activity context, View  rootView) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView =rootView;
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w-100);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(context.getResources().getDisplayMetrics().heightPixels/2);

        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        Drawable  drawable=context.getResources().getDrawable(R.drawable.drawable_caleandar);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(drawable);
        this.setAnimationStyle(R.style.Popuwindow_style);


    }


}

