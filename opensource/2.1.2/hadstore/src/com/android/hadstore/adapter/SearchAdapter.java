package com.android.hadstore.adapter;

import java.util.ArrayList;

import com.android.hadstore.Global;

import com.android.hadstore.R;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.tesk.ImageDownloader;
import com.operation.model.HsApplicationInfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class SearchAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
 //   private ImageView[] mImageViews;
    public int Selected;
    ArrayList<HsApplicationInfo> mList = new ArrayList<HsApplicationInfo>();
    private boolean IsLand;
    
    public SearchAdapter(Context c ,boolean island) {
    	IsLand = island;
        mContext = c;
    }
    
    public int getCount() {
        return mList.size();
    }
    
    public void AddItem(ArrayList<HsApplicationInfo> list){
    	int start = 0;
    	if(mList.size()>0){
    		start = mList.size();
    	}
    	
    	if(mList.size()==list.size()) return;
    	
    	for(int i=start;i<list.size();i++){
    		mList.add(list.get(i));
    	}
    	notifyDataSetChanged();
    }
    
    public void Clear(){
    	mList.clear();
    	notifyDataSetChanged();
    }
    
    public void setLand(boolean land){
    	IsLand = land;
    }

    public Object getItem(int position) {
        return mList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	if(convertView==null){
	    	if(IsLand){
	    		convertView = View.inflate(mContext,R.layout.search_list_item_land, null);
	    		//convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.1875)));
	    	}else{
	    		convertView = View.inflate(mContext,R.layout.search_list_item, null);	
	    	}
	    	convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.158)));
	    	
    	}
    	
    	TextView nickname = (TextView) convertView.findViewById(R.id.list_nickname);
    	TextView point = (TextView) convertView.findViewById(R.id.list_point);
    	TextView subject = (TextView) convertView.findViewById(R.id.list_subject);
    	TextView totalscore = (TextView) convertView.findViewById(R.id.list_totalscore);
    	ImageView image = (ImageView) convertView.findViewById(R.id.list_img);
    	
    	TextView index = (TextView) convertView.findViewById(R.id.search_index_text);
    	index.setText(""+(position+1));
    	
    	HsApplicationInfo info = (HsApplicationInfo) mList.get(position);
    	
    	subject.setText(info.getAppTitle());
    	totalscore.setText("ÃÑÁ¡¼ö:"+info.getAppTotalWeight());
    	point.setText(info.getAppDownPoint()+"p");
    	nickname.setText(info.getUserNickName());
    	image.setBackgroundResource(R.drawable.list_defimg);
    	ImageDownloader.download(NetInfo.APP_IMAGE+info.getAppTitleIcon(), image,true);
    	
    	if(info.getSponsorFlag().equals("Y")){
    		convertView.findViewById(R.id.list_notify).setVisibility(View.VISIBLE);
    	}else{
    		convertView.findViewById(R.id.list_notify).setVisibility(View.INVISIBLE);
    	}
    	
    	if(info.getInAppFlag().equals("T")){
    		convertView.findViewById(R.id.list_inapp).setVisibility(View.VISIBLE);
    	}else{
    		convertView.findViewById(R.id.list_inapp).setVisibility(View.INVISIBLE);
    	}
    	return convertView;
    }
    public void Release(){
    
    }
    public void invalidate(){
    	
    }
}