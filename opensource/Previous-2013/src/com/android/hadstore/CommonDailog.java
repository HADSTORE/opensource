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
		// ����Ʈ �ؽ�Ʈ
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
	 * �ۼ�Ʈ �� ���
	 */
	public void setUsePercentText(){
		mPercentView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * �ۼ��� �� ������
	 */
	public void setNotUsePercentText(){
		mPercentView.setVisibility(View.GONE);
	}
	
	/**
	 * �ۼ�Ʈ ���ڰ� �Է�
	 * @param percent
	 */
	public void setProgressUpdate(String percent){
		mPercentView.setText(percent);
	}
	
	/**
	 * ���� �ٿ�ε�� �����
	 */
	public void setUseSubText(){
		mSubTextView.setVisibility(View.VISIBLE);
	}
	
	/**
	 * ���� ���� ������  
	 * @param percent
	 */
	public void setNotUseSubText(){
		mSubTextView.setVisibility(View.GONE);
	}
	
	/**
	 * ���� �̸� �Է� or ����
	 */
	public void setSubText(String musicName){
		mSubTextView.setText(musicName);
	}
}