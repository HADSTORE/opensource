package com.android.hadstore.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.hadstore.CustomGallery;
import com.android.hadstore.Global;
import com.android.hadstore.R;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.tesk.ImageDownloader;
import com.operation.model.HsCommentHistory;

public class AppDetailAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<String> mList = new ArrayList<String>();
    
    public AppDetailAdapter(Context c) {
        mContext = c;
    }
    
    public int getCount() {
        return mList.size();
    }
    
    public void AddItem(List<String> list){
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
    		convertView = View.inflate(mContext,R.layout.horizontal_item, null);
    		convertView.setLayoutParams(new CustomGallery.LayoutParams(LayoutParams.WRAP_CONTENT,(int) (Global.DISPLAYHEIGHT*0.358)));
    	}
    	ImageView img = (ImageView) convertView.findViewById(R.id.imageview);
    	//img.setLayoutParams(new LinearLayout.LayoutParams((int)(Global.DISPLAYWIDTH*0.356),(int) (Global.DISPLAYHEIGHT*0.358)));
    	img.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,(int) (Global.DISPLAYHEIGHT*0.358)));
    	int magin = (int)(Global.DISPLAYWIDTH*0.016);
    	img.setPadding(magin, 0, magin, 0);
    	String uri = (String) mList.get(position);
    	ImageDownloader.download2(NetInfo.APP_IMAGE+uri, img);
    	return convertView;
    }
    public void Release(){
    
    }
    public void invalidate(){
    	
    }
}