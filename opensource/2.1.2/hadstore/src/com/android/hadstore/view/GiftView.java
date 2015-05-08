package com.android.hadstore.view;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnKeyListener;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.CustomDialog;
import com.android.hadstore.Encoder;
import com.android.hadstore.Global;
import com.android.hadstore.HadstroeActivity;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.adapter.GiftAdapter;
import com.android.hadstore.adapter.SeparatedListAdapter;
import com.android.hadstore.adapter.UpdatePayAdapter;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.android.hadstore.tesk.ImageDownloader;
import com.android.hadstore.util.DateUtil;
import com.operation.model.HsApplicationInfo;
import com.operation.model.HsDownHistory;
import com.operation.model.HsGiftInfo;

public class GiftView implements SubPageController,HttpRequestTaskListener,View.OnClickListener{

	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private PageController mPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private CommonDailog mCommon;
	
	private String mGiftUserNickName;
	
	private String mGiftUserID;
	
	private int mBookmarkCnt;
	
	private int mGiftCnt;
	
	private boolean mIsClear;
	
	private boolean mMyBookmark;
	
	private boolean mIdCheck;
	
	private ArrayList<HsApplicationInfo> mGiftlist = new ArrayList<HsApplicationInfo>();
	
	private ArrayList<HsApplicationInfo> mBookmarklist = new ArrayList<HsApplicationInfo>();
	
	private ArrayList<HsApplicationInfo> mBookmarkLinklist = new ArrayList<HsApplicationInfo>();
	
	private GiftAdapter mGiftListAdapter;
	
	private GiftAdapter mBookmarkListAdapter;
	
	private GiftAdapter mBookmarkLinkListAdapter;
	
	private int mGiftIndex = 0;
	
	private GiftLinkView mGiftLinkView;
	
	private SeparatedListAdapter mAdapter;
	
	private View.OnClickListener mGiftListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(mGiftLinkView!=null)return;
			
			mGiftIndex = (Integer) v.getTag();
			((HadstroeActivity)mActivity).setGiftMsg(mGiftlist.get(mGiftIndex).getEventComment());
			mActivity.showDialog(HadstroeActivity.DIALOG_GIFT_MSG);
		}
	};
	
	private View.OnClickListener mBookMarkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mGiftLinkView!=null)return;
			// TODO Auto-generated method stub
			int index = (Integer) v.getTag();
			mPageController.setAppSysId(mBookmarklist.get(index).getAppSysId());
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}
	};
	
	private View.OnClickListener mBookMarkLinkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(mGiftLinkView!=null)return;
			// TODO Auto-generated method stub
			int index = (Integer) v.getTag();
			mPageController.setAppSysId(mBookmarkLinklist.get(index).getAppSysId());
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}
	};
	
	public GiftView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mPageController = controller;
		mMainView = (FrameLayout) parent;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mCommon = new CommonDailog(mActivity);
	}
	
	

	public boolean isIdCheck() {
		return mIdCheck;
	}



	public void setIdCheck(boolean IdCheck) {
		this.mIdCheck = IdCheck;
	}



	public void Release() {
		// TODO Auto-generated method stub
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
		
		ListView listview = (ListView) mSubView.findViewById(R.id.gift_listview);
		ArrayList<View> list = new ArrayList<View>();
		View convertView = View.inflate(mActivity,R.layout.gift_item_1, null);
		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1385)));
		convertView.setBackgroundColor(0xfffafafa);
		TextView title  = (TextView) convertView.findViewById(R.id.title);
		title.setText("받은 선물 목록");
		convertView.setId(1001);
		convertView.setOnClickListener(this);
		list.add(convertView);
		
		convertView = View.inflate(mActivity,R.layout.gift_item_1, null);
		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1385)));
		title  = (TextView) convertView.findViewById(R.id.title);
		title.setText("즐겨 찾기 목록");
		convertView.setId(1002);
		convertView.setOnClickListener(this);
		list.add(convertView);
		
		convertView = View.inflate(mActivity,R.layout.gift_item_2, null);
		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1385)));
		convertView.setBackgroundColor(0xfffafafa);
		title  = (TextView) convertView.findViewById(R.id.title);
		title.setText("다른 사용자 즐겨찾기 연결");
		convertView.setId(1003);
		convertView.setOnClickListener(this);
		list.add(convertView);
		
		mAdapter = new SeparatedListAdapter(mActivity, list);
		
		mGiftListAdapter = new GiftAdapter(mActivity, mGiftlist, mGiftListener);
		mBookmarkListAdapter = new GiftAdapter(mActivity, mBookmarklist, mBookMarkListener);
		mBookmarkLinkListAdapter =  new GiftAdapter(mActivity, mBookmarkLinklist, mBookMarkLinkListener);
		
		mAdapter = new SeparatedListAdapter(mActivity, list);
		
		mAdapter.addSection("0", mGiftListAdapter);
		mAdapter.addSection("1", mBookmarkListAdapter);
		mAdapter.addSection("2", mBookmarkLinkListAdapter);
		
		mGiftListAdapter.Clear();
		mBookmarkListAdapter.Clear();
		mBookmarkLinkListAdapter.Clear();
		
		listview.setAdapter(mAdapter);
		
		mMainView.addView(mSubView);
		
		FristSend();
		
	}

	@Override
	public String getPageName() {
		// TODO Auto-generated method stub
		return mPageName;
	}

	@Override
	public int BackKey() {
		// TODO Auto-generated method stub
		if(mGiftLinkView!=null){
			mGiftLinkView.Release();
			mGiftLinkView = null;
			return 1;
		}else if(mCommon.isShowing()){
			return 1;
		}
		return 0;
	}
	
	public void ReleaseGiftLink(){
		if(mGiftLinkView!=null){
			mGiftLinkView.Release();
			mGiftLinkView = null;
		}
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
	
	public void GiftListSearch(){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_GIFT_LIST,true);
		task.setOnFinshListener(this);
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		params.add("userId",mPageController.getUserId());

		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
	}
	
	public void BookmarkListSearch(String id){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_BOOKMARK_LIST,true);
		task.setOnFinshListener(this);
		// 파람 만들기
		ParamMaker params= new ParamMaker();

		params.add("userId",id);

		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
	}
	
	public void FristSend(){
		
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.GIFT_FRIST_LIST,true);
		task.setOnFinshListener(this);
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		params.add("userId",mPageController.getUserId());

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
		if(url.equals(NetInfo.USER_GIFT_LIST)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					List<HsApplicationInfo> downHistory =  (List<HsApplicationInfo>) map.get(NetInfo.LIST);
					mGiftlist.clear();
					for(int i=0;i<downHistory.size();i++)
						mGiftlist.add(downHistory.get(i));
					int size = mGiftlist.size();
					if(size>0){
						mGiftListAdapter.setItem(mGiftlist);
						mAdapter.notifyDataSetChanged();
					}
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "Failed to retrieve list", 0).show();
				}
			}else{
				Toast.makeText(mActivity.getApplicationContext(), "연결 싶패", 0).show();
			}
		}else if(url.equals(NetInfo.USER_BOOKMARK_LIST)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					List<HsApplicationInfo> downHistory =  (List<HsApplicationInfo>) map.get(NetInfo.LIST);
					if(mMyBookmark){
						mBookmarklist.clear();
						for(int i=0;i<downHistory.size();i++)
							mBookmarklist.add(downHistory.get(i));
						int size = mBookmarklist.size();
						if(size>0){
							mBookmarkListAdapter.setItem(mBookmarklist);
							mAdapter.notifyDataSetChanged();
						}
					}else{
						if(downHistory==null)return;
						mBookmarkLinklist.clear();
						for(int i=0;i<downHistory.size();i++)
							mBookmarkLinklist.add(downHistory.get(i));
						int size = mBookmarkLinklist.size();
						if(size>0){
							mBookmarkLinkListAdapter.setItem(mBookmarkLinklist);
							mAdapter.notifyDataSetChanged();
						}
						
					}
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "Failed to retrieve list", 0).show();
				}
			}else{
				Toast.makeText(mActivity.getApplicationContext(), "연결 싶패", 0).show();
			}
		}else if(url.equals(NetInfo.GIFT_FRIST_LIST)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					mGiftUserID = (String) map.get("otherUserId"); 
					mGiftUserNickName = (String) map.get("otherUserName");
					mGiftCnt = Integer.parseInt((String) map.get("giftCnt"));
					mBookmarkCnt = Integer.parseInt((String) map.get("bookmarkCnt"));
					
					View convertView = mAdapter.getHeaderView(0);
					TextView text  = (TextView) convertView.findViewById(R.id.sub_text);
					text.setText(""+mGiftCnt);
					text.setVisibility(View.VISIBLE);
					
					convertView = mAdapter.getHeaderView(1);
					text  = (TextView) convertView.findViewById(R.id.sub_text);
					text.setText(""+mBookmarkCnt);
					text.setVisibility(View.VISIBLE);
					
					convertView = mAdapter.getHeaderView(2);
					if(mGiftUserNickName!=null&&mGiftUserNickName.length()>0){
						mIsClear = true;
						text  = (TextView) convertView.findViewById(R.id.sub_text);
						text.setText(mGiftUserNickName);
						text.setVisibility(View.VISIBLE);
						text  = (TextView) convertView.findViewById(R.id.text_btn);
						text.setText("clear");
						text.setOnClickListener(this);
					}else{
						mIsClear = false;
						text  = (TextView) convertView.findViewById(R.id.text_btn);
						text.setText("연결");
						text.setOnClickListener(this);
					}
					
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "리스트 가져오기 싶패", 0).show();
				}
			}else{
				Toast.makeText(mActivity.getApplicationContext(), "연결 싶패", 0).show();
			}
		}else if(url.equals(NetInfo.USER_BOOKMARK_LINK)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					View convertView = mAdapter.getHeaderView(2);
					TextView text  = (TextView) convertView.findViewById(R.id.sub_text);
					if(mGiftUserID==null){
						text.setText("");
						text.setVisibility(View.INVISIBLE);
						mIsClear = false;
						text  = (TextView) convertView.findViewById(R.id.text_btn);
						text.setText("연결");
						convertView.findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_down_arrow);
						mBookmarkLinklist.clear();
						mBookmarkLinkListAdapter.Clear();
						mAdapter.notifyDataSetChanged();
					}else{
						mIsClear = true;
						text.setText(mGiftUserNickName);
						text.setVisibility(View.VISIBLE);
						text  = (TextView) convertView.findViewById(R.id.text_btn);
						text.setText("clear");
					}
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "다른 사용자 즐겨찾기 연결 실패", 0).show();
				}
			}else{
				Toast.makeText(mActivity.getApplicationContext(), "연결 싶패", 0).show();
			}
		}
		
		
	}
	
	public void SendBookmarkLink(boolean clear){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_BOOKMARK_LINK,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();

		if(clear){
			mGiftUserID = null;
			mGiftUserNickName = null;
		}else{
			params.add("targetId", mGiftUserID);
		}
		params.add("userId", mPageController.getUserId());
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}
	
	public void setIdNickname(String id,String nick){
		mGiftUserID = id;
		mGiftUserNickName = nick;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(mGiftLinkView!=null)return;
		switch(v.getId()){
		case 1001:
			if(mGiftListAdapter.getCount()==0){
				GiftListSearch();
			}else{
				mAdapter.getHeaderView(0).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_down_arrow);
				mGiftListAdapter.Clear();
				mAdapter.notifyDataSetChanged();
			}
			break;
		case 1003:
			if(mBookmarkLinkListAdapter.getCount()==0){
				if(mGiftUserID!=null&&mGiftUserID.length()>0){
					mMyBookmark = false;
					BookmarkListSearch(mGiftUserID);
				}
			}else{
				mAdapter.getHeaderView(2).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_down_arrow);
				mBookmarkLinkListAdapter.Clear();
				mAdapter.notifyDataSetChanged();
			}
			break;
		case 1002:
			if(mBookmarkListAdapter.getCount()==0){
				// 최초 불러올땐 seq = 0을 입력한다
				mMyBookmark = true;
				BookmarkListSearch(mPageController.getUserId());
			}else{
				mAdapter.getHeaderView(1).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_down_arrow);
				mBookmarkListAdapter.Clear();
				mAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.text_btn:
			if(mIsClear){
				SendBookmarkLink(true);
			}else{
				//Hide();
				mGiftLinkView = new GiftLinkView(mMainView,mActivity,this);
				mGiftLinkView.setPageName(ResController.PAGE_LINK_DIALOG);
			}
			break;
		
		}
	}

	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		if(index==HadstroeActivity.DIALOG_OK){
			mPageController.setAppSysId(mGiftlist.get(mGiftIndex).getAppSysId());
			mPageController.setGiftFlag(true);
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}
	}
}
