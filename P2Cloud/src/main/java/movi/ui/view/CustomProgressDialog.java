	
package movi.ui.view;


import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.widget.ImageView;
import android.widget.TextView;

import com.ctvit.p2cloud.R;


public class CustomProgressDialog extends Dialog {
	private Context context = null;
	//private static CustomProgressDialog customProgressDialog = null;
	
	public CustomProgressDialog(Context context){
		super(context);
		this.context = context;
	}
	
	public CustomProgressDialog(Context context, int theme) {
        super(context, theme);
    }
	
//	public static CustomProgressDialog createDialog(Context context){
//		customProgressDialog = new CustomProgressDialog(context,R.style.CustomProgressDialog);
//		customProgressDialog.setContentView(R.layout.customprogressdialog);
//		customProgressDialog.getWindow().getAttributes().gravity = Gravity.CENTER;
//		
//		return customProgressDialog;
//	}
 
    public void onWindowFocusChanged(boolean hasFocus){
    	
        ImageView imageView = (ImageView) findViewById(R.id.loadingImageView);
        AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
        animationDrawable.start();
    }
    /**
     * 
     * [Summary]
     *       setTitile 设置标题
     * @param strTitle
     * @return
     *
     */
//    public CustomProgressDialog setTitile(String strTitle){
//    	return customProgressDialog;
//    }
    
    /**
     * 
     * [Summary]
     *       setMessage 设置消息
     * @param strMessage
     * @return
     *
     */
    public void setMessage(String strMessage){
    	TextView tvMsg = (TextView)findViewById(R.id.id_tv_loadingmsg);
    	
    	if (tvMsg != null){
    		tvMsg.setText(strMessage);
    	}
    	
    	return ;
    }
    
    
}
