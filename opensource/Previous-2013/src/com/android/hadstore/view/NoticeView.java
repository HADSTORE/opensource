package com.android.hadstore.view;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.adapter.NoticeAdapter;
import com.android.hadstore.adapter.SearchAdapter;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.android.hadstore.util.DateUtil;
import com.operation.model.HsApplicationInfo;
import com.operation.model.HsBoardInfo;
import com.operation.model.HsDownHistory;

public class NoticeView implements SubPageController,HttpRequestTaskListener,View.OnClickListener,OnItemClickListener{

	private Activity mActivity;
	
	private LinearLayout mMainView;
	
	private PageController mPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private CommonDailog mCommon;
	
	private NoticeAdapter mAdapter;
	
	private ListView mListView;
	
	private TextView mTime;
	
	private TextView mWeekMonth;
	
	private TextView mDay;	
	
	private int mStart;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			Calendar calendar = new GregorianCalendar();  
			mTime.setText(DateUtil.getMainTime(calendar));
			mWeekMonth.setText(DateUtil.getMonth(calendar));
			mDay.setText(""+calendar.get(Calendar.DATE));
			mHandler.sendEmptyMessageDelayed(0, 1000);
		};
	};
	
	public NoticeView(View parent,Activity activity,PageController controller){
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
	
	@Override
	public void Show(){
		mSubView.setVisibility(View.VISIBLE);
	}
	
	@Override
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
		
		mListView = (ListView) mSubView.findViewById(R.id.notice_list_listview);
		
		mMainView.addView(mSubView);
		
		mSubView.findViewById(R.id.notice_next_search).setOnClickListener(this);
		
		SendNoticeList(mStart);
		
		mHandler.sendEmptyMessageDelayed(0, 0);
	}
	
	public void SendNoticeList(int start){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.NOTICE_LIST,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();

		params.add("search.max", "10");
		params.add("search.pos", ""+start);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		

		
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
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.NOTICE_LIST)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					List<HsBoardInfo> list= (List<HsBoardInfo>)map.get(NetInfo.LIST);
					if(mAdapter==null){
						mAdapter = new NoticeAdapter(mActivity);
						mListView.setAdapter(mAdapter);
						mListView.setOnItemClickListener(this);
					}
					mStart+=list.size();
					mAdapter.AddItem(list);
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.notice_next_search:
				SendNoticeList(mStart);
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adpt, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		NoticeAdapter adapter = ((NoticeAdapter)adpt.getAdapter());
		HsBoardInfo item = (HsBoardInfo) adapter.getItem(arg2);
		mPageController.setNoticeSeq(item.getBoardSequence().intValue());
		mPageController.setPage(ResController.PAGE_NOTICE_DETAIL);
	}
}
