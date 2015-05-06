package com.android.hadstore.info;

import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

public class NetInfo {
	
	/**
	 * 네트워크 아이피 주소
	 */
//	public final static String NET_ADDRESS ="http://1.234.53.42";
	public final static String NET_ADDRESS ="http://www.hadstore.com";
//	public final static String NET_ADDRESS ="http://192.168.0.220";
	
	public final static String APP_IMAGE ="http://www.hadstore.com/files/application/";
	
	public final static String APP_APK ="http://www.hadstore.com/files/application/";
	
	/**
	 * 네트워크 포트 주소
	 */
	public final static String NET_PORT = "";
	
	/**
	 * XML정보로 가져오기
	 */
	public final static String XML = ".xml";
	
	/**
	 * 인코딩 타입
	 */
	public final static String ENCODING = "UTF-8";
	
	/**
	 * 사용자 생성
	 */
	public final static String CREATE_USER ="/Hadstore/mobile/user/create/user";
	/**
	 * 즐겨찾기 리스트
	 */
	public final static String USER_BOOKMARK_LIST="/Hadstore/mobile/bookmark/view/appBookmarkList";
	
	/**
	 * 선물 처음 확인
	 */
	public final static String GIFT_FRIST_LIST="/Hadstore/mobile/bookmark/view/allCount";
	
	/**
	 * 선물 리스트 
	 */
	public final static String USER_GIFT_LIST ="/Hadstore/mobile/gift/view/appGiftList";
	/**
	 * 사용자 로그인
	 */
	public final static String USER_LOGIN ="/Hadstore/mobile/user/login";
	
	/**
	 * 사용자 ID 체크
	 */
	public final static String USER_ID_CHECK ="/Hadstore/mobile/user/check/idCheck";
	/**
	 * 뉴스
	 */
	public final static String GOOGLING_SEARCH = "/Hadstore/mobile/googling/view/appList";
	
	/**
	 * 상세보기
	 */
	public final static String APP_DETAIL = "/Hadstore/mobile/googling/view/appDetail";
	
	/**
	 * 댓글작성
	 */
	public final static String APP_COMMENT_POST = "/Hadstore/mobile/googling/create/appComment";
	
	/**
	 * 좋아요
	 */
	public final static String APP_LIKE_POST = "/Hadstore/mobile/googling/create/like";
	
	/**
	 * 즐겨찾기 추가
	 */
	public final static String APP_BOOKMARK_POST = "/Hadstore/mobile/bookmark/create/appBookmark";
	
	/**
	 * 댓글작성
	 */
	public final static String APP_COMMENT_LIST = "/Hadstore/mobile/googling/view/commentList";
	
	/**
	 * 앱 다운
	 */
	public final static String APP_DOWN = "/Hadstore/mobile/googling/create/appDown";
	
	/**
	 * 다른 사용자 즐겨찾기 연결
	 */
	public final static String USER_BOOKMARK_LINK = "/Hadstore/mobile/bookmark/create/referenceUser";
	
	/**
	 * 사용자 정보 보기
	 */
	public final static String RESEARCH_PROFILE = "/Hadstore/mobile/user/view/profile";
	
	/**
	 * 사용자 정보 변경
	 */
	public final static String RESEARCH_CHANGE = "/Hadstore/mobile/user/update/personInfo";
	
	/**
	 * 환경설정 변경
	 */
	public final static String PREFARENCES_CHANGE = "/Hadstore/mobile/user/update/userSetting";
	
	/**
	 * 선물하기
	 */
	public final static String USER_GIFT = "/Hadstore/mobile/gift/create/appGift";
	
	/**
	 * 해드스토어 버전 확인
	 */
	public final static String HADSTORE_VERSION = "/Hadstore/mobile/setting/view/applicationInfo";
	
	/**
	 * 공지사항 리스트
	 */
	public final static String NOTICE_LIST  = "/Hadstore/mobile/board/view/boardList";
	
	/**
	 * 공지사항 상세보기
	 */
	public final static String NOTICE_DETAIL  = "/Hadstore/mobile/board/view/boardTopicDetail";
	
	
	
	

	public final static String LIST = "list";
	public final static String INFO = "info";
	public final static String TIME = "time";
	public final static String SPONSOR = "sponsorList";
	public final static String COMMNET = "commentList";
	public final static String YOUTUBE = "http://gdata.youtube.com/feeds/api/videos";
	
	/**
	 * 네트워크 결과 값
	 */
	public final static String STATUS = "status";
	
	/**
	 * 사용자 포인트 값
	 */
	public final static String USER_SALES_POINT = "userSalesPoint";
	
	/**
	 * 다운 히스트로
	 */
	public final static String USER_DOWN_HISTORY = "downHistory";

	/**
	 * 공지사항 카운트값
	 */
	public final static String USER_NOTICE_COUNT = "noticeCount";
	
	/**
	 * 닉네임
	 */
	public final static String USER_NICK_NAME = "userNickName";
	
	/**
	 * 선물 카운트
	 */
	public final static String USER_GIFT_COUNT = "giftCount";
	
	/**
	 * 앱버전
	 */
	public final static String APPVERSION = "appVersion";
	
	/**
	 * 앱 링크
	 */
	public final static String APPDOWNLINK = "appLocationLink";
	
	/**
	 * 성공
	 */
	public final static int SUCCESS = 0;
	public final static int PAY_FAIL = 402;
	public final static String EXIST = "exist";
	
	

	/**
	 * Xml URI 가져오기
	 * @param url
	 * @return
	 */
	public static String getXmlURI(String url){
		String value = NET_ADDRESS + url + XML;
		return value;
	}
	
		
	/**
	 * 저스틴 비버에 맞는 헤더값 가져오기
	 * @param context
	 * @return
	 */
	public static HashMap<String, String> getHeaders(Context context){
		SharedPreferences prefs = context.getSharedPreferences(
                AppInfo.USER_INFO, 0);
		int user_seq = prefs.getInt(AppInfo.USER_SEQ, 0);
		String deviceId = prefs.getString(AppInfo.DEVICE_ID, "");
		
		HashMap<String, String> hashmap = new HashMap<String, String>();
		hashmap.put(AppInfo.USER_SEQ, String.valueOf(user_seq));
		hashmap.put(AppInfo.DEVICE_ID, deviceId);
		return hashmap;
	}
}
