package com.android.hadstore.util;

import android.graphics.Typeface;
import android.widget.TextView;

public class FontUtiil {
	public static void setTitle(TextView text,Typeface font){
		text.setTypeface(font);
		text.setTextColor(0xffffff);
	}
	
	public static void setHearder(TextView text,Typeface font){
		text.setTypeface(font);
		text.setTextColor(0x88ffffff);
	}
	public static void setFontAndColor(TextView text,int color,Typeface font){
		text.setTypeface(font);
		text.setTextColor(color);
	}
}
