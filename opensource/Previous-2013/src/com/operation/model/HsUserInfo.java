package com.operation.model;

public class HsUserInfo extends SuperModel{
	private String userId;
	private String userPassword;
	private String userName;
	private String userNickName;
	private String userNickNameDesc;
	private String userEmailAddress;
	private String userEmailAddressFirst;
	private String userEmailAddressSecond;
	private String userGender;
	private String userSocialNo;
	private String userAge;
	private String userBirthday;
	private String userAdultRestriction;
	private String userShareFlag;
	private String userAutoShareAddFlag;
	private String userReferenceId;
	private String userType;
	private String userBankAccountNo;
	private String userBankCode;
	private String userCreateTime;
	private String userStatus;
	private Long userSalesPoint;
	private String userSalesPointReportTime;
	private String userIpinValue;
	
	
	public String getUserIpinValue() {
		return userIpinValue;
	}
	public void setUserIpinValue(String userIpinValue) {
		this.userIpinValue = userIpinValue;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserNickName() {
		return userNickName;
	}
	public void setUserNickName(String userNickName) {
		this.userNickName = userNickName;
	}
	public String getUserNickNameDesc() {
		return userNickNameDesc;
	}
	public void setUserNickNameDesc(String userNickNameDesc) {
		this.userNickNameDesc = userNickNameDesc;
	}
	public String getUserEmailAddress() {
		return userEmailAddress;
	}
	public void setUserEmailAddress(String userEmailAddress) {
		this.userEmailAddress = userEmailAddress;
	}
	public String getUserGender() {
		return userGender;
	}
	public void setUserGender(String userGender) {
		this.userGender = userGender;
	}
	public String getUserSocialNo() {
		return userSocialNo;
	}
	public void setUserSocialNo(String userSocialNo) {
		this.userSocialNo = userSocialNo;
	}
	public String getUserAge() {
		return userAge;
	}
	public void setUserAge(String userAge) {
		this.userAge = userAge;
	}
	public String getUserBirthday() {
		return userBirthday;
	}
	public void setUserBirthday(String userBirthday) {
		this.userBirthday = userBirthday;
	}
	public String getUserAdultRestriction() {
		return userAdultRestriction;
	}
	public void setUserAdultRestriction(String userAdultRestriction) {
		this.userAdultRestriction = userAdultRestriction;
	}
	public String getUserShareFlag() {
		return userShareFlag;
	}
	public void setUserShareFlag(String userShareFlag) {
		this.userShareFlag = userShareFlag;
	}
	public String getUserAutoShareAddFlag() {
		return userAutoShareAddFlag;
	}
	public void setUserAutoShareAddFlag(String userAutoShareAddFlag) {
		this.userAutoShareAddFlag = userAutoShareAddFlag;
	}
	public String getUserReferenceId() {
		return userReferenceId;
	}
	public void setUserReferenceId(String userReferenceId) {
		this.userReferenceId = userReferenceId;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}
	public String getUserBankAccountNo() {
		return userBankAccountNo;
	}
	public void setUserBankAccountNo(String userBankAccountNo) {
		this.userBankAccountNo = userBankAccountNo;
	}
	public String getUserBankCode() {
		return userBankCode;
	}
	public void setUserBankCode(String userBankCode) {
		this.userBankCode = userBankCode;
	}
	public String getUserCreateTime() {
		return userCreateTime;
	}
	public void setUserCreateTime(String userCreateTime) {
		this.userCreateTime = userCreateTime;
	}
	public String getUserStatus() {
		return userStatus;
	}
	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}
	public Long getUserSalesPoint() {
		return userSalesPoint;
	}
	public void setUserSalesPoint(Long userSalesPoint) {
		this.userSalesPoint = userSalesPoint;
	}
	public String getUserSalesPointReportTime() {
		return userSalesPointReportTime;
	}
	public void setUserSalesPointReportTime(String userSalesPointReportTime) {
		this.userSalesPointReportTime = userSalesPointReportTime;
	}
	public String getUserEmailAddressFirst() {
		String[] email= userEmailAddress.split("@");
		if(email == null){
			return "";
		}
		return email[0]; 
	}
	public void setUserEmailAddressFirst(String userEmailAddressFirst) {
		this.userEmailAddressFirst = userEmailAddressFirst;
	}
	public String getUserEmailAddressSecond() {
		String[] email= userEmailAddress.split("@");
		if(email == null || email.length != 2){
			return "";
		}
		return email[1];

	}
	public void setUserEmailAddressSecond(String userEmailAddressSecond) {
		this.userEmailAddressSecond = userEmailAddressSecond;
	}
	
	
	
}
