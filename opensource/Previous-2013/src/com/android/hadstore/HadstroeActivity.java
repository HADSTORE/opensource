package com.android.hadstore;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.android.hadstore.R;
import com.android.hadstore.controller.PageController;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

public class HadstroeActivity extends Activity {
	
	private final String TAG = "HadstroeActivity";
	
	private PageController mPageController;
	
	public static int SEARCH_ACTIVITY = 0;
	
	public static final int DIALOG_EXIT = 0;
	
	public static final int DIALOG_DOWN_CHECK = 1;
	
	public static final int DIALOG_DOWN_RE_CHECK = 2;
	
	public static final int DIALOG_GIFT_CHECK = 3;
	
	public static final int DIALOG_GIFT_RE_CHECK = 4;
	
	public static final int DIALOG_HADSTORE_DOWN = 5;
	
	public static final int DIALOG_OK = 0;
	
	public static final int DIALOG_CANCEL = 1;
	
	private int mPoint;
	
	private String mUriAppSysId;
	
	private BroadcastReceiver mReciver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			final String action = intent.getAction();
			if (Intent.ACTION_PACKAGE_ADDED.equals(action)|| Intent.ACTION_PACKAGE_CHANGED.equals(action)|| Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
                final String pkgNm = intent.getData().getSchemeSpecificPart();

                if (pkgNm == null || pkgNm.length() == 0) {
                    return;
                }
                
			}
		}
	};
	
	public void setPoint(int point){
		mPoint = point;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.tap_bar);
        Display dis= getWindowManager().getDefaultDisplay();
	    Global.DISPLAYWIDTH = dis.getWidth();
	    Global.DISPLAYHEIGHT = dis.getHeight();
	    Global.TOPBAR_HEIGHT = getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
        mPageController = new PageController(this);
        
        IntentFilter filter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(mCloseSystemDialogsReceiver, filter);
        
        Intent intent = new Intent("com.hadsotre.DOWNSERVICE");
        startService(intent);
        CheckIntent(getIntent());
        Log.e("mgdoo","onCreate");
    }
    
    protected void onNewIntent(Intent intent){
    	CheckIntent(intent);
    	super.onNewIntent(intent);
    }
    
    public void CheckIntent(Intent intent){
    	Uri uri = intent.getData();
    	if(uri!=null){
    		mPageController.startUriDetail(uri);
    	}
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
    	mPageController.onConfigurationChanged(newConfig);
      //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR); // 모두전환 됨
      //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 가로고정
      //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //세로고정
    	super.onConfigurationChanged(newConfig);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	// TODO Auto-generated method stub
    	if(keyCode == KeyEvent.KEYCODE_BACK){
    		if(mPageController.getPageIndex()>0){
    			if(mPageController.getPageIndex()==1){
    				showDialog(DIALOG_EXIT);
    			}else{
    				mPageController.BackKey();
    			}
    			return true;
    		}
    	}
    	return super.onKeyDown(keyCode, event);
    }
	@Override
    protected Dialog onCreateDialog(int id, Bundle bundle) {
    	AlertDialog alert=null;
    	 switch (id) {
	        case DIALOG_EXIT: {
	        	alert = new AlertDialog.Builder(this).setPositiveButton("ok", new DialogInterface.OnClickListener() {
	
	                public void onClick(DialogInterface dialog, int which) {
	                    dismissDialog(DIALOG_EXIT);
	                    finish();
	                }
	            }).setNeutralButton("cancel", new DialogInterface.OnClickListener() {
	
	                public void onClick(DialogInterface dialoginterface, int i) {
	                   
	                }
	            }).setMessage("Are you sure you want to quit?").create();
	        	
	            break;
	          }
	        case DIALOG_DOWN_CHECK:{
	        	alert = new AlertDialog.Builder(this).setPositiveButton("ok", new DialogInterface.OnClickListener() {
	
	                public void onClick(DialogInterface dialog, int which) {
	                	mPageController.DialogClick(DIALOG_OK);
	                	dismissDialog(DIALOG_DOWN_CHECK);
	                }
	            }).setNeutralButton("cancel", new DialogInterface.OnClickListener() {
	
	                public void onClick(DialogInterface dialoginterface, int i) {
	                	mPageController.DialogClick(DIALOG_CANCEL);
	                	dismissDialog(DIALOG_DOWN_CHECK);
	                }
	            }).setMessage(mPoint+" 포인트가 차감됩니다."+"결제 하시겠습니까?").create();
	        	
	            break;
	          }
	        case DIALOG_DOWN_RE_CHECK:{
	        	alert = new AlertDialog.Builder(this).setPositiveButton("ok", new DialogInterface.OnClickListener() {
	
	                public void onClick(DialogInterface dialog, int which) {
	                	mPageController.DialogClick(DIALOG_OK);
	                	dismissDialog(DIALOG_DOWN_RE_CHECK);
	                }
	            }).setNeutralButton("cancel", new DialogInterface.OnClickListener() {
	
	                public void onClick(DialogInterface dialoginterface, int i) {
	                	mPageController.DialogClick(DIALOG_CANCEL);
	                	dismissDialog(DIALOG_DOWN_RE_CHECK);
	                }
	            }).setMessage("결제 포인트가 "+mPoint+" 포인트로 변경되었습니다. "+"결제 하시겠습니까?").create();
	        	
	            break;
	          }
	        case DIALOG_HADSTORE_DOWN:
	        	{
	        	alert = new AlertDialog.Builder(this).setPositiveButton("ok", new DialogInterface.OnClickListener() {
	
	                public void onClick(DialogInterface dialog, int which) {
	                	mPageController.DialogClick(DIALOG_OK);
	                	dismissDialog(DIALOG_HADSTORE_DOWN);
	                }
	            }).setNeutralButton("cancel", new DialogInterface.OnClickListener() {
	
	                public void onClick(DialogInterface dialoginterface, int i) {
	                	mPageController.DialogClick(DIALOG_CANCEL);
	                	dismissDialog(DIALOG_HADSTORE_DOWN);
	                }
	            }).setMessage("HadStore를 업데이트 하시겠습니까?").create();
	        	
	            break;
	          }
    	 }
    	 if(alert==null)
    		 return super.onCreateDialog(id, bundle);
    	 else
    		 return alert;
    }
	
	@Override
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
    	// TODO Auto-generated method stub
		
		 switch (id) {
	        case DIALOG_EXIT:
	        	break;
	        case DIALOG_DOWN_CHECK:
	        	AlertDialog alert = (AlertDialog) dialog;
	        	alert.setMessage(mPoint+"p 포인트가 차감됩니다."+"다운로드 하시겠습니까?");
	        	break;
	        case DIALOG_DOWN_RE_CHECK:
	        	alert = (AlertDialog) dialog;
	        	alert.setMessage("결제 포인트가 "+mPoint+" 포인트로 변경되었습니다. "+"다운로드 하시겠습니까?");
	        	break;
		 }
		 
		
    	super.onPrepareDialog(id, dialog, args);
    }
	
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mPageController.onPause();
	}
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	Log.e("mgdoo","onResume");
    	mPageController.onResume();
    	
    }
    
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	mPageController.Release();
    	 try {
             unregisterReceiver(mCloseSystemDialogsReceiver);
         } catch (Exception e) {
             e.printStackTrace();
         }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	if(resultCode != RESULT_OK) {
    		mPageController.mSearchFlag = false;
			return;
		}
    	
    	if(requestCode==SEARCH_ACTIVITY){
    		mPageController.mSearchFlag = true;
    		mPageController.mSearchName = data.getExtras().getString("search");
    	}
    }
    
    private final BroadcastReceiver mCloseSystemDialogsReceiver = new CloseSystemDialogsIntentReceiver();
    private class CloseSystemDialogsIntentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String reason = intent.getStringExtra("reason");
            if ("homekey".equals(reason)) {
            	if(mPageController.mPageIndex>1){
            		finish();
            	}
            }
        }
    }
}