package com.android.hadstore.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class ParamMaker {
	private List<NameValuePair> nameValuePairs;
	
	public ParamMaker(){
		nameValuePairs = new ArrayList<NameValuePair>();
	}
	
	public void add(String name, String value){
		nameValuePairs.add(new BasicNameValuePair(name, value));
	}
	
	public List<NameValuePair> getParams(){
		return nameValuePairs;
	}
}
