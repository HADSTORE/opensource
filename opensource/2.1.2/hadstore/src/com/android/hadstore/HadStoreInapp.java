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
	
	//결제 성공
	public static final String PAY_SUCCESS = "pay_success";
	//결제 실패
	public static final String PAY_FAIL = "pay_fail";
	
	//결제 성공
	public static final String PAYMENT_CHECK_SUCCESS = "payment_check_success";
	//결제 실패
	public static final String PAYMENT_CHECK_FAIL = "payment_check_fail";

	
	//Extra 키값(결제 금액) 
	public static final String PAY = "pay";
	//Extra 키값(판매자 패키지명) 
	public static final String PAY_PACKAGE_NAME = "packege_name";
	//Extra 키값(디바이스 아이디) 
	public static final String PAY_MACHINE_NAME = "Machine_Name";
}
