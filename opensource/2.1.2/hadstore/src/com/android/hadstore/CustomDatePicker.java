package com.android.hadstore;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.android.hadstore.R;
import com.android.hadstore.NumberPicker.OnChangedListener;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

public class CustomDatePicker extends LinearLayout implements OnChangedListener{
	
	private NumberPicker mYear;
	
	private NumberPicker mMonth;
	
	private NumberPicker mDay;
	
	private Calendar mCalendar;
	
	public CustomDatePicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		setOrientation(HORIZONTAL);
		LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate( R.layout.date_picker, this, true);
        
        mYear =  (NumberPicker) findViewById(R.id.date_year);
        mYear.setRange(1900, 2099);
        mMonth =  (NumberPicker) findViewById(R.id.date_month);
        mMonth.setRange(0, 12);
        mDay =  (NumberPicker) findViewById(R.id.date_day);
        
        mCalendar = new GregorianCalendar();
        mYear.setCurrent(1900);
        mMonth.setCurrent(0);
        int day =  mCalendar.get(Calendar.DAY_OF_MONTH);
        mDay.resetRange(0, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mDay.setCurrent(0);
        
        mMonth.setOnChangeListener(this);
	}

	public CustomDatePicker(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public boolean IsValidationCheck(){
		if(mYear.getCurrent()==1900||mMonth.getCurrent()==0||mDay.getCurrent()==0)
			return false;
		else 
			return true;
	}

	public int getYear(){
		return mYear.getCurrent();
	}
	
	public int getMonth(){
		return mMonth.getCurrent();
	}
	
	public int getDay(){
		return mDay.getCurrent();
	}

	@Override
	public void onChanged(NumberPicker picker, int oldVal, int newVal) {
		// TODO Auto-generated method stub
		
		if(mYear.getCurrent()!=0 && mMonth.getCurrent()!=0){
			 mCalendar.set(mYear.getCurrent(), mMonth.getCurrent()-1, 1);
			 mDay.resetRange(1, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
		
	}
}
