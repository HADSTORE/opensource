package com.android.hadstore.view;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.InAppPageController;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.operation.model.HsDownHistory;

public class PaymentView implements SubPageController,HttpRequestTaskListener{
	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private PageController mPageController = null;
	
	private InAppPageController mInAppPageController = null;
	
	private View mSubView;
	
	private String mPageName;
	
	private WebView mWebView;
	
	private CommonDailog mCommon;
	
	private boolean isShowWebView; 
	
	class HadWebViewClient extends WebViewClient{
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			if("hadstore://cancel".equals(url)){
				 if(mPageController!=null)mPageController.BackKey();
				 else mInAppPageController.BackKey();
				 return true;
			}else if("hadstore://sucess".equals(url)){
				 AppUserInfoSend();
				 return true;
			}else if(url.startsWith("market://details")){
				Intent i = new Intent(Intent.ACTION_VIEW);
				Uri u = Uri.parse(url);
				i.setData(u);
				mActivity.startActivity(i);
				isShowWebView = true;
				return true;
			}else if(!url.startsWith("http://")){
				try{
					Intent i = new Intent(Intent.ACTION_VIEW);
					Uri u = Uri.parse(url);
					i.setData(u);
					mActivity.startActivity(i);
					isShowWebView = true;
				}catch(ActivityNotFoundException e){
					Toast.makeText(mActivity.getApplicationContext(), "결제 관련 앱설치가 필요 합니다.", 0).show();
				}
				
				return true;
			}
			view.loadUrl(url);
			return true;
        }
		
	}
	
	public class HelloWebChromeClient extends WebChromeClient {

		  @Override
		  public boolean onCreateWindow(WebView view, boolean dialog,
		    boolean userGesture, Message resultMsg) {
		   // TODO Auto-generated method stub
			  Log.e("mgdoo", resultMsg.toString());
		   return super.onCreateWindow(view, dialog, userGesture, resultMsg);
		  }
		  
		  @Override
		  public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
		   
		   // TODO Auto-generated method stub
		   return super.onJsAlert(view, url, message, result);
			/*  Log.e("mgdoo", message);
		    new AlertDialog.Builder(view.getContext())
		        .setTitle("알림")
		        .setMessage(message)
		        .setPositiveButton(android.R.string.ok,
		              new AlertDialog.OnClickListener(){
		                 public void onClick(DialogInterface dialog, int which) {
		                  result.confirm();
		                 }
		              })
		        .setCancelable(false)
		        .create()
		        .show();
		   return true;*/
		  }

		  @Override
		  public boolean onJsConfirm(WebView view, String url, String message,
		    final JsResult result) {
		   // TODO Auto-generated method stub
		   //return super.onJsConfirm(view, url, message, result);
			  Log.e("mgdoo",message);
		   new AlertDialog.Builder(view.getContext())
		        .setTitle("알림")
		        .setMessage(message)
		        .setPositiveButton("네",
		              new AlertDialog.OnClickListener(){
		                 public void onClick(DialogInterface dialog, int which) {
		                  result.confirm();
		                 }
		              })
		        .setNegativeButton("아니오", 
		          new AlertDialog.OnClickListener(){
		                public void onClick(DialogInterface dialog, int which) {
		                 result.cancel();
		                }
		             })
		        .setCancelable(false)
		        .create()
		        .show();
		   return true;
		  }

		 }
	
	public PaymentView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mPageController = controller;
		mMainView = (FrameLayout) parent;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mCommon = new CommonDailog(mActivity);
	}
	
	public PaymentView(View parent,Activity activity,InAppPageController controller){
		mActivity = activity;
		mInAppPageController = controller;
		mMainView = (FrameLayout) parent;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mCommon = new CommonDailog(mActivity);
	}

	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Show() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		if(mWebView != null){
			mWebView.destroy();
			mWebView = null;
		}
		mMainView.removeView(mSubView);
		
		mSubView = null;
		
		mActivity = null;
		
		mMainView = null;
		
		mPageController = null;
		
		mInAppPageController = null;
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		AppUserInfoSend();
	}
	
	public void AppUserInfoSend(){

		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_ID_INFO,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		if(mPageController!=null)
			params.add("userId", mPageController.getUserId());
		else
			params.add("userId", mInAppPageController.getUserId());
		
		if(mPageController!=null)
			params.add("userPassword", mPageController.getPassword());
		else
			params.add("userPassword", mInAppPageController.getUserPassword());
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
	}

	@Override
	public void setPageName(String name) {
		// TODO Auto-generated method stub
		mPageName = name;
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(mPageName),null);
		mMainView.addView(mSubView);
		mWebView = (WebView) mSubView.findViewById(R.id.payment_webview);
		mWebView.setWebViewClient(new HadWebViewClient());
		if(mInAppPageController!=null){
			String str = "user_id="+mInAppPageController.getUserId()+"&"+"userPassword="+mInAppPageController.getUserPassword();
			mWebView.postUrl("http://www.hadstore.com/Hadstore/mobile/point/view/mobile/point.htm", str.getBytes());
		}else{
			String str = "user_id="+mPageController.getUserId()+"&"+"userPassword="+mPageController.getPassword();
			mWebView.postUrl("http://www.hadstore.com/Hadstore/mobile/point/view/mobile/point.htm", str.getBytes());
			//mWebView.postUrl("http://192.168.142.138:8080/Hadstore/mobile/point/view/mobile/point.htm", str.getBytes());
		}
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setSupportMultipleWindows(true);
		HelloWebChromeClient testChromeClient = new HelloWebChromeClient();

		//생성한 크롬 클라이언트를 웹뷰에 셋한다
		mWebView .setWebChromeClient( testChromeClient  );
		//mWebView.loadUrl("http://www.google.com");
	}

	@Override
	public String getPageName() {
		// TODO Auto-generated method stub
		return mPageName;
	}

	@Override
	public int BackKey() {
		// TODO Auto-generated method stub
		if(isShowWebView){
			isShowWebView = false;
			return 1;
		}else if(mWebView.canGoBack()){
			Log.e("mgdoo", "BackKey");
			mWebView.goBack();
			return 1;
		}else if(mCommon.isShowing()){
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
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.USER_ID_INFO)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					int sales = (Integer)map.get(NetInfo.USER_SALES_POINT);
					int gift = (Integer)map.get(NetInfo.USER_GIFT_COUNT);
					int noty = (Integer)map.get(NetInfo.USER_NOTICE_COUNT);
					if(mInAppPageController!=null){
						mInAppPageController.setSalsePoint(sales);
						mInAppPageController.BackKey();
					}else{
						mPageController.mNotyLastCount = mPageController.getDbUtil().NoticeLastCnt(mPageController.getUserId());
						int gap = noty - mPageController.mNotyLastCount;
						
						mPageController.LoginSet(sales, gap, gift, mPageController.mNickName,true);
						
						List<HsDownHistory> downHistory =  (List<HsDownHistory>) map.get(NetInfo.USER_DOWN_HISTORY);
						mPageController.mDownList.clear();
						for(int i=0;i<downHistory.size();i++){
							mPageController.mDownList.put(downHistory.get(i).getPackageName(), downHistory.get(i));
						}
						mPageController.BackKey();
					}
				}else{
					Toast.makeText(mActivity.getApplicationContext(), "사용자 정보 확인 실패", 0).show();
				}
			}else{
				Toast.makeText(mActivity.getApplicationContext(), "사용자 정보 확인 실패", 0).show();
			}
		}
	}

}
