package com.android.hadstore;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageButton;

public class HadCustomBtn extends ImageButton {
	
	private int mBtnMode = 0;
	
	public final int MAIN_BTN_MODE = 0;
	
	public final int LIST_BTN_MODE = 1;

	public HadCustomBtn(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public HadCustomBtn(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mBtnMode = MAIN_BTN_MODE;
	}

	public HadCustomBtn(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mBtnMode = LIST_BTN_MODE;
	}
	
	public void setBtnMode(int mode){
		mBtnMode = mode;
	}
}
