package com.android.hadstore.util;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class DatabaseUtil extends SQLiteOpenHelper{
	private static final String TAG = "DatabaseUtil"; 
	
	public static final int LIST_ORDER_DATE = 0;
	
	public static final int LIST_ORDER_PD_NAME = 1;
	
	private static final String DATABASE_NAME = "hadstore.db";
    
	private static final int DATABASE_VERSION = 2002;
  
    private static final String DB_TABLE_NAME = "hadstore";
    
    private static final String DB_TABLE_NAME_NOTICE = "hadstore_notice";
    
	public class DBField implements BaseColumns
    {
        public static final String NAME = "name";
        public static final String GRAVITY = "gravity";
        public static final String CREATE_ID = "_id";
        public static final String USER_ID = "userid";
        public static final String NOTICE_NUM = "noticenum";
    }
	
	public class DbData{
		private String Name;
		private int Id;
		private int Gravity;
		public String getName() {
			return Name;
		}
		public void setName(String name) {
			Name = name;
		}
		public int getId() {
			return Id;
		}
		public void setId(int id) {
			Id = id;
		}
		public int getGravity() {
			return Gravity;
		}
		public void setGravity(int gravity) {
			Gravity = gravity;
		}
		
	}
	
	public DatabaseUtil(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.i(TAG, "onCreate() - SQLiteDatabase[" + db + "]");
        db.execSQL("CREATE TABLE " + DB_TABLE_NAME + " ("
                   + DBField._ID + " INTEGER PRIMARY KEY autoincrement, "
                   + DBField.NAME + " TEXT, " // NOT NULL
                   + DBField.GRAVITY + " INTEGER);");
        
        db.execSQL("CREATE TABLE " + DB_TABLE_NAME_NOTICE + " ("
                + DBField._ID + " INTEGER PRIMARY KEY autoincrement, "
                + DBField.USER_ID + " TEXT, "
                + DBField.NOTICE_NUM + " INTEGER);");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    	
    }
    
    public int insert(String name){
    	SQLiteDatabase db = null;
    	ContentValues values = null;
    	int count = 0;
    	try{
    		values = new ContentValues();
    		values.put(DBField.NAME, name);
    		values.put(DBField.GRAVITY, 1);
    		db = getWritableDatabase();
    		count = (int) db.insert(DB_TABLE_NAME, null, values);
    	}catch(SQLException e){
    		
    	}
    	return count;
    }
    
    public int update(DbData data){
    	SQLiteDatabase db = null;
    	ContentValues values = null;
    	int count = 0;
    	try{
    		values = new ContentValues();
    		values.put(DBField.GRAVITY, data.getGravity()+1);
    		db = getWritableDatabase();
    		count = (int) db.update(DB_TABLE_NAME, values, DBField._ID + "=?", new String[] { String.valueOf(data.getId())});
    	}catch(SQLException e){
    		
    	}
    	return count;
    }
    
    public ArrayList<DbData> Select(String search){
    	ArrayList<DbData> list = new ArrayList<DbData>();
    	SQLiteDatabase db = null;
    	Cursor cursor = null;
    	try{
    		db = getWritableDatabase();
    		cursor = db.rawQuery("select "+DBField._ID+","+DBField.NAME+","+DBField.GRAVITY+" FROM "+DB_TABLE_NAME +" where name like'%"+search+"%' order by gravity;", null);
    		if (cursor == null || cursor.getCount()==0) {
                Log.i(TAG, "loadApplications() is false");
                return list;
            }else{
            	int id = cursor.getColumnIndexOrThrow(DBField._ID);
                int name = cursor.getColumnIndexOrThrow(DBField.NAME);
                int gravity = cursor.getColumnIndexOrThrow(DBField.GRAVITY);
            	DbData data = null;
            	while(cursor.moveToNext()){
            		data = new DbData();
            		data.setId(cursor.getInt(id));
            		data.setName(cursor.getString(name));
            		data.setGravity(cursor.getInt(gravity)); 
            		list.add(data);
            	}
            }
    		
    	}catch(SQLException e){
    		
    	}finally{
    		if(cursor!=null){
    			cursor.close();
    		}
    	}
    	return list;
    }
    
    public int NoticeLastCnt(String userid){
    	int no = 0;
    	SQLiteDatabase db = null;
    	Cursor cursor = null;
    	try{
    		db = getWritableDatabase();
    		cursor = db.rawQuery("select "+DBField.NOTICE_NUM+" FROM "+DB_TABLE_NAME_NOTICE +" where "+DBField.USER_ID+"='"+userid+"';", null);
    		if (cursor == null || cursor.getCount()==0) {
    			NoticeCntInsert(userid,0);
            }else{
            	int id = cursor.getColumnIndexOrThrow(DBField.NOTICE_NUM);
            	if(cursor.moveToFirst()&&cursor.moveToNext())
            		no = cursor.getInt(id);
            	else{
            		NoticeCntInsert(userid,0);
            	}
            }
    		Log.e("mgdoo", "NoticeLastCnt "+no);
    	}catch(SQLException e){
    		e.printStackTrace();
    	}finally{
    		if(cursor!=null){
    			cursor.close();
    		}
    	}
    	return no;
    }
    
    public int NoticeCntInsert(String userid,int cnt){
    	SQLiteDatabase db = null;
    	ContentValues values = null;
    	int count = 0;
    	try{
    		values = new ContentValues();
    		values.put(DBField.USER_ID, userid);
    		values.put(DBField.NOTICE_NUM, cnt);
    		db = getWritableDatabase();
    		count = (int) db.insert(DB_TABLE_NAME_NOTICE, null, values);
    		Log.e("mgdoo", "NoticeCntInsert "+count);
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    	return count;
    }
    
    public int NoticeUpdate(String userid,int cnt){
    	SQLiteDatabase db = null;
    	ContentValues values = null;
    	int count = 0;
    	try{
    		values = new ContentValues();
    		values.put(DBField.NOTICE_NUM, cnt); 
    		db = getWritableDatabase();
    		count = (int) db.update(DB_TABLE_NAME_NOTICE, values, DBField.USER_ID + "=?", new String[] {userid});
    		
    	}catch(SQLException e){
    		e.printStackTrace();
    	}
    	Log.e("mgdoo", "NoticeUpdate "+cnt);
    	return count;
    }
    
    public ArrayList<DbData> getAllData(){
    	ArrayList<DbData> list = new ArrayList<DbData>();
    	SQLiteDatabase db = null;
    	Cursor cursor = null;
    	try{
    		db = getWritableDatabase();
    		cursor = db.rawQuery("select "+DBField._ID+","+DBField.NAME+","+DBField.GRAVITY+" FROM "+DB_TABLE_NAME +" order by gravity;", null);
    		if (cursor == null || cursor.getCount()==0) {
                Log.i(TAG, "loadApplications() is false");
                return list;
            }else{
            	int id = cursor.getColumnIndexOrThrow(DBField._ID);
                int name = cursor.getColumnIndexOrThrow(DBField.NAME);
                int gravity = cursor.getColumnIndexOrThrow(DBField.GRAVITY);
            	DbData data = null;
            	while(cursor.moveToNext()){
            		data = new DbData();
            		data.setId(cursor.getInt(id));
            		data.setName(cursor.getString(name));
            		data.setGravity(cursor.getInt(gravity)); 
            		list.add(data);
            	}
            }
    		
    	}catch(SQLException e){
    		
    	}finally{
    		if(cursor!=null){
    			cursor.close();
    		}
    	}
    	return list;
    }
    
    public DbData CheckSelect(String search){
    	DbData data = null;
    	SQLiteDatabase db = null;
    	Cursor cursor = null;
    	try{
    		db = getWritableDatabase();
    		cursor = db.rawQuery("select "+DBField.NAME+" FROM "+DB_TABLE_NAME +" where name like '%"+search+"%';", null);
    		Log.i(TAG, "CheckSelect  "+search);
    		if (cursor == null || cursor.getCount()==0) {
                Log.i(TAG, "loadApplications() is false");
                return data;
            }else{
            	if(cursor.getCount()>0){
            		data = new DbData();
            	}
            }
    		
    	}catch(SQLException e){
    		
    	}finally{
    		if(cursor!=null){
    			cursor.close();
    		}
    	}
    	return data;
    }
    
    public int Delete(String id){
    	SQLiteDatabase db = null;
    	int count = 0;
    	try{
    		db = getWritableDatabase();
    		count = (int) db.delete(DB_TABLE_NAME, DBField._ID + "=?", new String[] { id });
    	}catch(SQLException e){
    		
    	}
    	return count;
    }
    
    public int Delete(ArrayList<DbData> list){
    	SQLiteDatabase db = null;
    	int count = 0;
    	try{
    		db = getWritableDatabase();
    		int size = list.size();
    		db.beginTransaction();
    		for(int i=0;i<size;i++)
    		   count =+ (int) db.delete(DB_TABLE_NAME, DBField._ID + "=?", new String[] { (list.get(i).getId()+"") });
    		db.setTransactionSuccessful();
    	}catch(SQLException e){
    		
    	}finally{
    		if(db!=null){
    			db.endTransaction();
    		}
    	}
    	return count;
    }
}
