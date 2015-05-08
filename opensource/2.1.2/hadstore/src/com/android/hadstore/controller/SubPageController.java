package com.android.hadstore.controller;

import android.content.res.Configuration;

public interface SubPageController {
	/**
	 * �䰡 ������ ���� ���� �Լ�
	 */
	public void DialogClick(int index);
	/**
	 * �䰡 ������ ���� ���� �Լ�
	 */
	public void Hide();
	/**
	 * �䰡 ������ ���� ���� �Լ� 
	 */
	public void Show();
	/**
	 * ��ü�� �����ϴ� ������ �Լ�
	 */
	public void Release();
	/**
	 * activity�� onPause���۽� ��� ���� �Լ�
	 */
	public void onPause();
	/**
	 * activity�� onResume���۽� ��� ���� �Լ�
	 */
	public void onResume();
	/**
	 * ��ü�� �̸��� �����ϴ� �Լ� 
	 */
	public void setPageName(String name);
	/**
	 * ��ü�� �̸��� ��ȯ�ϴ� �Լ�
	 */
	public String getPageName();
	/**
	 * ���ϳ��� �����ϴ� ����� �Լ�
	 */
	public int BackKey();
	/**
	 * ���� �䰡 ���������� ����
	 */
	public boolean isShow();
	
	/**
	 * activity�� ���� ���� ������ ����
	 */
	public void onConfigurationChanged(Configuration newConfig);
}
