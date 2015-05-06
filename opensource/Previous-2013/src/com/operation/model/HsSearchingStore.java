package com.operation.model;

public class HsSearchingStore extends SuperModel{
	private String appSysId;
	private String appLinkName;
	private String appWord;
	private String appWords[];
	private String eventTime;
	
	
	public String[] getAppWords() {
		return appWords;
	}
	public void setAppWords(String[] appWords) {
		this.appWords = appWords;
	}
	public String getAppSysId() {
		return appSysId;
	}
	public void setAppSysId(String appSysId) {
		this.appSysId = appSysId;
	}
	public String getAppLinkName() {
		return appLinkName;
	}
	public void setAppLinkName(String appLinkName) {
		this.appLinkName = appLinkName;
	}
	public String getAppWord() {
		return appWord;
	}
	public void setAppWord(String appWord) {
		this.appWord = appWord;
	}
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
}
