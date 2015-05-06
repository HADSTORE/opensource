package com.android.hadstore.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Bitmap.Config;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.CustomGiftDialog;
import com.android.hadstore.DownLoadService;
import com.android.hadstore.R;
import com.android.hadstore.Global;
import com.android.hadstore.HadstroeActivity;
import com.android.hadstore.ResController;
import com.android.hadstore.DownLoadService.DownLoadData;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.operation.model.HsDownHistory;

public class MainView implements SubPageController,View.OnClickListener,HttpRequestTaskListener {
	
	private MainBottomView mBottomView;
	
	private SearchOptionView mTopView;
	
	private Activity mActivity;
	
	private LinearLayout mMainView;
	
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
	
	public MainView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mActivity.findViewById(R.id.had_info).setVisibility(View.VISIBLE);
		mActivity.findViewById(R.id.had_info_land).setVisibility(View.GONE);
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mPageController = controller; 
		mMainView = (LinearLayout) parent;
		mCommon = new CommonDailog(mActivity);
	}
	
	@Override
	public void Hide() {
		// TODO Auto-generated method stub
		mSubView.setVisibility(View.GONE);
		TextView home = (TextView) mActivity.findViewById(R.id.home);
		home.setText("HOME");
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		mSubView.setVisibility(View.VISIBLE);
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		Log.e("mgdoo", ""+mPageController.mCheckLogin);
		
		if(mPageController.mCheckLogin){
			mActivity.findViewById(R.id.top_login).setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.top_logout).setVisibility(View.GONE);
		}else{
			mActivity.findViewById(R.id.top_login).setVisibility(View.GONE);
			mActivity.findViewById(R.id.top_logout).setVisibility(View.VISIBLE);
		}
		TextView home = (TextView) mActivity.findViewById(R.id.home);
		if(!mPageController.mCheckLogin){
			mSubView.findViewById(R.id.login_visible_view).setVisibility(View.INVISIBLE);
			home.setText("HOME");
		}else{
			home.setText("MemberShip");
			new UpdateCheckAsync().execute("");
		}
		TextView text = (TextView) mSubView.findViewById(R.id.search_bar);
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
		mMoveSize = (int) (Global.DISPLAYHEIGHT*0.8025);
		mSubView.findViewById(R.id.main_search).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1025)));
		mBottomView = new MainBottomView(mActivity,this,mSubView);
		mBottomView.setPage();
		TextView home = (TextView) mActivity.findViewById(R.id.home);
		home.setText("MemberShip");
		mTopView = new SearchOptionView(mActivity,mSubView);
		mTopView.setPage(false);
		mMainView.addView(mSubView);
		mSubView.findViewById(R.id.search_bar).setOnClickListener(this);
		mSubView.findViewById(R.id.option_btn).setOnClickListener(this);
		mSubView.findViewById(R.id.googling).setOnClickListener(this);
		if(mPageController.mSearchName.length()>0){
			TextView text = (TextView) mSubView.findViewById(R.id.search_bar);
			text.setText(mPageController.mSearchName);
		}
		if(!mPageController.mCheckLogin){
			AppVereionSend();
			mSubView.findViewById(R.id.login_visible_view).setVisibility(View.INVISIBLE);
		}else{
			new UpdateCheckAsync().execute("");
		}
		mTopView.Hide();
		mOptionShow = false;
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
	
	public void OptionViewAni(){
		AnimationListener lis = new AnimationListener(){
			public void onAnimationEnd(Animation animation){
				mBottomView.Hide();
				mSubView.findViewById(R.id.option_btn).setBackgroundResource(R.drawable.mobile_search_up);
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
				mSubView.findViewById(R.id.option_btn).setBackgroundResource(R.drawable.mobile_search_down);
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
			case R.id.option_btn:
				if(mOptionShow){
					OptionHideAni();
				}else{
					OptionViewAni();
				}
				break;
			case R.id.googling:
				
				
				mPageController.mSearchFlag = true;
				mPageController.mSearchName = "";
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
			SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
					AppInfo.USER_INFO, 0);
			String id = prefs.getString(AppInfo.USER_ID, "");
			int count = mPageController.getDbUtil().NoticeLastCnt(id);
			mPageController.getDbUtil().NoticeUpdate(id, count+mPageController.mNotyCount);
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
        	mSubView.findViewById(R.id.login_visible_view).setVisibility(View.VISIBLE);
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
        	if(mHadStoreUrl==null)
        			AppVereionSend();
        }
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
					Log.e("mgdoo", version+ "  "+pInfo.versionName);
					mHadStoreUrl = NetInfo.APP_IMAGE+(String)map.get(NetInfo.APPDOWNLINK);
					if(!version.equals(pInfo.versionName)){
						mActivity.showDialog(HadstroeActivity.DIALOG_HADSTORE_DOWN);
					}
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "버전확인 실패", 0).show();
				}
			}
		}
	}
}
