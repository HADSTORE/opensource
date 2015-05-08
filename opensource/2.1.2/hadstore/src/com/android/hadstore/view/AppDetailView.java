package com.android.hadstore.view;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
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
import android.telephony.TelephonyManager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.CustomGallery;
import com.android.hadstore.CustomDialog;
import com.android.hadstore.CustomTextView;
import com.android.hadstore.DownLoadService;
import com.android.hadstore.Global;
import com.android.hadstore.HadstroeActivity;
import com.android.hadstore.HorizontalListView;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.adapter.AppDetailAdapter;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.android.hadstore.tesk.ImageDownloader;
import com.android.hadstore.util.BitmapUtil;
import com.android.hadstore.util.FormatUtil;
import com.operation.model.HsApplicationInfo;
import com.operation.model.HsDownHistory;
import com.operation.model.HsPartnershipInfo;
import com.operation.model.HsUserInfo;
import android.widget.Gallery;

public class AppDetailView implements SubPageController,HttpRequestTaskListener,View.OnClickListener,OnItemClickListener{
	
	private static final int PAY_DEFAULT = 0;
	
	private static final int PAY_CHECK = 1;
	
	private static final int PAY_VALIDATION = 2;

	private Activity mActivity;
	
	private FrameLayout mMainView;
	
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
	
	private CustomDialog mDialog;
	
	private int mIsDown;
	
	private final int DOWN_STEP_INSTALL = 1;
	
	private final int DOWN_STEP_UNINSTALL = 2;
	
	private boolean mIsPercent;
	
	private boolean mFirstDown = false;
	
	private int mPayStep;
	
	private int mPayPoint;
	
	private int mGiftPoint;
	
	private boolean mGiftFlag;
	
	private boolean mUpdateFlag;
	
	private AppDetailGiftView mDetailGiftView;
	
	private boolean mUpdate;
	
	private ArrayList<String> mScreenList = new ArrayList<String>();
	
	private LinearLayout mScrollLayout;
	
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
	
    View.OnClickListener mSreen = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int link = (Integer) v.getTag();
			Intent intent = new Intent();
	        intent.setComponent(new ComponentName("com.android.hadstore", "com.android.hadstore.ExpansionActivity"));
	        intent.putExtra("uri", mScreenList.get(link));
	        mActivity.startActivity(intent);
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
		mMainView = (FrameLayout) parent;
		mCommon = new CommonDailog(mActivity);
		mService = DownLoadService.getService();
		mGiftFlag = mPageController.isGiftFlag();
		mPageController.setGiftFlag(false);
		mUpdateFlag = mPageController.isUpdateFlag();
		mPageController.setUpdateFlag(false);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
		case R.id.detail_top_ori_point:
			if(FormatUtil.getOsVersionParser(android.os.Build.VERSION.RELEASE)<FormatUtil.getOsVersionParser(mAppinfo.getAppOsVersion())){
				Toast.makeText(mActivity.getApplicationContext(), "해당 앱은 안드로이드 "+mAppinfo.getAppOsVersion()+"버젼 이상 이여야 합니다." , 0).show();
				return;
			}
			if(!mPageController.mCheckLogin){
				if(mPayPoint>0)
					Toast.makeText(mActivity.getApplicationContext(), "유료 앱은 로그인이 필요합니다.", 0).show();
				else if(mSponorViewList.size()>0){
					Toast.makeText(mActivity.getApplicationContext(), "로그인이 필요합니다.", 0).show();
				}else
					SendPay();
			}else{
				if(mGiftFlag){
					SendPay();
				}else{
					if(mIsDown==DOWN_STEP_UNINSTALL){
						SendPay();
						mFirstDown = false;
						//mService.setAddDownload(mAppinfo.getAppTitle(),mAppinfo.getAppLocationLinkName(), mAppinfo.getPackageName(), NetInfo.APP_APK+mAppinfo.getAppLocationLink());
						//setProgres();
					}else{
						if(mPageController.mSalesPoint<mPayPoint){
							Toast.makeText(mActivity.getApplicationContext(), "포인트가 부족합니다.", 0).show();
							return;
						}else{
							((HadstroeActivity)mActivity).setPoint(mPayPoint);
							mActivity.showDialog(HadstroeActivity.DIALOG_DOWN_CHECK);
							mPayStep = PAY_CHECK; 
							mFirstDown = false;
						}
					}
				}
			}
			break;
		case R.id.detail_url_copy:
			android.text.ClipboardManager clipboard = (android.text.ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
			clipboard.setText("hadstoreapp://hadstore?appid="+mAppinfo.getAppSysId());
			Toast.makeText(mActivity.getApplicationContext(), "앱 url복사", 0).show();
			break;
		case R.id.detail_commet_post:
			if(!mPageController.mCheckLogin){
				Toast.makeText(mActivity.getApplicationContext(), "로그인이 필요합니다.", 0).show();
			}else{
				EditText edit = (EditText) mSubView.findViewById(R.id.detail_comment_edit);
				if(edit.length()>0){
					SendComment(edit.getText().toString());
				}
			}
			break;
		case R.id.detail_comment_view:
			mPageController.setAppSysId(mAppinfo.getAppSysId());
			mPageController.setPage(ResController.PAGE_COMMENT);
			break; 
		case R.id.detail_top_like:
			if(!mPageController.mCheckLogin){
				Toast.makeText(mActivity.getApplicationContext(), "로그인이 필요합니다.", 0).show();
			}else
				SendLike();
			break;
		case R.id.detail_top_bookmark:
			if(!mPageController.mCheckLogin){
				Toast.makeText(mActivity.getApplicationContext(), "로그인이 필요합니다.", 0).show();
			}else
				SendBookmark();
			break;
		case R.id.detail_top_gift:
			if(!mPageController.mCheckLogin){
				Toast.makeText(mActivity.getApplicationContext(), "로그인이 필요합니다.", 0).show();
			}else{
				GiftPointReset();
				if(mPageController.mSalesPoint<mGiftPoint){
					Toast.makeText(mActivity.getApplicationContext(), "포인트가 부족합니다.", 0).show();
					return;
				}
				InitDetailGiffView();
			}
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
				mProgress.setMax(100);
				mProgress.setProgress(0);
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
					mFirstDown = false;
					mService.setAddDownload(mAppinfo.getAppTitle(),mAppinfo.getAppLocationLinkName(), mAppinfo.getPackageName(), NetInfo.APP_APK+mAppinfo.getAppLocationLink());
					setProgres();
					return;
				}else if(status == NetInfo.PAY_FAIL){
					mFirstDown = true;
					Toast.makeText(mActivity.getApplicationContext(), "스폰서 포인트 변경으로 하여 결제 포인트가 변경 포인트가 부족합니다.", 0).show();
				}else if(status == NetInfo.PAY_LACK){
					Toast.makeText(mActivity.getApplicationContext(), "포인트가 부족합니다.", 0).show();
					return;
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
					setPointUI();
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
					Toast.makeText(mActivity.getApplicationContext(), "스폰서 포인트 변경으로 하여 결제 포인트가 변경 포인트가 부족합니다.", 0).show();
				}else if(status == NetInfo.PAY_LACK){
					Toast.makeText(mActivity.getApplicationContext(), "포인트가 부족합니다.", 0).show();
					return;
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
					setPointUI();
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
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		if(mHandler.hasMessages(0)){
			mHandler.removeMessages(0);
		}
		
		mMainView.removeView(mSubView);
		mSubView = null;
		setPageName(ResController.PAGE_DETAIL_LIST);
		//mSubView.setVisibility(View.VISIBLE);
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
			if(!mHandler.hasMessages(0)){
				CheckDown();
				if(mIsDown==DOWN_STEP_INSTALL){
					setDownUI();
				}else{
					setPointUI();
				}
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
   				if(mPageController.mDownList.containsKey(mAppinfo.getPackageName())){
   					mIsDown = DOWN_STEP_INSTALL;
   				}
				break;
	    	}
	    }
	    if(mIsDown != DOWN_STEP_INSTALL && mPageController.mCheckLogin){
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
		
		mScrollLayout = (LinearLayout) mSubView.findViewById(R.id.search_detail_scrollview_layout);
		
		LinearLayout totalmain = (LinearLayout) mSubView.findViewById(R.id.detail_total_score);
		totalmain.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.3125)));
		
		View editlayout = mSubView.findViewById(R.id.detail_comment);
		editlayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.2)));
		
		editlayout.findViewById(R.id.detail_commet_post).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_comment_view).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_like).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_gift).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_bookmark).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_url_copy).setOnClickListener(this);
		
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
		
		mScrollLayout = (LinearLayout) mSubView.findViewById(R.id.search_detail_scrollview_layout);
		
		editlayout.findViewById(R.id.detail_commet_post).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_comment_view).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_like).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_gift).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_top_bookmark).setOnClickListener(this);
		
		mSubView.findViewById(R.id.detail_url_copy).setOnClickListener(this);
		
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
			mPayPoint = 0;
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
			pInfo = mActivity.getPackageManager().getPackageInfo(mAppinfo.getPackageName(), PackageManager.GET_META_DATA);
		 } catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		 }
		 text = (TextView) del.findViewById(R.id.detail_top_other_btn);
		 if(pInfo==null||pInfo.versionName.equals(mAppinfo.getAppVersion())){
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
		if(mUpdateFlag){
			if(mAppinfo.getAppUpdateDesc()!=null&&!mAppinfo.getAppUpdateDesc().equals(""))
				text.setText(Html.fromHtml(mAppinfo.getAppUpdateDesc().replace("\n", "<br>")));
		}else{
			if(mAppinfo.getAppDesc()!=null&&!mAppinfo.getAppDesc().equals(""))
				text.setText(Html.fromHtml(mAppinfo.getAppDesc().replace("\n", "<br>")));
		}
		
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
		mScreenList.clear();
		
		int padding =  (int)(Global.DISPLAYWIDTH*0.016);
		if(mAppinfo.getAppScreen1()!=null&&mAppinfo.getAppScreen1().length()>0){
			mScreenList.add(mAppinfo.getAppScreen1());
		}
		if(mAppinfo.getAppScreen2()!=null&&mAppinfo.getAppScreen2().length()>0){
			mScreenList.add(mAppinfo.getAppScreen2());
		}
		if(mAppinfo.getAppScreen3()!=null&&mAppinfo.getAppScreen3().length()>0){
			mScreenList.add(mAppinfo.getAppScreen3());
		}
		if(mAppinfo.getAppScreen4()!=null&&mAppinfo.getAppScreen4().length()>0){
			mScreenList.add(mAppinfo.getAppScreen4());
		}
		if(mAppinfo.getAppScreen5()!=null&&mAppinfo.getAppScreen5().length()>0){
			mScreenList.add(mAppinfo.getAppScreen5());
		}
		
		if(mScreenList.size()>0){
			mSubView.findViewById(R.id.search_detail_scrollview).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.358)));
			for(int i = 0; i<mScreenList.size();i++){
				ImageView screen = new ImageView(mActivity);
				screen.setTag(i);
				screen.setOnClickListener(mSreen);
				screen.setLayoutParams(new LinearLayout.LayoutParams((int) (Global.DISPLAYHEIGHT*0.358),(int) (Global.DISPLAYHEIGHT*0.358)));
				screen.setBackgroundResource(R.drawable.list_defimg);
				ImageDownloader.download2(NetInfo.APP_IMAGE+mScreenList.get(i), screen);
				mScrollLayout.addView(screen);
				if(i < (mScreenList.size()-1)){
					View view  = new View(mActivity);
					view.setLayoutParams(new LinearLayout.LayoutParams((int) (Global.DISPLAYWIDTH*0.01),LayoutParams.FILL_PARENT));
					mScrollLayout.addView(view);
				}
			}
		}
		setCountUI();
		setSponserUI();
	}

	@Override
	public String getPageName() {
		// TODO Auto-generated method stub
		return mPageName;
	}

	@Override
	public int BackKey() {
		// TODO Auto-generated method stub
		if(mDetailGiftView!=null){
			ReleaseDetailGiffView();
			return 1;
		}
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
		if(mHandler.hasMessages(0)){
			mHandler.removeMessages(0);
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

		params.add("appSysId", appid);
		if(mPageController.mCheckLogin)
			params.add("userId", mPageController.getUserId());
		else
			params.add("userId", "-");
		
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

		params.add("appSysId", mAppinfo.getAppSysId());
		params.add("userId", mPageController.getUserId());
		
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

		params.add("appSysId", mAppinfo.getAppSysId());
		params.add("userId", mPageController.getUserId());
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

		params.add("appSysId", mAppinfo.getAppSysId());
		if(mPageController.mCheckLogin)
			params.add("userId", mPageController.getUserId());
		else
			params.add("userId", "-");
			
		if(mGiftFlag)
			params.add("pointSum", "0");
		else
			params.add("pointSum", ""+mPayPoint);
		
		final TelephonyManager tm = (TelephonyManager)mActivity.getBaseContext().getSystemService(Activity.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(mActivity.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
		
		params.add("appDownMachineName", tmDevice+android.os.Build.SERIAL+deviceId);
		
		if(mGiftFlag){
			params.add("isGift", "Y");
		}else{
			params.add("isGift", "N");
		}
		
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

		params.add("appSysId", mAppinfo.getAppSysId());
		params.add("userId", mPageController.getUserId());
		
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
		
		params.add("appSysId", mAppinfo.getAppSysId());
		params.add("targetUserId", mDetailGiftView.getGiftId());
		params.add("userId", mPageController.getUserId());
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
				if(mPayStep==PAY_CHECK){
					if(mPayPoint>0){
						mActivity.showDialog(HadstroeActivity.DIALOG_DOWN_VALIDATION);
						mPayStep = PAY_VALIDATION; 
					}else{
						mPayStep = PAY_DEFAULT;
						SendPay();
					}
				}else if(mPayStep==PAY_VALIDATION){
					SendPay();
				}
			}
		}else if(index==HadstroeActivity.DIALOG_CANCEL){
			mPayStep=PAY_DEFAULT;
			mFirstDown = false;
		}
	}
	
	public class ImageSizeCheckTask extends AsyncTask<String, Void, Bitmap>
	{

		@Override
		protected Bitmap doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			if(isCancelled())
			{
				bitmap = null;
				return;
			}
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
			this.targetUrl = url.replaceAll (" ", "%20");
			this.imageViewReference = new WeakReference<ImageView>(imageView);
		}
		@Override
		protected Bitmap doInBackground(String... params)
		{
			mIsRun = true;
			
			HttpClient client = null; 
			HttpGet getRequest = null;

			try
			{
				client = new DefaultHttpClient();
				getRequest = new HttpGet(targetUrl);
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
				if(getRequest!=null)getRequest.abort();
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
				if(imageView!=null){
					imageView.setBackgroundDrawable(null);
					imageView.setImageBitmap(bitmap);
				}
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
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.android.hadstore", "com.android.hadstore.ExpansionActivity"));
        //intent.putExtra("uri", (String)mAppDetailAdapter.getItem(arg2));
        mActivity.startActivity(intent);
	}
}
