package com.android.hadstore.controller;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import com.android.hadstore.R;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.android.hadstore.util.DatabaseUtil;
import com.android.hadstore.view.*;
import com.android.hadstore.CommonDailog;
import com.android.hadstore.Encoder;
import com.android.hadstore.HadstroeActivity;
import com.android.hadstore.ResController;
import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.AppPackageInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.view.LoginView;
import com.android.hadstore.view.MainView;
import com.android.hadstore.view.RegisterView;
import com.android.hadstore.view.SearchList;
import com.operation.model.HsDownHistory;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PageController implements OnClickListener,HttpRequestTaskListener{
	
	private HadstroeActivity mActivity;
	
	private LinearLayout mMainView;
	
	private ArrayList<SubPageController> mArrayList = new ArrayList<SubPageController>();
	
	public int mPageIndex;
	
	public boolean mCheckLogin = false;
	
	public String mSearchName ="";
	
	public boolean mSearchFlag; 
	
	public HashMap<String,HsDownHistory> mDownList = new HashMap<String,HsDownHistory>();
	
	public int mSalesPoint;
	
	public int mNotyCount;
	
	public int mNotyLastCount;
	
	public int mGiftCount;
	
	public int mUpdateCount;
	
	public String mNickName;
	
	private String mAppSysId="";
	
	private Uri mStartUri;
	
	private DatabaseUtil mDbUtil;
	
	private CommonDailog mCommon;
	
	private int mOrientation;
	
	private boolean mGiftFlag;

	public static final int START_NOMAL=0;
	
	public static final int START_LOGIN=1;
	
	public static final int START_UNLOGIN=2;
	
	public static final int START_END=3;
	
	private int mNoticeSeq;
	
	public String mVersion;

	private NotificationManager mNotificationManager;
	
	public int getNoticeSeq() {
		return mNoticeSeq;
	}

	public void setNoticeSeq(int NoticeSeq) {
		this.mNoticeSeq = NoticeSeq;
	}
	
	public boolean isGiftFlag() {
		return mGiftFlag;
	}

	public void setGiftFlag(boolean GiftFlag) {
		this.mGiftFlag = GiftFlag;
	}
	
	public int getOrientation(){
		return mOrientation;
	}
	
	public void setAppSysId(String id){
		mAppSysId = id;
	}
	
	public String getAppSysId(){
		return mAppSysId;
	}
	
	public DatabaseUtil getDbUtil(){
		return mDbUtil;
	}
	
	public void startUriDetail(Uri uri){
		mStartUri = uri;
		if(mStartUri!=null){
			String appid = mStartUri.getQueryParameter("appid");
			if(!mCommon.isShowing()){
				if(mCheckLogin){
					mAppSysId = appid;
					setPage(ResController.PAGE_DETAIL_LIST);
					mStartUri = null;
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "로그인이 필요합니다.", 0).show();
					setPage(ResController.PAGE_LOGIN);
				}
			}
		}
	}
	
	public Uri getStartUri(){
		return mStartUri;
	}
	
	public PageController(HadstroeActivity activity){
		mActivity = activity;
		mMainView = (LinearLayout) mActivity.findViewById(R.id.hd_main);
		mActivity.findViewById(R.id.login).setOnClickListener(this);
		mActivity.findViewById(R.id.join).setOnClickListener(this);
		mActivity.findViewById(R.id.logout).setOnClickListener(this);
		mActivity.findViewById(R.id.home).setOnClickListener(this);
		mPageIndex = 0;
		
		mCommon = new CommonDailog(mActivity);
		mDbUtil = new DatabaseUtil(mActivity);
		mOrientation = Configuration.ORIENTATION_PORTRAIT;
		
		mNotificationManager = (NotificationManager) mActivity.getSystemService(Activity.NOTIFICATION_SERVICE);
		
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		mCheckLogin = prefs.getBoolean(AppInfo.LOGIN_AUTO, false);
		if(mCheckLogin){
			LoginSend();
		}else{
			mSearchName="";
			setPage(ResController.PAGE_MAIN);
		}
	}
	
	public void LoginSend(){
		mCommon.setText("로그인..");
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

		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
	}
	
	public void LoginSet(int salespoint,int notycount,int giftcount,String nickname){
		mSalesPoint = salespoint;
		mNotyCount = notycount;
		mGiftCount = giftcount;
		mNickName = nickname;
		TextView text = (TextView) mActivity.findViewById(R.id.nickname);
		text.setText(mNickName);
		text = (TextView) mActivity.findViewById(R.id.point);
		text.setText(""+salespoint);
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
		if(name.equals(ResController.PAGE_MAIN)){
			controller = new MainView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_LOGIN)){
			controller = new LoginView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_SEARCH_LIST)){
			controller = new SearchList(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_REGISTER_TERMS)){
			controller = new RegisterView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_GIFT)){
			controller = new GiftView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_UPDATE)){
			controller = new UpdateView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_NOTICE)){
			controller = new NoticeView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_DETAIL_LIST)){
			controller = new AppDetailView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_COMMENT)){
			controller = new CommentView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_MEMBERSHIP)){
			controller = new MemberShipView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_NOTICE_DETAIL)){
			controller = new NoticeDetailView(mMainView,mActivity,this);
		}else if(name.equals(ResController.PAGE_DETAIL_GIFT)){
			controller = new AppDetailGiftView(mMainView,mActivity,this);
		}
		controller.setPageName(name);
		mArrayList.add(controller);
	}
	public void Release(){
		mActivity = null;
		
		mMainView = null;
		
		mArrayList = null;
		
		mDbUtil.close();
		
		mDbUtil = null;
	}
	/**
	 * 뷰하나를 해제하는 기능의 함수
	 */
	public int BackKey(){
		if(mPageIndex>0){
			SubPageController controller = mArrayList.get(mPageIndex-1);
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
	/**
	 * activity의 가로 세로 모드시의 동작
	 */
	public void onConfigurationChanged(Configuration newConfig){
		if(mArrayList.size()>0){
			if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE||newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
				mOrientation = newConfig.orientation;
				mArrayList.get(mArrayList.size()-1).onConfigurationChanged(newConfig);
			}
		}
	}
	
	public int getPageIndex(){
		return mPageIndex;
	}
	
	public void InitHome(){
		for(int i=mPageIndex-1;i>0;i--){
			mArrayList.get(i).Release();
			mArrayList.remove(i);
		}
		mArrayList.get(0).Show();
		mPageIndex = 1;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.login:
			mActivity.findViewById(R.id.top_login).setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.top_logout).setVisibility(View.GONE);
			mActivity.findViewById(R.id.logout).setVisibility(View.INVISIBLE);
			mActivity.findViewById(R.id.nickname).setVisibility(View.INVISIBLE);
			mActivity.findViewById(R.id.point).setVisibility(View.INVISIBLE);
			setPage(ResController.PAGE_LOGIN);
			break;
		case R.id.join:
			mActivity.findViewById(R.id.top_login).setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.top_logout).setVisibility(View.GONE);
			mActivity.findViewById(R.id.logout).setVisibility(View.INVISIBLE);
			mActivity.findViewById(R.id.nickname).setVisibility(View.INVISIBLE);
			mActivity.findViewById(R.id.point).setVisibility(View.INVISIBLE);
			setPage(ResController.PAGE_REGISTER_TERMS);
			break;
		case R.id.logout:
			mActivity.findViewById(R.id.top_login).setVisibility(View.GONE);
			mActivity.findViewById(R.id.top_logout).setVisibility(View.VISIBLE);
			SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
					AppInfo.USER_INFO, 0);
			Editor editor = prefs.edit();
			editor.putBoolean(AppInfo.LOGIN_AUTO, false);
			editor.putString(AppInfo.USER_ID,"");
			editor.putString(AppInfo.USER_PASSWORD,"");
			editor.commit();
			mDownList.clear();
			mCheckLogin = false;
			InitHome();
			break;
		case R.id.home:
			TextView text = (TextView) v;
			if(text.getText().equals("MemberShip")){
				setPage(ResController.PAGE_MEMBERSHIP);
			}else{
				if(mPageIndex>1)	
						InitHome();
			}
			break;
		}
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
					String nickname = (String)map.get(NetInfo.USER_NICK_NAME);
					int gift = (Integer)map.get(NetInfo.USER_GIFT_COUNT);
					int noty = (Integer)map.get(NetInfo.USER_NOTICE_COUNT);
					SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
							AppInfo.USER_INFO, 0);
					String id = prefs.getString(AppInfo.USER_ID, "");
					mNotyLastCount = mDbUtil.NoticeLastCnt(id);
					Log.e("mgdoo", "mNotyLastCount "+mNotyLastCount);
					int gap = noty - mNotyLastCount;
					LoginSet(sales, gap, gift, nickname);
					List<HsDownHistory> downHistory =  (List<HsDownHistory>) map.get(NetInfo.USER_DOWN_HISTORY);
					for(int i=0;i<downHistory.size();i++){
						mDownList.put(downHistory.get(i).getPackageName(), downHistory.get(i));
					}
					
					mActivity.findViewById(R.id.top_login).setVisibility(View.VISIBLE);
					mActivity.findViewById(R.id.top_logout).setVisibility(View.GONE);
					mActivity.findViewById(R.id.logout).setVisibility(View.VISIBLE);
					mActivity.findViewById(R.id.nickname).setVisibility(View.VISIBLE);
					mActivity.findViewById(R.id.point).setVisibility(View.VISIBLE);
					
					mSearchName="";
				}else{
					mCheckLogin = false;
					Toast.makeText(mActivity.getApplicationContext(), "로그인 실패", 0).show();
				}
				setPage(ResController.PAGE_MAIN);
				if(mStartUri!=null){
					String appid = mStartUri.getQueryParameter("appid");
					if(mCheckLogin){
						mAppSysId = appid;
						setPage(ResController.PAGE_DETAIL_LIST);
						mStartUri = null;
					}else{
						Toast.makeText(mActivity.getApplicationContext(), "로그인이 필요합니다.", 0).show();
						setPage(ResController.PAGE_LOGIN);
					}
				}
				
			}
		}
	}
	
	public void ShowUpdateNotification(String msg){
		Notification noti = new Notification(R.drawable.defaultimage,msg,System.currentTimeMillis());
		noti.flags = noti.flags|Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent(mActivity, HadstroeActivity.class);
		PendingIntent content  = PendingIntent.getActivity(mActivity, 0, intent, 0);
		noti.setLatestEventInfo(mActivity, "", msg, content);
		mNotificationManager.notify(1001, noti);
	}
	
	public void DialogClick(int index){
		if(mArrayList.size()>0)mArrayList.get(mPageIndex-1).DialogClick(index);
	}
}
