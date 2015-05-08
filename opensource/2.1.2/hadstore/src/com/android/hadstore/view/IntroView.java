package com.android.hadstore.view;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.Encoder;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.operation.model.HsDownHistory;

public class IntroView implements SubPageController,HttpRequestTaskListener {

	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private PageController mPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private boolean mLoginTesk = false;
	
	private boolean mAniEnd = false;
	
	public IntroView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mPageController = controller;
		mMainView = (FrameLayout) parent;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	
	}
	
	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void Hide() {
		// TODO Auto-generated method stub
		mSubView.setVisibility(View.GONE);
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		mSubView.setVisibility(View.VISIBLE);
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		mMainView.removeView(mSubView);
		
		mSubView = null;
		
		mActivity = null;
		
		mMainView = null;
		
		mPageController = null;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPageName(String name) {
		// TODO Auto-generated method stub
		AnimationListener ani = new AnimationListener(){
			public void onAnimationEnd(Animation animation){
				mAniEnd = true;
				if(!mLoginTesk){
					setPageMain();
				}
			}
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}
		};
		Animation animation= AnimationUtils.loadAnimation(mActivity.getApplicationContext(),R.anim.fade_in); 
		animation.setAnimationListener(ani);
		mPageName = name;
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(mPageName),null);
		mMainView.addView(mSubView);
		mSubView.findViewById(R.id.intro_imgview).startAnimation(animation);
		mActivity.findViewById(R.id.top_background).startAnimation(animation);
		if(mPageController.mCheckLogin)
			LoginSend();
	}

	public void LoginSend(){
		mLoginTesk = true;
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_LOGIN,true);
		task.setOnFinshListener(this);
		
		String id = prefs.getString(AppInfo.USER_ID, "");
		
		String pw = prefs.getString(AppInfo.USER_PASSWORD, "");
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		// 최초 불러올땐 seq = 0을 입력한다
		params.add("userId",id);
		params.add("userPassword",pw);
		
		params.add("osName","android");
		params.add("osVersion",android.os.Build.VERSION.SDK);
		
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
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

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(url.equals(NetInfo.USER_LOGIN)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					
					int sales = (Integer)map.get(NetInfo.USER_SALES_POINT);
					String nickname = (String)map.get(NetInfo.USER_NICK_NAME);
					int gift = (Integer)map.get(NetInfo.USER_GIFT_COUNT);
					int noty = (Integer)map.get(NetInfo.USER_NOTICE_COUNT);
					String type = (String)map.get(NetInfo.USER_TYPE);
					if(type.equals("private")){
						String name = (String)map.get(NetInfo.USER_NAME);
						nickname = nickname + "("+name+")"; 
					}
					SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
							AppInfo.USER_INFO, 0);
					String id = prefs.getString(AppInfo.USER_ID, "");
					String pw = prefs.getString(AppInfo.USER_PASSWORD, "");
					mPageController.mNotyLastCount = mPageController.getDbUtil().NoticeLastCnt(id);
					int gap = noty - mPageController.mNotyLastCount;
					
					mPageController.LoginSet(sales, gap, gift, nickname,false);
					
					List<HsDownHistory> downHistory =  (List<HsDownHistory>) map.get(NetInfo.USER_DOWN_HISTORY);
					for(int i=0;i<downHistory.size();i++){
						mPageController.mDownList.put(downHistory.get(i).getPackageName(), downHistory.get(i));
					}
					
					mActivity.findViewById(R.id.nickname).setVisibility(View.VISIBLE);
					mActivity.findViewById(R.id.point).setVisibility(View.VISIBLE);
					
					mPageController.setUserId(id);
					mPageController.setPassword(pw);
					mPageController.mCheckLogin = true;
				}else{
					mPageController.mCheckLogin = false;
					Toast.makeText(mActivity.getApplicationContext(), "로그인 실패", 0).show();
				}
				
				mLoginTesk = false;
				if(mAniEnd){
					setPageMain();
				}
			}
		}
	}
	
	public void setPageMain(){
		if(mPageController.mCheckLogin){
			mPageController.LoginSet(mPageController.mSalesPoint, mPageController.mNotyCount, mPageController.mGiftCount, mPageController.mNickName,true);
		}

		mPageController.PageChange(ResController.PAGE_MAIN,ResController.PAGE_INTRO);
		
	}
}
