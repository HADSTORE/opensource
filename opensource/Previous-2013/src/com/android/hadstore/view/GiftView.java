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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.CustomGiftDialog;
import com.android.hadstore.Encoder;
import com.android.hadstore.Global;
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
import com.android.hadstore.util.DateUtil;
import com.operation.model.HsApplicationInfo;
import com.operation.model.HsDownHistory;
import com.operation.model.HsGiftInfo;

public class GiftView implements SubPageController,HttpRequestTaskListener,View.OnClickListener{

	private Activity mActivity;
	
	private LinearLayout mMainView;
	
	private PageController mPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private boolean mIsAuto;
	
	private CommonDailog mCommon;
	
	private TextView mTime;
	
	private TextView mWeekMonth;
	
	private TextView mDay;
	
	private String mGiftUserNickName;
	
	private String mGiftUserID;
	
	private int mBookmarkCnt;
	
	private int mGiftCnt;
	
	private boolean mIsClear;
	
	private boolean mMyBookmark;
	
	private CustomGiftDialog mDialog;
	
	private ArrayList<HsApplicationInfo> mGiftlist = new ArrayList<HsApplicationInfo>();
	
	private ArrayList<HsApplicationInfo> mBookmarklist = new ArrayList<HsApplicationInfo>();
	
	private ArrayList<HsApplicationInfo> mBookmarkLinklist = new ArrayList<HsApplicationInfo>();
	
	OnKeyListener mOnKey = new OnKeyListener() {

        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

            if (keyCode == KeyEvent.KEYCODE_BACK) {
            	mDialog.dismissDialog();
    			mDialog = null;
            }
            return false;
        }
    };
	
	private View.OnClickListener mGiftListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int index = (Integer) v.getTag();
			mPageController.setAppSysId(mGiftlist.get(index).getAppSysId());
			mPageController.setGiftFlag(true);
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}
	};
	
	private View.OnClickListener mBookMarkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int index = (Integer) v.getTag();
			mPageController.setAppSysId(mBookmarklist.get(index).getAppSysId());
			mPageController.setPage(ResController.PAGE_DETAIL_LIST);
		}
	};
	
	private View.OnClickListener mBookMarkLinkListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			int index = (Integer) v.getTag();
			mPageController.setAppSysId(mBookmarkLinklist.get(index).getAppSysId());
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
	
	public GiftView(View parent,Activity activity,PageController controller){
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
		
		View convertView = mSubView.findViewById(R.id.gift_list);
		convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1125)));
		TextView title  = (TextView) convertView.findViewById(R.id.title);
		convertView.findViewById(R.id.text_btn).setVisibility(View.INVISIBLE);
		title.setText("받은 선물 목록");
		convertView.setOnClickListener(this);
		
		convertView = mSubView.findViewById(R.id.bookmark_list);
		convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1125)));
		title  = (TextView) convertView.findViewById(R.id.title);
		convertView.findViewById(R.id.text_btn).setVisibility(View.INVISIBLE);
		title.setText("즐겨 찾기 목록");
		convertView.setOnClickListener(this);
		
		
		convertView = mSubView.findViewById(R.id.bookmark_link_list);
		convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1125)));
		title  = (TextView) convertView.findViewById(R.id.title);
		title.setText("다른 사용자 즐겨찾기 연결");
		convertView.setOnClickListener(this);
		
		mMainView.addView(mSubView);
		mHandler.sendEmptyMessageDelayed(0, 0);
		
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
	
	public void GiftListSearch(){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_GIFT_LIST,true);
		task.setOnFinshListener(this);
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		// 최초 불러올땐 seq = 0을 입력한다
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		
		String id = prefs.getString(AppInfo.USER_ID, "");
		
		params.add("userId",id);

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
		
		Log.e("mgdoo", "BookmarkListSearch "+id);
		
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
		// 최초 불러올땐 seq = 0을 입력한다
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		
		String id = prefs.getString(AppInfo.USER_ID, "");
		
		params.add("userId",id);

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
					for(int i=0;i<downHistory.size();i++)
						mGiftlist.add(downHistory.get(i));
					LinearLayout layout = (LinearLayout) mSubView.findViewById(R.id.gift);
					int size = mGiftlist.size();
					if(size>0){
						mSubView.findViewById(R.id.gift_list).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.top_arrow_img);
						for(int i=0;i<size;i++){
							HsApplicationInfo gift =mGiftlist.get(i);
				    		View convertView = View.inflate(mActivity,R.layout.gift_bookmark_item, null);
				    		
				    		convertView.setTag(i);
				    		
				    		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.15)));
				    		
				    		TextView nickname = (TextView) convertView.findViewById(R.id.list_nickname);
				        	TextView subject = (TextView) convertView.findViewById(R.id.list_subject);
				        	TextView date = (TextView) convertView.findViewById(R.id.list_date);
				        	TextView apptitle = (TextView) convertView.findViewById(R.id.list_apptitle);
				        	ImageView image = (ImageView) convertView.findViewById(R.id.list_img);
				        	
				        	apptitle.setText(gift.getAppTitle());
				        	subject.setText(gift.getAppDesc());
				        	date.setText(gift.getEventTime());
				        	nickname.setText(gift.getUserNickName());
				        	ImageDownloader.download(NetInfo.APP_IMAGE+gift.getAppTitleIcon(), image,true);
				    		
				        	convertView.setOnClickListener(mGiftListener);
				        	
				    		layout.addView(convertView);
						}
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
						for(int i=0;i<downHistory.size();i++)
							mBookmarklist.add(downHistory.get(i));
						LinearLayout layout = (LinearLayout) mSubView.findViewById(R.id.bookmark);
						int size = mBookmarklist.size();
						if(size>0){
							mSubView.findViewById(R.id.bookmark_list).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.top_arrow_img);
							for(int i=0;i<mBookmarklist.size();i++){
								HsApplicationInfo bookmark =mBookmarklist.get(i);
					    		View convertView = View.inflate(mActivity,R.layout.gift_bookmark_item, null);
					    		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.15)));
					    		
					    		convertView.setTag(i);
					    		
					    		TextView nickname = (TextView) convertView.findViewById(R.id.list_nickname);
					        	TextView subject = (TextView) convertView.findViewById(R.id.list_subject);
					        	TextView date = (TextView) convertView.findViewById(R.id.list_date);
					        	TextView apptitle = (TextView) convertView.findViewById(R.id.list_apptitle);
					        	ImageView image = (ImageView) convertView.findViewById(R.id.list_img);
					        	
					        	apptitle.setText(bookmark.getAppTitle());
					        	subject.setText(bookmark.getAppDesc());
					        	date.setText(bookmark.getEventTime());
					        	nickname.setText(bookmark.getUserNickName());
					        	ImageDownloader.download(NetInfo.APP_IMAGE+bookmark.getAppTitleIcon(), image,true);
					        	
					        	convertView.setOnClickListener(mBookMarkListener);
					    		
					    		layout.addView(convertView);
							}
						}
					}else{
						if(downHistory==null)return;
						for(int i=0;i<downHistory.size();i++)
							mBookmarkLinklist.add(downHistory.get(i));
						LinearLayout layout = (LinearLayout) mSubView.findViewById(R.id.bookmark_link);
						int size = mBookmarkLinklist.size();
						if(size>0){
							mSubView.findViewById(R.id.bookmark_link_list).findViewById(R.id.open_btn).setBackgroundResource(R.drawable.top_arrow_img);
							for(int i=0;i<mBookmarkLinklist.size();i++){
								HsApplicationInfo bookmark =mBookmarkLinklist.get(i);
					    		View convertView = View.inflate(mActivity,R.layout.gift_bookmark_item, null);
					    		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.15)));
					    		
					    		convertView.setTag(i);
					    		
					    		TextView nickname = (TextView) convertView.findViewById(R.id.list_nickname);
					        	TextView subject = (TextView) convertView.findViewById(R.id.list_subject);
					        	TextView date = (TextView) convertView.findViewById(R.id.list_date);
					        	TextView apptitle = (TextView) convertView.findViewById(R.id.list_apptitle);
					        	ImageView image = (ImageView) convertView.findViewById(R.id.list_img);
					        	
					        	apptitle.setText(bookmark.getAppTitle());
					        	subject.setText(bookmark.getAppDesc());
					        	date.setText(bookmark.getEventTime());
					        	nickname.setText(bookmark.getUserNickName());
					        	ImageDownloader.download(NetInfo.APP_IMAGE+bookmark.getAppTitleIcon(), image,true);
					        	
					        	convertView.setOnClickListener(mBookMarkLinkListener);
					    		
					    		layout.addView(convertView);
							}
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
					
					View convertView = mSubView.findViewById(R.id.gift_list);
					TextView text  = (TextView) convertView.findViewById(R.id.sub_text);
					text.setText(""+mGiftCnt);
					text.setVisibility(View.VISIBLE);
					
					convertView = mSubView.findViewById(R.id.bookmark_list);
					text  = (TextView) convertView.findViewById(R.id.sub_text);
					text.setText(""+mBookmarkCnt);
					text.setVisibility(View.VISIBLE);
					
					convertView = mSubView.findViewById(R.id.bookmark_link_list);
					if(mGiftUserNickName!=null&&mGiftUserNickName.length()>0){
						mIsClear = true;
						text  = (TextView) convertView.findViewById(R.id.sub_text);
						text.setText(mGiftUserNickName);
						text.setVisibility(View.VISIBLE);
						text  = (TextView) convertView.findViewById(R.id.text_btn);
						text.setText("clear");
					}else{
						mIsClear = false;
						text  = (TextView) convertView.findViewById(R.id.text_btn);
						text.setText("연결");
					}
					text.setOnClickListener(this);
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "리스트 가져오기 싶패", 0).show();
				}
			}else{
				Toast.makeText(mActivity.getApplicationContext(), "연결 싶패", 0).show();
			}
		}else if(url.equals(NetInfo.USER_BOOKMARK_LINK)){
			if(mDialog!=null){
				mDialog.dismissDialog();
				mDialog.dismiss();
				mDialog = null;
			}
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					View convertView = mSubView.findViewById(R.id.bookmark_link_list);
					TextView text  = (TextView) convertView.findViewById(R.id.sub_text);
					if(mGiftUserID==null){
						text.setText("");
						text.setVisibility(View.INVISIBLE);
						mIsClear = false;
						text  = (TextView) convertView.findViewById(R.id.text_btn);
						text.setText("연결");
						LinearLayout layout = (LinearLayout) mSubView.findViewById(R.id.bookmark_link);
						convertView.findViewById(R.id.open_btn).setBackgroundResource(R.drawable.bottom_arrow_img);
						mBookmarkLinklist.clear();
						layout.removeAllViews();
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
		}else if(url.equals(NetInfo.USER_ID_CHECK)){
			mDialog.dismissDialog();
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					mGiftUserNickName = (String) map.get("userNickNameDesc");
					EditText id = (EditText) mDialog.getView().findViewById(R.id.link_dialog_id);
					TextView nickname = (TextView) mDialog.getView().findViewById(R.id.link_dialog_nickname);
					nickname.setText(mGiftUserNickName);
					mGiftUserID = id.getText().toString();
					mDialog.setIdCheck(true);
				}else{
					mDialog.setIdCheck(false);
					Toast.makeText(mActivity.getApplicationContext(), "존재하지 않는 아이디 입니다.", 0).show();
				}
			}else{
				mDialog.setIdCheck(false);
				Toast.makeText(mActivity.getApplicationContext(), "아이디 체크 실패", 0).show();
			}
		}
		
		
	}
	
	public void SendIdCheck(String id){
		
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_ID_CHECK,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		params.add("userId", id);
		
		mDialog.showDialog();
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}
	
	public void SendBookmarkLink(boolean clear){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_BOOKMARK_LINK,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		String id = prefs.getString(AppInfo.USER_ID, "");

		if(clear){
			mGiftUserID = null;
			mGiftUserNickName = null;
		}else{
			params.add("targetId", mGiftUserID);
		}
		params.add("userId", id);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.gift_list:
			LinearLayout layout = (LinearLayout) mSubView.findViewById(R.id.gift);
			if(layout.getChildCount()==0){
				GiftListSearch();
			}else{
				v.findViewById(R.id.open_btn).setBackgroundResource(R.drawable.bottom_arrow_img);
				mGiftlist.clear();
				layout.removeAllViews();
			}
			break;
		case R.id.bookmark_link_list:
			layout = (LinearLayout) mSubView.findViewById(R.id.bookmark_link);
			if(layout.getChildCount()==0){
				if(mGiftUserID!=null&&mGiftUserID.length()>0){
					mMyBookmark = false;
					BookmarkListSearch(mGiftUserID);
				}
			}else{
				v.findViewById(R.id.open_btn).setBackgroundResource(R.drawable.bottom_arrow_img);
				mBookmarkLinklist.clear();
				layout.removeAllViews();
			}
			break;
		case R.id.bookmark_list:
			layout = (LinearLayout) mSubView.findViewById(R.id.bookmark);
			if(layout.getChildCount()==0){
				// 최초 불러올땐 seq = 0을 입력한다
				SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
						AppInfo.USER_INFO, 0);
				
				String id = prefs.getString(AppInfo.USER_ID, "");
				mMyBookmark = true;
				BookmarkListSearch(id);
			}else{
				v.findViewById(R.id.open_btn).setBackgroundResource(R.drawable.bottom_arrow_img);
				mBookmarklist.clear();
				layout.removeAllViews();
			}
			break;
		case R.id.text_btn:
			if(mIsClear){
				SendBookmarkLink(true);
			}else{
				mDialog = new CustomGiftDialog(mActivity);
				layout = (LinearLayout) View.inflate(mActivity,R.layout.link_dialog,null);
				layout.setLayoutParams(new LinearLayout.LayoutParams((int) (Global.DISPLAYWIDTH*0.7292),(int) (Global.DISPLAYHEIGHT*0.24625)));
				layout.findViewById(R.id.link_dialog_cancel).setOnClickListener(this);
				layout.findViewById(R.id.link_dialog_ok).setOnClickListener(this);
				layout.findViewById(R.id.link_dialog_check).setOnClickListener(this);
				mDialog.setOnKeyListener(mOnKey);
				mDialog.setView(layout);
				mDialog.show();
			}
			break;
		case R.id.link_dialog_cancel:
			mDialog.dismiss();
			mDialog = null;
			break;
		case R.id.link_dialog_ok:
			if(mDialog.isIdCheck()){
				SendBookmarkLink(false);
			}else{
				Toast.makeText(mActivity.getApplicationContext(), "아이디를 체크 해주세요", 0).show();
			}
			break;
		case R.id.link_dialog_check:
			EditText id = (EditText) mDialog.getView().findViewById(R.id.link_dialog_id);
			SendIdCheck(id.getText().toString());
			mDialog.showDialog();
			break;
		}
	}

	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		
	}

}
