package com.android.hadstore;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;


public class CustomGallery extends Gallery{
	//private static Camera mCamera; 
	
	public interface ScrollPosListener {
    	public void computeScroll(int y);
    }
	
	ScrollPosListener  Listener;
	int beforpos;
	public void setOnScrollPosListener(ScrollPosListener lis){
		Listener = lis;
	}
	public CustomGallery(Context context) {
		 this(context, null); 
	}
	public CustomGallery(Context context, AttributeSet attrs) {
		 this(context, attrs, 0); 
	}
	public CustomGallery(Context context, AttributeSet attrs, int defStyle) {
		 super(context, attrs, defStyle);
		// if(mCamera==null)
		//	 mCamera = new Camera();
		// setSpacing(-50);
	}
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		velocityX = velocityX/3;
		return super.onFling(e1, e2, velocityX, velocityY);
	}
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return super.onScroll(e1, e2, distanceX, distanceY);
	}
	@Override
	protected int computeHorizontalScrollOffset() {
		// TODO Auto-generated method stub
		 int pos = super.computeHorizontalScrollOffset();
		return pos;
	}
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		
		super.computeScroll();
		if(Listener!=null){
			int pos = this.computeHorizontalScrollOffset();
			if(beforpos==pos){
				beforpos = pos;
				Listener.computeScroll(pos);
			}
		}
	}
	@Override
	protected void onAnimationEnd() {
		// TODO Auto-generated method stub
		super.onAnimationEnd();
		
	}
	 protected boolean getChildStaticTransformation(View child, Transformation t) {
		
		/*final int mCenter =(getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
		final int childCenter = child.getLeft() + child.getWidth() / 2;
		final int childWidth = child.getWidth();
		
		t.clear();
		t.setTransformationType(Transformation.TYPE_MATRIX);

		float rate = Math.abs((float)(mCenter - childCenter)/ childWidth);
		
		mCamera.save();
		final Matrix matrix = t.getMatrix();
		float zoomAmount = (float) (rate * 300.0);
		mCamera.translate(0.0f, 0.0f, zoomAmount);   
		mCamera.getMatrix(matrix);     
		matrix.preTranslate(-(childWidth/2), -(childWidth/2));
		matrix.postTranslate((childWidth/2), (childWidth/2));
		mCamera.restore();*/
		
		return true;
    }


}
