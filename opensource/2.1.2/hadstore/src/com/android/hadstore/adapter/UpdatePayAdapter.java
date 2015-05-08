package com.android.hadstore.adapter;

import java.util.ArrayList;

import com.android.hadstore.Global;
import com.android.hadstore.R;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.tesk.ImageDownloader;
import com.operation.model.HsApplicationInfo;
import com.operation.model.HsDownHistory;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class UpdatePayAdapter extends BaseAdapter{
	private Context mContext;
	private ArrayList<?> mList;
	private View.OnClickListener mListener;
	    
	public UpdatePayAdapter(Context c,ArrayList<?> list,View.OnClickListener listener) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView==null){
    		convertView = View.inflate(mContext,R.layout.update_pay_item, null);
    		convertView.setLayoutParams(new ListView.LayoutParams(LayoutParams.FILL_PARENT,(int) (Global.DISPLAYHEIGHT*0.15)));
    	}
		
		HsDownHistory update = (HsDownHistory) mList.get(position);
		
		convertView.setTag(position);
		
		TextView nickname = (TextView) convertView.findViewById(R.id.list_nickname);
    	TextView apptitle = (TextView) convertView.findViewById(R.id.list_apptitle);
    	TextView appversion = (TextView) convertView.findViewById(R.id.list_version);
    	ImageView image = (ImageView) convertView.findViewById(R.id.list_img);
    	
    	appversion.setText(update.getAppVersion());
    	apptitle.setText(update.getAppTitle());
    	nickname.setText(update.getUserNickName());
    	
    	ImageDownloader.download(NetInfo.APP_IMAGE+update.getAppTitleIcon(), image,true);
    	
    	convertView.setOnClickListener(mListener);
		return convertView;
	}

}
