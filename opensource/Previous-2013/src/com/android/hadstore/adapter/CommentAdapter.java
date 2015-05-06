package com.android.hadstore.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.hadstore.R;
import com.operation.model.HsCommentHistory;

public class CommentAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    public int Selected;
    ArrayList<HsCommentHistory> mList = new ArrayList<HsCommentHistory>();
    
    public CommentAdapter(Context c) {
        mContext = c;
    }
    
    public int getCount() {
        return mList.size();
    }
    
    public void AddItem(List<HsCommentHistory> list){
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
    		convertView = View.inflate(mContext,R.layout.comment_item, null);
    	}
    	
    	TextView commet = (TextView) convertView.findViewById(R.id.comment_item_comment);
    	TextView nickname = (TextView) convertView.findViewById(R.id.comment_item_nickname);
    	TextView time = (TextView) convertView.findViewById(R.id.comment_item_time);
    	
    	HsCommentHistory info = (HsCommentHistory) mList.get(position);
    	commet.setText(info.getAppComment());
    	nickname.setText(info.getUserNickName());
    	time.setText(info.getEventTime());
    	return convertView;
    }
    public void Release(){
    
    }
    public void invalidate(){
    	
    }
}
