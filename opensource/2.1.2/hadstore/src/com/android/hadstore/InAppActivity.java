package com.android.hadstore;

import com.android.hadstore.controller.InAppPageController;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class InAppActivity extends Activity{
	
	private final String TAG = "InAppActivity";
	
	private InAppPageController mPageController;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inapp_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        String packagename = getIntent().getStringExtra(HadStoreInapp.PAY_PACKAGE_NAME);
		String pay = getIntent().getStringExtra(HadStoreInapp.PAY);
		if(packagename==null || packagename.equals("")){
			Toast.makeText(getApplicationContext(), "패키지 명이 없습니다.", 0).show();
			Intent intent = InAppPageController.getInappIntentSet("not found packgename error",HadStoreInapp.PAY_FAIL);
			setResult(RESULT_OK, intent);
			finish();
			return;
		}
		if(pay==null || pay.equals("")){
			Toast.makeText(getApplicationContext(), "결제 금액이 없습니다.", 0).show();
			Intent intent = InAppPageController.getInappIntentSet("not found pay error",HadStoreInapp.PAY_FAIL);
			setResult(RESULT_OK, intent);
			finish();
			return;
		}
		Log.e(TAG, packagename+"    "+pay);
		boolean error = false;
		int paypoint = 0;
		try{
			paypoint = Integer.parseInt(pay.toString().replace(",",""));
		}catch(Exception e){
			error = true;
		}
		
		if(paypoint<=0||error){
			Toast.makeText(getApplicationContext(), "결제 금액이 잘못되었습니다.", 0).show();
			Intent intent = InAppPageController.getInappIntentSet("go wrong pay error",HadStoreInapp.PAY_FAIL);
			setResult(RESULT_OK, intent);
			finish();
			return;
		}
        mPageController = new InAppPageController(this);
        mPageController.setIdPay(packagename, paypoint);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		if(mPageController.getPageIndex()>0){
    			if(mPageController.getPageIndex()==1){
    			}else{
    				mPageController.BackKey();
    				return true;
    			}
    			
    		}
    	}
    	return super.onKeyDown(keyCode, event);
    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if(mPageController!=null)mPageController.onPause();
	}
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	if(mPageController!=null)mPageController.onResume();
    	
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	if(mPageController!=null)
    		mPageController.Release();
    }
}
