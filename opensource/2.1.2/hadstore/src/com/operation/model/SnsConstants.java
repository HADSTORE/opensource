package com.operation.model;

import java.util.regex.Pattern;


public class SnsConstants {
	
	public static final String ROOT_SERVER_PATH = "http://1.234.21.112:8080";
	public static final String ROOT_FILE_PATH = "/files/";
	
	public static String getServerImageInfo(){
		return ROOT_SERVER_PATH +  ROOT_FILE_PATH ;
	}
	
	public static final String STATUS = "status";
	public static final String EXIST = "exist";
	public static final String LIST = "list";
	
	public static final String INFO = "info";
	public static final String CATE_PHOTO = "cate_photo";
	public static final String CATE_MUSIC = "cate_music";
	public static final String CATE_VIDEO = "cate_video";
	
	
	/*
	 * http request
	 */
	public static final String START_TIME = "start_time";
	
	public static final String END_TIME = "end_time";
	
	public static final String SESSION = "session";

	public static final String SESSION_ID = "session_id";

	public static final String REQUEST_CONTEXT = "context";

	public static final String ACCESS_TOKEN = "access_token";

	/*
	 * http request header: platform_kind, platform_ver, device_id, app_name, app_vers, device_model, network_type
	 */

	/**
	 * ????��? �?? {@value} : ???�????[android], ?????[iphone], ????��? [wphone], 기�? [etc]
	 */
	public static final String PLATFORM_KIND = "platform_kind";

	/**
	 * ?????�?? {@value}
	 */
	public static final String PLATFORM_VER = "platform_ver";

	/**
	 * ?��?�??????{@value}
	 */
	public static final String DEVICE_ID = "device_id";

	/**
	 * ?��?�?모�? {@value}
	 */
	public static final String DEVICE_MODEL = "device_model";

	/**
	 * ?��?�???? {@value}
	 */
	public static final String DEVICE_TYPE = "device_type";

	/**
	 * ???��? {@value}
	 */
	public static final String APP_NAME = "app_name";

	/**
	 * ??�?? {@value}
	 */
	public static final String APP_VERS = "app_vers";

	/**
	 * ?��???? ?�결 �?? {@value} : Wifi, 3g, lan
	 */
	public static final String NETWORK_TYPE = "network_type";

	/**
	 * ?????????��? {@value}
	 */
	public static final String EMULATOR_MODE = "emulator";

	/**
	 * ???�??��? {@value}
	 */
	public static final String DEBUG_MODE = "debug";

	/**
	 * ?????�??: ?????모�???{@value}
	 */
	public static final String PLATFORM_KIND_WINDOW_PHONE = "10";

	/**
	 * ?????�??: ?????{@value}
	 */
	public static final String PLATFORM_KIND_IPHONE = "20";

	/**
	 * ?????�??: ???�????{@value}
	 */
	public static final String PLATFORM_KIND_ANDROID = "30";

	/**
	 * ?????�??: ???????{@value}
	 */
	public static final String PLATFORM_KIND_WINDOW_TAB = "40";

	/**
	 * ?????�??: iPad {@value}
	 */
	public static final String PLATFORM_KIND_IPAD = "50";

	/**
	 * ?????�??: ???�??????{@value}
	 */
	public static final String PLATFORM_KIND_TAB = "60";

	/**
	 * ?????�??: 기�? {@value}
	 */
	public static final String PLATFORM_KIND_ETC = "99";

	/*
	 * 기�? ???
	 */

	public static final String YES = "Y";

	public static final String NO = "N";
	
	public static final String TRUE = "T";

	public static final String FALSE = "F";
	
	/**
	 * hub user ??? �?�� ??????��??��? ?��???? ???????????�?��???��???
	 */
	public static final String USER_HUB_NO = "W";

	public static final String USER_PNS_TOKEN_DEL = "P";

	public static final String USER_DEVICE_ID = "D";

	/**
	 * ??????��?�? Size 93x93
	 */
	public static final int THUMBNAIL_SIZE_PROFILE = 93;

	/**
	 * ?��? ????��?�? Size 93x93
	 */
	public static final int THUMBNAIL_SIZE_CLUB = 93;

	/**
	 * ?��? �???? ?��?�? Size 292x292
	 */
	public static final int THUMBNAIL_SIZE_TOPTIC = 292;

	/**
	 * charset set {@value}
	 */
	public static final String ENCODING_CHARSET = "UTF-8";

	/**
	 * &#64;ID ?��?, �????(\\uce90\\uc2a4\\ud130)
	 */
	public static final Pattern AT_ID_PATTERN = Pattern.compile("(^|[^\\w])@((\\uce90\\uc2a4\\ud130)|([^@\\s]{2,15}))");

	/**
	 * &#64;ID ?��? 그룹 카�???
	 */
	public static final int AT_ID_PATTERN_GROUP_COUNT = 2;

	/**
	 * ??????��??��?
	 */
	public static final String TERMS_KIND_SERVICE = "10";

	/**
	 * �????�� ?��??��?
	 */
	public static final String TERMS_KIND_USER = "20";

	/**
	 * ?��?�?? {@value} ??��
	 */
	public static final int LENGTH_AUTHORITY = 6;

	/*
	 * ??? 구�?
	 */
	public static final String CHAT_MAKE_DIV_NOMAL = "000";

	public static final String CHAT_MAKE_DIV_IMG = "010";

	public static final String CHAT_MAKE_DIV_MOV = "020";

	public static final String CHAT_MAKE_DIV_SND = "030";

	public static final String CHAT_MAKE_DIV_ETC = "999";

	public static final String CHAT_MAKE_NAME_NOMAL = "?��?";

	public static final String CHAT_MAKE_NAME_IMG = "?��?";

	public static final String CHAT_MAKE_NAME_MOV = "???";

	public static final String CHAT_MAKE_NAME_SND = "???";

	public static final String CHAT_MAKE_NAME_ETC = "기�?";

	public static final String DEFAULT_AUTH_LEVEL = "1";
	
	// ??��
	public static final String REDOWN  = "redown";
	// �?? ?��?
	public static final String DOWN = "down";
	
	public static final String PURCHASE = "purchase";
	
	public static final String SALES = "sales";
		
	public static final String FROZEN = "Frozen";

	
	public static final String GIFT = "??��";
	public static final String COMMENT_PUCHASE = "구매";
	
	// �???????
	public static final String COMPANY = "Company";
	public static final String CUSTOMER = "Customer";

	
	// ?��?리�??????
	public static final String MY_APP_LIST = "my_App_list";
}
