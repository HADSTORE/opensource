package com.android.hadstore.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.Global;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.adapter.SearchAdapter;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.tesk.ImageDownloader;
import com.android.hadstore.util.DateUtil;
import com.operation.model.HsApplicationInfo;
import com.operation.model.HsDownHistory;

public class UpdateView implements SubPageController,View.OnClickListener{

	private Activity mActivity;
	
	private LinearLayout mMainView;
	
	private PageController mPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private boolean mIsAuto;
	
	private CommonDailog mCommon;
	
	private SearchAdapter mAdapter;
	
	private TextView mTime;
	
	private TextView mWeekMonth;
	
	private TextView mDay;
	
	private View.OnClickListener mPayListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String key = (String) v.getTag();
			HsDownHistory pay =mPageController.mDownList.get(key);
			mPageController.setAppSysId(pay.getAppSysId());
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}
	};
	
	private View.OnClickListener mUpdateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int key = (Integer) v.getTag();
			HsDownHistory pay =mArrayList.get(key);
			mPageController.setAppSysId(pay.getAppSysId());
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}
	};
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			Calendar calendar = new GregorianCalendar();  
			mTime.setText(DateUtil.getMainTime(calendar));
			mWeekMonth.setText(DateUtil.getMonth(calendar));
			mDay.setText(""+calendar.get(Calendar.DATE));
			mHandler.sendEmptyMessageDelayed(0, 1000);
		};
	};
	
	public ArrayList<HsDownHistory> mArrayList = new ArrayList<HsDownHistory>();
	
	public UpdateView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mPageController = controller;
		mMainView = (LinearLayout) parent;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mCommon = new CommonDailog(mActivity);
	}

	public void Release() {
		// TODO Auto-generated method stub
		if(mHandler.hasMessages(0)){
			mHandler.removeMessages(0);
		}
		
		mMainView.removeView(mSubView);
		
		mSubView = null;
		
		mActivity = null;
		
		mMainView = null;
		
		mPageController = null;
	}
	
	public void Show(){
		mSubView.setVisibility(View.VISIBLE);
	}
	
	public void Hide(){
		mSubView.setVisibility(View.GONE);
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
		mTime = (TextView) mSubView.findViewById(R.id.clock_time);
		mWeekMonth = (TextView) mSubView.findViewById(R.id.clock_week_month);
		mDay = (TextView) mSubView.findViewById(R.id.clock_day);
		
		View convertView = mSubView.findViewById(R.id.update_list);
		convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1125)));
		TextView title  = (TextView) convertView.findViewById(R.id.title);
		convertView.findViewById(R.id.text_btn).setVisibility(View.INVISIBLE);
		title.setText("Update 목록");

		TextView textbtn = (TextView) convertView.findViewById(R.id.text_btn);
		textbtn.setText("전체 Update");
		textbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		convertView.setOnClickListener(this);
		
		convertView = mSubView.findViewById(R.id.purchase_list);
		convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1125)));
		title  = (TextView) convertView.findViewById(R.id.title);
		convertView.findViewById(R.id.text_btn).setVisibility(View.INVISIBLE);
		title.setText("구매 목록");
		convertView.setOnClickListener(this);
		
		mMainView.addView(mSubView);
		
		mHandler.sendEmptyMessageDelayed(0, 0);
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
            if(! mCommon.isShowing()){
    			mCommon.show();
    		}
        }
        
		@Override
        protected Boolean doInBackground(String... aurl) {
		   List<ResolveInfo> listResolve = findAllActivitiesForPackage();
	        //  Exist ItemInfo in Packages but not exist in Database
		   mPageController.mUpdateCount = 0;
		   mArrayList.clear();
	       for (ResolveInfo info : listResolve) {
	       		if(mPageController.mDownList.containsKey(info.activityInfo.packageName)) {
	       			HsDownHistory hs = mPageController.mDownList.get(info.activityInfo.packageName);
	       			PackageInfo pInfo = null;
					try {
						pInfo = mActivity.getPackageManager().getPackageInfo(info.activityInfo.packageName, PackageManager.GET_META_DATA);
						if(!hs.getAppVersion().equals(pInfo.versionName)){
							mArrayList.add(mPageController.mDownList.get(info.activityInfo.packageName));
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
        	if( mCommon.isShowing()){
				mCommon.dismiss();
			}
        	setUpdateUI();
        	
        }
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.purchase_list:
				LinearLayout layout = (LinearLayout) mSubView.findViewById(R.id.purchase);
				if(layout.getChildCount()==0){
					setPurchaseUI();
				}else{
					mSubView.findViewById(R.id.purchase_list).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.bottom_arrow_img);
					layout.removeAllViews();
				}
				break;
			case R.id.update_list:
				layout = (LinearLayout) mSubView.findViewById(R.id.update);
				if(layout.getChildCount()==0){
					new UpdateCheckAsync().execute("");
				}else{
					mSubView.findViewById(R.id.update_list).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.bottom_arrow_img);
					layout.removeAllViews();
				}
				break;
		}
	}
	
	public void setUpdateUI(){
		int size = mArrayList.size();
		if(size>0){
			mSubView.findViewById(R.id.update_list).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.top_arrow_img);
			LinearLayout layout = (LinearLayout) mSubView.findViewById(R.id.update);
			for(int i=0;i<size;i++){
				HsDownHistory update =mArrayList.get(i);
	    		View convertView = View.inflate(mActivity,R.layout.update_pay_item, null);
	    		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.15)));
	    		
	    		convertView.setTag(i);
	    		
	    		TextView nickname = (TextView) convertView.findViewById(R.id.list_nickname);
	        	TextView apptitle = (TextView) convertView.findViewById(R.id.list_apptitle);
	        	TextView appversion = (TextView) convertView.findViewById(R.id.list_version);
	        	ImageView image = (ImageView) convertView.findViewById(R.id.list_img);
	        	
	        	appversion.setText(update.getAppVersion());
	        	apptitle.setText(update.getAppTitle());
	        	nickname.setText(update.getUserNickName());
	        	ImageDownloader.download(NetInfo.APP_IMAGE+update.getAppTitleIcon(), image,true);
	        	
	        	convertView.setOnClickListener(mUpdateListener);
	    		
	    		layout.addView(convertView);
			}
		}
	}
	
	public void setPurchaseUI(){
		int size = mPageController.mDownList.size();
		if(size>0){
			mSubView.findViewById(R.id.purchase_list).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.top_arrow_img);
			LinearLayout layout = (LinearLayout) mSubView.findViewById(R.id.purchase);
			Set<String> keys =  mPageController.mDownList.keySet();
	        int i=0;
	        for (String key : keys) {
				HsDownHistory purchase =mPageController.mDownList.get(key);
	    		View convertView = View.inflate(mActivity,R.layout.update_pay_item, null);
	    		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.15)));
	    		
	    		convertView.setTag(key);
	    		
	    		TextView nickname = (TextView) convertView.findViewById(R.id.list_nickname);
	        	TextView apptitle = (TextView) convertView.findViewById(R.id.list_apptitle);
	        	TextView appversion = (TextView) convertView.findViewById(R.id.list_version);
	        	ImageView image = (ImageView) convertView.findViewById(R.id.list_img);
	        	
	        	appversion.setText(purchase.getAppVersion());
	        	apptitle.setText(purchase.getAppTitle());
	        	nickname.setText(purchase.getUserNickName());
	        	ImageDownloader.download(NetInfo.APP_IMAGE+purchase.getAppTitleIcon(), image,true);
	        	
	        	convertView.setOnClickListener(mPayListener);
	    		
	    		layout.addView(convertView);
	    		i++;
			}
		}
	}

	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		
	}
}
