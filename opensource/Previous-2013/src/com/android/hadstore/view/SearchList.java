package com.android.hadstore.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.R;
import com.android.hadstore.Global;
import com.android.hadstore.HadstroeActivity;
import com.android.hadstore.ResController;
import com.android.hadstore.adapter.SearchAdapter;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.android.hadstore.tesk.ImageDownloader;
import com.operation.model.HsApplicationInfo;

public class SearchList implements SubPageController,View.OnClickListener,HttpRequestTaskListener,OnItemClickListener,OnScrollListener{
	private ListView mListView;
	
	private SearchOptionView mSearchOptionView;
	
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
	
	private boolean mOrientation;
	
	private SearchAdapter mAdapter;
	
	private CommonDailog mCommon;
	
	ArrayList<HsApplicationInfo> mhsList  = new ArrayList<HsApplicationInfo>();
	
	private int mCount;
	
	private int mScrollY;
	
	private String mTime = null;
	
	public SearchList(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
		mPageController = controller; 
		mOrientation = false;
		mCommon = new CommonDailog(mActivity);
		mMainView = (LinearLayout) parent;
	}
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()){
			case R.id.option_btn:
				if(mOptionShow){
					OptionHideAni();
				}else{
					mCount = 0;
					mScrollY = 0;
					mhsList.clear();
					mAdapter.Clear();
					OptionViewAni();
				}
				break;
			case R.id.next_search:
				SearchData(mPageController.mSearchName,mCount);
				break;
			case R.id.search_bar:
				mCount = 0;
				mScrollY = 0;
				mPageController.mSearchName = "";
				Intent intent = new Intent();
		        intent.setComponent(new ComponentName("com.android.hadstore", "com.android.hadstore.HDSearchActivity"));
		        mActivity.startActivityForResult(intent, HadstroeActivity.SEARCH_ACTIVITY);
				break;
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
		if(mPageController.getOrientation()==Configuration.ORIENTATION_LANDSCAPE){
			mActivity.findViewById(R.id.had_info).setVisibility(View.GONE);
			mActivity.findViewById(R.id.had_info_land).setVisibility(View.VISIBLE);
			mOrientation = true;
    	}else if(mPageController.getOrientation()==Configuration.ORIENTATION_PORTRAIT){
    		mActivity.findViewById(R.id.had_info).setVisibility(View.VISIBLE);
    		mActivity.findViewById(R.id.had_info_land).setVisibility(View.GONE);
			mOrientation = false;
    	}
		mSearchOptionView.Release();
		mMainView.removeView(mSubView);
		mSubView = null;
		setPageName(ResController.PAGE_SEARCH_LIST);
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
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
		if(mPageController.mSearchFlag){
			TextView text = (TextView) mSubView.findViewById(R.id.search_bar);
			mhsList.clear();
			mAdapter.Clear();
			mTime = null;
			text.setText(mPageController.mSearchName);
			SearchData(mPageController.mSearchName,0);
			mPageController.mSearchFlag = false;
		}
	}

	@Override
	public void setPageName(String name) {
		// TODO Auto-generated method stub
		mPageName = name;

		if(mPageController.getOrientation()==Configuration.ORIENTATION_LANDSCAPE){
			setPageLand(ResController.PAGE_SEARCH_LIST_LAND);
		}else{
			setPageProt(ResController.PAGE_SEARCH_LIST);
		}
	}
	
	void setPageProt(String name){
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(name),null);
		mMoveSize = (int) (Global.DISPLAYHEIGHT*0.8025);
		mSubView.findViewById(R.id.search_list_search).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1025)));
		mSubView.findViewById(R.id.search_list_bottom).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.8025)));
		mSearchOptionView = new SearchOptionView(mActivity,mSubView);
		mSearchOptionView.setPage(false);
		mMainView.addView(mSubView);
		mListView = (ListView) mSubView.findViewById(R.id.search_list_listview);
		if(mPageController.mSearchName!=null&&mPageController.mSearchName.length()>0){
			TextView text = (TextView) mSubView.findViewById(R.id.search_bar);
			text.setText(mPageController.mSearchName);
		}
		if(mPageController.mSearchFlag){
			SearchData(mPageController.mSearchName,mCount);
			mPageController.mSearchFlag = false;
		}else{
			if(mAdapter==null){
				mAdapter = new SearchAdapter(mActivity.getApplicationContext(),mOrientation);
				if(mhsList.size()>0){
					mAdapter.AddItem(mhsList);
				}
			}else{
				mAdapter.notifyDataSetChanged();
			}
			mListView.setAdapter(mAdapter);
			mAdapter.setLand(mOrientation);
			mListView.setSelectionFromTop(mScrollY, 0);
		}
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mSubView.findViewById(R.id.next_search).setOnClickListener(this);
		mSubView.findViewById(R.id.search_bar).setOnClickListener(this);
		mSubView.findViewById(R.id.option_btn).setOnClickListener(this);
		mSearchOptionView.Hide();
	}

	public void setPageLand(String name) {
		// TODO Auto-generated method stub
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(name),null);
		mMoveSize = (int) (Global.DISPLAYHEIGHT*0.6917);
		mSubView.findViewById(R.id.search_list_search).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYWIDTH*0.171)));
		mSubView.findViewById(R.id.search_list_bottom).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYWIDTH*0.6917)));
		mSearchOptionView = new SearchOptionView(mActivity,mSubView);
		mSearchOptionView.setPage(true);
		mMainView.addView(mSubView);
		mListView = (ListView) mSubView.findViewById(R.id.search_list_listview);
		if(mPageController.mSearchName!=null&&mPageController.mSearchName.length()>0){
			TextView text = (TextView) mSubView.findViewById(R.id.search_bar);
			text.setText(mPageController.mSearchName);
		}
		if(mPageController.mSearchFlag){
			SearchData(mPageController.mSearchName,mCount);
			mPageController.mSearchFlag = false;
			
		}else{
			if(mAdapter==null){
				mAdapter = new SearchAdapter(mActivity.getApplicationContext(),mOrientation);
				if(mhsList.size()>0){
					mAdapter.AddItem(mhsList);
				}
			}else{
				mAdapter.notifyDataSetChanged();
			}
			mListView.setAdapter(mAdapter);
			mAdapter.setLand(mOrientation);
			mListView.setSelectionFromTop(mScrollY, 0);
		}
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mSubView.findViewById(R.id.next_search).setOnClickListener(this);
		mSubView.findViewById(R.id.search_bar).setOnClickListener(this);
		mSubView.findViewById(R.id.option_btn).setOnClickListener(this);
		mSearchOptionView.Hide();
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
		if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
			mActivity.findViewById(R.id.had_info).setVisibility(View.GONE);
			mActivity.findViewById(R.id.had_info_land).setVisibility(View.VISIBLE);
			mOrientation = true;
    	}else if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT){
    		mActivity.findViewById(R.id.had_info).setVisibility(View.VISIBLE);
    		mActivity.findViewById(R.id.had_info_land).setVisibility(View.GONE);
			mOrientation = false;
    	}
		mSearchOptionView.Release();
		mMainView.removeView(mSubView);
		mSubView = null;
		setPageName(ResController.PAGE_SEARCH_LIST);
		if(mOptionShow){
			mSubView.findViewById(R.id.search_list_bottom).setVisibility(View.GONE);
			mSearchOptionView.Show();
			mSubView.findViewById(R.id.option_btn).setBackgroundResource(R.drawable.mobile_search_up);
		}
	}
	public void OptionViewAni(){
		AnimationListener lis = new AnimationListener(){
			public void onAnimationEnd(Animation animation){
				mSubView.findViewById(R.id.search_list_bottom).setVisibility(View.GONE);
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
		mSearchOptionView.Show();
		mSubView.findViewById(R.id.search_list_bottom).setVisibility(View.VISIBLE);
		mSubView.findViewById(R.id.search_list_view).startAnimation(ani);
		mOptionShow = true;
	}
	
	public void OptionHideAni(){
		AnimationListener lis = new AnimationListener(){
			public void onAnimationEnd(Animation animation){
				mSearchOptionView.Hide();
				mSubView.findViewById(R.id.option_btn).setBackgroundResource(R.drawable.mobile_search_down);
				mCount = 0;
				mhsList.clear();
				mTime = null;
				SearchData(mPageController.mSearchName,mCount);
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
		mSearchOptionView.Show();
		mSubView.findViewById(R.id.search_list_bottom).setVisibility(View.VISIBLE);
		mSubView.findViewById(R.id.search_list_view).startAnimation(ani);
		mOptionShow = false;
	}
	

	@SuppressWarnings("unchecked")
	public void SearchData(String search,int pos){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.GOOGLING_SEARCH,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		// 최초 불러올땐 seq = 0을 입력한다
		if(SearchOptionView.mCheck[0])
			params.add("point_free", "Y");
		else
			params.add("point_free", "N");
		
		if(SearchOptionView.mCheck[1])
			params.add("point_not_free", "Y");
		else
			params.add("point_not_free", "N");
		
		if(SearchOptionView.mCheck[2])
			params.add("point_sponsor", "Y");
		else
			params.add("point_sponsor", "N");

		params.add("search.max", "10");
		params.add("search.pos", ""+pos);
		
		if(mTime!=null)
			params.add("time", mTime);
		
		for(int i=3;i<SearchOptionView.mCheck.length;i++){
			if(SearchOptionView.mCheck[i]){
				params.add("category", SearchOptionView.getCheckBoxName(i));
			}
		}
		
		if(search!=null&&search.length()>0){
			params.add("hintWord", search);
		}
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}
	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.GOOGLING_SEARCH)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					List<HsApplicationInfo> list = (List<HsApplicationInfo>) map.get(NetInfo.LIST);
					
					for(int i=0;i<list.size();i++){
						mhsList.add(list.get(i));
					}
					
					if(mAdapter==null){
						mAdapter = new SearchAdapter(mActivity.getApplicationContext(),mOrientation);
						mListView.setAdapter(mAdapter);
					}  
					
					mAdapter.AddItem(mhsList);
					
					if(mhsList.size()==0){
						mListView.setBackgroundResource(R.drawable.no_result);
					}else{
						mListView.setBackgroundDrawable(null);
						mListView.setBackgroundColor(0xffffff);
						mCount += mhsList.size();
						Log.e("SearchList", ""+mCount);
						
					}
					
					if(mTime==null)
						mTime = (String) map.get(NetInfo.TIME);
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "네트워크 오류", 0).show();
				}
			}
		}
	}
	@Override
	public void onItemClick(AdapterView<?> adpt, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		
		if(mPageController.mCheckLogin){
			SearchAdapter adapter = ((SearchAdapter)adpt.getAdapter());
			HsApplicationInfo item = (HsApplicationInfo) adapter.getItem(arg2);
			mPageController.setAppSysId(item.getAppSysId());
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}else{
			Toast.makeText(mActivity.getApplicationContext(), "로그인이 필요합니다.", 0).show();
			mPageController.setPage(ResController.PAGE_LOGIN);
		}
	}
	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		mScrollY = firstVisibleItem;
	}
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		  switch (scrollState) {
          case OnScrollListener.SCROLL_STATE_IDLE :                // 스크롤이 정지되어 있는 상태야.정지되어 있는 상태일 때 해야 할 일들을 써줘.
          
              break;
          case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL :  //  스크롤이 터치되어 있을 때 상태고, 스크롤이 터치되어 있는 상태일 때 해야 할 일들을 써줘.
        
              break;
          case OnScrollListener.SCROLL_STATE_FLING :           // 이건 스크롤이 움직이고 있을때 상태야.   여기도 마찬가지.
           
              break;
        }
	}
}
