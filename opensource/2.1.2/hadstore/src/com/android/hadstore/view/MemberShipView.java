package com.android.hadstore.view;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
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
import com.operation.model.HsUserInfo;

public class MemberShipView implements SubPageController,View.OnClickListener,HttpRequestTaskListener {

	private final int RESEARCH = 1;
	
	private final int PREFERENCES = 2;
	
	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private PageController mPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private CommonDailog mCommon;
	
	private HsUserInfo mInfo;
	
	private InputFilter mFilterEmail = new InputFilter() {
		
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			// TODO Auto-generated method stub
			
			Pattern ps = Pattern.compile("^[a-zA-Z0-9@.]+$");
			
			if(!ps.matcher(source).matches()){
				return "";
			}
			return null;
		}
	};
	
	public MemberShipView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mPageController = controller; 
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
		View convertView = mSubView.findViewById(R.id.research_list);
		convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.083)));
		convertView.setOnClickListener(this);
		
		View btn = convertView.findViewById(R.id.text_btn);
		btn.setTag(RESEARCH);
		btn.setOnClickListener(this);
		TextView title  = (TextView) convertView.findViewById(R.id.title);
		title.setText("정보 조회/변경");
		
		convertView = mSubView.findViewById(R.id.preferences_list);
		convertView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.083)));
		convertView.setOnClickListener(this);
		btn = convertView.findViewById(R.id.text_btn);
		btn.setTag(PREFERENCES);
		btn.setOnClickListener(this);
		title  = (TextView) convertView.findViewById(R.id.title);
		title.setText("환경 설정");
		
		SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		boolean auto = prefs.getBoolean(AppInfo.LOGIN_AUTO, false);
		
		CheckBox autologin =  (CheckBox) mSubView.findViewById(R.id.preferences_auto_login);
		
		autologin.setChecked(auto);
		
		PackageInfo pInfo = null;
		try {
			pInfo = mActivity.getPackageManager().getPackageInfo(mActivity.getPackageName(), PackageManager.GET_META_DATA);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		TextView text = (TextView) mSubView.findViewById(R.id.app_version);
		
		if(pInfo!=null){
			text.setText("Hadstore version "+pInfo.versionName);
		}
		
		mSubView.findViewById(R.id.payment_btn).setOnClickListener(this);
		
		ResearchSend();
		
		mMainView.addView(mSubView);
	}

	public void ResearchSend(){
		
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.RESEARCH_PROFILE,true);
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
	
	public void ResearchChange(String pw,String email){
		
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.RESEARCH_CHANGE,true);
		task.setOnFinshListener(this);
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		if(pw!=null&&pw.length()>0)
				params.add("userPassword",pw);
		
		if(email!=null&&email.length()>0)
				params.add("userEmailAddress",email);
		
		params.add("userId",mPageController.getUserId());

		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
	}
	
	public void PrefarencesChange(boolean share,boolean bookmarkauto){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.PREFARENCES_CHANGE,true);
		task.setOnFinshListener(this);
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		if(share)
			params.add("userShareFlag","T");
		else
			params.add("userShareFlag","F");
		
		if(bookmarkauto)
			params.add("userAutoShareAddFlag","T");
		else
			params.add("userAutoShareAddFlag","F");
		
		params.add("userId",mPageController.getUserId());

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
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.payment_btn:
			mPageController.setPage(ResController.PAGE_PAYMENT);
			break;
		case R.id.research_list:
			View view = mSubView.findViewById(R.id.research);
			if(view.getVisibility()==View.GONE){
				v.findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_up_arrow);
				view.setVisibility(View.VISIBLE);
			}else{
				v.findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_down_arrow);
				view.setVisibility(View.GONE);
			}
			break;
		case R.id.preferences_list:
			view = mSubView.findViewById(R.id.preferences);
			if(view.getVisibility()==View.GONE){
				v.findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_up_arrow);
				view.setVisibility(View.VISIBLE);
			}else{
				v.findViewById(R.id.open_btn).setBackgroundResource(R.drawable.list_down_arrow);
				view.setVisibility(View.GONE);
			}
			break;
		case R.id.text_btn:
			int index = (Integer) v.getTag();
			if(index == RESEARCH){
				view = mSubView.findViewById(R.id.research);
				if(view.getVisibility()==View.VISIBLE){
					EditText pwpre = (EditText) mSubView.findViewById(R.id.research_pw_pre);
					String check = null;
					String pwcheck = null;
					
					if(pwpre.length()>0){
						try {
							check = Encoder.encryptToSha(pwpre.getText().toString());
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if(!check.equals(mPageController.getPassword())){
							Toast.makeText(mActivity.getApplicationContext(), "현재 비밀번호가 일치하지 않습니다", 0).show();
							return;
						}
						
						EditText pw = (EditText) mSubView.findViewById(R.id.research_pw);
						EditText pwre = (EditText) mSubView.findViewById(R.id.research_pw_re);
						if(!pw.getText().toString().equals(pwre.getText().toString())){
							Toast.makeText(mActivity.getApplicationContext(), "비밀번호가 일치하지 않습니다", 0).show();
							return;
						}
						if(pw.length()>=8){
							if(!StringCheck(pw.getText().toString())){
								Toast.makeText(mActivity.getApplicationContext(), "비밀번호는 숫자 영문 포한 8자 이상입니다.", 0).show();
								return;
							}
						}else{
							Toast.makeText(mActivity.getApplicationContext(), "비밀번호는 숫자 영문 포한 8자 이상입니다.", 0).show();
							return;
						}
						
						try {
							pwcheck = Encoder.encryptToSha(pw.getText().toString());
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
					ResearchChange(pwcheck,null);
				}
			}else if(index == PREFERENCES){
				view = mSubView.findViewById(R.id.preferences);
				if(view.getVisibility()==View.VISIBLE){
					CheckBox auto = (CheckBox) mSubView.findViewById(R.id.preferences_auto_login);
					SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
							AppInfo.USER_INFO, 0);
					Editor editor = prefs.edit();
					editor.putBoolean(AppInfo.LOGIN_AUTO, auto.isChecked());
					if(auto.isChecked()){
						editor.putString(AppInfo.USER_ID, mPageController.getUserId());
						editor.putString(AppInfo.USER_PASSWORD, mPageController.getPassword());
					}else{
						editor.putString(AppInfo.USER_ID, "");
						editor.putString(AppInfo.USER_PASSWORD, "");
					}
					editor.commit();
					CheckBox share = (CheckBox) mSubView.findViewById(R.id.preferences_bookmark_share);
					CheckBox bookmarkauto = (CheckBox) mSubView.findViewById(R.id.preferences_bookmark_auto);
					
					PrefarencesChange(share.isChecked(), bookmarkauto.isChecked());
				}
			}
			break;
		}
	}
	
	public boolean StringCheck(String str){
		
		int len = str.length();
		
		boolean isNum = false;
		
		boolean isStr = false;
		
		for(int i=0;i<len;i++){
			char chr = str.charAt(i);
			
			if((chr>=0x41&&chr<=0x5A)||(chr>=0x63&&chr<=0x7A)){
				isStr = true;
			}
			if(chr>=0x30&&chr<=0x39){
				isNum = true;
			}
		}
		
		if(isNum||isStr) 
			return true;
		else
			return false;
	}


	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.RESEARCH_PROFILE)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					View view = mSubView.findViewById(R.id.research);
					
					mInfo = (HsUserInfo) map.get(NetInfo.INFO);
					
					TextView text;
					if(mInfo.getUserType().equals("company")){
						view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.5775)));
						view.findViewById(R.id.research_private).setVisibility(View.GONE);
					}else if(mInfo.getUserType().equals("private")){
						view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.7)));
						text =  (TextView) mSubView.findViewById(R.id.research_birthday);
						String brithay = mInfo.getUserAge()+"/"+mInfo.getUserBirthday().substring(0, 2)+"/"+mInfo.getUserBirthday().substring(2, 4);
						text.setText(brithay);
						text =  (TextView) mSubView.findViewById(R.id.research_gender);
						if(mInfo.getUserGender().equals("male")){
							text.setText("남자");
						}else if(mInfo.getUserGender().equals("female")){
							text.setText("여자");
						}
					}
					
					text =  (TextView) mSubView.findViewById(R.id.research_id);
					text.setText(mInfo.getUserId());
					text =  (TextView) mSubView.findViewById(R.id.research_nickname1);
					text.setText(mInfo.getUserNickName());
					text =  (TextView) mSubView.findViewById(R.id.research_nickname2);
					text.setText(mInfo.getUserNickNameDesc());
					
					text = (TextView) mSubView.findViewById(R.id.research_name);
					text.setText(mInfo.getUserName());
					
					text =  (TextView) mSubView.findViewById(R.id.research_email);
					text.setText(mInfo.getUserEmailAddressFirst()+"@"+mInfo.getUserEmailAddressSecond());
					
					CheckBox autobookmark =  (CheckBox) mSubView.findViewById(R.id.preferences_bookmark_auto);
					
					if(mInfo.getUserAutoShareAddFlag().equals("F")){
						autobookmark.setChecked(false);
					}else{
						autobookmark.setChecked(true);
					}
					
					CheckBox sharebookmark =  (CheckBox) mSubView.findViewById(R.id.preferences_bookmark_share);
					
					if(mInfo.getUserShareFlag().equals("F")){
						sharebookmark.setChecked(false);
					}else{
						sharebookmark.setChecked(true);
					}
				}
			}
		 }else if(url.equals(NetInfo.RESEARCH_CHANGE)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					Toast.makeText(mActivity.getApplicationContext(), "사용자 정보 변경 완료", 0).show();
					EditText pwpre = (EditText) mSubView.findViewById(R.id.research_pw_pre);
					
					pwpre.setText("");
					EditText pw = (EditText) mSubView.findViewById(R.id.research_pw);
					if(pw.length()>0){
						SharedPreferences prefs = mActivity.getApplicationContext().getSharedPreferences(
								AppInfo.USER_INFO, 0);
						Editor editor = prefs.edit();
						String password = null;
						try {
							password = Encoder.encryptToSha(pw.getText().toString());
							
						} catch (NoSuchAlgorithmException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						editor.putString(AppInfo.USER_PASSWORD, password);
						mPageController.setPassword(password);
						editor.commit();
					}
					pw.setText("");
					EditText pwre = (EditText) mSubView.findViewById(R.id.research_pw_re);
					pwre.setText("");
					
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "사용자 정보 변경 실패", 0).show();
				}
			}
		 }else if(url.equals(NetInfo.PREFARENCES_CHANGE)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					CheckBox autobookmark =  (CheckBox) mSubView.findViewById(R.id.preferences_bookmark_auto);
					
					if(autobookmark.isChecked()){
						mInfo.setUserAutoShareAddFlag("T");
					}else{
						mInfo.setUserAutoShareAddFlag("F");
					}
					
					CheckBox sharebookmark =  (CheckBox) mSubView.findViewById(R.id.preferences_bookmark_share);
					if(sharebookmark.isChecked()){
						mInfo.setUserShareFlag("T");
					}else{
						mInfo.setUserShareFlag("F");
					}
					
					Toast.makeText(mActivity.getApplicationContext(), "환경설정 변경 완료", 0).show();
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "환경설정 변경 실패", 0).show();
				}
			}
		}
		
		
	}

}
