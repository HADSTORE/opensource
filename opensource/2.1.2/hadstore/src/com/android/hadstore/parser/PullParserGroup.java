package com.android.hadstore.parser;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.android.hadstore.data.Group;

public class PullParserGroup {
	XmlPullParser mXpp;
	Group group = new Group();
	
	public PullParserGroup(XmlPullParser xpp) {
		mXpp = xpp;
	}
	
	public void startParse() {
		
		try {
			int eventType = mXpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_DOCUMENT) {

				} else if (eventType == XmlPullParser.END_DOCUMENT) {
					
				} else if (eventType == XmlPullParser.START_TAG) {
					if(mXpp.getName().equals("description")){
					
						for(int i=0; i< mXpp.getAttributeCount(); i++){
							
							String name = mXpp.getAttributeName(i);
							String prefix = mXpp.getAttributePrefix(i);
							
							String type = mXpp.getAttributeType(i);
							String value = mXpp.getAttributeValue(i);
						}
						
						group.description = mXpp.nextText();
					}
					
					if(mXpp.getName().equals("title")){
						group.title = mXpp.nextText();
					}
					
					if(mXpp.getName().equals("thumbnail")){
						for(int i=0; i< mXpp.getAttributeCount(); i++){
							String name = mXpp.getAttributeName(i);
							if(name.equals("url")){
								group.thumbnail = mXpp.getAttributeValue(i);
							}
						}
					}
					
					if(mXpp.getName().equals("player")){
						for(int i=0; i< mXpp.getAttributeCount(); i++){
							
							String name = mXpp.getAttributeName(i);
							if(name.equals("url")){
								group.player =mXpp.getAttributeValue(i); 
							}
						}
					}
					
					
				} else if (eventType == XmlPullParser.END_TAG) {
					if(mXpp.getName().equals("group")){
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
	
	public Group getGroup() {
		return group;
	}
}
