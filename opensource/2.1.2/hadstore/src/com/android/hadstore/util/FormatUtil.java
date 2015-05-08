package com.android.hadstore.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class FormatUtil {
	public static String makeStringComma(String str) {
		  if (str.length() == 0)
		   return "";
		  long value = Long.parseLong(str);
		  DecimalFormat format = new DecimalFormat("###,###");
		  return format.format(value);
	}
	
	public static int getOsVersionParser(String version) {	 
		String parser = version.replace(".", "");
		Log.e("mgdoo",parser);
		if(parser.length()<3){
			int len = 3-parser.length();
			for(int i = 0;i<len;i++)
				parser = parser+"0";
		}
		return Integer.parseInt(parser);
	}
}
