package com.operation.model;

import java.sql.Blob;

public class HsPartnershipInfo extends HsApplicationInfo{
	private String userId;
	private String partnerName;
	private String partnerTitleIcon;
	private String partnerWebAddres;
	private Long partnerSeq;
	private String partnerAppSysId;
	private String downUserId;
	private Long partnerAppPoint;
	private Long partnerMaxCount;
	private String partnerAppStatus;
	private String createTime;
	private String createTime2;
	private String expireTime;
	private String eventTime;		
	
	public String getDownUserId() {
		return downUserId;
	}
	public void setDownUserId(String downUserId) {
		this.downUserId = downUserId;
	}
	public String getEventTime() {
		return eventTime;
	}
	public void setEventTime(String eventTime) {
		this.eventTime = eventTime;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPartnerName() {
		return partnerName;
	}
	public void setPartnerName(String partnerName) {
		this.partnerName = partnerName;
	}
	
	public String getPartnerTitleIcon() {
		return partnerTitleIcon;
	}
	public void setPartnerTitleIcon(String partnerTitleIcon) {
		this.partnerTitleIcon = partnerTitleIcon;
	}
	public String getPartnerWebAddres() {
		return partnerWebAddres;
	}
	public void setPartnerWebAddres(String partnerWebAddres) {
		this.partnerWebAddres = partnerWebAddres;
	}
	public Long getPartnerSeq() {
		return partnerSeq;
	}
	public void setPartnerSeq(Long partnerSeq) {
		this.partnerSeq = partnerSeq;
	}
	public String getPartnerAppSysId() {
		return partnerAppSysId;
	}
	public void setPartnerAppSysId(String partnerAppSysId) {
		this.partnerAppSysId = partnerAppSysId;
	}
	public Long getPartnerAppPoint() {
		return partnerAppPoint;
	}
	public void setPartnerAppPoint(Long partnerAppPoint) {
		this.partnerAppPoint = partnerAppPoint;
	}
	public Long getPartnerMaxCount() {
		return partnerMaxCount;
	}
	public void setPartnerMaxCount(Long partnerMaxCount) {
		this.partnerMaxCount = partnerMaxCount;
	}
	public String getPartnerAppStatus() {
		return partnerAppStatus;
	}
	public void setPartnerAppStatus(String partnerAppStatus) {
		this.partnerAppStatus = partnerAppStatus;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getExpireTime() {
		return expireTime;
	}
	public void setExpireTime(String expireTime) {
		this.expireTime = expireTime;
	}
	public String getCreateTime2() {
		return createTime2;
	}
	public void setCreateTime2(String createTime2) {
		this.createTime2 = createTime2;
	}
	
	
	
}
