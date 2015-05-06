package com.operation.model;

public class HsCommentHistory extends SuperModel{
	private String appSysId;
	private String userId;
	private String eventTime;
	private String appComment;
	private String userNickName;
	
	
	
	public String getUserNickName() {
		return userNickName;
	}
	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}
	public String getAppComment() {
		return appComment;
	}
	public void setAppComment(String appComment) {
		this.appComment = appComment;
	}
	public String getAppSysId() {
		return appSysId;
	}
	public void setAppSysId(String appSysId) {
		this.appSysId = appSysId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
	
}
