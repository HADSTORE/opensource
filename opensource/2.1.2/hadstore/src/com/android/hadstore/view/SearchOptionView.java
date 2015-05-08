package com.android.hadstore.view;

import com.android.hadstore.R;
import com.android.hadstore.Global;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.ToggleButton;

public class SearchOptionView implements View.OnClickListener{
	
	private Activity mActivity;
	
	private LinearLayout mSearchOption;
	
	private final int mCheckBoxRes[] = {
		R.id.option_free,R.id.option_pay,R.id.option_sponsor,
		R.id.option_health,R.id.option_game,R.id.option_financial,R.id.option_education,
		R.id.option_news,R.id.option_books,R.id.option_business,R.id.option_social,
		R.id.option_woman,R.id.option_entertainment,R.id.option_utility,R.id.option_region,
		R.id.option_kids,R.id.option_office,R.id.option_school
	};
	
	public static boolean mCheck[] = new boolean[]{
		true,true,true,
		false,false,false,false,
		false,false,false,false,
		false,false,false,false,
		false,false,false,
	};
	
	private ToggleButton mCheckBoxs[] = new ToggleButton[mCheckBoxRes.length];
	
	private boolean mLand;
	
	private boolean mPointAll;
	private boolean mCateogtyAll;
	
	SearchOptionView(Activity activity,View subView){
		mActivity = activity;
		mSearchOption = (LinearLayout) subView.findViewById(R.id.search_option);
		for(int i=0;i<mCheckBoxRes.length;i++){
			mCheckBoxs[i] = (ToggleButton) mSearchOption.findViewById(mCheckBoxRes[i]);
			mCheckBoxs[i].setOnClickListener(this);
			mCheckBoxs[i].setChecked(mCheck[i]);
		}
		mSearchOption.findViewById(R.id.option_point_all).setOnClickListener(this);
		mSearchOption.findViewById(R.id.option_cateogry_all).setOnClickListener(this);
	}
	
	public static String getCheckBoxName(int index){
		switch(index){
			case 0:
				return "point_free";
			case 1:
				return "point_not_free";
			case 2:
				return "point_sponsor";
			case 3:
				return "health";
			case 4:
				return "game";
			case 5:
				return "finance";
			case 6:
				return "education";
			case 7:
				return "news";
			case 8:
				return "book";
			case 9:
				return "business";
			case 10:
				return "socialnetwork";
			case 11:
				return "woman";
			case 12:
				return "entertainment";
			case 13:
				return "utility";
			case 14:
				return "region";
			case 15:
				return "kids";
			case 16:
				return "office";
			case 17:
				return "school";
		}
		
		return "";
	}
	
	public void Release() {
		// TODO Auto-generated method stub
		mActivity = null;
		
		mSearchOption = null;
	}
	
	public void setPage(boolean land,int height) {
		// TODO Auto-generated method stub
		mLand = land;
		mSearchOption.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,height));
	}
	
	public void Show(){
		mSearchOption.setVisibility(View.VISIBLE);
		mSearchOption.invalidate();
		for(int i=0;i<mCheckBoxRes.length;i++){
			mCheckBoxs[i].setChecked(mCheck[i]);
		}
	}
	
	public void Hide(){
		mSearchOption.setVisibility(View.GONE);
	}
	
	public int getHeight(){
		return mSearchOption.getHeight();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		boolean check = false;
		switch(v.getId()){
		case R.id.option_point_all:
			if(mPointAll){
				for(int i=0;i<3;i++){
					mCheckBoxs[i].setChecked(false);
					mCheck[i] = false;
				}
				mPointAll = false;
			}else{
				for(int i=0;i<3;i++){
					mCheckBoxs[i].setChecked(true);
					mCheck[i] = true;
				}
				mPointAll = true;
			}
			break;
		case R.id.option_free:
		case R.id.option_pay:
		case R.id.option_sponsor:
			for(int i=0;i<3;i++){
				mCheck[i] = mCheckBoxs[i].isChecked();
				if(!mCheck[i]){
					check = true;
				}
			}
			if(!check) mPointAll = true;
			else mPointAll = false;
			break;
		case R.id.option_cateogry_all:
			if(mCateogtyAll){
				for(int i=3;i<mCheckBoxRes.length;i++){
					mCheckBoxs[i].setChecked(false);
					mCheck[i] = false;
				}
				mCateogtyAll = false;
			}else{
				for(int i=3;i<mCheckBoxRes.length;i++){
					mCheckBoxs[i].setChecked(true);
					mCheck[i] = true;
				}
				mCateogtyAll = true;
			}
			break;
		default:
			for(int i=3;i<mCheckBoxRes.length;i++){
				mCheck[i] = mCheckBoxs[i].isChecked();
				if(!mCheck[i]){
					check = true;
				}
			}
			if(!check) mCateogtyAll = true;
			else mCateogtyAll = false;
			break;
		}
	}
}
