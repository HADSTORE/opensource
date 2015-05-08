/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.hadstore;

import com.android.hadstore.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
//import android.widget.NumberPicker;
import android.widget.LinearLayout;


//import com.android.internal.R;

/**
 * This class exists purely to cancel long click events, that got
 * started in NumberPicker
 */
class NumberPickerButton extends LinearLayout {

    private NumberPicker mNumberPicker;

    private View mImageView;
    
    public NumberPickerButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        LayoutInflater inflater =  (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate( R.layout.picker_button, this, true);
        
        mImageView = findViewById(R.id.PickerImage);
        
        Display display = ((WindowManager)context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
	    int width = display.getWidth();
	    int height = display.getHeight();
	    
	    FrameLayout.LayoutParams pram =  new FrameLayout.LayoutParams((int) (width*0.051389), (int)(height*0.02578125));
	    pram.gravity = Gravity.CENTER;
        mImageView.setLayoutParams(pram);
        
        if (R.id_number_picker.increment == getId()) {
        	mImageView.setBackgroundResource(R.drawable.alarmplus);
        } else if (R.id_number_picker.decrement == getId()) {
        	mImageView.setBackgroundResource(R.drawable.alarmminus);
        }
    }

    public NumberPickerButton(Context context) {
        super(context);
    }

    public void setNumberPicker(NumberPicker picker) {
        mNumberPicker = picker;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        cancelLongpressIfRequired(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        cancelLongpressIfRequired(event);
        return super.onTrackballEvent(event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER)
                || (keyCode == KeyEvent.KEYCODE_ENTER)) {
            cancelLongpress();
        }
        return super.onKeyUp(keyCode, event);
    }

    private void cancelLongpressIfRequired(MotionEvent event) {
        if ((event.getAction() == MotionEvent.ACTION_CANCEL)
                || (event.getAction() == MotionEvent.ACTION_UP)) {
            cancelLongpress();
        }
    }

    private void cancelLongpress() {
        if (R.id_number_picker.increment == getId()) {
            mNumberPicker.cancelIncrement();
        } else if (R.id_number_picker.decrement == getId()) {
            mNumberPicker.cancelDecrement();
        }
    }
}
