package com.android.hadstore.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.android.hadstore.data.Entry;

public class PullParserEntry {
	XmlPullParser mXpp;
	public Entry entry = new Entry();
	
	public PullParserEntry(XmlPullParser xpp) {
		mXpp = xpp;
	}
	
	public void startParse() {
		
		try {
			int eventType = mXpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {

				} else if (eventType == XmlPullParser.END_DOCUMENT) {
					
				} else if (eventType == XmlPullParser.START_TAG) {
					if(mXpp.getName().equals("group")){
						PullParserGroup group = new PullParserGroup(mXpp);
						group .startParse();
						entry.group = group.getGroup();
					}
					
					if(mXpp.getName().equals("statistics")){
						for(int i=0; i< mXpp.getAttributeCount(); i++){
							String name = mXpp.getAttributeName(i);
							if(name.equals("viewCount")){
								entry.viewCount = mXpp.getAttributeValue(i); 
							}else if(name.equals("favoriteCount")){
								entry.favoritCount = mXpp.getAttributeValue(i);
							}
						}	
					}
					
					if(mXpp.getName().equals("rating")){
						for(int i=0; i< mXpp.getAttributeCount(); i++){
							String name = mXpp.getAttributeName(i);
							if(name.equals("numLikes")){
								entry.numLikes = mXpp.getAttributeValue(i); 
							}else if(name.equals("numDislikes")){
								entry.numDisLikes = mXpp.getAttributeValue(i);
							}
						}	
					}
					
					if(mXpp.getName().equals("published")){
						entry.published= mXpp.nextText();
					}
					
					if(mXpp.getName().equals("updated")){
						entry.updated= mXpp.nextText();
					}
					
				} else if (eventType == XmlPullParser.END_TAG) {
					if(mXpp.getName().equals("entry")){
						break;
					}
				} else if (eventType == XmlPullParser.TEXT) {
				}
				eventType = mXpp.next();
			}
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Entry getEntry(){
		return entry;
	}
}
