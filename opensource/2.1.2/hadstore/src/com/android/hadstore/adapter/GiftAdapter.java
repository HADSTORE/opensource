package com.android.hadstore.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.Html;
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
    private Context mContext;
    private ArrayList<?> mList;
    private View.OnClickListener mListener;
    
    public GiftAdapter(Context c,ArrayList<?> list,View.OnClickListener listener) {
        mContext = c;
        mList = list;
        mListener= listener;
    }
    
    public int getCount() {
        return mList.size();
    }
    
    public void setItem(ArrayList<?> list){
    	mList = list;
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
    		convertView = View.inflate(mContext,R.layout.gift_bookmark_item, null);
    		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.15)));
    	}
    	
    	convertView.setTag(position);
    	
    	TextView nickname = (TextView) convertView.findViewById(R.id.list_nickname);
    	TextView subject = (TextView) convertView.findViewById(R.id.list_subject);
    	TextView date = (TextView) convertView.findViewById(R.id.list_date);
    	TextView apptitle = (TextView) convertView.findViewById(R.id.list_apptitle);
    	ImageView image = (ImageView) convertView.findViewById(R.id.list_img);
    	
    	HsApplicationInfo info = (HsApplicationInfo) mList.get(position);
    	
    	apptitle.setText(info.getAppTitle());
    	subject.setText(Html.fromHtml(info.getAppDesc()));
    	date.setText(info.getEventTime());
    	nickname.setText(info.getUserNickName());
    	ImageDownloader.download(NetInfo.APP_IMAGE+info.getAppTitleIcon(), image,true);
    	
    	convertView.setOnClickListener(mListener);
    	
    	return convertView;
    }
    public void Release(){
    
    }
    public void invalidate(){
    	
    }

}
