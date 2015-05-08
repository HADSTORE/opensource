package com.android.hadstore.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;

public class FileUtil {
	
//	public static SettingData makeDataRead(String path,String str) {
//		SettingData settingdata=null;
//		FileInputStream fis = null;
//		ObjectInputStream ois = null;
//		
//		try {
//			fis = new FileInputStream(new File(path + "/" + str + ".txt"));
//			ois = new ObjectInputStream(fis);
//			settingdata = (SettingData) ois.readObject();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}catch (ClassNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			try {
//				if (fis != null){
//					fis.close();
//				}
//				if(ois!=null){
//					ois.close();
//				}
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//		
//		return settingdata;
//	}
//	public static void makeDataSave(String path,String str,SettingData settingdata) {
//		FileOutputStream fos = null;
//		ObjectOutputStream oos = null;
//		
//		try {
//			fos = new FileOutputStream(new File(path + "/" + str + ".txt"));
//			oos = new ObjectOutputStream(fos);
//			oos.writeObject(settingdata);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}finally{
//			try {
//				if (fos != null){
//					fos.close();
//				}
//				if(oos!=null){
//					oos.close();
//				}
//			}catch(IOException e){
//				e.printStackTrace();
//			}
//		}
//	}
	public static String getFileName(String name,long dateTime,String extension) {		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String stringDateTime = dateFormat.format(new Date(dateTime));
		return name+ stringDateTime + "."+extension; 
	}
}
