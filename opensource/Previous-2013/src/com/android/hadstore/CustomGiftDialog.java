package com.android.hadstore;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

public class CustomGiftDialog extends Dialog {

	
	LinearLayout mMainView;
	
	private CommonDailog mCommon;
	
	private boolean mIdCheck = false;
	
	public boolean isIdCheck() {
		return mIdCheck;
	}
	public void setIdCheck(boolean IdCheck) {
		this.mIdCheck = IdCheck;
	}
	
	public CustomGiftDialog(Context context) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		mMainView = new LinearLayout(context);
		mMainView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setContentView(mMainView);
		mCommon = new CommonDailog(context);
	}
	public void setView(View view){
		mMainView.removeAllViews();
		mMainView.addView(view);
		mMainView.invalidate();
	}
	
	public View getView(){
		return mMainView;
	}
	
	public void setParams(int width,int height){
		//mMainView.setLayoutParams(new FrameLayout.LayoutParams(width, height));
	}
	
	public void showDialog(){
		if(!mCommon.isShowing())mCommon.show();
	}
	
	public void dismissDialog(){
		if(mCommon.isShowing())mCommon.dismiss();
	}
}