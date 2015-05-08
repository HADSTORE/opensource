package com.android.hadstore.view;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.Global;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.operation.model.HsApplicationInfo;

public class AppDetailGiftView implements SubPageController,OnClickListener,HttpRequestTaskListener {
	
	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private View mSubView;
	
	private String mPageName;
	
	private CommonDailog mCommon;
	
	private AppDetailView mAppDetailView;
	
	private boolean mIdCheck = false;
	
	public boolean isIdCheck() {
		return mIdCheck;
	}
	
	public AppDetailGiftView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mMainView = (FrameLayout) parent;
		mCommon = new CommonDailog(mActivity);
	}

	public AppDetailGiftView(View parent,Activity activity,AppDetailView controller){
		mActivity = activity;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mAppDetailView = controller;
		mMainView = (FrameLayout) parent;
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
		mMainView.removeView(mSubView);
		
		mAppDetailView = null;
		
		mSubView = null;
		
		mActivity = null;
		
		mMainView = null;
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
		
		mSubView = View.inflate(mActivity, R.layout.layout_detail_gift,null);
		
		//mSubView.findViewById(R.id.detail_gift_main).setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.905)));
		
		mSubView.findViewById(R.id.detail_gift_cancel).setOnClickListener(this);
		mSubView.findViewById(R.id.detail_gift_ok).setOnClickListener(this);
		mSubView.findViewById(R.id.detail_gift_check).setOnClickListener(this);
		
		TextView text = (TextView) mSubView.findViewById(R.id.detail_gift_appname);
		text.setText(mAppDetailView.getAppinfo().getAppTitle());
		
		text = (TextView) mSubView.findViewById(R.id.detail_gift_my_point);
		text.setText(mAppDetailView.getPageController().mSalesPoint+"");
		
		text = (TextView) mSubView.findViewById(R.id.detail_gift_pay);
		text.setText(mAppDetailView.getGiftPoint()+"");
		
		mMainView.addView(mSubView);
	}

	@Override
	public String getPageName() {
		// TODO Auto-generated method stub
		return mPageName;
	}

	@Override
	public int BackKey() {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
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

	public void SendIdCheck(String id){
		
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.USER_ID_CHECK,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		params.add("userId", id);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.detail_gift_cancel:
			mAppDetailView.ReleaseDetailGiffView();
			break;
		case R.id.detail_gift_ok:
			mAppDetailView.GiftClickOk();
			break;
		case R.id.detail_gift_check:
			EditText edit = (EditText) mSubView.findViewById(R.id.detail_gift_id);
			if(edit.length()>0)
				SendIdCheck(edit.getText().toString());
			break;
		
		}
	}
	
	public String getGiftId(){
		EditText edit = (EditText)mSubView.findViewById(R.id.detail_gift_id);
		return edit.getText().toString();
	}
	
	public String getGiftMsg(){
		EditText edit = (EditText)mSubView.findViewById(R.id.detail_gift_msg);
		return edit.getText().toString();
	}
	
	public void PointReset(){
		TextView text = (TextView) mSubView.findViewById(R.id.detail_gift_pay);
		text.setText(mAppDetailView.getGiftPoint()+"");
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.USER_ID_CHECK)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					String exist = (String)map.get(NetInfo.EXIST);
					if(exist.equals("Y")){
						String nickname = (String) map.get("userNickNameDesc");
						TextView text = (TextView) mSubView.findViewById(R.id.detail_gift_nickname);
						text.setText(nickname);
						mSubView.findViewById(R.id.detail_gift_id).setEnabled(true);
						mIdCheck = true;
					}else{
						mIdCheck = false;
						Toast.makeText(mActivity.getApplicationContext(), "존재하지 않는 아이디 입니다.", 0).show();
					}
				}else{
					mIdCheck = false;
					Toast.makeText(mActivity.getApplicationContext(), "존재하지 않는 아이디 입니다.", 0).show();
				}
			}
		}
	}

}
