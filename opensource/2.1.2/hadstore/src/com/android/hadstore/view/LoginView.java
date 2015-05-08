package com.android.hadstore.view;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.Encoder;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.InAppPageController;
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
	
	private FrameLayout mMainView;
	
	private PageController mPageController;
	
	private InAppPageController mInAppPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private EditText mPassWord;
	
	private EditText mLoginId;
	
	private boolean mIsAuto;
	
	private ToggleButton mCheckBox;
	
	private CommonDailog mCommon;
	
	private InputFilter mFilterNum = new InputFilter() {
		
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			// TODO Auto-generated method stub
			
			Pattern ps = Pattern.compile("^[0-9]+$");
			
			if(!ps.matcher(source).matches()){
				return "";
			}
			return null;
		}
	};
	
	public LoginView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mPageController = controller; 
		mMainView = (FrameLayout) parent;
		mCommon = new CommonDailog(mActivity);
	}
	
	public LoginView(View parent,Activity activity,InAppPageController controller){
		mActivity = activity;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mInAppPageController = controller; 
		mMainView = (FrameLayout) parent;
		mCommon = new CommonDailog(mActivity);
	}
	
	@Override
	public void Hide() {
		// TODO Auto-generated method stub
		mSubView.setVisibility(View.GONE);
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mSubView.setVisibility(View.VISIBLE);
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
		
		mInAppPageController = null;
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
		if(mInAppPageController==null){
			if(mActivity.findViewById(R.id.join).getVisibility()==View.VISIBLE){
				mActivity.findViewById(R.id.join).setVisibility(View.GONE);
			}
		}
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(mPageName),null);
		mSubView.findViewById(R.id.login_btn).setOnClickListener(this);
		mCheckBox = (ToggleButton) mSubView.findViewById(R.id.login_auto);
		mCheckBox.setOnClickListener(this);
		mPassWord = (EditText) mSubView.findViewById(R.id.login_pw);
		mLoginId = (EditText) mSubView.findViewById(R.id.login_id);
		TelephonyManager telManager = (TelephonyManager)mActivity.getSystemService(Context.TELEPHONY_SERVICE); 
		String phoneNum = telManager.getLine1Number();
		
		if(phoneNum!=null&&phoneNum.length()>0){
			mLoginId.setText(phoneNum.replace("+82", "0"));
		}
		mLoginId.setFilters(new InputFilter[]{mFilterNum});
	
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
		if(mInAppPageController==null){
			if(mPageController.getStartUri()!=null){
				mPageController.startUriDetail(null);
			}
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

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.login_auto:
				mIsAuto = mCheckBox.isChecked();
				break;
			case R.id.login_btn:
				
				if(mInAppPageController==null){
					if(mLoginId.getText().toString().equals("123456789")){
						Toast.makeText(mActivity.getApplicationContext(), "인앱 테스트용 아이디는 일반 로그인 불가 합니다.", 0).show();
						return;
					}
				}
				
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
				if(mInAppPageController!=null){
					mInAppPageController.setPage(ResController.PAGE_INAPP_REGISTER_TERMS);
				}else{
					mPageController.setPage(ResController.PAGE_REGISTER_TERMS);
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
		if(url.equals(NetInfo.USER_LOGIN)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(mInAppPageController!=null){
					if(status == NetInfo.SUCCESS){
						int sales = (Integer)map.get(NetInfo.USER_SALES_POINT);
						mInAppPageController.setSalsePoint(sales);
						mInAppPageController.mCheckLogin = true;
						
						try {
							mInAppPageController.setLoginIdPassword(mLoginId.getText().toString(), Encoder.encryptToSha(mPassWord.getText().toString()));
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						mInAppPageController.BackKey();
					}else{
						mInAppPageController.mCheckLogin = false;
						Toast.makeText(mActivity.getApplicationContext(), "로그인 실패", 0).show();
					}
				}else{
					if(status == NetInfo.SUCCESS){
						int sales = (Integer)map.get(NetInfo.USER_SALES_POINT);
						String nickname = (String)map.get(NetInfo.USER_NICK_NAME);
						int gift = (Integer)map.get(NetInfo.USER_GIFT_COUNT);
						int noty = (Integer)map.get(NetInfo.USER_NOTICE_COUNT);
						String type = (String)map.get(NetInfo.USER_TYPE);
						if(type.equals("private")){
							String name = (String)map.get(NetInfo.USER_NAME);
							nickname = nickname + "("+name+")"; 
						}
						SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
								AppInfo.USER_INFO, 0);
						Editor editor = prefs.edit();
						editor.putBoolean(AppInfo.LOGIN_AUTO, mIsAuto);
						
						
						mPageController.mNotyLastCount = mPageController.getDbUtil().NoticeLastCnt(mLoginId.getText().toString());
						int gap = noty - mPageController.mNotyLastCount;
						
						mPageController.LoginSet(sales, gap, gift, nickname,true);
						
						List<HsDownHistory> downHistory =  (List<HsDownHistory>) map.get(NetInfo.USER_DOWN_HISTORY);
						for(int i=0;i<downHistory.size();i++){
							mPageController.mDownList.put(downHistory.get(i).getPackageName(), downHistory.get(i));
						}
						try {
							mPageController.setUserId(mLoginId.getText().toString());
							mPageController.setPassword(Encoder.encryptToSha(mPassWord.getText().toString()));
							if(mIsAuto){
								editor.putString(AppInfo.USER_ID, mLoginId.getText().toString());
								editor.putString(AppInfo.USER_PASSWORD, mPageController.getPassword());
							}else{
								editor.putString(AppInfo.USER_ID, "");
								editor.putString(AppInfo.USER_PASSWORD, "");
							}
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						editor.commit();
						mActivity.findViewById(R.id.nickname).setVisibility(View.VISIBLE);
						mActivity.findViewById(R.id.point).setVisibility(View.VISIBLE);
						
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
		
	}

	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		
	}

}
