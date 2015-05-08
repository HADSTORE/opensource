package com.android.hadstore.view.inapp;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.InAppPageController;
import com.android.hadstore.controller.SubPageController;

public class InAppDialg implements SubPageController {

	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private InAppMainView mInAppMainView;
	
	private View mSubView;
	
	private String mPageName;
	
	public InAppDialg(View parent,Activity activity,InAppMainView controller){
		mActivity = activity;
		mInAppMainView = controller;
		mMainView = (FrameLayout) parent;
	}
	
	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void Hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		mMainView.removeView(mSubView);
		
		mSubView = null;
		
		mActivity = null;
		
		mMainView = null;
		
		mInAppMainView = null;
	}

	@Override
	public void setPageName(String name) {
		// TODO Auto-generated method stub
		mPageName = name;
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(mPageName),null);
		mSubView.findViewById(R.id.inapp_check).setOnClickListener(mInAppMainView);
		mMainView.addView(mSubView);
	}

	@Override
	public String getPageName() {
		// TODO Auto-generated method stub
		return mPageName;
	}

	@Override
	public int BackKey() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isShow() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub

	}

}
