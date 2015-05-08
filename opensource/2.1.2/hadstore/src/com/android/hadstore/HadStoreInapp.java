package com.android.hadstore;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

public class HadStoreInapp {
	public static final int INAPPPAY = 0;
	
	public static final int PAYMENT_CHECK = 1;
	
	public static final String MSG_IDENTIFY = "msg_identify";
	
	public static final String ERROR_MSG = "error_msg";
	
	//���� ����
	public static final String PAY_SUCCESS = "pay_success";
	//���� ����
	public static final String PAY_FAIL = "pay_fail";
	
	//���� ����
	public static final String PAYMENT_CHECK_SUCCESS = "payment_check_success";
	//���� ����
	public static final String PAYMENT_CHECK_FAIL = "payment_check_fail";

	
	//Extra Ű��(���� �ݾ�) 
	public static final String PAY = "pay";
	//Extra Ű��(�Ǹ��� ��Ű����) 
	public static final String PAY_PACKAGE_NAME = "packege_name";
	//Extra Ű��(����̽� ���̵�) 
	public static final String PAY_MACHINE_NAME = "Machine_Name";
}
