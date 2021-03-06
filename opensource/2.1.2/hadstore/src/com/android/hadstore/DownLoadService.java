package com.android.hadstore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import com.android.hadstore.util.DateUtil;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class DownLoadService extends Service {
	
	public static final int NODIFICATION_DOWN_ID = 0;
	
	public static final int NODIFICATION_INSTALL_ID = 1;
	
	public static final int SERVICE_ID = 0x101104;
	
	public static final int BYTES_BUFFER_SIZE = 32 * 1024;
	
	public static final int NON_STATE = 0;
	
	public static final int DOWN_STATE = 1;
	
	public static final int WATE_STATE = 2;
	
	public static final int NOMAL = 0;
	
	public static final int DOWN= 1;
	
	public static final int INSTALL = 2;

	private NotificationManager mNotificationManager;
	
	private static boolean isRunning = false;
	
	private static int mNotiCnt = 1; 
	
	private static int DownloadingState = NOMAL;
	
	private AsyncDownloadTask mDownTask = null;
	
	private final IBinder mBinder = new DownLoadBinder();
	
	private ArrayList<DownLoadData> mDownList = new ArrayList<DownLoadData>();
	
	private HashMap<DownLoadData,Integer> mDeleteList = new HashMap<DownLoadData,Integer>();
	
	private Object mLock = new Object();
	
	public static final Object mLockDelete = new Object();
	
	private int mDuration;
	
	private int mCurrentPosition;
	
	private NotificationCompat.Builder mNoti;
	
	private boolean mIsNetwork;
	
	private Handler mHandler = new Handler();
	
	public String strFilePath = Environment.getExternalStorageDirectory()+"/.hadstore";
	
	private static DownLoadService mService;
	
	private BroadcastReceiver mReciver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			final String action = intent.getAction();
			if (Intent.ACTION_PACKAGE_CHANGED.equals(action) || Intent.ACTION_PACKAGE_ADDED.equals(action)) {
                final String pkgNm = intent.getData().getSchemeSpecificPart();

                if (pkgNm == null || pkgNm.length() == 0) {
                    return;
                }
                synchronized (mLockDelete){
                	if(mDeleteList.size()>0){
                		Set<DownLoadData> keys =  mDeleteList.keySet();
                		boolean mDelete = false;
                		DownLoadData deletekey = null;
                		int index = 0;
            	        for (DownLoadData key : keys) {
            	        	index = mDeleteList.get(key);
            	        	if(key.mPakageName.equals(pkgNm)){
            	        		mDelete = true;
            	        		deletekey = key; 
            	        		break;
            	        	}
            	        }
            	        if(mDelete){
	            	        new File(strFilePath,deletekey.mFileName).delete();
	    	        		mNotificationManager.cancel(NODIFICATION_INSTALL_ID+index);
	    	        		ShowInstallStateNotification(deletekey," 설치 완료",NODIFICATION_INSTALL_ID+index);
	    	        		mDeleteList.remove(deletekey);
            	        }
                    }
    			}
			}
		}
	};
	
	public class DownLoadData
    {
	   public String mFileName;
	   public String mPakageName;
	   public String mTitle;
	   public String mUri;
	   public String mDate;
    }

	public class DownLoadBinder extends Binder
    {
	   DownLoadService getService()
	   {
	       return DownLoadService.this;
	   }
    }
	
	private class AsyncDownloadTask extends AsyncTask<Void, Integer, Boolean>
	{
		DownLoadData mData = null;
		boolean mCancel = false;
		
		public AsyncDownloadTask(DownLoadData data){
			mData = data;
		}
		
		public String getDownName(){
			return mData.mPakageName;
		}
		
		public DownLoadData getData(){
			return mData;
		}
		
		public void CustomCancel(){
			mCancel = true;
		}
		
		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			DownloadingState = DOWN;
	        ShowDownNotification(0);
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Boolean result = false;
			
			try {
                //connecting to url
                URL u = new URL(mData.mUri);
                HttpURLConnection conn = (HttpURLConnection) u.openConnection();
                conn.setConnectTimeout(60000);
                conn.setReadTimeout(60000);
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
                conn.connect();
                
                //lenghtOfFile is used for calculating download progress
                int lenghtOfFile = conn.getContentLength();
                
                setDuration(lenghtOfFile);
                File dir = new File(strFilePath);
                if(!dir.isDirectory()){
                	dir.mkdirs();
                }
                File file = new File(strFilePath,mData.mFileName);
                if(!file.exists()){
					file.createNewFile();
				}
                //this is where the file will be seen after the download
                FileOutputStream f = new FileOutputStream(file);
                //file input is from the url
                
                InputStream in = conn.getInputStream();

                //here's the download code
                byte[] buffer = new byte[1024];
                int len1 = 0;
                long total = 0;
                
                while ((len1 = in.read(buffer)) > 0) {
                    total += len1; //total = total + len1
                    int pos = (int)((total*100)/lenghtOfFile);
                    if(mCancel){
                    	return false;
                    }
                    if(mCurrentPosition!=pos){
                    	mCurrentPosition = pos;
            			mHandler.post(new Runnable() {
            				@Override
            				public void run() {
            					// TODO Auto-generated method stub
            					mNoti.setProgress(100, mCurrentPosition, false);
            					
            					mNotificationManager.notify(NODIFICATION_DOWN_ID, mNoti.build());
            				}
            			});
                    }
                    f.write(buffer, 0, len1);
                }
                f.close();
                result = true;
            } catch (Exception e) {
            	e.printStackTrace();
            	mIsNetwork = true;
            }
			
			return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			
			super.onPostExecute(result);
			
			mNotificationManager.cancel(NODIFICATION_DOWN_ID);
			
			DownloadingState = NOMAL;
			
			mCurrentPosition = 0;
			
			if(!result.booleanValue() || mCancel){
				if(mCancel){
					ShowInstallStateNotification(mData,"다운로드 취소",true);
				}else
					ShowInstallStateNotification(mData,"다운로드 실패",true);
				
				new File(strFilePath,mData.mFileName).delete();
				synchronized (mLock) {
					mDownList.remove(mDownTask.getData());
				}
				
				mDownTask = null;
			}else{
				File apkfile = new File(strFilePath,mData.mFileName);
				Uri apkuri = Uri.fromFile(apkfile);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK ); 
				intent.setDataAndType(apkuri, "application/vnd.android.package-archive");
				startActivity(intent);
				synchronized (mLockDelete){
					if(!mDeleteList.containsKey(mDownTask.getData()))
								mDeleteList.put(mDownTask.getData(),mNotiCnt);
				}
				ShowInstallStateNotification(mData,"다운로드 완료",true);
			}
			synchronized (mLock) {
				if(mDownTask!=null)
					mDownList.remove(mDownTask.getData());
				
				if(mDownList.size()>0){
					mDownTask = new AsyncDownloadTask(mDownList.get(0));
					mDownTask.execute();
				}else{
					mDownTask = null;
				}
			}
		}
	}
	
	public void SetNetworkState(boolean network){
		mIsNetwork = network;
	}
	
	public boolean CheckNetwork(){
		return mIsNetwork;
	}
	
	public void ShowDownNotification(int update){
		
		Intent intent = new Intent();
		PendingIntent content  = PendingIntent.getActivity(this, 0, intent, 0);
		mNoti = new NotificationCompat.Builder(this).setAutoCancel(false)
		.setContentTitle(mDownTask.getData().mTitle)
		.setContentText(mDownTask.getData().mDate)
		.setContentIntent(content)
		.setProgress(100, update, false)
		.setSmallIcon(R.drawable.defaultimage);

		mNotificationManager.notify(NODIFICATION_DOWN_ID, mNoti.build());
	}
	
	public void ShowInstallStateNotification(DownLoadData data,String msg,boolean cencel){
		Notification noti = new Notification(R.drawable.defaultimage,data.mTitle,System.currentTimeMillis());
		noti.flags = noti.flags|Notification.FLAG_AUTO_CANCEL;
		if(!cencel){
			noti.flags = noti.flags|Notification.FLAG_ONGOING_EVENT;
		}
		Intent intent = new Intent();
		PendingIntent content  = PendingIntent.getActivity(this, 0, intent, 0);
		noti.setLatestEventInfo(this, data.mTitle, msg, content);
		mNotificationManager.notify(NODIFICATION_INSTALL_ID+mNotiCnt, noti);
		mNotiCnt++;
		if(mNotiCnt>1000){
			mNotiCnt = 1;
		}
	}
	
	public void ShowInstallStateNotification(DownLoadData data,String msg,int index){
		Notification noti = new Notification(R.drawable.defaultimage,data.mTitle,System.currentTimeMillis());
		noti.flags = noti.flags|Notification.FLAG_AUTO_CANCEL;
		Intent intent = new Intent();
		PendingIntent content  = PendingIntent.getActivity(this, 0, intent, 0);
		noti.setLatestEventInfo(this, data.mTitle, msg, content);
		mNotificationManager.notify(NODIFICATION_INSTALL_ID+index, noti);
	}
	
	public int getDownState(String name){
		if(mDownTask==null){
			return NON_STATE;
		}
		synchronized (mLock) {
			int size = mDownList.size();
			for(int i=0;i<size;i++){
				if(mDownList.get(i).mPakageName.equals(name)){
					if(mDownTask.getDownName().equals(name)){
						return DOWN_STATE;
					}else{
						return WATE_STATE;
					}
				}
			}
		}
		return NON_STATE;
	}
	
	public void DownCencel(String pakegename){
		if(mDownTask!=null){
			if(mDownTask.getData().mPakageName.equals(pakegename)){
				mDownTask.CustomCancel();
			}
		}
	}
	
	public int getDownState(){
		return DownloadingState;
	}
	
	 public int getDuration() {
	  return mDuration;
	 }

	 public void setDuration(int duration) {
		 mDuration = duration;
	 }

	 public int getCurrentPosition() {
	  return mCurrentPosition;
	 }

	 public void setCurrentPosition(int currentPosition) {
		 mCurrentPosition = currentPosition;
	 }
	
	public static boolean isRunning()
    {
      return isRunning;
    }
	
	public void setAddDownload(String title,String filename,String pakegename,String uri){
		DownLoadData data = new DownLoadData();
		data.mFileName = filename;
		data.mPakageName = pakegename;
		data.mTitle = title;
		data.mUri = uri.replace(" ", "%20");
		data.mDate = DateUtil.getNotificationTime(new Date());
		synchronized (mLock) {
			mDownList.add(data);
			if(DownloadingState==NOMAL){
				mDownTask = new AsyncDownloadTask(mDownList.get(0));
				mDownTask.execute();
			}
		}
		
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		// TODO Auto-generated method stub
		super.onStart(intent, startId);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub	
		if(mDownTask != null)
	    {
	      if(!mDownTask.isCancelled())
	    	  mDownTask.CustomCancel();
	    }
		unregisterReceiver(mReciver);
		mService = null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		mService = this;
		IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");
		registerReceiver(mReciver, filter);
		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		return super.onUnbind(intent);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}
	
	public static DownLoadService getService(){
		return mService;
	}
}
