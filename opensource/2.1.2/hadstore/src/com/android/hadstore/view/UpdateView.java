package com.android.hadstore.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;

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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.Global;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.adapter.SearchAdapter;
import com.android.hadstore.adapter.SeparatedListAdapter;
import com.android.hadstore.adapter.UpdatePayAdapter;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.tesk.ImageDownloader;
import com.android.hadstore.util.DateUtil;
import com.operation.model.HsApplicationInfo;
import com.operation.model.HsDownHistory;

public class UpdateView implements SubPageController,View.OnClickListener{

	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private PageController mPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private boolean mIsAuto;
	
	private CommonDailog mCommon;
	
	public static boolean misRun;
	
	private UpdatePayAdapter mUpdateAdapter;
	
	private UpdatePayAdapter mPayAdapter;

	private SeparatedListAdapter mAdapter;
	
	class ImgRedown{
		String mPath;
		ImageView mImg;
	};
	
	private View.OnClickListener mPayListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int key = (Integer) v.getTag();
			HsDownHistory pay = mArrayList.get(key);
			mPageController.setAppSysId(pay.getAppSysId());
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}
	};
	
	private View.OnClickListener mUpdateListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int key = (Integer) v.getTag();
			HsDownHistory pay = mArrayUpdateList.get(key);
			mPageController.setUpdateFlag(true);
			mPageController.setAppSysId(pay.getAppSysId());
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}
	};
	
	public ArrayList<HsDownHistory> mArrayList = new ArrayList<HsDownHistory>();
	
	public ArrayList<HsDownHistory> mArrayUpdateList = new ArrayList<HsDownHistory>();
	
	public UpdateView(View parent,Activity activity,PageController controller){
		misRun = true;
		mActivity = activity;
		mPageController = controller;
		mMainView = (FrameLayout) parent;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mCommon = new CommonDailog(mActivity);
	}

	public void Release() {
		// TODO Auto-generated method stub
		
		misRun = false;
		
		//mHandlerRedown = null;
		
		mMainView.removeView(mSubView);
		
		mSubView = null;
		
		mActivity = null;
		
		mMainView = null;
		
		mPageController = null;
	}
	
	public void Show(){
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
		
		ListView listview = (ListView) mSubView.findViewById(R.id.update_listview);
		ArrayList<View> list = new ArrayList<View>();
		View convertView = View.inflate(mActivity,R.layout.update_item, null);
		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1385)));
		convertView.setBackgroundColor(0xfffafafa);
		TextView title  = (TextView) convertView.findViewById(R.id.title);
		title.setText("Update 목록");
		convertView.setOnClickListener(this);
		convertView.setId(1001);
		list.add(convertView);
		
		convertView = View.inflate(mActivity,R.layout.update_item, null);
		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1385)));
		title  = (TextView) convertView.findViewById(R.id.title);
		title.setText("구매 목록");
		convertView.setOnClickListener(this);
		convertView.setId(1002);
		list.add(convertView);
		mUpdateAdapter = new UpdatePayAdapter(mActivity, mArrayUpdateList, mUpdateListener);
		mPayAdapter = new UpdatePayAdapter(mActivity, mArrayList, mPayListener);
		mAdapter = new SeparatedListAdapter(mActivity, list);
		mAdapter.addSection("0", mUpdateAdapter);
		mAdapter.addSection("1", mPayAdapter);
		mPayAdapter.Clear();
		mUpdateAdapter.Clear();
		
		listview.setAdapter(mAdapter);
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
		   mArrayUpdateList.clear();
	       for (ResolveInfo info : listResolve) {
	    	   Log.e("package name", info.activityInfo.packageName);
	       		if(mPageController.mDownList.containsKey(info.activityInfo.packageName)) {
	       			HsDownHistory hs = mPageController.mDownList.get(info.activityInfo.packageName);
	       			PackageInfo pInfo = null;
					try {
						pInfo = mActivity.getPackageManager().getPackageInfo(info.activityInfo.packageName, PackageManager.GET_META_DATA);
						if(!hs.getAppVersion().equals(pInfo.versionName)){
							mArrayUpdateList.add(mPageController.mDownList.get(info.activityInfo.packageName));
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
			case 1002:
				if(mPayAdapter.getCount()==0){
					setPurchaseUI();
				}else{
					mAdapter.getHeaderView(1).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_down_arrow);
					mPayAdapter.Clear();
					mAdapter.notifyDataSetChanged();
				}
				break;
			case 1001:
				if(mUpdateAdapter.getCount()==0){
					// 최초 불러올땐 seq = 0을 입력한다
					new UpdateCheckAsync().execute("");
				}else{
					mAdapter.getHeaderView(0).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_down_arrow);
					mUpdateAdapter.Clear();
					mAdapter.notifyDataSetChanged();
				}
				break;
		}
	}
	
	public void setUpdateUI(){
		int size = mArrayUpdateList.size();
		if(size>0){
			mUpdateAdapter.setItem(mArrayUpdateList);
			mAdapter.notifyDataSetChanged();
			mAdapter.getHeaderView(0).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_up_arrow);
			/*mSubView.findViewById(R.id.update_list).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_up_arrow);
			ListView listview = (ListView) mSubView.findViewById(R.id.update);
			mUpdateAdapter = new UpdatePayAdapter(mActivity, mArrayUpdateList, mUpdateListener);
			listview.setVisibility(View.VISIBLE);
			if(size>4){
				listview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.6)));
			}else{
				listview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) ((Global.DISPLAYHEIGHT*0.15)*size)));
			}
			listview.setAdapter(mUpdateAdapter);*/
		}
	}
	
	public void setPurchaseUI(){
		int size = mPageController.mDownList.size();
		if(size>0){
			Set<String> keys =  mPageController.mDownList.keySet();
			mArrayList.clear();
	        for (String key : keys) {
	        	mArrayList.add(mPageController.mDownList.get(key));
	        }
			mPayAdapter.setItem(mArrayList);
			mAdapter.notifyDataSetChanged();
			mAdapter.getHeaderView(1).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_up_arrow);
			/*mSubView.findViewById(R.id.purchase_list).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_up_arrow);
			ListView listview = (ListView) mSubView.findViewById(R.id.purchase);
			Set<String> keys =  mPageController.mDownList.keySet();
			listview.setVisibility(View.VISIBLE);
			mArrayList.clear();
	        for (String key : keys) {
	        	mArrayList.add(mPageController.mDownList.get(key));
	        }
			mPayAdapter = new UpdatePayAdapter(mActivity, mArrayList, mPayListener);
			listview.setVisibility(View.VISIBLE);
			if(size>4){
				listview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.6)));
			}else{
				listview.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) ((Global.DISPLAYHEIGHT*0.15)*size)));
			}
			listview.setAdapter(mPayAdapter);*/
			
		}
	}

	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		
	}
}
