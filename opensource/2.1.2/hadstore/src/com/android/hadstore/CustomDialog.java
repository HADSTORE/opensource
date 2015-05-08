package com.android.hadstore;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.LinearLayout;

public class CustomDialog extends Dialog implements OnClickListener{
	LinearLayout mMainView;
	
	private CommonDailog mCommon;
	
	private boolean mIdCheck = false;
	
	public boolean isIdCheck() {
		return mIdCheck;
	}
	public void setIdCheck(boolean IdCheck) {
		this.mIdCheck = IdCheck;
	}
	
	public CustomDialog(Context context) {
		super(context);
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the title */
		getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		mCommon = new CommonDailog(context);
		mMainView = new LinearLayout(context);
		mMainView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		setContentView(mMainView);
	}
	
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
	}

	public void cancel() {
		// TODO Auto-generated method stub
		super.cancel();
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}
	
	public void setCustomView(View view){
		//mMainView.removeAllViews();
		mMainView.addView(view);
		//mMainView.invalidate();
	}
	
	public View getCustomView(){
		return mMainView;
	}
	
	public void showDialog(){
		if(!mCommon.isShowing())mCommon.show();
	}
	
	public void dismissDialog(){
		if(mCommon.isShowing())mCommon.dismiss();
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
}