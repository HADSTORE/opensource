package com.android.hadstore.parser;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.hadstore.info.NetInfo;
import com.android.hadstore.util.BitmapUtil;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.util.Log;

public class MutipartParser {
	MutipartParser mMulti;
	//URL설정
	private URL connectURL;
	private String Userseq;
	private final String POST = "POST";
	private final String GET = "GET";
	private String mFileName = "image.jpg";
	private final String BOUNDARY = "0xKhTmLbOuNdArY---This_Is_ThE_BoUnDaRyy---ms";
	private ArrayList<MutiData> mDataList = new ArrayList<MutiData>();
	class MutiData{
		String mName;
		String mValue;
		public MutiData(String name,String value){
			mName = name;
			mValue = value;
		}
	}
	
	public void setFileName(String filename){
		mFileName = filename;
	}
	
	public MutipartParser(){
		 mDataList = new ArrayList<MutiData>();
	}
	public MutipartParser getInstance(){
		if(mMulti==null)mMulti=new MutipartParser();
		return mMulti;
	}
	
	public void setConnetURI(String url, String userseq){
		try {
			connectURL = new URL(url);
			Userseq = userseq;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addData(String name,String value){
		String val = value.replace(" ", "%20");
		try {
			mDataList.add(new MutiData(new String(name.getBytes(),"UTF-8"),val));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean MutipartUpload(Uri path){
		boolean Check = false;
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection)connectURL.openConnection();
		
			//읽기와 쓰기 모두 가능하게 설정
			conn.setDoInput(true);
			conn.setDoOutput(true);
			
			//캐시를 사용하지 않게 설정
			conn.setUseCaches(false); 
	
			//POST타입으로 설정
			conn.setRequestMethod(POST); 
			
			//헤더 설정
			conn.setRequestProperty("user_seq", Userseq);
			conn.setRequestProperty("Connection","Keep-Alive");
			conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+BOUNDARY); 
			
			//Output스트림을 열어
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream()); 
			int size = mDataList.size();
			for(int i=0;i<size;i++){
				MutiData da = mDataList.get(i);
				wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
				wr.writeBytes("Content-Disposition: form-data; name=\""+da.mName+"\"\r\n\r\n" + da.mValue);
				
			}
	
			//버퍼사이즈를 설정하여 buffer할당
			wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
			wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""+mFileName+"\"\r\n");
			wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
			FileInputStream fileInputStream = new FileInputStream(path.getPath());
			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];
	
			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0)
			{
			// Upload file part(s)
				//DataOutputStream dataWrite = new DataOutputStream(conn.getOutputStream());
				wr.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available(); 
				bufferSize = Math.min(bytesAvailable, maxBufferSize); 
				bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
			}
			fileInputStream.close();
			wr.writeBytes("\r\n--" + BOUNDARY + "--\r\n");
			 
			//써진 버퍼를 stream에 출력.  
			wr.flush();
			
			//전송. 결과를 수신.
			BufferedReader rd = null;
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = rd.readLine()) != null) {
				try {
					//Log.e("mgdoo",line);
					JSONObject jobj = new JSONObject(line);
					if(jobj.getInt(NetInfo.STATUS)==0){
						Check = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		mDataList.clear();
		return Check;
	}
	public boolean MutipartUpload(Bitmap bitmap){
		boolean Check = false;
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection)connectURL.openConnection();
		
			//읽기와 쓰기 모두 가능하게 설정
			conn.setDoInput(true);
			conn.setDoOutput(true);
	
			//캐시를 사용하지 않게 설정
			conn.setUseCaches(false); 
	
			//POST타입으로 설정
			conn.setRequestMethod(POST); 
			
			//헤더 설정
			conn.setRequestProperty("user_seq", Userseq);
			conn.setRequestProperty("Connection","Keep-Alive");
			conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+BOUNDARY); 
			
			//Output스트림을 열어
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream()); 
			int size = mDataList.size();
			for(int i=0;i<size;i++){
				MutiData da = mDataList.get(i);
				wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
				wr.writeBytes("Content-Disposition: form-data; name=\""+da.mName+"\"\r\n\r\n");
				wr.writeUTF(da.mValue);
			}
	
			//버퍼사이즈를 설정하여 buffer할당
			wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
			wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""+mFileName+"\"\r\n");
			wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
			byte []buffer = BitmapUtil.bitmapToByteArray(bitmap);
			int maxBufferSize = 1024;
			int cnt = buffer.length/maxBufferSize;
			int mod = buffer.length%maxBufferSize;
			for(int i=0;i<cnt;i++){
				wr.write(buffer, i*1024, maxBufferSize);
			}
			wr.write(buffer, cnt*1024, mod);
			wr.writeBytes("\r\n--" + BOUNDARY + "--\r\n");
			 
			//써진 버퍼를 stream에 출력.  
			wr.flush();
			
			//전송. 결과를 수신.
			BufferedReader rd = null;
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = rd.readLine()) != null) {
				try {
					//Log.e("mgdoo",line);
					JSONObject jobj = new JSONObject(line);
					if(jobj.getInt(NetInfo.STATUS)==0){
						Check = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		mDataList.clear();
		return Check;
	}
	public boolean MutipartUploadMp3(Uri path){
		boolean Check = false;
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection)connectURL.openConnection();
		
			//읽기와 쓰기 모두 가능하게 설정
			conn.setDoInput(true);
			conn.setDoOutput(true);
	
			//캐시를 사용하지 않게 설정
			conn.setUseCaches(false); 
	
			//POST타입으로 설정
			conn.setRequestMethod(POST); 
			
			//헤더 설정
			conn.setRequestProperty("user_seq", Userseq);
			conn.setRequestProperty("Connection","Keep-Alive"); 
			conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+BOUNDARY); 
			
			//Output스트림을 열어
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream()); 
			int size = mDataList.size();
			for(int i=0;i<size;i++){
				MutiData da = mDataList.get(i);
				wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
				wr.writeBytes("Content-Disposition: form-data; name=\""+da.mName+"\"\r\n\r\n" + da.mValue);
				
			}
	
			//버퍼사이즈를 설정하여 buffer할당
			wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
			if(mFileName.equals("image.jpg")){
				wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\"voicerecoder.3gpp\"\r\n");
			}else{
				wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""+mFileName+"\"\r\n");
			}
			wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
			FileInputStream fileInputStream = new FileInputStream(path.getPath());
			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];
	
			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0)
			{
			// Upload file part(s)
				//DataOutputStream dataWrite = new DataOutputStream(conn.getOutputStream());
				wr.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available(); 
				bufferSize = Math.min(bytesAvailable, maxBufferSize); 
				bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
			} 
			fileInputStream.close();
			wr.writeBytes("\r\n--" + BOUNDARY + "--\r\n");
			 
			//써진 버퍼를 stream에 출력.  
			wr.flush();
			
			//전송. 결과를 수신.
			BufferedReader rd = null;
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = rd.readLine()) != null) {
				try {
					//Log.e("mgdoo",line);
					JSONObject jobj = new JSONObject(line);
					if(jobj.getInt(NetInfo.STATUS)==0){
						Check = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		mDataList.clear();
		return Check;
	}
	public boolean MutipartUpload(String path){
		boolean Check = false;
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection)connectURL.openConnection();
		
			//읽기와 쓰기 모두 가능하게 설정
			conn.setDoInput(true);
			conn.setDoOutput(true);
	
			//캐시를 사용하지 않게 설정
			conn.setUseCaches(false); 
	
			//POST타입으로 설정
			conn.setRequestMethod(POST); 
			
			//헤더 설정
			conn.setRequestProperty("user_seq", Userseq);
			conn.setRequestProperty("Connection","Keep-Alive"); 
			conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+BOUNDARY); 
			
			//Output스트림을 열어
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream()); 
			int size = mDataList.size();
			for(int i=0;i<size;i++){
				MutiData da = mDataList.get(i);
				wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
				wr.writeBytes("Content-Disposition: form-data; name=\""+da.mName+"\"\r\n\r\n" + da.mValue);
				
			}
	
			//버퍼사이즈를 설정하여 buffer할당
			wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
			wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""+mFileName+"\"\r\n");
			wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
			FileInputStream fileInputStream = new FileInputStream(path);
			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];
	
			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			while (bytesRead > 0)
			{
			// Upload file part(s)
				//DataOutputStream dataWrite = new DataOutputStream(conn.getOutputStream());
				wr.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available(); 
				bufferSize = Math.min(bytesAvailable, maxBufferSize); 
				bytesRead = fileInputStream.read(buffer, 0, bufferSize); 
			} 
			fileInputStream.close();
			wr.writeBytes("\r\n--" + BOUNDARY + "--\r\n");
			 
			//써진 버퍼를 stream에 출력.  
			wr.flush();
			
			//전송. 결과를 수신.
			BufferedReader rd = null;
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = rd.readLine()) != null) {
				try {
					//Log.e("mgdoo",line);
					JSONObject jobj = new JSONObject(line);
					if(jobj.getInt(NetInfo.STATUS)==0){
						Check = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		mDataList.clear();
		return Check;
	}
	public boolean MutipartUpload(String path,Bitmap bitmap){
		boolean Check = false;
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection)connectURL.openConnection();
		
			//읽기와 쓰기 모두 가능하게 설정
			conn.setDoInput(true);
			conn.setDoOutput(true);
	
			//캐시를 사용하지 않게 설정
			conn.setUseCaches(false); 
	
			//POST타입으로 설정
			conn.setRequestMethod(POST); 
			
			//헤더 설정
			conn.setRequestProperty("user_seq", Userseq);
			conn.setRequestProperty("Connection","Keep-Alive");
			conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+BOUNDARY); 
			
			//Output스트림을 열어
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream()); 
			int size = mDataList.size();
			for(int i=0;i<size;i++){
				MutiData da = mDataList.get(i);
				wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
				wr.writeBytes("Content-Disposition: form-data; name=\""+da.mName+"\"\r\n\r\n" + da.mValue);
				
			}
	
			//버퍼사이즈를 설정하여 buffer할당
			wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
			wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""+mFileName+"\"\r\n");
			wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
			byte []buffer = BitmapUtil.bitmapToByteArray(bitmap);
			int maxBufferSize = 1024;
			int cnt = buffer.length/maxBufferSize;
			int mod = buffer.length%maxBufferSize;
			for(int i=0;i<cnt;i++){
				wr.write(buffer, i*1024, maxBufferSize);
			}
			wr.write(buffer, cnt*1024, mod);
			wr.writeBytes("\r\n--" + BOUNDARY + "--\r\n");
			 
			//써진 버퍼를 stream에 출력.  
			wr.flush();
			
			//전송. 결과를 수신.
			BufferedReader rd = null;
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = rd.readLine()) != null) {
				try {
					//Log.e("mgdoo",line);
					JSONObject jobj = new JSONObject(line);
					if(jobj.getInt(NetInfo.STATUS)==0){
						Check = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		mDataList.clear();
		return Check;
	}
	public boolean MutipartUserCreate(Bitmap bitmap){
		boolean Check = false;
		HttpURLConnection conn;
		try {
			conn = (HttpURLConnection)connectURL.openConnection();
		
			//읽기와 쓰기 모두 가능하게 설정
			conn.setDoInput(true);
			conn.setDoOutput(true);
	
			//캐시를 사용하지 않게 설정
			conn.setUseCaches(false); 
	
			//POST타입으로 설정
			conn.setRequestMethod(POST); 
			
			//헤더 설정
			conn.setRequestProperty("Connection","Keep-Alive");
			conn.setRequestProperty("Content-Type","multipart/form-data;boundary="+BOUNDARY); 
			
			//Output스트림을 열어
			DataOutputStream wr = new DataOutputStream(conn.getOutputStream()); 
			int size = mDataList.size();
			for(int i=0;i<size;i++){
				MutiData da = mDataList.get(i);
				wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
				wr.writeBytes("Content-Disposition: form-data; name=\""+da.mName+"\"\r\n\r\n" + da.mValue);
				
			}
	
			//버퍼사이즈를 설정하여 buffer할당
			wr.writeBytes("\r\n--" + BOUNDARY + "\r\n");
			wr.writeBytes("Content-Disposition: form-data; name=\"file\"; filename=\""+mFileName+"\"\r\n");
			wr.writeBytes("Content-Type: application/octet-stream\r\n\r\n");
			byte []buffer = BitmapUtil.bitmapToByteArray(bitmap);
			int maxBufferSize = 1024;
			int cnt = buffer.length/maxBufferSize;
			int mod = buffer.length%maxBufferSize;
			for(int i=0;i<cnt;i++){
				wr.write(buffer, i*1024, maxBufferSize);
			}
			wr.write(buffer, cnt*1024, mod);
			wr.writeBytes("\r\n--" + BOUNDARY + "--\r\n");
			 
			//써진 버퍼를 stream에 출력.  
			wr.flush();
			
			//전송. 결과를 수신.
			BufferedReader rd = null;
			rd = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = rd.readLine()) != null) {
				try {
					//Log.e("mgdoo",line);
					JSONObject jobj = new JSONObject(line);
					if(jobj.getInt(NetInfo.STATUS)==0){
						Check = true;
					}
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		mDataList.clear();
		return Check;
	}
}
