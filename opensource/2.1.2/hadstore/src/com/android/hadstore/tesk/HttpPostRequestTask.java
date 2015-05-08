package com.android.hadstore.tesk;

import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import android.util.Log;
import android.widget.Toast;

import com.android.hadstore.info.NetInfo;
import com.android.hadstore.parser.NetWorkParser;
import com.thoughtworks.xstream.XStream;
/**
 * AsyncTask <List<NameValuePair>, Void , HashMap<?, ?>>
 *          <List<NameValuePair> :  post 방식의 params 값이 들어옴
 *          Void : 
 *            
 * @author android
 *
 */
public class HttpPostRequestTask extends AsyncTask <List<NameValuePair>, Void , HashMap<?, ?> > {
	private HttpRequestTaskListener taskListener;

//	private ProgressDialog pd;
	
	private String url;
	
	private Context context;
	
	private long startTime;
	
	private boolean mIsFost;
	
	public HttpPostRequestTask(Context context, String url,boolean isfost){
		this.context = context;
		this.url = url;
		mIsFost = isfost;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		
		startTime = System.currentTimeMillis();
		Log.i("start Task", "start Task : " + url);
		// 다이얼로그 띄우기
//		pd = ProgressDialog.show(context, "Working..", "Calculating Pi", true,
//                false);
		super.onPreExecute();
	}
	
	/**
	 * 파싱하기
	 */
	@Override
	protected HashMap<?, ?> doInBackground(List<NameValuePair>... params) {
		//xml 파일 가져오기
		NetWorkParser netWorkParser = NetWorkParser.getInstance();
		netWorkParser.setHeaders(NetInfo.getHeaders(context));
		Log.i("time request 전", "time request전: "+(startTime-System.currentTimeMillis()));
		String xml = null;
		if(mIsFost)
			xml=netWorkParser.getPostXml(NetInfo.getXmlURI(url), params[0]);
		else
			xml=netWorkParser.getGetXml(NetInfo.getXmlURI(url), params[0]);
		Log.i("time request 후", "time request후: "+(startTime-System.currentTimeMillis()));
		
		// xml 파싱
		XStream xstream = new XStream();
		HashMap<?, ?> map = (HashMap<?, ?>)xstream.fromXML(xml);
		Log.i("time 파싱 후", "time 파싱 후: "+(startTime-System.currentTimeMillis()));
		
		return map;
	}

	@Override
	protected void onPostExecute(HashMap<?, ?> map) {
		// TODO Auto-generated method stub
		// Activity 에서 리스너 실행
		if(taskListener!=null)taskListener.onFinshTask(map, url);
		
		// 다이얼로그 제거
//		pd.dismiss();
		
		Log.i("time", "time : "+(startTime-System.currentTimeMillis()));
		super.onPostExecute(map);
	}
	
	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
//		pd.dismiss();
		super.onCancelled();
	}
		
	public void setOnFinshListener(HttpRequestTaskListener listener){
		taskListener = listener;
	}
}
