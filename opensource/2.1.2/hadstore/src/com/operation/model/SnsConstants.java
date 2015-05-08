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
	 * ????¼ì? ì¢?? {@value} : ???ë¡????[android], ?????[iphone], ????°í? [wphone], ê¸°í? [etc]
	 */
	public static final String PLATFORM_KIND = "platform_kind";

	/**
	 * ?????ë²?? {@value}
	 */
	public static final String PLATFORM_VER = "platform_ver";

	/**
	 * ?¨ë?ê¸??????{@value}
	 */
	public static final String DEVICE_ID = "device_id";

	/**
	 * ?¨ë?ê¸?ëª¨ë? {@value}
	 */
	public static final String DEVICE_MODEL = "device_model";

	/**
	 * ?¨ë?ê¸???? {@value}
	 */
	public static final String DEVICE_TYPE = "device_type";

	/**
	 * ???´ë? {@value}
	 */
	public static final String APP_NAME = "app_name";

	/**
	 * ??ë²?? {@value}
	 */
	public static final String APP_VERS = "app_vers";

	/**
	 * ?¤í???? ?°ê²° ì¢?? {@value} : Wifi, 3g, lan
	 */
	public static final String NETWORK_TYPE = "network_type";

	/**
	 * ?????????¬ë? {@value}
	 */
	public static final String EMULATOR_MODE = "emulator";

	/**
	 * ???ê·??¬ë? {@value}
	 */
	public static final String DEBUG_MODE = "debug";

	/**
	 * ?????ì¢??: ?????ëª¨ë???{@value}
	 */
	public static final String PLATFORM_KIND_WINDOW_PHONE = "10";

	/**
	 * ?????ì¢??: ?????{@value}
	 */
	public static final String PLATFORM_KIND_IPHONE = "20";

	/**
	 * ?????ì¢??: ???ë¡????{@value}
	 */
	public static final String PLATFORM_KIND_ANDROID = "30";

	/**
	 * ?????ì¢??: ???????{@value}
	 */
	public static final String PLATFORM_KIND_WINDOW_TAB = "40";

	/**
	 * ?????ì¢??: iPad {@value}
	 */
	public static final String PLATFORM_KIND_IPAD = "50";

	/**
	 * ?????ì¢??: ???ë¡??????{@value}
	 */
	public static final String PLATFORM_KIND_TAB = "60";

	/**
	 * ?????ì¢??: ê¸°í? {@value}
	 */
	public static final String PLATFORM_KIND_ETC = "99";

	/*
	 * ê¸°í? ???
	 */

	public static final String YES = "Y";

	public static final String NO = "N";
	
	public static final String TRUE = "T";

	public static final String FALSE = "F";
	
	/**
	 * hub user ??? ì¤?³µ ??????¬ì??´ë? ?´ì???? ???????????ì²?¦¬???¬ì???
	 */
	public static final String USER_HUB_NO = "W";

	public static final String USER_PNS_TOKEN_DEL = "P";

	public static final String USER_DEVICE_ID = "D";

	/**
	 * ??????´ë?ì§? Size 93x93
	 */
	public static final int THUMBNAIL_SIZE_PROFILE = 93;

	/**
	 * ?´ë? ????´ë?ì§? Size 93x93
	 */
	public static final int THUMBNAIL_SIZE_CLUB = 93;

	/**
	 * ?´ë? ê¸???? ?´ë?ì§? Size 292x292
	 */
	public static final int THUMBNAIL_SIZE_TOPTIC = 292;

	/**
	 * charset set {@value}
	 */
	public static final String ENCODING_CHARSET = "UTF-8";

	/**
	 * &#64;ID ?¨í?, ìº????(\\uce90\\uc2a4\\ud130)
	 */
	public static final Pattern AT_ID_PATTERN = Pattern.compile("(^|[^\\w])@((\\uce90\\uc2a4\\ud130)|([^@\\s]{2,15}))");

	/**
	 * &#64;ID ?¨í? ê·¸ë£¹ ì¹´ì???
	 */
	public static final int AT_ID_PATTERN_GROUP_COUNT = 2;

	/**
	 * ??????´ì??½ê?
	 */
	public static final String TERMS_KIND_SERVICE = "10";

	/**
	 * ê°????³´ ?´ì??½ê?
	 */
	public static final String TERMS_KIND_USER = "20";

	/**
	 * ?¸ì?ë²?? {@value} ??¦¬
	 */
	public static final int LENGTH_AUTHORITY = 6;

	/*
	 * ??? êµ¬ë?
	 */
	public static final String CHAT_MAKE_DIV_NOMAL = "000";

	public static final String CHAT_MAKE_DIV_IMG = "010";

	public static final String CHAT_MAKE_DIV_MOV = "020";

	public static final String CHAT_MAKE_DIV_SND = "030";

	public static final String CHAT_MAKE_DIV_ETC = "999";

	public static final String CHAT_MAKE_NAME_NOMAL = "?¼ë?";

	public static final String CHAT_MAKE_NAME_IMG = "?¬ì?";

	public static final String CHAT_MAKE_NAME_MOV = "???";

	public static final String CHAT_MAKE_NAME_SND = "???";

	public static final String CHAT_MAKE_NAME_ETC = "ê¸°í?";

	public static final String DEFAULT_AUTH_LEVEL = "1";
	
	// ??¬¼
	public static final String REDOWN  = "redown";
	// ìµ?? ?¤ì?
	public static final String DOWN = "down";
	
	public static final String PURCHASE = "purchase";
	
	public static final String SALES = "sales";
		
	public static final String FROZEN = "Frozen";

	
	public static final String GIFT = "??¬¼";
	public static final String COMMENT_PUCHASE = "êµ¬ë§¤";
	
	// ê²???????
	public static final String COMPANY = "Company";
	public static final String CUSTOMER = "Customer";

	
	// ?´í?ë¦¬ì??????
	public static final String MY_APP_LIST = "my_App_list";
}
