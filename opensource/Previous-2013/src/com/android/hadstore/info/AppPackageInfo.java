package com.android.hadstore.info;

public class AppPackageInfo {
	//앱 버전 
	private String mVersion;
	//앱 패키지 이름
	private String mPackageName;
	//앱 액티비티 이름
	private String mActivityName;
	//업데이트 여부
	private boolean mUpdate;
	
	public boolean isUpdate() {
		return mUpdate;
	}
	public void setUpdate(boolean Update) {
		this.mUpdate = Update;
	}
	public String getVersion() {
		return mVersion;
	}
	public void setVersion(String Version) {
		this.mVersion = Version;
	}
	public String getPackageName() {
		return mPackageName;
	}
	public void setPackageName(String PackageName) {
		this.mPackageName = PackageName;
	}
	public String getActivityName() {
		return mActivityName;
	}
	public void setActivityName(String ActivityName) {
		this.mActivityName = ActivityName;
	}
	
}
