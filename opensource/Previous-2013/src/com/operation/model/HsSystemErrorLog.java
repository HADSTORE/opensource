package com.operation.model;

public class HsSystemErrorLog extends SuperModel{
	private Long eventTime;
	private String errorType;
	private String errorFunction;
	private String errorContents;
	private String errorDevice;
	private String errorOsVersion;
	private String errorMachineName;
	public Long getEventTime() {
		return eventTime;
	}
	public void setEventTime(Long eventTime) {
		this.eventTime = eventTime;
	}
	public String getErrorType() {
		return errorType;
	}
	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	public String getErrorFunction() {
		return errorFunction;
	}
	public void setErrorFunction(String errorFunction) {
		this.errorFunction = errorFunction;
	}
	public String getErrorContents() {
		return errorContents;
	}
	public void setErrorContents(String errorContents) {
		this.errorContents = errorContents;
	}
	public String getErrorDevice() {
		return errorDevice;
	}
	public void setErrorDevice(String errorDevice) {
		this.errorDevice = errorDevice;
	}
	public String getErrorOsVersion() {
		return errorOsVersion;
	}
	public void setErrorOsVersion(String errorOsVersion) {
		this.errorOsVersion = errorOsVersion;
	}
	public String getErrorMachineName() {
		return errorMachineName;
	}
	public void setErrorMachineName(String errorMachineName) {
		this.errorMachineName = errorMachineName;
	}
	
	
}
