package com.android.hadstore.view;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.Encoder;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.operation.model.HsDownHistory;


public class LoginView implements SubPageController,View.OnClickListener,HttpRequestTaskListener {

	private Activity mActivity;
	
	private LinearLayout mMainView;
	
	private PageController mPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private EditText mPassWord;
	
	private EditText mLoginId;
	
	private boolean mIsAuto;
	
	private CheckBox mCheckBox;
	
	private CommonDailog mCommon;
	
	public LoginView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mPageController = controller; 
		mMainView = (LinearLayout) parent;
		mCommon = new CommonDailog(mActivity);
	}
	
	@Override
	public void Hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		mMainView.removeView(mSubView);
		
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		
		mCommon = null;
		
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
		mPageName = name;
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(mPageName),null);
		mSubView.findViewById(R.id.login_btn).setOnClickListener(this);
		mCheckBox = (CheckBox) mSubView.findViewById(R.id.login_auto);
		mCheckBox.setOnClickListener(this);
		mPassWord = (EditText) mSubView.findViewById(R.id.login_pw);
		mLoginId = (EditText) mSubView.findViewById(R.id.login_id);
		mSubView.findViewById(R.id.login_register_btn).setOnClickListener(this);
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
		if(mPageController.getStartUri()!=null){
			mPageController.startUriDetail(null);
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

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.login_auto:
				mIsAuto = mCheckBox.isChecked();
				break;
			case R.id.login_btn:
				HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_LOGIN,true);
				task.setOnFinshListener(this);
				
				// 파람 만들기
				ParamMaker params= new ParamMaker();
				// 최초 불러올땐 seq = 0을 입력한다
				params.add("userId",mLoginId.getText().toString());
				try {
					params.add("userPassword",Encoder.encryptToSha(mPassWord.getText().toString()));
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				params.add("osName","android");
				params.add("osVersion",android.os.Build.VERSION.SDK);

				
				if(!mCommon.isShowing()){
					mCommon.show();
				}
				// Request 실행
				List<NameValuePair> realParams = params.getParams();
				task.execute(realParams);
				break;
			case R.id.login_register_btn:
				mPageController.PageChange(ResController.PAGE_REGISTER_TERMS,ResController.PAGE_LOGIN);
				break;
		}
		
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.USER_LOGIN)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					int sales = (Integer)map.get(NetInfo.USER_SALES_POINT);
					String nickname = (String)map.get(NetInfo.USER_NICK_NAME);
					int gift = (Integer)map.get(NetInfo.USER_GIFT_COUNT);
					int noty = (Integer)map.get(NetInfo.USER_NOTICE_COUNT);
					SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
							AppInfo.USER_INFO, 0);
					Editor editor = prefs.edit();
					editor.putBoolean(AppInfo.LOGIN_AUTO, mIsAuto);
					editor.putString(AppInfo.USER_ID, mLoginId.getText().toString());
					mPageController.mNotyLastCount = mPageController.getDbUtil().NoticeLastCnt(mLoginId.getText().toString());
					int gap = noty - mPageController.mNotyLastCount;
					
					mPageController.LoginSet(sales, gap, gift, nickname);
					
					List<HsDownHistory> downHistory =  (List<HsDownHistory>) map.get(NetInfo.USER_DOWN_HISTORY);
					for(int i=0;i<downHistory.size();i++){
						mPageController.mDownList.put(downHistory.get(i).getPackageName(), downHistory.get(i));
					}
					
					try {
						editor.putString(AppInfo.USER_PASSWORD, Encoder.encryptToSha(mPassWord.getText().toString()));
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					editor.commit();
					mActivity.findViewById(R.id.logout).setVisibility(View.VISIBLE);
					mActivity.findViewById(R.id.nickname).setVisibility(View.VISIBLE);
					mActivity.findViewById(R.id.point).setVisibility(View.VISIBLE);
					mActivity.findViewById(R.id.top_login).setVisibility(View.VISIBLE);
					mActivity.findViewById(R.id.top_logout).setVisibility(View.GONE);
					mPageController.mCheckLogin = true;
					if(mPageController.getStartUri()!=null){
						String appid = mPageController.getStartUri().getQueryParameter("appid");
						mPageController.startUriDetail(null);
						mPageController.setAppSysId(appid);
						mPageController.PageChange(ResController.PAGE_DETAIL_LIST,ResController.PAGE_LOGIN);
					}else{
						mPageController.BackKey();
					}
				}else{
					mPageController.mCheckLogin = false;
					Toast.makeText(mActivity.getApplicationContext(), "로그인 실패", 0).show();
				}
			}
			
		}
		
	}

	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		
	}

}
