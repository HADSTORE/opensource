package com.operation.model;

public class HsDownHistory extends SuperModel{
	private String appSysId;
	private String userId;
	private String appDownMachineName;
	private String appDownType;
	private String eventTime;
	private String appVersion;
	private String packageName;
	private String userNickName;
	private String appTitleIcon;
	private String appTitle;
	
	
	
	public String getUserNickName() {
		return userNickName;
	}
	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}
	public String getAppTitleIcon() {
		return appTitleIcon;
	}
	public void setAppTitleIcon(String appTitleIcon) {
		this.appTitleIcon = appTitleIcon;
	}
	public String getAppTitle() {
		return appTitle;
	}
	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
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
	public String getAppDownMachineName() {
		return appDownMachineName;
	}
	public void setAppDownMachineName(String appDownMachineName) {
		this.appDownMachineName = appDownMachineName;
	}
	public String getAppDownType() {
		return appDownType;
	}
	public void setAppDownType(String appDownType) {
		this.appDownType = appDownType;
	}
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
}
