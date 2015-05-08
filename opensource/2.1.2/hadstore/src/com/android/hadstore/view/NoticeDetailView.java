package com.android.hadstore.view;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.hadstore.CommonDailog;
import com.android.hadstore.R;
import com.android.hadstore.ResController;
import com.android.hadstore.controller.PageController;
import com.android.hadstore.controller.SubPageController;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;
import com.operation.model.HsBoardInfo;

public class NoticeDetailView implements SubPageController,HttpRequestTaskListener {

	private Activity mActivity;
	
	private FrameLayout mMainView;
	
	private PageController mPageController;
	
	private View mSubView;
	
	private String mPageName;
	
	private CommonDailog mCommon;
	
	public NoticeDetailView(View parent,Activity activity,PageController controller){
		mActivity = activity;
		mPageController = controller;
		mMainView = (FrameLayout) parent;
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		mCommon = new CommonDailog(mActivity);
	}
	
	@Override
	public void DialogClick(int index) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void Show(){
		mSubView.setVisibility(View.VISIBLE);
	}
	
	@Override
	public void Hide(){
		mSubView.setVisibility(View.GONE);
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		mMainView.removeView(mSubView);
		
		mSubView = null;
		
		mActivity = null;
		
		mMainView = null;
		
		mPageController = null;
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
		mSubView = View.inflate(mActivity, ResController.mLayouts.get(mPageName),null);
		mMainView.addView(mSubView);
		int seq = mPageController.getNoticeSeq();
		SendNoticeList(seq);
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
	
	public void SendNoticeList(int seq){
		HttpPostRequestTask task = new HttpPostRequestTask(mActivity, NetInfo.NOTICE_DETAIL,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();

		params.add("boardSequence", ""+seq);
		
		if(!mCommon.isShowing()){
			mCommon.show();
		}
		
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		if(mCommon.isShowing()){
			mCommon.dismiss();
		}
		if(url.equals(NetInfo.NOTICE_DETAIL)){
			if(map != null){
				int status = (Integer)map.get(NetInfo.STATUS);
				if(status == NetInfo.SUCCESS){
					HsBoardInfo info= (HsBoardInfo)map.get(NetInfo.INFO);
					TextView text = (TextView) mSubView.findViewById(R.id.notice_detail_title);
					text.setText(info.getBoardTitle());
					text = (TextView) mSubView.findViewById(R.id.notice_detail_count);
					text.setText("조회수 : "+info.getBoardViewCount().intValue());
					text = (TextView) mSubView.findViewById(R.id.notice_detail_date);
					text.setText(info.getBoardCreateTime());
					text = (TextView) mSubView.findViewById(R.id.notice_detail_nickname);
					text.setText("글쓴이 : "+info.getBoardUserNickName());
					text = (TextView) mSubView.findViewById(R.id.notice_detail_content);
					text.setText(info.getBoardContents());
				}
			}
		}
	}

}
