package com.android.hadstore.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {  
	public static String getMainTime(Calendar calendar) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String aa = null;
        if(hour>12){
        	hour = hour-12;
        	aa="PM";
        }else{
        	aa="AM";
        }
        int min = calendar.get(Calendar.MINUTE);
		String day=null;
		if(hour>=10)
			day=""+hour;
		else
			day="0"+hour;
		
		if(min>=10){
			day += " : "+min+" "+aa;
		}else{
			day += " : 0"+min+" "+aa;
		}
		
		return day;
	}
	
	
	
	public static String getNotificationTime(Date date) {	 
		SimpleDateFormat format = new java.text.SimpleDateFormat("yyyy.MM.dd");
		String day = format.format(date);
		return day;
	}
	
	public static String getMonth(Calendar calendar) {	 
        int month = calendar.get(Calendar.MONTH);
        int day =  calendar.get(Calendar.DAY_OF_WEEK);
		String smonth = null;
        switch(month){
	        case 0:
	        	smonth = "January";
	        	break;
	        case 1:
	        	smonth = "February";
	        	break;
	        case 2:
	        	smonth = "March";
	        	break;
	        case 3:
	        	smonth = "April";
	        	break;
	        case 4:
	        	smonth = "May";
	        	break;
	        case 5:
	        	smonth = "June";
	        	break;
	        case 6:
	        	smonth = "July";
	        	break;
	        case 7:
	        	smonth = "August";
	        	break;
	        case 8:
	        	smonth = "September";
	        	break;
	        case 9:
	        	smonth = "October";
	        	break;
	        case 10:
	        	smonth = "November";
	        	break;
	        case 11:
	        	smonth = "December";
	        	break;
        }
        
        String week = null;
        switch(day){
	        case 2:
	        	week = "Monday";
	        	break;
	        case 3:
	        	week = "Tuesday";
	        	break;
	        case 4:
	        	week = "Wednesday";
	        	break;
	        case 5:
	        	week = "Thursday";
	        	break;
	        case 6:
	        	week = "Friday";
	        	break;
	        case 7:
	        	week = "Saturday";
	        	break;
	        case 1:
	        	week = "Sunday";
	        	break;
	    }
        
		String str=smonth+"\n"+week;
		
		return str;
	}
}
