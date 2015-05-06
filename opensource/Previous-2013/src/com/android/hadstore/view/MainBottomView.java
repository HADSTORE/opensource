package com.android.hadstore.view;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.android.hadstore.R;
import com.android.hadstore.Global;
import com.android.hadstore.util.DateUtil;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainBottomView implements OnClickListener{

	private Activity mActivity;
	
	private LinearLayout mBottomView;
	
	private MainView mMainController;
	
	private TextView mTime;
	
	private TextView mWeekMonth;
	
	private TextView mDay;
	
	private TextView mGift_Notify;
	
	private TextView mUpdate_Notify;
	
	private TextView mNotify_Notify;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			Calendar calendar = new GregorianCalendar();  
			mTime.setText(DateUtil.getMainTime(calendar));
			mWeekMonth.setText(DateUtil.getMonth(calendar));
			mDay.setText(""+calendar.get(Calendar.DATE));
			mHandler.sendEmptyMessageDelayed(0, 1000);
		};
	};
	
	MainBottomView(Activity activity,MainView main,View subView){
		mActivity = activity;
		mMainController = main;
		mBottomView = (LinearLayout) subView.findViewById(R.id.main_bottom);
		mTime = (TextView) mBottomView.findViewById(R.id.clock_time);
		mWeekMonth = (TextView) mBottomView.findViewById(R.id.clock_week_month);
		mDay = (TextView) mBottomView.findViewById(R.id.clock_day);
		mGift_Notify = (TextView) mBottomView.findViewById(R.id.gift_notify);
		mUpdate_Notify = (TextView) mBottomView.findViewById(R.id.update_notify);
		mNotify_Notify = (TextView) mBottomView.findViewById(R.id.notify_notify);
		mBottomView.findViewById(R.id.gift_btn).setOnClickListener(this);
		mBottomView.findViewById(R.id.update_btn).setOnClickListener(this);
		mBottomView.findViewById(R.id.notify_btn).setOnClickListener(this);
		mHandler.sendEmptyMessageDelayed(0, 0);
	}

	public void Release() {
		// TODO Auto-generated method stub
		mActivity = null;
		
		mBottomView = null;
		
		mMainController = null;
	}
	
	public void setPage() {
		// TODO Auto-generated method stub
		mBottomView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.8025)));
	}
	
	public void Show(){
		mBottomView.setVisibility(View.VISIBLE);
	}
	
	public void Hide(){
		mBottomView.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.gift_btn:
				mMainController.onClickGift();
				break;
			case R.id.update_btn:
				mMainController.onClickUpdate();
				break;
			case R.id.notify_btn:
				mMainController.onClickNotify();
				break;
		}
	}
	
	public void setNotify(int gift,int update,int notify){
		if(gift>0){
			
			if(mGift_Notify.getVisibility()!=View.VISIBLE)
				mGift_Notify.setVisibility(View.VISIBLE); 
			mGift_Notify.setText(""+gift);
		}else
			mGift_Notify.setVisibility(View.INVISIBLE);
		
		if(update>0){
			if(mUpdate_Notify.getVisibility()!=View.VISIBLE)
				mUpdate_Notify.setVisibility(View.VISIBLE);
			mUpdate_Notify.setText(""+update);
		}else
			mUpdate_Notify.setVisibility(View.INVISIBLE);
		
		if(notify>0){
			if(mNotify_Notify.getVisibility()!=View.VISIBLE)
				mNotify_Notify.setVisibility(View.VISIBLE);
			mNotify_Notify.setText(""+notify);
		}else
			mNotify_Notify.setVisibility(View.INVISIBLE);
	}
}
