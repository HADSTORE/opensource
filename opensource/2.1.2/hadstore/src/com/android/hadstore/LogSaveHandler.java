package com.android.hadstore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;

import com.android.hadstore.info.AppInfo;
import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.ParamMaker;
import com.android.hadstore.tesk.HttpPostRequestTask;
import com.android.hadstore.tesk.HttpRequestTaskListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

public class LogSaveHandler implements UncaughtExceptionHandler,HttpRequestTaskListener {
	
	public static final String TAG = "LogSaveHandler";
	
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	private static LogSaveHandler INSTANCE = new LogSaveHandler();
	private Context mContext;
	private Map<String, String> infos = new HashMap<String, String>();

	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

	private LogSaveHandler() {
	}

	public static LogSaveHandler getInstance() {
		return INSTANCE;
	}

	public void init(Context context) {
		mContext = context;
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(this);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if (!handleException(ex) && mDefaultHandler != null) {
			mDefaultHandler.uncaughtException(thread, ex);
		} else {
			saveCrashInfo2File(ex);
		}
	}

	private boolean handleException(Throwable ex) {
		if (ex == null) {
			return false;
		}
		new Thread() {
			@Override
			public void run() {
				Looper.prepare();
				Looper.loop();
			}
		}.start();
		collectDeviceInfo(mContext);
		return true;
	}

	public void collectDeviceInfo(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null" : pi.versionName;
				String versionCode = pi.versionCode + "";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		} catch (NameNotFoundException e) {
			Log.e(TAG, "an error occured when collect package info", e);
		}
		Field[] fields = Build.class.getDeclaredFields();
		for (Field field : fields) {
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch (Exception e) {
				Log.e(TAG, "an error occured when collect crash info", e);
			}
		}
	}

	private String saveCrashInfo2File(Throwable ex) {
		
		StringBuffer sb = new StringBuffer();
		for (Map.Entry<String, String> entry : infos.entrySet()) {
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key + "=" + value + "\n");
		}
		
		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		
		
		HttpPostRequestTask task = new HttpPostRequestTask(mContext, NetInfo.ERROR,true);
		task.setOnFinshListener(this);
		
		// 파람 만들기
		ParamMaker params= new ParamMaker();
		
		SharedPreferences prefs = mContext.getApplicationContext().getSharedPreferences(
				AppInfo.USER_INFO, 0);
		String id = prefs.getString(AppInfo.USER_ID, "");

		
		if(id.equals("")){
			params.add("userId", "login not");
		}else{
			params.add("userId", id);
		}
		params.add("logs", sb.toString());
		
		// Request 실행
		List<NameValuePair> realParams = params.getParams();
		task.execute(realParams);
		
		/*Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while (cause != null) {
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();
		String result = writer.toString();
		sb.append(result);
		try {
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "hadstore-" + time + "-" + timestamp + ".log";
			if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
				String path = "/sdcard/.hadstore/";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		} catch (Exception e) {
			Log.e(TAG, "an error occured while writing file...", e);
		}*/
		return null;
	}

	@Override
	public void onFinshTask(HashMap<?, ?> map, String url) {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Log.e(TAG, "error : ", e);
		}
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(1);
	}
}
