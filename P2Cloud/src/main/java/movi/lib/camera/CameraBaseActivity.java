package movi.lib.camera;

import android.app.Activity;
import android.os.Bundle;


public abstract class CameraBaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getContentViewId());
		findViews();
		initGetData();
		widgetListener();
		init();
	}
	protected abstract int getContentViewId();
	protected abstract void findViews();
	protected abstract void init();
	protected void initGetData() {
	};
	protected abstract void widgetListener();

	@Override
	protected void onStart() {
		super.onStart();
	}
}
