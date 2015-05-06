package com.operation.model;

import java.util.Date;

import org.apache.commons.io.FilenameUtils;

@SuppressWarnings("serial")
public class HsApplicationInfo extends HsTotalWeight{
	private String appSysId;
	private String userId;
	private String userNickName;
	private String appTitle;
	private String appOs;
	private String appOsVersion;
	private String appDevice;
	private String appVersion;
	private String appUpdateVersion;
	private Long appLevel;
	private Long appDownPoint;
	private String appCategory;
	private Long appPremiumPoint;
	private Date appPremiumPointDate;
	private String appHint;
	private String appDesc;
	private String appUpdateDesc;
	private String appTitleIcon;
	private String appScreen1;
	private String appScreen1Name;
	private String appScreen2;
	private String appScreen2Name;
	private String appScreen3;
	private String appScreen3Name;
	private String appScreen4;
	private String appScreen4Name;
	private String appScreen5;
	private String appScreen5Name;
	private String appLocationLink;
	private String appLocationLinkName;
	private String appUpdateLocationLink;
	private String appStatus;
	private String eventTime;
	private String updateEventTime;
	private String sponsorFlag;
	private String packageName;
	
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getAppScreen1Name() {
		return FilenameUtils.getName(appScreen1);
	}
	public String getAppScreen2Name() {
		return FilenameUtils.getName(appScreen2);
	}
	public String getAppScreen3Name() {
		return FilenameUtils.getName(appScreen3);
	}
	public String getAppScreen4Name() {
		return FilenameUtils.getName(appScreen4);
	}
	public String getAppScreen5Name() {
		return FilenameUtils.getName(appScreen5);
	}
	public String getAppLocationLinkName() {
		return FilenameUtils.getName(appLocationLink);
	}
	public String getSponsorFlag() {
		return sponsorFlag;
	}
	public void setSponsorFlag(String sponsorFlag) {
		this.sponsorFlag = sponsorFlag;
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
	public String getUserNickName() {
		return userNickName;
	}
	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}
	public String getAppTitle() {
		return appTitle;
	}
	public void setAppTitle(String appTitle) {
		this.appTitle = appTitle;
	}
	public String getAppOs() {
		return appOs;
	}
	public void setAppOs(String appOs) {
		this.appOs = appOs;
	}
	public String getAppOsVersion() {
		return appOsVersion;
	}
	public void setAppOsVersion(String appOsVersion) {
		this.appOsVersion = appOsVersion;
	}
	public String getAppDevice() {
		return appDevice;
	}
	public void setAppDevice(String appDevice) {
		this.appDevice = appDevice;
	}
	public String getAppVersion() {
		return appVersion;
	}
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	public String getAppUpdateVersion() {
		return appUpdateVersion;
	}
	public void setAppUpdateVersion(String appUpdateVersion) {
		this.appUpdateVersion = appUpdateVersion;
	}
	public Long getAppLevel() {
		return appLevel;
	}
	public void setAppLevel(Long appLevel) {
		this.appLevel = appLevel;
	}
	public Long getAppDownPoint() {
		return appDownPoint;
	}
	public void setAppDownPoint(Long appDownPoint) {
		this.appDownPoint = appDownPoint;
	}
	public String getAppCategory() {
		return appCategory;
	}
	public void setAppCategory(String appCategory) {
		this.appCategory = appCategory;
	}
	public Long getAppPremiumPoint() {
		return appPremiumPoint;
	}
	public void setAppPremiumPoint(Long appPremiumPoint) {
		this.appPremiumPoint = appPremiumPoint;
	}
	public Date getAppPremiumPointDate() {
		return appPremiumPointDate;
	}
	public void setAppPremiumPointDate(Date appPremiumPointDate) {
		this.appPremiumPointDate = appPremiumPointDate;
	}
	public String getAppHint() {
		return appHint;
	}
	public void setAppHint(String appHint) {
		this.appHint = appHint;
	}
	public String getAppDesc() {
		return appDesc;
	}
	public void setAppDesc(String appDesc) {
		this.appDesc = appDesc;
	}
	public String getAppUpdateDesc() {
		return appUpdateDesc;
	}
	public void setAppUpdateDesc(String appUpdateDesc) {
		this.appUpdateDesc = appUpdateDesc;
	}
	
	public String getAppTitleIcon() {
		return appTitleIcon;
	}
	public void setAppTitleIcon(String appTitleIcon) {
		this.appTitleIcon = appTitleIcon;
	}
	public String getAppScreen1() {
		return appScreen1;
	}
	public void setAppScreen1(String appScreen1) {
		this.appScreen1 = appScreen1;
	}
	public String getAppScreen2() {
		return appScreen2;
	}
	public void setAppScreen2(String appScreen2) {
		this.appScreen2 = appScreen2;
	}
	public String getAppScreen3() {
		return appScreen3;
	}
	public void setAppScreen3(String appScreen3) {
		this.appScreen3 = appScreen3;
	}
	public String getAppScreen4() {
		return appScreen4;
	}
	public void setAppScreen4(String appScreen4) {
		this.appScreen4 = appScreen4;
	}
	public String getAppScreen5() {
		return appScreen5;
	}
	public void setAppScreen5(String appScreen5) {
		this.appScreen5 = appScreen5;
	}
	public String getAppLocationLink() {
		return appLocationLink;
	}
	public void setAppLocationLink(String appLocationLink) {
		this.appLocationLink = appLocationLink;
	}
	public String getAppUpdateLocationLink() {
		return appUpdateLocationLink;
	}
	public void setAppUpdateLocationLink(String appUpdateLocationLink) {
		this.appUpdateLocationLink = appUpdateLocationLink;
	}
	public String getAppStatus() {
		return appStatus;
	}
	public void setAppStatus(String appStatus) {
		this.appStatus = appStatus;
	}
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
	public String getUpdateEventTime() {
		return updateEventTime;
	}
	public void setUpdateEventTime(String updateEventTime) {
		this.updateEventTime = updateEventTime;
	}
	
}
