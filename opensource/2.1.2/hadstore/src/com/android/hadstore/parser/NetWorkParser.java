package com.android.hadstore.parser;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.android.hadstore.info.NetInfo;


public class NetWorkParser {

	DefaultHttpClient httpClient;
	
	private HashMap<String, String> headers;


	NetWorkParser() {
		httpClient = new DefaultHttpClient();
	}

	public static NetWorkParser getInstance() {
		return new NetWorkParser();
	}

	/**
	 * Post Request 후 값 가져 가져 오기
	 * 
	 * @param uri
	 * @param nameValuePairs
	 * @return xml
	 */
	public String getPostXml(String uri, List<NameValuePair> nameValuePairs) {
		String xml = "";

		// UTF-8
		HttpEntity entity = null;
		if(NetInfo.ENCODING.equals("UTF-8")){
			entity = getUTF8EnCodedEntity(nameValuePairs);
		}
		
		try {
			HttpPost httpPost = new HttpPost(uri);
			httpPost.setEntity(entity);
			//httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			
			// 헤더값 셋팅
			if(headers != null){
				Set<Entry<String, String>> set = headers.entrySet();
				Iterator <Entry<String, String>> iter = set.iterator();
				while(iter.hasNext()){
					Entry<String, String> entry = iter.next();
					httpPost.setHeader(entry.getKey(), entry.getValue());
				}
			}
			
			httpClient = new DefaultHttpClient();
			HttpParams params = httpClient.getParams();
			HttpConnectionParams.setConnectionTimeout(params, 300000);
			HttpConnectionParams.setSoTimeout(params, 300000);
			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);
		} catch (UnknownHostException e){
			xml = "<map><entry><string>status</string><int>904</int></entry></map>";
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			xml = "<map><entry><string>status</string><int>900</int></entry></map>";
			e.printStackTrace();
		} catch (MalformedURLException e) {
			xml = "<map><entry><string>status</string><int>901</int></entry></map>";
			e.printStackTrace();
		} catch (IOException e) {
			xml = "<map><entry><string>status</string><int>902</int></entry></map>";
			e.printStackTrace();
		} catch(Exception e){
			xml = "<map><entry><string>status</string><int>903</int></entry></map>";
			e.printStackTrace();
		}
		return xml;
	}
	
	/**
	 * Get Request 후 값 가져 가져 오기
	 * 
	 * @param uri
	 * @param nameValuePairs
	 * @return xml
	 */
	public String getGetXml(String uri, List<NameValuePair> nameValuePairs) {
		String xml = "";
		
		// UTF-8
		HttpEntity entity = null;
		if(NetInfo.ENCODING.equals("UTF-8")){
			entity = getUTF8EnCodedEntity(nameValuePairs);
		}
		String paramString = URLEncodedUtils.format(nameValuePairs, "utf-8");
		uri = uri + "?" + paramString; 
			
		HttpGet httpGet = new HttpGet(uri);
		try {
			httpClient = new DefaultHttpClient();
		
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			xml = EntityUtils.toString(httpEntity);
		} catch (UnknownHostException e){
			xml = "<map><entry><string>status</string><int>904</int></entry></map>";
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			xml = "<map><entry><string>status</string><int>900</int></entry></map>";
			e.printStackTrace();
		} catch (MalformedURLException e) {
			xml = "<map><entry><string>status</string><int>901</int></entry></map>";
			e.printStackTrace();
		} catch (IOException e) {
			xml = "<map><entry><string>status</string><int>902</int></entry></map>";
			e.printStackTrace();
		} catch(Exception e){
			xml = "<map><entry><string>status</string><int>903</int></entry></map>";
			e.printStackTrace();
		}
		return xml;
	}
	
	/**
	 * 
	 * @param nameValuePairs
	 * @return
	 */
	public HttpEntity getUTF8EnCodedEntity(List<NameValuePair> nameValuePairs){
		AbstractHttpEntity entity = null;
		try {
			entity = new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8);
			// entity = new UrlEncodedFormEntity(nameValuePairs);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		entity.setContentType("application/x-www-form-urlencoded; charset=UTF-8");
		entity.setContentEncoding("UTF-8");
		return entity;
	}
	
	public HashMap<String, String> getHeaders() {
		return headers;
	}
	
	public void setHeaders(HashMap<String, String> headers) {
		this.headers = headers;
	}
}
