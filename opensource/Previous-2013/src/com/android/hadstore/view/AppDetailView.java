package com.android.hadstore.view;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.CustomGiftDialog;
import com.android.hadstore.CustomTextView;
import com.android.hadstore.DownLoadService;
import com.android.hadstore.Global;
import com.android.hadstore.HadstroeActivity;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.android.hadstore.tesk.ImageDownloader;
import com.android.hadstore.util.BitmapUtil;
import com.operation.model.HsApplicationInfo;
import com.operation.model.HsDownHistory;
import com.operation.model.HsPartnershipInfo;
import com.operation.model.HsUserInfo;

public class AppDetailView implements SubPageController,HttpRequestTaskListener,View.OnClickListener{

	private Activity mActivity;
	
	private LinearLayout mMainView;
	
	private PageController mPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private CommonDailog mCommon;
	
	private HsApplicationInfo mAppinfo;
	
	private ArrayList<HsPartnershipInfo> mSponorList = new ArrayList<HsPartnershipInfo>();
	
	private ArrayList<View> mSponorViewList = new ArrayList<View>();
	
	private DownLoadService mService;
	
	private ProgressBar mProgress;
	
	private Bitmap mDownBitmap;
	
	private ImageDownloadTask mTask;
	
	private CustomGiftDialog mDialog;
	
	private int mIsDown;
	
	private final int DOWN_STEP_INSTALL = 1;
	
	private final int DOWN_STEP_UNINSTALL = 2;
	
	private boolean mIsPercent;
	
	private boolean mFirstDown = false;
	
	private int mPayPoint;
	
	private int mGiftPoint;
	
	private boolean mGiftFlag;
	
	private AppDetailGiftView mDetailGiftView;
	
	private boolean mUpdate;
	
	public HsApplicationInfo getAppinfo() {
		return mAppinfo;
	}

	public ArrayList<HsPartnershipInfo> getSponorList() {
		return mSponorList;
	}
	
	public int getGiftPoint() {
		return mGiftPoint;
	}
	
	public PageController getPageController(){
		return mPageController;
	}

	OnKeyListener mOnKey = new OnKeyListener() {

        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

            if (keyCode == KeyEvent.KEYCODE_BACK) {
    			mDialog = null;
            	mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
            return false;
        }
    };
	
	View.OnClickListener mSponser = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String link = (String) v.getTag();
			Uri uri = Uri.parse(link);
			Intent it = new Intent(Intent.ACTION_VIEW,uri);
			mActivity.startActivity(it);
		}
	};
	
	public class SizeData{
    	public int mWith;
    	public int mHeight;
    }
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			boolean network = mService.CheckNetwork();
			if(!network){
				mProgress.setMax(100);
				mProgress.setProgress(mService.getCurrentPosition());
				TextView text =  (TextView) mSubView.findViewById(R.id.detail_top_del_percent);
				text.setText(mService.getCurrentPosition()+"%");
			}
			if(mService.getDownState()!=DownLoadService.NOMAL){
				mHandler.sendEmptyMessageDelayed(0, 200);
			}else{
				StopProgress();
			}
			if(!network){
				mService.SetNetworkState(false);
			}
		}
	};
	
	public void StopProgress(){
		mIsPercent = false;
		mIsDown = DOWN_STEP_INSTALL;
		HsDownHistory down = new HsDownHistory();
		down.setPackageName(mAppinfo.getPackageName());
		down.setAppSysId(mAppinfo.getAppSysId());
		down.setAppVersion(mAppinfo.getAppVersion());
		down.setAppTitle(mAppinfo.getAppTitle());
		down.setAppTitleIcon(mAppinfo.getAppTitleIcon());
		down.setUserNickName(mAppinfo.getUserNickName());
		mPageController.mDownList.put(mAppinfo.getPackageName(), down);
		mSubView.findViewById(R.id.progress_one).setVisibility(View.VISIBLE);
		mSubView.findViewById(R.id.progress_two).setVisibility(View.GONE);
		setDownUI();
	}
	
	public AppDetailView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		mPageController = controller; 
		mMainView = (LinearLayout) parent;
		mCommon = new CommonDailog(mActivity);
		mService = DownLoadService.getService();
		mGiftFlag = mPageController.isGiftFlag();
		mPageController.setGiftFlag(false);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.detail_top_ori_point:
			if(mIsDown==DOWN_STEP_UNINSTALL){
				mService.setAddDownload(mAppinfo.getAppTitle(),mAppinfo.getAppLocationLinkName(), mAppinfo.getPackageName(), NetInfo.APP_APK+mAppinfo.getAppLocationLink());
				setProgres();
			}else{
				if(mGiftFlag){
					SendPay();
				}else{
					((HadstroeActivity)mActivity).setPoint(mPayPoint);
					mActivity.showDialog(HadstroeActivity.DIALOG_DOWN_CHECK);
					mFirstDown = false;
				}
			}
			break;
		case R.id.detail_commet_post:
			EditText edit = (EditText) mSubView.findViewById(R.id.detail_comment_edit);
			if(edit.length()>0){
				SendComment(edit.getText().toString());
			}
			break;
		case R.id.detail_comment_view:
			mPageController.setAppSysId(mAppinfo.getAppSysId());
			mPageController.setPage(ResController.PAGE_COMMENT);
			break; 
		case R.id.detail_top_like:
			SendLike();
			break;
		case R.id.detail_top_bookmark:
			SendBookmark();
			break;
		case R.id.detail_top_gift:
			GiftPointReset();
			if(mPageController.mSalesPoint<mGiftPoint){
				Toast.makeText(mActivity.getApplicationContext(), "포인트가 부족합니다.", 0).show();
				return;
			}
			InitDetailGiffView();
			break;
		
		case R.id.detail_top_del_percent:
			if(mIsDown==DOWN_STEP_INSTALL){
				Intent intent = new Intent(Intent.ACTION_DELETE)
	            .setData(Uri.parse("package:" + mAppinfo.getPackageName()));
				mActivity.startActivity(intent);	
			}
			
			break;
		case R.id.detail_top_other_btn:
			if(mIsPercent){
				mService.DownCencel(mAppinfo.getPackageName());
				mSubView.findViewById(R.id.progress_one).setVisibility(View.VISIBLE);
				mSubView.findViewById(R.id.progress_two).setVisibility(View.GONE);
				if(mIsDown==DOWN_STEP_INSTALL){
					setDownUI();
				}else{
					 View install = mSubView.findViewById(R.id.detail_top_install);
					 install.setVisibility(View.VISIBLE);
					 View del = mSubView.findViewById(R.id.detail_top_del);
					 del.setVisibility(View.GONE);
					 setPointUI();
				}
				if(mHandler.hasMessages(0)){
					mHandler.removeMessages(0);
				}
			}else if(mIsDown==DOWN_STEP_INSTALL){
				if(mUpdate){
					mService.setAddDownload(mAppinfo.getAppTitle(),mAppinfo.getAppLocationLinkName(), mAppinfo.getPackageName(), NetInfo.APP_APK+mAppinfo.getAppLocationLink());
					setProgres();
				}else{
					Intent intent = mActivity.getPackageManager().getLaunchIntentForPackage(mAppinfo.getPackageName());
					mActivity.startActivity(intent);
				}
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
		if(url.equals(NetInfo.APP_DOWN)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					mService.setAddDownload(mAppinfo.getAppTitle(),mAppinfo.getAppLocationLinkName(), mAppinfo.getPackageName(), NetInfo.APP_APK+mAppinfo.getAppLocationLink());
					setProgres();
					mFirstDown = false;
				}else if(status == NetInfo.PAY_FAIL){
					mFirstDown = true;
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "네트워크 연결 실패", 0).show();
				}
				if(status == NetInfo.SUCCESS||status == NetInfo.PAY_FAIL){
					HsApplicationInfo info = (HsApplicationInfo) map.get(NetInfo.INFO);
					List<HsPartnershipInfo> list = (List<HsPartnershipInfo>) map.get(NetInfo.SPONSOR);
					if(list!=null){
						PayPointReset();
						mSponorList.clear();
						if(list!=null){
							for(int i=0;i<list.size();i++)
								mSponorList.add(list.get(i));
						}
						setSponserUI();
					}
					PayPointReset();
					if(info!=null){
						mAppinfo.setAppDownCount(info.getAppDownCount());
						mAppinfo.setAppLikeCount(info.getAppLikeCount());
						mAppinfo.setAppViewCount(info.getAppViewCount());
						mAppinfo.setAppPremiumPoint(info.getAppPremiumPoint());
						mAppinfo.setAppTotalWeight(info.getAppTotalWeight());
						setCountUI();
					}
					
					if(status == NetInfo.PAY_FAIL){
						((HadstroeActivity)mActivity).setPoint(mPayPoint);
						mActivity.showDialog(HadstroeActivity.DIALOG_DOWN_RE_CHECK);
					}
				}
			}
		}else if(url.equals(NetInfo.USER_GIFT)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					ReleaseDetailGiffView();
				}else if(status == NetInfo.PAY_FAIL){
					
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "네트워크 연결 실패", 0).show();
				}
				if(status == NetInfo.SUCCESS||status == NetInfo.PAY_FAIL){
					HsApplicationInfo info = (HsApplicationInfo) map.get(NetInfo.INFO);
					List<HsPartnershipInfo> list = (List<HsPartnershipInfo>) map.get(NetInfo.SPONSOR);
					if(list!=null){
						mSponorList.clear();
						if(list!=null){
							for(int i=0;i<list.size();i++)
								mSponorList.add(list.get(i));
						}
						setSponserUI();
					}
					GiftPointReset();
					if(info!=null){
						mAppinfo.setAppDownCount(info.getAppDownCount());
						mAppinfo.setAppLikeCount(info.getAppLikeCount());
						mAppinfo.setAppViewCount(info.getAppViewCount());
						mAppinfo.setAppPremiumPoint(info.getAppPremiumPoint());
						mAppinfo.setAppTotalWeight(info.getAppTotalWeight());
						setCountUI();
					}
					if(mGiftPoint>mPageController.mSalesPoint){
						Toast.makeText(mActivity.getApplicationContext(), "스폰서 포인트 변경으로 하여 결제 포인트가 변경 포인트가 부족합니다.", 0).show();
						return;
					}
					if(status == NetInfo.PAY_FAIL){
						mDetailGiftView.PointReset();
						((HadstroeActivity)mActivity).setPoint(mGiftPoint);
						mActivity.showDialog(HadstroeActivity.DIALOG_DOWN_RE_CHECK);
					}
				}
			}
		}else if(url.equals(NetInfo.APP_DETAIL)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					mAppinfo = (HsApplicationInfo) map.get(NetInfo.INFO);
					List<HsPartnershipInfo> list = (List<HsPartnershipInfo>) map.get(NetInfo.SPONSOR);
					for(int i=0;i<list.size();i++)
						mSponorList.add(list.get(i));
					
					CheckDown();
					setUI();
					setProgres();
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "네트워크 연결 오류", 0).show();
					mPageController.BackKey();
				}
			}
		}else if(url.equals(NetInfo.APP_COMMENT_POST)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					EditText edit = (EditText) mSubView.findViewById(R.id.detail_comment_edit);
					edit.setText("");
					Toast.makeText(mActivity.getApplicationContext(), "댓글 올리기 성공", 0).show();
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "댓글 올리기 실패", 0).show();
				}
			}
		}else if(url.equals(NetInfo.APP_LIKE_POST)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					Toast.makeText(mActivity.getApplicationContext(), "Like 성공", 0).show();
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "Like 실패", 0).show();
				}
			}
		}else if(url.equals(NetInfo.APP_BOOKMARK_POST)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					Toast.makeText(mActivity.getApplicationContext(), "즐겨찾기 추가 성공", 0).show();
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "즐겨찾기 추가 실패", 0).show();
				}
			}
		}
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
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		if(mDetailGiftView!=null){
			mDetailGiftView.Release();
			mDetailGiftView = null;
		}
		
		if(mHandler.hasMessages(0)){
			mHandler.removeMessages(0);
		}
		
		if(mDownBitmap!=null){
			mDownBitmap.recycle();
			mDownBitmap = null;
		}
		
		mMainView.removeView(mSubView);
		
		mService = null;
		
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
		if(mAppinfo!=null){
			CheckDown();
			if(mIsDown==DOWN_STEP_INSTALL){
				setDownUI();
			}else{
				setPointUI();
			}
			
		}
	}
	
	public void GiftClickOk(){
		mFirstDown = false;
		if(mDetailGiftView.isIdCheck()){
			UserGift();
		}
	}
	
	public void InitDetailGiffView(){
		Hide();
		mDetailGiftView = new AppDetailGiftView(mMainView,mActivity,this);
		mDetailGiftView.setPageName(ResController.PAGE_DETAIL_GIFT);
		
	}
	
	public void ReleaseDetailGiffView(){
		Show();
		mDetailGiftView.Release();
		mDetailGiftView = null;
	}
	
	public void GiftPointReset(){
		int point = mAppinfo.getAppDownPoint().intValue();
		int sponserpoint = 0;
		Log.e("mgdoo", "point "+point+"  sponserpoint"+sponserpoint);
		if(mSponorList.size()>0){
			for(int i=0;i<mSponorList.size();i++){
				HsPartnershipInfo info  = mSponorList.get(i);
				sponserpoint += info.getPartnerAppPoint().intValue();
			}
		}
		if((point-sponserpoint)<=0){
			mGiftPoint = 0;
		}else{
			mGiftPoint=(point-sponserpoint);
		}
	}
	
	public void PayPointReset(){
		int point = mAppinfo.getAppDownPoint().intValue();
		int sponserpoint = 0;
		Log.e("mgdoo", "point "+point+"  sponserpoint"+sponserpoint);
		if(mSponorList.size()>0){
			for(int i=0;i<mSponorList.size();i++){
				HsPartnershipInfo info  = mSponorList.get(i);
				sponserpoint += info.getPartnerAppPoint().intValue();
			}
		}
		if((point-sponserpoint)<=0){
			mPayPoint = 0;
		}else{
			mPayPoint=(point-sponserpoint);
		}
	}
	
	
	
	public void CheckDown(){
		List<ResolveInfo> listResolve = findAllActivitiesForPackage();
        //  Exist ItemInfo in Packages but not exist in Database
		mIsDown = 0;
	    for (ResolveInfo info : listResolve) {
	   		if(mAppinfo.getPackageName().equals(info.activityInfo.packageName)) {
					mIsDown = DOWN_STEP_INSTALL;
					break;
	    	}
	    }
	    if(mIsDown != DOWN_STEP_INSTALL){
	    	if(mPageController.mDownList.containsKey(mAppinfo.getPackageName())) {
				mIsDown = DOWN_STEP_UNINSTALL;
	    	}
	    }
	    
	}

	@Override
	public void setPageName(String name) {
		// TODO Auto-generated method stub
		mPageName = name;
		
		if(mPageController.getOrientation()==Configuration.ORIENTATION_LANDSCAPE){
			setPageLand(ResController.PAGE_DETAIL_LIST_LAND);
		}else{
			setPageProt(ResController.PAGE_DETAIL_LIST);
		}
		
	}

	public void setPageLand(String name) {
		// TODO Auto-generated method stub
		
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(name),null); 
		mMainView.addView(mSubView);
		mSubView.findViewById(R.id.detail_top_ori_point).setOnClickListener(this);
		
		LinearLayout totalmain = (LinearLayout) mSubView.findViewById(R.id.detail_total_score);
		totalmain.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.3125)));
		
		View editlayout = mSubView.findViewById(R.id.detail_comment);
		editlayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.2)));
		
		editlayout.findViewById(R.id.detail_commet_post).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_comment_view).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_like).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_gift).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_bookmark).setOnClickListener(this);
		
		if(mPageController.getAppSysId().length()>0){
			DetaiData(mPageController.getAppSysId());
			mPageController.setAppSysId("");
		}else{
			if(mAppinfo!=null){
				setUI();
			}
			setProgres();
		}
	}
	
	void setPageProt(String name){
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(name),null);
		mMainView.addView(mSubView);
		mSubView.findViewById(R.id.detail_top_ori_point).setOnClickListener(this);
		LinearLayout totalmain = (LinearLayout) mSubView.findViewById(R.id.detail_total_score);
		totalmain.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.3125)));
		
		View editlayout = mSubView.findViewById(R.id.detail_comment);
		editlayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.2)));
		
		editlayout.findViewById(R.id.detail_commet_post).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_comment_view).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_like).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_gift).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_bookmark).setOnClickListener(this);
		
		if(mPageController.getAppSysId().length()>0){
			DetaiData(mPageController.getAppSysId());
			mPageController.setAppSysId("");
		}else{
			if(mAppinfo!=null){
				setUI();
			}
			setProgres();
		}
	}
	
	public void setProgres(){
		if(mService!=null&&mAppinfo!=null){
			if(mService.getDownState(mAppinfo.getPackageName())!=DownLoadService.NON_STATE){
				mSubView.findViewById(R.id.progress_one).setVisibility(View.GONE);
				mSubView.findViewById(R.id.progress_two).setVisibility(View.VISIBLE);
				mProgress = (ProgressBar) mSubView.findViewById(R.id.detail_progress);
				mHandler.sendEmptyMessageDelayed(0, 200);
				mIsPercent = true;
				setInstallUI();
			}
		}
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
	
	public void setPointUI(){
		CustomTextView textview = (CustomTextView) mSubView.findViewById(R.id.detail_top_point);
		TextView text = (TextView) mSubView.findViewById(R.id.detail_top_ori_point);
		View install = mSubView.findViewById(R.id.detail_top_install);
		install.setVisibility(View.VISIBLE);
		View del = mSubView.findViewById(R.id.detail_top_del);
		del.setVisibility(View.GONE);
		text.setEnabled(true);
		if(mIsDown==DOWN_STEP_UNINSTALL){
			text.setText("재설치");
			text.setBackgroundColor(0xffffc000);
			textview.setVisibility(View.INVISIBLE);
		}else{
			if(!mGiftFlag){
				int point = mAppinfo.getAppDownPoint().intValue();
				int sponserpoint = 0;
				if(mSponorList.size()>0){
					textview.setVisibility(View.VISIBLE);
					textview.setHarfLine(true);
					textview.invalidate();
					textview.setEnabled(false);
					for(int i=0;i<mSponorList.size();i++){
						HsPartnershipInfo info  = mSponorList.get(i);
						sponserpoint += info.getPartnerAppPoint().intValue();
					}
				}
				text.setText(mPayPoint+"P");
				if(sponserpoint>0){
					textview.setText(""+point+"P");
				}
			}else{
				text.setText("설치");
			}
		}
	}
	
	public void setDownUI(){
		Log.e("mgdoo", "setDownUI "+mIsDown);
		 View install = mSubView.findViewById(R.id.detail_top_install);
		 install.setVisibility(View.GONE);
		 View del = mSubView.findViewById(R.id.detail_top_del);
		 del.setVisibility(View.VISIBLE);
		 TextView text =  (TextView) del.findViewById(R.id.detail_top_del_percent);
		 text.setBackgroundDrawable(null);
		 text.setBackgroundColor(0xff606060);
		 text.setTextColor(0xffffffff);
		 text.setEnabled(true);
		 text.setText("제거");
		 PackageInfo pInfo = null;
		 try {
			pInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
		 } catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 text = (TextView) del.findViewById(R.id.detail_top_other_btn);
		 if(pInfo.versionName.equals(mAppinfo.getAppVersion())){
			mUpdate = false;
		 }else{
			mUpdate = true;
		 }
		 if(!mUpdate){
			 text.setBackgroundDrawable(null);
			 text.setBackgroundColor(0xff606060);
			 text.setTextColor(0xffffffff);
			 text.setEnabled(true);
			 text.setText("실행");
		 }else{
			 text.setBackgroundDrawable(null);
			 text.setBackgroundColor(0xffffc000);
			 text.setTextColor(0xff000000);
			 text.setEnabled(true);
			 text.setText("Update");
		 }
	}
	
	public void setInstallUI(){
		 View install = mSubView.findViewById(R.id.detail_top_install);
		 install.setVisibility(View.GONE);
		 View del = mSubView.findViewById(R.id.detail_top_del);
		 del.setVisibility(View.VISIBLE);
		 TextView text =  (TextView) del.findViewById(R.id.detail_top_del_percent);
		 text.setBackgroundDrawable(null);
		 text.setBackgroundColor(0xffffffff);
		 text.setTextColor(0xff000000);
		 text.setEnabled(false);
		 text.setText("0%");
		 text = (TextView) del.findViewById(R.id.detail_top_other_btn);
		 text.setBackgroundDrawable(null);
		 text.setBackgroundColor(0xff606060);
		 text.setTextColor(0xffffffff);
		 text.setEnabled(true);
		 text.setText("취소");
	}
	
	public void setSponserUI(){
		LinearLayout sponsermain = (LinearLayout) mSubView.findViewById(R.id.detail_spon);
		int cnt = mSponorViewList.size();
		if(cnt>0){
			for(int i=0;i<cnt;i++){
				sponsermain.removeView(mSponorViewList.get(i));
			}
			mSponorViewList.clear();
		}
		if(mSponorList.size()>0){
			sponsermain.setVisibility(View.VISIBLE);
			for(int i=0;i<mSponorList.size();i++){
				HsPartnershipInfo info = mSponorList.get(i);
				LinearLayout sponser = null;
				if(mPageController.getOrientation()==Configuration.ORIENTATION_LANDSCAPE){
					sponser = (LinearLayout) View.inflate(mActivity,R.layout.sponser_item_land, null);
					sponser.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.175)));
		    	}else if(mPageController.getOrientation()==Configuration.ORIENTATION_PORTRAIT){
		    		sponser = (LinearLayout) View.inflate(mActivity,R.layout.sponser_item, null);
		    		sponser.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.175)));
		    	}
				
				sponser.setOnClickListener(mSponser); 
				
				sponser.setTag(info.getPartnerWebAddres());
				
				ImageView imgview = (ImageView) sponser.findViewById(R.id.sponser_img);
				
				ImageDownloader.download(NetInfo.APP_IMAGE+info.getPartnerTitleIcon(), imgview,true);
				
				TextView text = (TextView) sponser.findViewById(R.id.sponser_name);
				text.setText(info.getPartnerName());
				text = (TextView) sponser.findViewById(R.id.sponser_cnt);
				text.setText(info.getPartnerMaxCount()+"회 지원 남음");
				
				sponsermain.addView(sponser);
				
				mSponorViewList.add(sponser);
			}
		}else{
			sponsermain.setVisibility(View.GONE);
		}
	}
	
	public void setCountUI(){
		LinearLayout totalmain = (LinearLayout) mSubView.findViewById(R.id.detail_total_score);
		TextView text = (TextView) totalmain.findViewById(R.id.detail_total_score_down_point);
		text.setText(""+mAppinfo.getAppDownCount());
		text = (TextView) totalmain.findViewById(R.id.detail_total_score_link_point);
		text.setText(""+mAppinfo.getAppLikeCount());
		text = (TextView) totalmain.findViewById(R.id.detail_total_score_view_point);
		text.setText(""+mAppinfo.getAppViewCount());
		text = (TextView) totalmain.findViewById(R.id.detail_total_score_premium_point);
		text.setText(""+mAppinfo.getAppPremiumPoint());
		text = (TextView) totalmain.findViewById(R.id.detail_total_score_total_point);
		text.setText(""+mAppinfo.getAppTotalWeight());
	}
	
	public void setUI(){
		TextView text;
		PayPointReset();
		if(mIsDown==DOWN_STEP_INSTALL){
			setDownUI();
		}else{
			setPointUI();
		}
		text = (TextView) mSubView.findViewById(R.id.detail_top_title);
		text.setText(mAppinfo.getAppTitle());
		text = (TextView) mSubView.findViewById(R.id.detail_top_nickname);
		text.setText(mAppinfo.getUserNickName());
		mSubView.findViewById(R.id.detail_top_del_percent).setOnClickListener(this);
		mSubView.findViewById(R.id.detail_top_other_btn).setOnClickListener(this);
		
		text = (TextView) mSubView.findViewById(R.id.detail_desc_text);
		text.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.WRAP_CONTENT));
		text.setText(""+mAppinfo.getAppDesc());
		
		ImageView img = (ImageView) mSubView.findViewById(R.id.detail_top_img);
		SizeData dz = new SizeData();
		dz.mWith = 64;
		dz.mHeight = 64;
		img.setTag(dz);
		if(mDownBitmap==null){
			if(mTask!=null){
				if(mTask.IsRun()){
					mTask.cancel(true);
				}
			}
			mTask = new ImageDownloadTask(NetInfo.APP_IMAGE+mAppinfo.getAppTitleIcon(),img);
			mTask.execute("");
		}else{
			img.setBackgroundDrawable(null);
			img.setImageBitmap(mDownBitmap);
		}
		ImageView imgview;
		
		LinearLayout layout = (LinearLayout) mSubView.findViewById(R.id.detail_desc);
		int padding = (int) (Global.DISPLAYHEIGHT*0.00625);
		if(mAppinfo.getAppScreen1()!=null&&mAppinfo.getAppScreen1().length()>0){
			imgview = new ImageView(mActivity);
			imgview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.5)));
			ImageDownloader.download(NetInfo.APP_IMAGE+mAppinfo.getAppScreen1(), imgview,false);
			imgview.setPadding(0, padding, padding, 0);
			layout.addView(imgview);
		}
		if(mAppinfo.getAppScreen2()!=null&&mAppinfo.getAppScreen2().length()>0){
			imgview = new ImageView(mActivity);
			imgview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.5)));
			ImageDownloader.download(NetInfo.APP_IMAGE+mAppinfo.getAppScreen2(), imgview,false);
			imgview.setPadding(0, padding, padding, 0);
			layout.addView(imgview);
		}
		if(mAppinfo.getAppScreen3()!=null&&mAppinfo.getAppScreen3().length()>0){
			imgview = new ImageView(mActivity);
			imgview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.5)));
			ImageDownloader.download(NetInfo.APP_IMAGE+mAppinfo.getAppScreen3(), imgview,false);
			imgview.setPadding(0, padding, padding, 0);
			layout.addView(imgview);
		}
		if(mAppinfo.getAppScreen4()!=null&&mAppinfo.getAppScreen4().length()>0){
			imgview = new ImageView(mActivity);
			imgview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.5)));
			ImageDownloader.download(NetInfo.APP_IMAGE+mAppinfo.getAppScreen4(), imgview,false);
			imgview.setPadding(0, padding, padding, 0);
			layout.addView(imgview);
		}
		if(mAppinfo.getAppScreen5()!=null&&mAppinfo.getAppScreen5().length()>0){
			imgview = new ImageView(mActivity);
			imgview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.5)));
			ImageDownloader.download(NetInfo.APP_IMAGE+mAppinfo.getAppScreen5(), imgview,false);
			imgview.setPadding(0, padding, padding, 0);
			layout.addView(imgview);
		}
		
		setCountUI();
		setSponserUI();
	}

	@Override
	public String getPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int BackKey() {
		// TODO Auto-generated method stub
		if(mDetailGiftView!=null){
			ReleaseDetailGiffView();
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
		if(mHandler.hasMessages(0)){
			mHandler.removeMessages(0);
		}
		if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
			mActivity.findViewById(R.id.had_info).setVisibility(View.GONE);
			mActivity.findViewById(R.id.had_info_land).setVisibility(View.VISIBLE);
    	}else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
    		mActivity.findViewById(R.id.had_info).setVisibility(View.VISIBLE);
    		mActivity.findViewById(R.id.had_info_land).setVisibility(View.GONE);
    	}
		mMainView.removeView(mSubView);
		mSubView = null;
		setPageName(ResController.PAGE_DETAIL_LIST);
		
		
	}
	
	public void DetaiData(String appid){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.APP_DETAIL,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		String id = prefs.getString(AppInfo.USER_ID, "");
		
		Log.e("mgdoo", appid);

		params.add("appSysId", appid);
		params.add("userId", id);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}
	
	public void SendBookmark(){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.APP_BOOKMARK_POST,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		String id = prefs.getString(AppInfo.USER_ID, "");

		params.add("appSysId", mAppinfo.getAppSysId());
		params.add("userId", id);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}
	
	public void SendComment(String comment){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.APP_COMMENT_POST,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		String id = prefs.getString(AppInfo.USER_ID, "");

		params.add("appSysId", mAppinfo.getAppSysId());
		params.add("userId", id);
		params.add("comment", comment);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}
	
	public void SendPay(){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.APP_DOWN,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		String id = prefs.getString(AppInfo.USER_ID, "");

		params.add("appSysId", mAppinfo.getAppSysId());
		params.add("userId", id);
		params.add("pointSum", ""+mPayPoint);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}
	
	public void SendLike(){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.APP_LIKE_POST,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		String id = prefs.getString(AppInfo.USER_ID, "");

		params.add("appSysId", mAppinfo.getAppSysId());
		params.add("userId", id);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}
	
	public void UserGift(){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_GIFT,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		String id = prefs.getString(AppInfo.USER_ID, "");
		
		params.add("appSysId", mAppinfo.getAppSysId());
		params.add("targetUserId", mDetailGiftView.getGiftId());
		params.add("userId", id);
		params.add("eventComment",mDetailGiftView.getGiftMsg());
		params.add("pointSum",""+ mGiftPoint);
		
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
			if(mDialog!=null){
				UserGift();
			}else{
				SendPay();
			}
		}else if(index==HadstroeActivity.DIALOG_CANCEL){
			mFirstDown = false;
		}
	}
	public class ImageDownloadTask extends AsyncTask<String, Void, Bitmap>
	{
		public String url;
		public String targetUrl;
		public boolean mIsRun;
		private WeakReference<ImageView> imageViewReference;

		public ImageDownloadTask(String url, ImageView imageView)
		{
			this.targetUrl = url;
			this.imageViewReference = new WeakReference<ImageView>(imageView);
		}
		@Override
		protected Bitmap doInBackground(String... params)
		{
			mIsRun = true;
			final HttpClient client = new DefaultHttpClient();
			final HttpGet getRequest = new HttpGet(targetUrl);

			try
			{
				HttpResponse response = client.execute(getRequest);
				final int statusCode = response.getStatusLine().getStatusCode();
				if(statusCode != HttpStatus.SC_OK)
				{
					Log.w("ImageDownloader", "Error " + statusCode + " while retrieving bitmap from " + targetUrl);
					return null;
				}

				final HttpEntity entity = response.getEntity();
				if(entity != null)
				{
					InputStream inputStream = null;
					
					try
					{
						inputStream = entity.getContent();
						final Bitmap bitmap = BitmapFactory.decodeStream(new FlushedInputStream(inputStream)); 
						return BitmapUtil.getBitmapReflection(bitmap,bitmap.getWidth(),bitmap.getHeight());
					}
					finally
					{
						if(inputStream != null)
						{
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			}
			catch(Exception e)
			{
				getRequest.abort();
			}
			return null;
		}
		
		public boolean IsRun(){
			return mIsRun;
		}

		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			
			if(isCancelled())
			{
				bitmap = null;
				return;
			}
			
			mIsRun = false;

			if(imageViewReference != null)
			{
				ImageView imageView = imageViewReference.get();
				imageView.setBackgroundDrawable(null);
				imageView.setImageBitmap(bitmap);
				mDownBitmap = bitmap;
			}
			
			mTask = null;
			
		}
	}
	public class FlushedInputStream extends FilterInputStream
	{
		public FlushedInputStream(InputStream inputStream)
		{
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException
		{
			long totalBytesSkipped = 0L;
			while(totalBytesSkipped < n)
			{
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if(bytesSkipped == 0L)
				{
					int bytes = read();
					if(bytes < 0)
					{
						break; // we reached EOF
					}
					else
					{
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}
}
