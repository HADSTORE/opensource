package com.android.hadstore.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory.Options;
import android.graphics.PorterDuff.Mode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

public class BitmapUtil {
	public static Drawable getDrawable(String rootFilePath, String fileName,Options options){
		Bitmap bitmapImage = BitmapFactory.decodeFile(rootFilePath + "/" + fileName,options);
        Drawable drawable = new BitmapDrawable(bitmapImage);
		return drawable;
	}
	public static Bitmap getBitmap(String rootFilePath, String fileName,Options options){
		return BitmapFactory.decodeFile(rootFilePath + "/" + fileName,options);
	}
	public static Bitmap getBitmap(Resources res,String resname,Options options){
		  Bitmap dump= BitmapFactory.decodeResource(res,res.getIdentifier(resname, "drawable", "com.adnroid.hadstore"),options);
		  return dump;
	}
	
	public static Bitmap getBitmap(Resources res,int id,Options option){
		  Bitmap dump= BitmapFactory.decodeResource(res,id,option);
		  return dump;
	}
	public static Bitmap setBackgroundBitmap(View view,Resources res,Bitmap bit,int id,Options options){
		  Bitmap dump= BitmapFactory.decodeResource(res, id,options);
		  view.setBackgroundDrawable(new BitmapDrawable(dump));
		  if(bit!=null){
			  bit.recycle();
			  bit = null;
		  }
		  return dump;
	}
	public static Bitmap setBackgroundBitmap(View view,Bitmap bit,String rootFilePath, String fileName,Options options){
		  Bitmap dump= BitmapFactory.decodeFile(rootFilePath + "/" + fileName,options);
		  view.setBackgroundDrawable(new BitmapDrawable(dump));
		  if(bit!=null){
			  bit.recycle();
			  bit = null;
		  }
		  return dump;
	}
	public static void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath)
	{
	        File fileCacheItem = new File(strFilePath);
	    	OutputStream out = null;

	    	try
	    	{
	    		fileCacheItem.createNewFile();
	    		out = new FileOutputStream(fileCacheItem);
	    		            
	    		bitmap.compress(CompressFormat.JPEG, 100, out);
	    	}
	    	catch (Exception e) 
	    	{
	    		e.printStackTrace();
	    	}
	    	finally
	    	{
	    		try
	    		{
	    			out.close();
	    		}
	    		catch (IOException e)
	    		{
	    			e.printStackTrace();
	    		}
	    	}
	}
	public static byte[] bitmapToByteArray( Bitmap bitmap ) {  
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;  
        bitmap.compress( CompressFormat.JPEG, 100, stream) ;  
        byte[] byteArray = stream.toByteArray() ;  
        return byteArray ;  
    }  
	
	public static Bitmap getBitmapReflection(Bitmap dump,int with,int height){
		Bitmap bitmapOriginal  = Bitmap.createScaledBitmap(dump, with, height, true);
		int nWidth = bitmapOriginal.getWidth();
		int nHeight = bitmapOriginal.getHeight();
		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);
		Bitmap bitmapReflection = Bitmap.createBitmap(bitmapOriginal, 0,
		nHeight / 2, nWidth, nHeight / 2, matrix, false);
		Bitmap bitmapOrigianlAndReflection = Bitmap.createBitmap(nWidth,
		(nHeight + nHeight / 3), Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmapOrigianlAndReflection);
		canvas.drawBitmap(bitmapOriginal, 0, 0, null);
		Paint deafaultPaint = new Paint();
		canvas.drawRect(0, nHeight, nWidth, nHeight, deafaultPaint);
		canvas.drawBitmap(bitmapReflection, 0, nHeight, null);
		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmapOriginal
		.getHeight(), 0, bitmapOrigianlAndReflection.getHeight() + 5,
		0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, nHeight, nWidth, bitmapOrigianlAndReflection
		.getHeight() + 5, paint);
		bitmapOriginal.recycle();
		bitmapOriginal = null;
		bitmapReflection.recycle();
		bitmapReflection = null;
		dump.recycle();
		dump = null;
		return bitmapOrigianlAndReflection;
	}
}
