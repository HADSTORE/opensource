package com.android.hadstore.view;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.Global;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.adapter.CommentAdapter;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.operation.model.HsApplicationInfo;
import com.operation.model.HsCommentHistory;
import com.operation.model.HsPartnershipInfo;

public class CommentView implements SubPageController,HttpRequestTaskListener,View.OnClickListener {

	private ListView mListView;
	
	private Activity mActivity;
	
	private LinearLayout mMainView;
	
	private PageController mPageController;
	
	public int mMainMode = 0;
	
	private View mSubView;
	
	private String mPageName;
	
	private CommonDailog mCommon;
	
	private String mAppSysid;
	
	private CommentAdapter mAdapter;
	
	private int mListCount = 0;
	
	private String mTime=null;
	
	public CommentView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		mPageController = controller; 
		mMainView = (LinearLayout) parent;
		mCommon = new CommonDailog(mActivity);
		mAppSysid = mPageController.getAppSysId();
		mPageController.setAppSysId("");
	}
	
	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub

	}

	@Override
	public void Hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		mMainView.removeView(mSubView);
		
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

	}

	@Override
	public void setPageName(String name) {
		// TODO Auto-generated method stub
		mPageName  = name;
		if(mPageController.getOrientation()==Configuration.ORIENTATION_LANDSCAPE){
			setPageLand(ResController.PAGE_COMMENT_LAND);
		}else{
			setPageProt(ResController.PAGE_COMMENT);
		}
		SendComment(mListCount);
	}
	
	public void SendComment(int start){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.APP_COMMENT_LIST,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();

		params.add("appSysId", mAppSysid);
		params.add("search.max", "10");
		
		
		if(mTime!=null)
			params.add("search.time", mTime);
		else
			params.add("search.time", "0");
		
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
	public void setPageLand(String name){
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(name),null);
		
		mSubView.findViewById(R.id.comment_next).setOnClickListener(this);
		mSubView.findViewById(R.id.comment_top).setOnClickListener(this);
		
		mListView = (ListView) mSubView.findViewById(R.id.comment_listview);
		
		mMainView.addView(mSubView);
		
		if(mAdapter!=null){
			mListView.setAdapter(mAdapter);
		}
	}

	public void setPageProt(String name){
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(name),null);
		
		mSubView.findViewById(R.id.comment_next).setOnClickListener(this);
		mSubView.findViewById(R.id.comment_top).setOnClickListener(this);
		
		mListView = (ListView) mSubView.findViewById(R.id.comment_listview);
		
		mMainView.addView(mSubView);
		
		if(mAdapter!=null){
			mListView.setAdapter(mAdapter);
		}
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		mMainView.removeView(mSubView);
		mSubView = null;
		if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
			mActivity.findViewById(R.id.had_info).setVisibility(View.GONE);
			mActivity.findViewById(R.id.had_info_land).setVisibility(View.VISIBLE);
			setPageLand(ResController.PAGE_COMMENT_LAND);
    	}else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
    		mActivity.findViewById(R.id.had_info).setVisibility(View.VISIBLE);
    		mActivity.findViewById(R.id.had_info_land).setVisibility(View.GONE);
    		setPageProt(ResController.PAGE_COMMENT);
    	}
		
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.comment_next:
				SendComment(mListCount);
				break;
			case R.id.comment_top:
				mListView.setSelectionFromTop(0, 0);
				break;
		}
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.APP_COMMENT_LIST)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					if(mTime==null)
						mTime = (String) map.get(NetInfo.TIME);
					List<HsCommentHistory> list = (List<HsCommentHistory>) map.get(NetInfo.COMMNET);
					if(mAdapter==null){
						mAdapter = new CommentAdapter(mActivity);
						mListView.setAdapter(mAdapter);
					}
					if(list!=null&&list.size()>0)
						mTime = list.get(list.size()-1).getEventTime2();
					
					mListCount+=list.size();
					mAdapter.AddItem(list);
				}
			}
		}
		
	}

}
