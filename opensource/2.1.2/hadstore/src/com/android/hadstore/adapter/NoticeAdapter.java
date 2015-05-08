package com.android.hadstore.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.hadstore.Global;
import com.android.hadstore.R;
import com.operation.model.HsBoardInfo;

public class NoticeAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    public int Selected;
    ArrayList<HsBoardInfo> mList = new ArrayList<HsBoardInfo>();
    
    public NoticeAdapter(Context c) {
        mContext = c;
    }
    
    public int getCount() {
        return mList.size();
    }
    
    public void AddItem(List<HsBoardInfo> list){
    	int start = 0;
    	for(int i=start;i<list.size();i++){
    		mList.add(list.get(i));
    	}
    	notifyDataSetChanged();
    }
    
    public void Clear(){
    	mList.clear();
    	notifyDataSetChanged();
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	
    	if(convertView==null){
    		convertView = View.inflate(mContext,R.layout.notice_item, null);
    		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1375)));
    	}
    	
    	int rem = position%2;
    	
    	if(rem==1){
    		convertView.setBackgroundColor(0xfffafafa);
    	}else{
    		convertView.setBackgroundColor(0xffc8c8c8);
    	}
    	
    	TextView num = (TextView) convertView.findViewById(R.id.notice_num);
    	TextView title = (TextView) convertView.findViewById(R.id.notice_title);
    	TextView date = (TextView) convertView.findViewById(R.id.notice_date);
    	
    	HsBoardInfo info = (HsBoardInfo) mList.get(position);
    	
    	num.setText(""+info.getBoardSequence());
    	title.setText(info.getBoardTitle());
    	date.setText(info.getBoardCreateTime());
    	return convertView;
    }
    public void Release(){
    
    }
    public void invalidate(){
    	
    }
}
