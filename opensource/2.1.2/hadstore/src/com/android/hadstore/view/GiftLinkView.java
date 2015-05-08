package com.android.hadstore.view;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.CustomDialog;
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

public class GiftLinkView implements SubPageController,HttpRequestTaskListener,View.OnClickListener{
	
	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private GiftView mGiftView;
	
	private View mSubView;
	
	private boolean mIsAuto;
	
	private CommonDailog mCommon;
	
	private String mPageName;
	
	public GiftLinkView(View parent,Activity activity,GiftView controller){
		mActivity = activity;
		mGiftView = controller;
		mMainView = (FrameLayout) parent;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mCommon = new CommonDailog(mActivity);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.link_dialog_cancel:
			mGiftView.ReleaseGiftLink();
			break;
		case R.id.link_dialog_ok:
			if(mGiftView.isIdCheck()){
				mGiftView.SendBookmarkLink(false);
				mGiftView.ReleaseGiftLink();
			}else{
				Toast.makeText(mActivity.getApplicationContext(), "아이디를 체크 해주세요", 0).show();
			}
			break;
		case R.id.link_dialog_check:
			EditText id = (EditText) mSubView.findViewById(R.id.link_dialog_id);
			SendIdCheck(id.getText().toString());
			break;
		}
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing())
			mCommon.dismiss();
		
		if(url.equals(NetInfo.USER_ID_CHECK)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					String exist = (String)map.get(NetInfo.EXIST);
					if(exist.equals("Y")){
						String giftUserNickName = (String) map.get("userNickNameDesc");
						EditText id = (EditText) mSubView.findViewById(R.id.link_dialog_id);
						TextView nickname = (TextView) mSubView.findViewById(R.id.link_dialog_nickname);
						nickname.setText(giftUserNickName);
						String giftUserID = id.getText().toString();
						mGiftView.setIdNickname(giftUserID,giftUserNickName);
						mGiftView.setIdCheck(true);
					}else{
						mGiftView.setIdCheck(false);
						Toast.makeText(mActivity.getApplicationContext(), "존재하지 않는 아이디 입니다.", 0).show();
					}
				}else{
					mGiftView.setIdCheck(false);
					Toast.makeText(mActivity.getApplicationContext(), "존재하지 않는 아이디 입니다.", 0).show();
				}
			}else{
				mGiftView.setIdCheck(false);
				Toast.makeText(mActivity.getApplicationContext(), "아이디 체크 실패", 0).show();
			}
		}
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
		
		mGiftView = null;
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
		mSubView.findViewById(R.id.link_dialog_cancel).setOnClickListener(this);
		mSubView.findViewById(R.id.link_dialog_ok).setOnClickListener(this);
		mSubView.findViewById(R.id.link_dialog_check).setOnClickListener(this);
		mMainView.addView(mSubView);
		mMainView.invalidate();
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
	
	public void SendIdCheck(String id){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_ID_CHECK,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		params.add("userId", id);
		
		if(!mCommon.isShowing())
			mCommon.show();
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}

}
