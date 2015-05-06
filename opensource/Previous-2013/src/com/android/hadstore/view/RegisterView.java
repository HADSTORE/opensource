package com.android.hadstore.view;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.CustomDatePicker;
import com.android.hadstore.Encoder;
import com.android.hadstore.R;
import com.android.hadstore.Global;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;

public class RegisterView implements SubPageController,View.OnClickListener,HttpRequestTaskListener{

	public static final int TERMS_MODE = 0;
	
	public static final int REGISTER_MODE = 1;
	
	private Activity mActivity;
	
	private LinearLayout mMainView;
	
	private PageController mPageController;
	
	public int mRegisterMode = 0;
	
	private View mSubView;
	
	private String mPageName;
	
	private boolean mResiterCheck;
	
	private boolean mIdCheck = false;
	
	private CommonDailog mCommon;
	
	private InputFilter mFilterAlphaNum = new InputFilter() {
		
		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			// TODO Auto-generated method stub
			
			Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
			
			if(!ps.matcher(source).matches()){
				return "";
			}
			return null;
		}
	};
	
	public RegisterView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mPageController = controller;
		mMainView = (LinearLayout) parent;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
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
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		ViewRelease();
		
		mActivity = null;
		
		mCommon = null;
		
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
		InitTerms();
	}

	@Override
	public String getPageName() {
		// TODO Auto-generated method stub
		return mPageName;
	}

	@Override
	public int BackKey() {
		// TODO Auto-generated method stub
		if(mRegisterMode==REGISTER_MODE){
			ViewRelease();
			if(!mResiterCheck){
				InitTerms();
				return 1;
			}
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
	
	public void InitTerms(){
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(mPageName),null);
		mSubView.findViewById(R.id.terms_agree).setOnClickListener(this);
		mMainView.addView(mSubView);
		mRegisterMode = TERMS_MODE;
	}
	
	public void InitRegister(){
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(ResController.PAGE_REGISTER),null);
		mSubView.findViewById(R.id.register_agree).setOnClickListener(this);
		RadioButton man = (RadioButton) mSubView.findViewById(R.id.register_man);
		man.setChecked(true);
		man.setOnClickListener(this);
		RadioButton women = (RadioButton) mSubView.findViewById(R.id.register_women);
		women.setChecked(false);
		women.setOnClickListener(this);
		mMainView.addView(mSubView);
		TelephonyManager telManager = (TelephonyManager)mActivity.getSystemService(Context.TELEPHONY_SERVICE); 
		String phoneNum = telManager.getLine1Number();
		EditText id = (EditText) mSubView.findViewById(R.id.register_id);
		
		mSubView.findViewById(R.id.register_id_check).setOnClickListener(this);
		if(phoneNum!=null&&phoneNum.length()>0){
			id.setText(phoneNum.replace("+82", "0"));
		}
		
		EditText pw = (EditText) mSubView.findViewById(R.id.register_pw);
		pw.setFilters(new InputFilter[]{mFilterAlphaNum});
		EditText pwre = (EditText) mSubView.findViewById(R.id.register_pw_re);
		pwre.setFilters(new InputFilter[]{mFilterAlphaNum});
		mRegisterMode = REGISTER_MODE;
	}
	
	public void ViewRelease(){
		if(mRegisterMode==TERMS_MODE){
			mMainView.removeView(mSubView);
			mSubView = null;
		}else{
			mMainView.removeView(mSubView);
			mSubView = null;
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.terms_agree:
				CheckBox checkbox = (CheckBox) mSubView.findViewById(R.id.option_free);
				if(checkbox.isChecked()){
					ViewRelease();
					InitRegister();
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "위사항에 동의 하십시요", 0).show();
				}
				break;
			case R.id.register_id_check:
				{
					EditText id = (EditText) mSubView.findViewById(R.id.register_id);
					if(id.getText().length()==0){
						Toast.makeText(mActivity.getApplicationContext(), "아이디가 없습니다.", 0).show();
						return;
					}
					HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_ID_CHECK,true);
					task.setOnFinshListener(this);
					
					// 파람 만들기
					ParamMaker params= new ParamMaker();
					// 최초 불러올땐 seq = 0을 입력한다
					
					params.add("userId",id.getText().toString());
		
					if(!mCommon.isShowing()){
						mCommon.show();
					}
					// Request 실행
					List<NameValuePair> realParams = params.getParams();
					task.execute(realParams);
					
				}
				break;
			case R.id.register_agree:
				{
					EditText pw = (EditText) mSubView.findViewById(R.id.register_pw);
					EditText pwre = (EditText) mSubView.findViewById(R.id.register_pw_re);
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
					EditText id = (EditText) mSubView.findViewById(R.id.register_id);
					if(id.getText().length()==0){
						Toast.makeText(mActivity.getApplicationContext(), "아이디가 없습니다.", 0).show();
						return;
					}
					if(!mIdCheck){
						Toast.makeText(mActivity.getApplicationContext(), "아이디를 체크하세요", 0).show();
						return;
					}
					HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.CREATE_USER,true);
					task.setOnFinshListener(this);
					
					// 파람 만들기
					ParamMaker params= new ParamMaker();
					// 최초 불러올땐 seq = 0을 입력한다
					
					params.add("userId",id.getText().toString());
					try {
						params.add("userPassword",Encoder.encryptToSha(pw.getText().toString()));
					} catch (NoSuchAlgorithmException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					TextView mail = (TextView) mSubView.findViewById(R.id.register_mail);
					params.add("userEmailAddress",mail.getText().toString());
					
					TextView name = (TextView) mSubView.findViewById(R.id.register_name);
					params.add("userName",name.getText().toString());
					
					RadioButton man = (RadioButton) mSubView.findViewById(R.id.register_man);
					
					if(man.isChecked()){
						params.add("userGender","male");
					}else{
						params.add("userGender","female");
					}
						
					params.add("userType","private");
					
					CustomDatePicker picker = (CustomDatePicker) mSubView.findViewById(R.id.date_picker);
					
					params.add("year",""+picker.getYear()); 
					
					params.add("month",""+picker.getMonth());
					
					params.add("day",""+picker.getDay());
	
					if(!mCommon.isShowing()){
						mCommon.show();
					}
					// Request 실행
					List<NameValuePair> realParams = params.getParams();
					task.execute(realParams);
					
				}
				break;
			case R.id.register_man:
				RadioButton man = (RadioButton)v;
				man.setChecked(true);
				RadioButton women = (RadioButton) mSubView.findViewById(R.id.register_women);
				women.setChecked(false);
				break;
			case R.id.register_women:
				man = (RadioButton) mSubView.findViewById(R.id.register_man);
				man.setChecked(false);
				women = (RadioButton)v;
				women.setChecked(true);
				break;
		}
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.CREATE_USER)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					mResiterCheck = true;
					mPageController.BackKey();
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "회원가입 실패", 0).show();
				}
			}
		}
		if(url.equals(NetInfo.USER_ID_CHECK)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					String exist = (String)map.get(NetInfo.EXIST);
					if(exist.equals("Y")){
						Toast.makeText(mActivity.getApplicationContext(), "존재하는 아이디 입니다.", 0).show();
					}else{
						EditText id = (EditText) mSubView.findViewById(R.id.register_id);
						id.setEnabled(true);
						mIdCheck = true;
						Toast.makeText(mActivity.getApplicationContext(), "아이디 체크 성공", 0).show();
					}
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "아이디 체크 실패", 0).show();
				}
			}
		}
	}

	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		
	}
}
