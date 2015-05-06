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
        mYear.setRange(1, 2099);
        mMonth =  (NumberPicker) findViewById(R.id.date_month);
        mMonth.setRange(1, 12);
        mDay =  (NumberPicker) findViewById(R.id.date_day);
        
        mCalendar = new GregorianCalendar();
        mYear.setCurrent(mCalendar.get(Calendar.YEAR));
        mMonth.setCurrent(mCalendar.get(Calendar.MONTH)+1);
        int day =  mCalendar.get(Calendar.DAY_OF_MONTH);
        mDay.setRange(1, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        mDay.setCurrent(day);
        
        mMonth.setOnChangeListener(this);
	}

	public CustomDatePicker(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
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
		mCalendar.set(mYear.getCurrent(), mMonth.getCurrent()-1, 1);
		mDay.resetRange(1, mCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
	}
}
