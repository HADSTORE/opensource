package com.android.hadstore;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.apache.http.NameValuePair;

import com.android.hadstore.controller.InAppPageController;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

public class AppPaymentActivity extends Activity implements HttpRequestTaskListener {
	private final String TAG = "InAppActivity";
	private CommonDailog mCommon;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_main);
        mCommon = new CommonDailog(this);
        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        String packagename = getIntent().getStringExtra(HadStoreInapp.PAY_PACKAGE_NAME);
        final TelephonyManager tm = (TelephonyManager)getBaseContext().getSystemService(Activity.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
      //intent.putExtra(HadStoreInapp.PAY_MACHINE_NAME, tmDevice+android.os.Build.SERIAL+deviceId);
		if(packagename==null || packagename.equals("")){
			Toast.makeText(getApplicationContext(), "패키지 명이 없습니다.", 0).show();
			Intent intent = InAppPageController.getInappIntentSet("not found packgename error",HadStoreInapp.PAYMENT_CHECK_FAIL);
			setResult(RESULT_OK, intent);
			finish();
			return;
		}
		CheckPayment(packagename);
	}
	
	
	public void CheckPayment(String packagename){
		HttpPostRequestTask task = new HttpPostRequestTask(this, NetInfo.PAYMENT_CHECK,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		final TelephonyManager tm = (TelephonyManager)this.getBaseContext().getSystemService(Activity.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(this.getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long)tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
		
		params.add("appDownMachineName", tmDevice+android.os.Build.SERIAL+deviceId);
		params.add("packageName", packagename);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		if(mCommon.isShowing()) return true;
    	}
    	return super.onKeyDown(keyCode, event);
    }
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		
	}
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    }

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.PAYMENT_CHECK)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					Intent intent = InAppPageController.getInappIntentSet("success",HadStoreInapp.PAYMENT_CHECK_SUCCESS);
					setResult(RESULT_OK, intent);
				}else{
					Intent intent = InAppPageController.getInappIntentSet("error",HadStoreInapp.PAYMENT_CHECK_FAIL);
					setResult(RESULT_OK, intent);
				}
			}else{
				Intent intent = InAppPageController.getInappIntentSet("error",HadStoreInapp.PAYMENT_CHECK_FAIL);
				setResult(RESULT_OK, intent);
			}
			finish();
		}
	}
}
