package com.android.hadstore;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

public class CustomTextView extends TextView {
	
	private int mLineWidth = 0;
	
	private int mLineHeight = 0;
	
	private int mLineStartX = 0;
	
	private int mLineStartY = 0;
	
	private Paint mPaint;
	
	private boolean mHarf;

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		setPaint();
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setPaint();
	}

	public CustomTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		setPaint();
	}
	
	private void setPaint(){
		mPaint = new Paint();
		mPaint.setColor(0xffffff);
	}
	
	public void setHarfLine(boolean harf){
		mHarf = harf;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(mLineWidth==0){
			mLineWidth = (int) (getWidth()*0.8f);
			mLineHeight = 2;
			mLineStartX = (int) (getWidth()*0.1f);
			mLineStartY = (int) (getHeight()/2-1);
		}
		//canvas.drawLine(mLineStartX, mLineStartY, stopX, mLineStartY, paint);
		if(mHarf){
			mPaint.setColor(Color.WHITE);
			canvas.drawRect(mLineStartX, mLineStartY, mLineStartX+mLineWidth, mLineStartY+mLineHeight, mPaint);
			//canvas.drawLine(mLineStartX, mLineStartY, mLineWidth, mLineStartY+mLineHeight, mPaint);
		}
	}
	
	@Override
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.draw(canvas);
		
	}
}
