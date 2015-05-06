package com.operation.model;

public class HsTotalWeight extends SuperModel{
	private String appSysId;
	private Long appDownCount;
	private Long appViewCount;
	private Long appLikeCount;
	private Long appPremiumPoint;
	private Long appTotalWeight;
	private String appLastReportTime;
	public String getAppSysId() {
		return appSysId;
	}
	public void setAppSysId(String appSysId) {
		this.appSysId = appSysId;
	}
	public Long getAppDownCount() {
		return appDownCount;
	}
	public void setAppDownCount(Long appDownCount) {
		this.appDownCount = appDownCount;
	}
	public Long getAppViewCount() {
		return appViewCount;
	}
	public void setAppViewCount(Long appViewCount) {
		this.appViewCount = appViewCount;
	}
	public Long getAppLikeCount() {
		return appLikeCount;
	}
	public void setAppLikeCount(Long appLikeCount) {
		this.appLikeCount = appLikeCount;
	}
	public Long getAppPremiumPoint() {
		return appPremiumPoint;
	}
	public void setAppPremiumPoint(Long appPremiumPoint) {
		this.appPremiumPoint = appPremiumPoint;
	}
	public Long getAppTotalWeight() {
		return appTotalWeight;
	}
	public void setAppTotalWeight(Long appTotalWeight) {
		this.appTotalWeight = appTotalWeight;
	}
	public String getAppLastReportTime() {
		return appLastReportTime;
	}
	public void setAppLastReportTime(String appLastReportTime) {
		this.appLastReportTime = appLastReportTime;
	}
	
}
