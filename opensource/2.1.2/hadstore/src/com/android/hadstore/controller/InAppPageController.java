package com.android.hadstore.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.HadStoreInapp;
import com.android.hadstore.InAppActivity;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.android.hadstore.view.LoginView;
import com.android.hadstore.view.PaymentView;
import com.android.hadstore.view.RegisterView;
import com.android.hadstore.view.inapp.InAppMainView;

public class InAppPageController implements HttpRequestTaskListener{

	private InAppActivity mActivity;
	
	private FrameLayout mMainView;
	
	private ArrayList<SubPageController> mArrayList = new ArrayList<SubPageController>();
	
	public int mPageIndex = 0;
	
	public boolean mCheckLogin = false;
	
	private String mUserId;
	
	private String mPassword;
	
	private String mPackageName;
	
	private int mUserPayPoint;
	
	private CommonDailog mCommon;
	
	private int mSalsePoint;
	
	public InAppPageController(InAppActivity activity) {

		mActivity = activity;
		mMainView = (FrameLayout) mActivity.findViewById(R.id.inapp_main);
		setPage(ResController.PAGE_INAPP_MAIN);
		
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		mCheckLogin = prefs.getBoolean(AppInfo.LOGIN_AUTO, false);
		
		mCommon = new CommonDailog(mActivity);
		
		if(mCheckLogin){
			LoginSend();
		}else{
			setPage(ResController.PAGE_INAPP_LOGIN);
		}
		
		// TODO Auto-generated constructor stub
	}
	
	public void setSalsePoint(int point){
		mSalsePoint = point;
	}
	
	public int getSalsePoint(){
		return mSalsePoint;
	}
	
	public void LoginSend(){
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		
		String id = prefs.getString(AppInfo.USER_ID, "");
		
		String pw = prefs.getString(AppInfo.USER_PASSWORD, "");
		
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_LOGIN,true);
		task.setOnFinshListener(this);
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		// 최초 불러올땐 seq = 0을 입력한다
		params.add("userId",id);
		params.add("userPassword",pw);
		
		params.add("osName","android");
		params.add("osVersion",android.os.Build.VERSION.SDK);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
	}
	
	public void setIdPay(String packagename,int pay){
		mPackageName = packagename;
		mUserPayPoint = pay;
	}
	
	public int getPay(){
		return mUserPayPoint;
	}
	
	public String getPackageName(){
		return mPackageName;
	}
	
	public void setLoginIdPassword(String id,String pass){
		mUserId = id;
		mPassword = pass;
	}
	
	public String getUserId(){
		return mUserId;
	}
	
	public String getUserPassword(){
		return mPassword;
	}
	
	/**
	 * 뷰를 교체하는 동작의 함수
	 * @param in -> 추가되는 View 이름 
	 * @param out -> 제거되는 View 이름 
	 */
	public void PageChange(String in,String out){
		mArrayList.get(mPageIndex-1).Release();
		mArrayList.remove(mPageIndex-1);
		setPageInit(in);
	}
	/**
	 * 뷰를 추가하는 함수
	 * @param name -> 추가되는 View 이름 
	 */
	public void setPage(String name){
		if(mPageIndex>0){
			mArrayList.get(mPageIndex-1).Hide();
		}
		mPageIndex++;
		setPageInit(name);
	}
	/**
	 * 객체를 해제하는 동작의 함수
	 */
	public void setPageInit(String name){
		SubPageController controller = null;
		if(name.equals(ResController.PAGE_INAPP_MAIN)){
			controller = new InAppMainView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_INAPP_LOGIN)){
			controller = new LoginView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_INAPP_REGISTER_TERMS)){
			controller = new RegisterView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_PAYMENT)){
			controller = new PaymentView(mMainView,mActivity,this);
		}
		controller.setPageName(name);
		mArrayList.add(controller);
	}
	public void Release(){
		if(mPageIndex>0){
			for(int i=mPageIndex-1;i>=0;i--){
				mArrayList.get(i).Release();
				mArrayList.remove(i);
			}
		}
		mActivity = null;
		
		mMainView = null;
		
		mArrayList = null;
	}
	
	public void InitHome(){
		for(int i=mPageIndex-1;i>0;i--){
			mArrayList.get(i).Release();
			mArrayList.remove(i);
		}
		mArrayList.get(0).Show();
		mPageIndex = 1;
	}
	
	/**
	 * activity의 onPause동작시 기능 구현 함수
	 */
	public void onPause(){
		if(mArrayList.size()>0)mArrayList.get(mPageIndex-1).onPause();
	}
	/**
	 * activity의 onResume동작시 기능 구현 함수
	 */
	public void onResume(){
		if(mArrayList.size()>0)mArrayList.get(mPageIndex-1).onResume();
	}
	
	public int BackKey(){
		if(mPageIndex>0){
			SubPageController controller = mArrayList.get(mPageIndex-1);
			if(controller.getPageName().equals(ResController.PAGE_INAPP_LOGIN)&&!mCheckLogin){
				mActivity.finish();
				return 0;
			}
			if(controller.BackKey()==0){
				controller.Release();
				mArrayList.remove(controller);
				mPageIndex--;
				if(mPageIndex>0){
					mArrayList.get(mPageIndex-1).Show();
				}
			}
		}
		return 0;
	}
	
	public int getPageIndex(){
		return mPageIndex;
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.USER_LOGIN)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					int sales = (Integer)map.get(NetInfo.USER_SALES_POINT);
					setSalsePoint(sales);
					SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
							AppInfo.USER_INFO, 0);
					
					String id = prefs.getString(AppInfo.USER_ID, "");
					
					String pw = prefs.getString(AppInfo.USER_PASSWORD, "");
					setLoginIdPassword(id,pw);
					mCheckLogin = true;
				}else{
					mCheckLogin = false;
					Toast.makeText(mActivity.getApplicationContext(), "로그인 실패", 0).show();
				}
				mArrayList.get(0).Show();
			}
		}
	}
	
	public static Intent getInappIntentSet(String error,String result){
		Intent intent = new Intent(); 
		if(result!=null){
			intent.putExtra(HadStoreInapp.MSG_IDENTIFY, result);
		}
		if(error!=null){
			intent.putExtra(HadStoreInapp.ERROR_MSG, error);
		}
		return intent;
	}
}
