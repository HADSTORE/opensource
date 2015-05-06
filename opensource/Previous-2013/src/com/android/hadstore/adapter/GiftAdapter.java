package com.android.hadstore.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.hadstore.Global;
import com.android.hadstore.R;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.tesk.ImageDownloader;
import com.operation.model.HsApplicationInfo;
import com.operation.model.HsGiftInfo;

public class GiftAdapter  extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    public int Selected;
    ArrayList<?> mList;
    private boolean IsLand;
    private int cnt;
    
    public GiftAdapter(Context c,ArrayList<?> list) {
        mContext = c;
        cnt = list.size();
        mList = list;
    }
    
    public int getCount() {
        return cnt;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	if(convertView==null){
    		convertView = View.inflate(mContext,R.layout.gift_bookmark_item, null);
    		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.15)));
    	}
    	TextView nickname = (TextView) convertView.findViewById(R.id.list_nickname);
    	TextView point = (TextView) convertView.findViewById(R.id.list_point);
    	TextView subject = (TextView) convertView.findViewById(R.id.list_subject);
    	TextView date = (TextView) convertView.findViewById(R.id.list_date);
    	ImageView image = (ImageView) convertView.findViewById(R.id.list_img);
    	
    	HsApplicationInfo info = (HsApplicationInfo) mList.get(position);
    	
    	subject.setText(info.getAppTitle());
    	date.setText(info.getEventTime());
    	point.setText(info.getAppDownPoint()+"p");
    	nickname.setText(info.getUserNickName());
    	ImageDownloader.download(NetInfo.APP_IMAGE+info.getAppTitleIcon(), image,true);
    	
    	return convertView;
    }
    public void Release(){
    
    }
    public void invalidate(){
    	for(int i=0; i<cnt; i++){
    		if(Selected==i){
	    //		mImageViews[i].setAlpha(255);
	    	}else{
	    //		mImageViews[i].setAlpha(100);
	    	}
    	// 	mImageViews[i].invalidate();
       }
    }

}
