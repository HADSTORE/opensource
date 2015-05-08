package com.android.hadstore.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.DownLoadService;
import com.android.hadstore.R;
import com.android.hadstore.Global;
import com.android.hadstore.HadstroeActivity;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.operation.model.HsDownHistory;

public class MainView implements SubPageController,View.OnClickListener,HttpRequestTaskListener {
	
	private MainBottomView mBottomView;
	
	private SearchOptionView mTopView;
	
	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private PageController mPageController;
	
	public int mMainMode = 0;
	
	private View mSubView;
	
	private String mPageName;
	
	public static final int LOGIN_MODE = 0;
	
	public static final int LOGOUT_MODE = 1;
	
	private int mMoveSize;
	
	private boolean mOptionShow;
	
	private String mHadStoreUrl = null;
	
	private CommonDailog mCommon;
	
	private boolean misUpdateNoty;
	
	private boolean mLoginView;
	
	private Bitmap mEventBtmap;
	
	private LinearLayout mEventView;
	
	private boolean mEventBig;
	
	private int mTopStatus;
	
	private int mEventLeftMar;
	
	private int mEventTopMar;
	
	private int mEventMove;
	
	private boolean mAniRun;
	
	private WebView mWebView;
	
	private LinearLayout.LayoutParams mEventParams;
	
	class HadWebViewClientPop extends WebViewClient{
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if(!mEventBig||mAniRun)return true;
			if("hadstore://cancel".equals(url)){
				 EventPopupHideAni(200);
				 return true;
			}
			Intent i = new Intent(Intent.ACTION_VIEW); 
		    Uri u = Uri.parse(url); 
		    i.setData(u); 
		    mActivity.startActivity(i);   
			return true;
        }
	}
	
	private View.OnTouchListener mEventTouch = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			if(mAniRun)return true;	
			switch(v.getId()){
				case R.id.event_popup_webview:
					if(event.getAction() == MotionEvent.ACTION_UP){
						if(!mEventBig){
							EventPopupShowAni(200);
							return true;	
						}
					}
					break;
			}
			return false;
		}
	};
	
	public MainView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mPageController = controller; 
		mMainView = (FrameLayout) parent;
		mCommon = new CommonDailog(mActivity);
		mLoginView = false;
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
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mLoginView = false;
		TextView text = (TextView) mSubView.findViewById(R.id.login_text);
		text.setOnClickListener(this);
		if(mActivity.findViewById(R.id.join).getVisibility()==View.GONE){
			mActivity.findViewById(R.id.join).setVisibility(View.VISIBLE);
		}
		if(mPageController.mCheckLogin){
			mActivity.findViewById(R.id.top_login).setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.top_logout).setVisibility(View.GONE);
			text.setText("로그아웃");
			setLoginOutView(true);
			new UpdateCheckAsync().execute("");
		}else{
			setLoginOutView(false);
			mActivity.findViewById(R.id.top_login).setVisibility(View.GONE);
			mActivity.findViewById(R.id.top_logout).setVisibility(View.VISIBLE);
			text.setText("로그인");
		}
		
		text = (TextView) mSubView.findViewById(R.id.search_bar);
		if(mPageController.mSearchName.length()>0){
			text.setText(mPageController.mSearchName);
		}else{
			text.setText("검색어 또는 URL");
		}
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		mMainView.removeView(mSubView);
		
		mBottomView.Release();
		
		mTopView.Release();
		
		if(mEventBtmap!=null){
			mEventBtmap.recycle();
			mEventBtmap = null;
		}
		
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
		if(mPageController.mSearchFlag){
			mPageController.setPage(ResController.PAGE_SEARCH_LIST);
		}
	}

	@Override
	public void setPageName(String name) {
		// TODO Auto-generated method stub
		mPageName = name;
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(mPageName),null);
		Rect rectgle= new Rect(); 
		Window window= mActivity.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(rectgle); 
		int StatusBarHeight= rectgle.top; 
		int contentViewTop= window.findViewById(Window.ID_ANDROID_CONTENT).getTop(); 
		int TitleBarHeight= contentViewTop - StatusBarHeight; 
		mTopStatus = StatusBarHeight+TitleBarHeight;
				//(int) (Global.DISPLAYHEIGHT*0.0792);
		int height = (int) (Global.DISPLAYHEIGHT*0.06854);
		mMoveSize = (int)(Global.DISPLAYHEIGHT - (mTopStatus+height));
		mSubView.findViewById(R.id.main_search).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,height));
		mBottomView = new MainBottomView(mActivity,this,mSubView);
		mBottomView.setPage(mMoveSize);
		mTopView = new SearchOptionView(mActivity,mSubView);
		mTopView.setPage(false,mMoveSize);
		mMainView.addView(mSubView);
		mSubView.findViewById(R.id.search_bar).setOnClickListener(this);
		mSubView.findViewById(R.id.option_btn_layout).setOnClickListener(this);
		mSubView.findViewById(R.id.main_google_btn).setOnClickListener(this);
		mSubView.findViewById(R.id.login_layout).setOnClickListener(this);
		mSubView.findViewById(R.id.main_setting_btn).setOnClickListener(this);
		mSubView.findViewById(R.id.main_newapp_btn).setOnClickListener(this);
		TextView text = (TextView) mSubView.findViewById(R.id.login_text);
		mTopView.Hide();
		mOptionShow = false;
		setEventPopup();
		if(mPageController.getStartUri()!=null){
			String appid = mPageController.getStartUri().getQueryParameter("appid");
			mPageController.startUriDetail(null);
			mPageController.setAppSysId(appid);
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}else{
			if(!mPageController.mCheckLogin){
				mActivity.findViewById(R.id.top_login).setVisibility(View.GONE);
				mActivity.findViewById(R.id.top_logout).setVisibility(View.VISIBLE);
				setLoginOutView(false);
				text.setText("로그인");
				AppVereionSend();
			}else{
				mActivity.findViewById(R.id.top_login).setVisibility(View.VISIBLE);
				mActivity.findViewById(R.id.top_logout).setVisibility(View.GONE);
				setLoginOutView(true);
				text.setText("로그아웃");
				new UpdateCheckAsync().execute("");
			}
			if(mPageController.mSearchName.length()>0){
				text = (TextView) mSubView.findViewById(R.id.search_bar);
				text.setText(mPageController.mSearchName);
			}
		}
		
		
		
	}
	
	public void setEventPopup(){
		mEventView = (LinearLayout) mSubView.findViewById(R.id.event_popup_layout);
		int parentWidth = (int) (Global.DISPLAYWIDTH * 0.85);
		int parentHight = (int) (Global.DISPLAYHEIGHT * 0.41);
		int hight = (Global.DISPLAYHEIGHT - mTopStatus) - (int) (parentHight * 0.15f);
		mEventLeftMar = (Global.DISPLAYWIDTH - parentWidth)/ 2;
		mEventTopMar = (Global.DISPLAYHEIGHT - mTopStatus - parentHight)/2;
		mEventMove = hight - mEventTopMar;
		mEventParams= new LinearLayout.LayoutParams(parentWidth,parentHight);
		mEventParams.setMargins(mEventLeftMar, mEventTopMar+mEventMove, 0, 0);
		mEventView.setLayoutParams(mEventParams);
		mWebView = (WebView) mEventView.findViewById(R.id.event_popup_webview);
		mWebView.setOnTouchListener(mEventTouch);
		mWebView.setWebViewClient(new HadWebViewClientPop());
		mWebView.loadUrl("http://www.hadstore.com/files/application/file/popup/popup.jsp");
		mWebView.getSettings().setJavaScriptEnabled(true);
		//WebSettings set = mWebView.getSettings();
		//set.setLoadWithOverviewMode(true);
		//set.setUseWideViewPort(true);
		mWebView.setHorizontalScrollBarEnabled(false); // 세로 scroll 제거
		mWebView.setVerticalScrollBarEnabled(false); // 가로 scroll 제거
		mSubView.findViewById(R.id.event_popup).setVisibility(View.VISIBLE);
		mEventBig = false;
		mAniRun = false;
	}
	
	public void setLoginOutView(boolean islogin){
		if(islogin){
			mSubView.findViewById(R.id.main_setting_btn).setVisibility(View.VISIBLE);
			mSubView.findViewById(R.id.main_gift_btn).setVisibility(View.VISIBLE);
			mSubView.findViewById(R.id.main_update_btn).setVisibility(View.VISIBLE);
			mBottomView.setNotify(mPageController.mGiftCount,mPageController.mUpdateCount,mPageController.mNotyCount);
		}else{
			mSubView.findViewById(R.id.main_setting_btn).setVisibility(View.INVISIBLE);
			mSubView.findViewById(R.id.main_gift_btn).setVisibility(View.INVISIBLE);
			mSubView.findViewById(R.id.main_update_btn).setVisibility(View.INVISIBLE);
			mBottomView.setNotify(0,0,mPageController.mNotyCount);
		}
	}

	@Override
	public String getPageName() {
		// TODO Auto-generated method stub
		return mPageName;
	}

	@Override
	public int BackKey() {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			return 1;
		}
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
	
	public void EventPopupHideAni(long delay){
		mAniRun = true;
		mEventBig = false;
		Animation animation = new TranslateAnimation(
				 0, 0
               , 0, mEventMove);
		animation.setDuration(delay);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				mAniRun = false;
				//mEventView.findViewById(R.id.event_popup_close).setVisibility(View.GONE);
				mEventView.clearAnimation();
				mEventParams.setMargins(mEventLeftMar, mEventTopMar+mEventMove, 0, 0);
				mEventView.setLayoutParams(mEventParams);
			}
		});
		 animation.setFillAfter(true);
		 mEventView.startAnimation(animation);
	}
	
	public void EventPopupShowAni(long delay){
		mAniRun = true;
		mEventBig = true;
		
		
		Animation animation = new TranslateAnimation(
				 0, 0
                , 0, -mEventMove);
		animation.setDuration(delay);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				//mEventView.findViewById(R.id.event_popup_close).setVisibility(View.VISIBLE);
				mAniRun = false;
				mEventView.clearAnimation();
				mEventParams.setMargins(mEventLeftMar, mEventTopMar, 0, 0);
				mEventView.setLayoutParams(mEventParams);
			}
		});
		 animation.setFillAfter(true);
		 mEventView.startAnimation(animation);
	}
	
	public void OptionViewAni(){
		AnimationListener lis = new AnimationListener(){
			public void onAnimationEnd(Animation animation){
				mBottomView.Hide();
				mSubView.findViewById(R.id.option_btn).setBackgroundResource(R.drawable.roll_btn_up);
			}
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}
		};
		TranslateAnimation ani = new TranslateAnimation(0, 0, -mMoveSize, 0);
		ani.setDuration(500);
		ani.setAnimationListener(lis);
		mTopView.Show();
		mBottomView.Show();
		mSubView.findViewById(R.id.main_view).startAnimation(ani);
		mOptionShow = true;
	}
	
	public void OptionHideAni(){
		AnimationListener lis = new AnimationListener(){
			public void onAnimationEnd(Animation animation){
				mTopView.Hide();
				mSubView.findViewById(R.id.option_btn).setBackgroundResource(R.drawable.roll_btn_down);
			}
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}
		};
		TranslateAnimation ani = new TranslateAnimation(0, 0, 0, -mMoveSize);
		ani.setDuration(500);
		ani.setAnimationListener(lis);
		mTopView.Show();
		mBottomView.Show();
		mSubView.findViewById(R.id.main_view).startAnimation(ani);
		mOptionShow = false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.main_setting_btn:
				mPageController.setPage(ResController.PAGE_MEMBERSHIP);
				break;
			case R.id.login_layout:
				if(mPageController.isCheckLogin()){
					TextView text = (TextView) mSubView.findViewById(R.id.login_text);
					text.setText("로그인");
					setLoginOutView(false);
					mPageController.Logout(false);
				}else{
					mLoginView = true;
					mPageController.setPage(ResController.PAGE_LOGIN);
				}
				break;
			case R.id.option_btn_layout:
				if(mOptionShow){
					OptionHideAni();
				}else{
					OptionViewAni();
				}
				break;
			case R.id.main_newapp_btn:
				mPageController.mSearchFlag = true;
				mPageController.mSearchName = "";
				mPageController.setIsNewApp(true);
				mPageController.setPage(ResController.PAGE_SEARCH_LIST);
				break;
			case R.id.main_google_btn:
				mPageController.mSearchFlag = true;
				mPageController.mSearchName = "";
				mPageController.setIsNewApp(false);
				mPageController.setPage(ResController.PAGE_SEARCH_LIST);
				break;
			case R.id.search_bar:
				mPageController.mSearchName = "";
				Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.hadstore", "com.android.hadstore.HDSearchActivity"));
                mActivity.startActivityForResult(intent, HadstroeActivity.SEARCH_ACTIVITY);
				break;
		}
	}
	
	public void onClickGift(){
		mPageController.setPage(ResController.PAGE_GIFT);
	}
	
	public void onClickUpdate(){
		mPageController.setPage(ResController.PAGE_UPDATE);
	}
	
	public void onClickNotify(){
		if(mPageController.mNotyCount>0){
			int count = mPageController.getDbUtil().NoticeLastCnt(mPageController.getUserId());
			mPageController.getDbUtil().NoticeUpdate(mPageController.getUserId(), count+mPageController.mNotyCount);
		}
		mPageController.mNotyCount = 0;
		mBottomView.setNotify(mPageController.mGiftCount,mPageController.mUpdateCount,mPageController.mNotyCount);
		mPageController.setPage(ResController.PAGE_NOTICE);
	}
	
	public List<ResolveInfo> findAllActivitiesForPackage() {
        final PackageManager pkgManager = mActivity.getPackageManager();

        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        final List<ResolveInfo> apps = pkgManager.queryIntentActivities(intent, 0);
        final List<ResolveInfo> matches = new ArrayList<ResolveInfo>();

        if (apps != null) {
            int count = apps.size();

            for (int i = 0; i < count; i++) {
                final ResolveInfo info = apps.get(i);
                final ActivityInfo aInfo = info.activityInfo;
                matches.add(info);
            }
        }
        return matches;
    }
	
	class UpdateCheckAsync extends AsyncTask<String, String, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mCommon.setText("Loading..");
            if(! mCommon.isShowing()){
    			mCommon.show();
    		}
        }
        
		@Override
        protected Boolean doInBackground(String... aurl) {
		   List<ResolveInfo> listResolve = findAllActivitiesForPackage();
	        //  Exist ItemInfo in Packages but not exist in Database
		   mPageController.mUpdateCount = 0;
	       for (ResolveInfo info : listResolve) {
	    	   if(mPageController.mDownList.containsKey(info.activityInfo.packageName)) {
	       			HsDownHistory hs = mPageController.mDownList.get(info.activityInfo.packageName);
	       			PackageInfo pInfo = null;
					try {
						pInfo = mActivity.getPackageManager().getPackageInfo(info.activityInfo.packageName, PackageManager.GET_META_DATA);
						if(!hs.getAppVersion().equals(pInfo.versionName)){
							mPageController.mUpdateCount++;
						}
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
	        	}
	        }
			
            return false;
        } 

        @Override
        protected void onPostExecute(Boolean unused) {
            //dismiss the dialog after the file was downloaded
        	//mSubView.findViewById(R.id.login_visible_view).setVisibility(View.VISIBLE);
        	mBottomView.setNotify(mPageController.mGiftCount,mPageController.mUpdateCount,mPageController.mNotyCount);
        	if(mPageController.mUpdateCount>0){
        		if(!misUpdateNoty)
        			mPageController.ShowUpdateNotification(mPageController.mUpdateCount+"개의 업데이트 항목이 있습니다.");
        		else
        			misUpdateNoty = true;
        	}
        	if( mCommon.isShowing()){
				mCommon.dismiss();
			}
        	if(mHadStoreUrl==null){
        		AppVereionSend();
        	}else if(!mLoginView){
        		AppUserInfoSend();
        	}
        }
    }
	
	public void AppUserInfoSend(){

		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_ID_INFO,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();

		params.add("userId", mPageController.getUserId());
		
		params.add("userPassword", mPageController.getPassword());
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
	}
	
	public void AppVereionSend(){
		mCommon.setText("Hadstore 버전확인..");
		
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.HADSTORE_VERSION,true);
		task.setOnFinshListener(this);
		
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();

		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
	}
	

	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		if(index==HadstroeActivity.DIALOG_OK){
			DownLoadService.getService().setAddDownload("hadstore","hadstore.apk", mActivity.getPackageName(), mHadStoreUrl);
		}else if(index==HadstroeActivity.DIALOG_CANCEL){
			
		}
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.HADSTORE_VERSION)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					String version = (String)map.get(NetInfo.APPVERSION);
					PackageInfo pInfo = null;
					try {
						pInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
					} catch (NameNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mHadStoreUrl = NetInfo.APP_IMAGE+(String)map.get(NetInfo.APPDOWNLINK);
					if(!version.equals(pInfo.versionName)){
						mActivity.showDialog(HadstroeActivity.DIALOG_HADSTORE_DOWN);
					}
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "버전확인 실패", 0).show();
				}
			}
		}else if(url.equals(NetInfo.USER_ID_INFO)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					
					int sales = (Integer)map.get(NetInfo.USER_SALES_POINT);
					int gift = (Integer)map.get(NetInfo.USER_GIFT_COUNT);
					int noty = (Integer)map.get(NetInfo.USER_NOTICE_COUNT);
					String type = (String)map.get(NetInfo.USER_TYPE);
					
					mPageController.mNotyLastCount = mPageController.getDbUtil().NoticeLastCnt(mPageController.getUserId());
					int gap = noty - mPageController.mNotyLastCount;
					
					mPageController.LoginSet(sales, gap, gift, mPageController.mNickName,true);
					
					List<HsDownHistory> downHistory =  (List<HsDownHistory>) map.get(NetInfo.USER_DOWN_HISTORY);
					mPageController.mDownList.clear();
					for(int i=0;i<downHistory.size();i++){
						mPageController.mDownList.put(downHistory.get(i).getPackageName(), downHistory.get(i));
					}
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "사용자 정보 확인 실패", 0).show();
				}
			}else{
				Toast.makeText(mActivity.getApplicationContext(), "사용자 정보 확인 실패", 0).show();
			}
		}
	}
}
