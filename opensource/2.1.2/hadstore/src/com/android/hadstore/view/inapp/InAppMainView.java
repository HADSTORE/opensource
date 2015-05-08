package com.android.hadstore.view.inapp;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.Encoder;
import com.android.hadstore.HadStoreInapp;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.InAppPageController;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;

public class InAppMainView implements SubPageController,View.OnClickListener,HttpRequestTaskListener{

	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private InAppPageController mInAppPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private CommonDailog mCommon;
	
	private InAppDialg mInAppDialg;
	
	public InAppMainView(View parent,Activity activity,InAppPageController controller){
		mActivity = activity;
		mInAppPageController = controller;
		mMainView = (FrameLayout) parent;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mCommon = new CommonDailog(mActivity);
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
		TextView text = (TextView) mSubView.findViewById(R.id.point_pay);
		text.setText(mInAppPageController.getPay()+"");
		text = (TextView) mSubView.findViewById(R.id.point_less);
		text.setText(mInAppPageController.getSalsePoint()+"");
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		mMainView.removeView(mSubView);
		
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		
		mCommon = null;
		
		mActivity = null;
		
		mMainView = null;
		
		mInAppPageController = null;
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
		mPageName = name;
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(mPageName),null);
		TextView text = (TextView) mSubView.findViewById(R.id.point_pay);
		text.setText(mInAppPageController.getPay()+"");
		mSubView.findViewById(R.id.point_pay_btn).setOnClickListener(this);
		mSubView.findViewById(R.id.point_payment_btn).setOnClickListener(this);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId()==R.id.point_payment_btn){
			mInAppPageController.setPage(ResController.PAGE_PAYMENT);
		}else if(v.getId()==R.id.point_pay_btn&&mInAppDialg==null){
			
			if(mInAppPageController.getPay()>mInAppPageController.getSalsePoint()){
				Toast.makeText(mActivity.getApplicationContext(), "결제 잔액이 부족합니다.", 0).show();
				return;
			}
			
			sendPay();
		}else if(v.getId()==R.id.inapp_check){
			if(mInAppDialg!=null){
				mInAppDialg.Release();
				mInAppDialg = null;
				Intent intent = InAppPageController.getInappIntentSet(null,HadStoreInapp.PAY_SUCCESS);
				mActivity.setResult(Activity.RESULT_OK, intent);
				mActivity.finish();
			}
		}
	}
	
	public void sendPay(){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.INAPP_PAY,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		// 최초 불러올땐 seq = 0을 입력한다
		params.add("userId",mInAppPageController.getUserId());
		
		params.add("userPassword",mInAppPageController.getUserPassword());
		
		params.add("packageName",mInAppPageController.getPackageName());
		
		params.add("pointSum",""+mInAppPageController.getPay());
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.INAPP_PAY)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					mInAppDialg = new InAppDialg(mMainView,mActivity,this);
					mInAppDialg.setPageName(ResController.PAGE_INAPP_DIALOG);
				}else if(status == NetInfo.PAY_LACK){
					Toast.makeText(mActivity.getApplicationContext(), "결제 금액이 부족합니다.", 0).show();
				}else if(status == NetInfo.INAPP_PACKAGENAME_FAIL){
					Toast.makeText(mActivity.getApplicationContext(), "판매자 패키지명이 존재하지 않습니다.", 0).show();
				}else if(status == NetInfo.INAPP_ID_PASSWORD_FAIL){
					Toast.makeText(mActivity.getApplicationContext(), "로그인 아이디 패스워드가 잘못되었습니다.", 0).show();
					mInAppPageController.mCheckLogin = false;
					mInAppPageController.setPage(ResController.PAGE_INAPP_LOGIN);
				}
			}
		}
	}

}
