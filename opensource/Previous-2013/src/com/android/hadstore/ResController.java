package com.android.hadstore;

import java.util.HashMap;

import com.android.hadstore.R;

public class ResController {

	public static final String PAGE_MAIN="page_main";
	public static final String PAGE_LOGIN="page_login";
	public static final String PAGE_REGISTER="page_register";
	public static final String PAGE_GIFT="page_gift";
	public static final String PAGE_UPDATE="page_update";
	public static final String PAGE_NOTICE="page_notice";
	public static final String PAGE_NOTICE_DETAIL="page_notice_detail";
	public static final String PAGE_REGISTER_TERMS="page_register_terms";
	public static final String PAGE_SEARCH_LIST="page_search_list";
	public static final String PAGE_SEARCH_LIST_LAND="page_search_list_land";
	public static final String PAGE_DETAIL_GIFT="page_detail_gift";
	public static final String PAGE_DETAIL_LIST="page_detail_list";
	public static final String PAGE_DETAIL_LIST_LAND="page_detail_list_land";
	public static final String PAGE_COMMENT="page_comment";
	public static final String PAGE_COMMENT_LAND="page_comment_land";
	public static final String PAGE_MEMBERSHIP="page_membership";
	
	public static HashMap<String,Integer> mLayouts = new HashMap<String,Integer>();
	
	static{
		mLayouts.put(PAGE_MAIN, R.layout.layout_main);
		mLayouts.put(PAGE_LOGIN, R.layout.layout_login);
		mLayouts.put(PAGE_SEARCH_LIST, R.layout.layout_search_list);
		mLayouts.put(PAGE_SEARCH_LIST_LAND, R.layout.layout_search_list_land);
		mLayouts.put(PAGE_DETAIL_LIST, R.layout.layout_search_detail);
		mLayouts.put(PAGE_DETAIL_LIST_LAND, R.layout.layout_search_detail_land);
		mLayouts.put(PAGE_REGISTER_TERMS, R.layout.layout_terms);
		mLayouts.put(PAGE_REGISTER, R.layout.layout_register);
		mLayouts.put(PAGE_GIFT, R.layout.layout_gift);
		mLayouts.put(PAGE_UPDATE, R.layout.layout_update);
		mLayouts.put(PAGE_NOTICE, R.layout.layout_notice);
		mLayouts.put(PAGE_COMMENT, R.layout.layout_comment);
		mLayouts.put(PAGE_COMMENT_LAND, R.layout.layout_comment_land);
		mLayouts.put(PAGE_MEMBERSHIP, R.layout.layout_membership);
		mLayouts.put(PAGE_NOTICE_DETAIL, R.layout.layout_notice_detail);
		mLayouts.put(PAGE_DETAIL_GIFT, R.layout.layout_detail_gift);
		
	}
}
