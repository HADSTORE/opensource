package com.android.hadstore.info;

public class AppPackageInfo {
	//�� ���� 
	private String mVersion;
	//�� ��Ű�� �̸�
	private String mPackageName;
	//�� ��Ƽ��Ƽ �̸�
	private String mActivityName;
	//������Ʈ ����
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
