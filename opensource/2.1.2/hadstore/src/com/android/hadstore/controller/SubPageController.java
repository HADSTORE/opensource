package com.android.hadstore.controller;

import android.content.res.Configuration;

public interface SubPageController {
	/**
	 * 뷰가 가려질 시의 동작 함수
	 */
	public void DialogClick(int index);
	/**
	 * 뷰가 가려질 시의 동작 함수
	 */
	public void Hide();
	/**
	 * 뷰가 보여질 시의 동작 함수 
	 */
	public void Show();
	/**
	 * 객체를 해제하는 동작의 함수
	 */
	public void Release();
	/**
	 * activity의 onPause동작시 기능 구현 함수
	 */
	public void onPause();
	/**
	 * activity의 onResume동작시 기능 구현 함수
	 */
	public void onResume();
	/**
	 * 객체의 이름을 저장하는 함수 
	 */
	public void setPageName(String name);
	/**
	 * 객체의 이름을 반환하는 함수
	 */
	public String getPageName();
	/**
	 * 뷰하나를 해제하는 기능의 함수
	 */
	public int BackKey();
	/**
	 * 현재 뷰가 보여지는지 여부
	 */
	public boolean isShow();
	
	/**
	 * activity의 가로 세로 모드시의 동작
	 */
	public void onConfigurationChanged(Configuration newConfig);
}
