package com.android.hadstore;

import com.android.hadstore.R;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

public class CommonDailog  extends Dialog implements OnClickListener  {
	private TextView mTextView;	
	private TextView mSubTextView;	
	private TextView mPercentView;	
	public static final String TEXT_LOADING = "LOADING..."; 
	public static final String TEXT_DOWNLLOADING = "Downloading..."; 
	
	public CommonDailog(Context context) {
		super(context, R.style.Theme_Dialog_noBack);
		// TODO Auto-generated constructor stub
		/** 'Window.FEATURE_NO_TITLE' - Used to hide the TITLE */
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		/** Design the dialog in main.xml file */
		setContentView(R.layout.common_popup);
		 setCancelable(false);
		setUI();
	}
	
	private void setUI() {
		
		mPercentView = (TextView)findViewById(R.id.percent);
		mPercentView.setTypeface(Global.Fonts);
		
		mTextView = (TextView)findViewById(R.id.text);
		mTextView.setTypeface(Global.Fonts);
		
		mSubTextView = (TextView)findViewById(R.id.subtext);
		mTextView.setTypeface(Global.Fonts);
		// 디폴트 텍스트
		mTextView.setText(TEXT_LOADING); 
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
	}

		
	@Override
	public void show() {
		// TODO Auto-generated method stub
		super.show();
		
	}


	public void cancel() {
		// TODO Auto-generated method stub
		super.cancel();
	}

	@Override
	public void dismiss() {
		// TODO Auto-generated method stub
		super.dismiss();
	}
	
	public void setText(String text){
		mTextView.setText(text);
	}
	
	/**
	 * 퍼센트 뷰 사용
	 */
	public void setUsePercentText(){
		mPercentView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 퍼센터 뷰 사용안함
	 */
	public void setNotUsePercentText(){
		mPercentView.setVisibility(View.GONE);
	}
	
	/**
	 * 퍼센트 숫자값 입력
	 * @param percent
	 */
	public void setProgressUpdate(String percent){
		mPercentView.setText(percent);
	}
	
	/**
	 * 음악 다운로드시 제목용
	 */
	public void setUseSubText(){
		mSubTextView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 음악 제목 사용안함  
	 * @param percent
	 */
	public void setNotUseSubText(){
		mSubTextView.setVisibility(View.GONE);
	}
	
	/**
	 * 뮤직 이름 입력 or 변경
	 */
	public void setSubText(String musicName){
		mSubTextView.setText(musicName);
	}
}