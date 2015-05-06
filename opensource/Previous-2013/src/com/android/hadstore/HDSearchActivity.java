package com.android.hadstore;

import java.util.ArrayList;

import com.android.hadstore.R;
import com.android.hadstore.util.DatabaseUtil;
import com.android.hadstore.util.DatabaseUtil.DbData;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class HDSearchActivity extends Activity {
	
	private ArrayList<DbData> mList = new ArrayList<DbData>();
	
	private ListView mListView;
	
	private SearchListAdapter mAdapter;
	
	private DatabaseUtil mDbUtil;
	
	private EditText mEditText;
	
	OnClickListener mTextClick = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
//			if(v instanceof TextView){
				DbData data = (DbData) v.getTag();
				mDbUtil.update(data);
				Bundle extra = new Bundle();
				Intent intent = new Intent(); 
				extra.putString("search", data.getName());
				intent.putExtras(extra);
				setResult(RESULT_OK, intent);
				finish();
/*			}else{
				if(mEditText.length()>0){
					DbData data = mDbUtil.CheckSelect(mEditText.getText().toString());
					if(data==null){
						mDbUtil.insert(mEditText.getText().toString());
					}else{
						mDbUtil.update(data);
					}
					Bundle extra = new Bundle();
					Intent intent = new Intent(); 
					extra.putString("search", mEditText.getText().toString());
					intent.putExtras(extra);
					setResult(RESULT_OK, intent);
					finish();
				}
			}*/
		}
	};
	
	View.OnKeyListener mSearch = new View.OnKeyListener(){

		@Override
		public boolean onKey(View arg0, int keyCode, KeyEvent arg2) {
			// TODO Auto-generated method stub
			
			if (keyCode == KeyEvent.KEYCODE_ENTER) {
				if(arg2.getAction() == KeyEvent.ACTION_UP){
					
			     }
				return true;
			}
			return false;
		}
		
	};
	
	OnClickListener mCancelClick = new OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
	};
	
	TextWatcher mTextWatcher = new TextWatcher() {
		  //해당 리스너를 등록한 EditText 에 문자열이 변경되었을때 호출되는 메소드
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		   //입력한 값 읽어오기
			if(s.length()>0){
				mList = mDbUtil.Select(s.toString());
			}else{
				mList = mDbUtil.getAllData();
			}
			if(mList.size()>0){
				if(mAdapter==null){
					mAdapter = new SearchListAdapter(HDSearchActivity.this,(ArrayList<DbData>)mList.clone());
					mListView.setAdapter(mAdapter);
				}else{
					mAdapter.ListInit();
					mAdapter.addFileList(mList);
				}
			}else{
				if(mAdapter!=null) mAdapter.ListInit();
			}
		}

		@Override
		public void afterTextChanged(Editable arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
				int arg3) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_search);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        mDbUtil = new DatabaseUtil(this);
        mEditText = (EditText) findViewById(R.id.search_edit);
        mEditText.addTextChangedListener(mTextWatcher);
        mEditText.requestFocus();
        new Handler().postDelayed(new Runnable(){
            public void run(){
             InputMethodManager aaa = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
             aaa.showSoftInput(mEditText,InputMethodManager.SHOW_IMPLICIT);
 	        mEditText.setOnEditorActionListener(new OnEditorActionListener() {
 				@Override
 				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
 					// TODO Auto-generated method stub
 					
 					 switch(actionId) {
 		                case EditorInfo.IME_ACTION_SEARCH:
 		                	if(mEditText.length()>0){
 								DbData data = mDbUtil.CheckSelect(mEditText.getText().toString());
 								Log.i("HDSearchActivity", "CheckSelect  "+data);
 								if(data==null){
 									mDbUtil.insert(mEditText.getText().toString());
 								}else{
 									mDbUtil.update(data);
 								}
 								Bundle extra = new Bundle();
 								Intent intent = new Intent(); 
 								extra.putString("search", mEditText.getText().toString());
 								intent.putExtras(extra);
 								setResult(RESULT_OK, intent);
 								finish();
 								mEditText.setText("");
 							}else{
 								Bundle extra = new Bundle();
 								Intent intent = new Intent(); 
 								extra.putString("search", "");
 								intent.putExtras(extra);
 								setResult(RESULT_OK, intent);
 								finish();
 							}
 		                	return true;
 		                }
 					
 					return false;
 				}
 			});
            }}, 100 );
       
        mListView = (ListView) findViewById(R.id.search_list);
        findViewById(R.id.cancel_btn).setOnClickListener(mCancelClick);
        View searchbody = findViewById(R.id.search_body);
        searchbody.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.075)));
        mList = mDbUtil.getAllData();
        if(mList.size()>0){
	        mAdapter = new SearchListAdapter(HDSearchActivity.this,(ArrayList<DbData>)mList.clone());
			mListView.setAdapter(mAdapter);
        }
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
		mDbUtil.close();
		mDbUtil = null;
	}
	
	public class SearchListAdapter extends BaseAdapter{
        private Context mContext;
        
        private int Selected;
        private int cnt;
        private ArrayList<DbData> mList;
        public SearchListAdapter(Context c,ArrayList<DbData> list) {
            mContext = c;
            mList = list;
        }
        
        public void ListInit(){
        	mList.clear();
        	((HDSearchActivity)mContext).runOnUiThread(new Runnable() {
                public void run() {
                    notifyDataSetChanged();
                }   
            });
		}
        
        public int getCount() {
            return mList.size();
        }
        
        public void addFileList(ArrayList<DbData> list){
        	for(int i=0; i<list.size(); i++){
    			mList.add(list.get(i));
    			
    		}
        	((HDSearchActivity)mContext).runOnUiThread(new Runnable() {
                public void run() {
                    notifyDataSetChanged();
                }   
            });
        }

        public DbData getItem(int position) {
            return mList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
        	TextView text = new TextView(mContext);
			text.setGravity(Gravity.CENTER_VERTICAL|Gravity.LEFT);
			text.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.06)));
        	text.setTag(mList.get(position));
        	text.setText(mList.get(position).getName());
    		text.setOnClickListener(mTextClick);
    		return text;
        }
    }
}
