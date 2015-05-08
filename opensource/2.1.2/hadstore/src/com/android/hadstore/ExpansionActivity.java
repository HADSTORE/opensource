package com.android.hadstore;

import com.android.hadstore.info.NetInfo;
import com.android.hadstore.tesk.ImageDownloader;

import android.app.Activity;
import android.os.Bundle;

public class ExpansionActivity extends Activity {
	CustomImgView mImageView;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.expansion_main);
        mImageView = (CustomImgView) findViewById(R.id.imageview);
        
        String uri = getIntent().getStringExtra("uri");
        if(uri!=null){
        	ImageDownloader.download(NetInfo.APP_IMAGE+uri, mImageView,false);
        }
	}
}
