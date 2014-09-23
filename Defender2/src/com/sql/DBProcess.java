package com.sql;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBProcess extends SQLiteOpenHelper
{
	/**
	 * 数据库操作类
	 */
	private final static String DATABASE_NAME = "list_db";
	private final static int DATABASE_VERSION = 1;
	private final static String TABLE_WHITE = "whitelist_table";
	private final static String TABLE_BLACK = "blacklist_table";
  	public final static String FIELD_id = "_id";
  	public final static String FIELD_NAME = "name_text";
  	public final static String FIELD_PHONE = "phone_text";

	private final static String TABLE_LOG = "log_table";
  	public final static String FIELD_TIME = "time_text";
  	public final static String FIELD_PROCESS = "process_text";

  	public DBProcess(Context context)
  	{
  		super(context, DATABASE_NAME, null, DATABASE_VERSION);
  	}

  	@Override
  	public void onCreate(SQLiteDatabase db)
  	{
  		//创建白名单表
  		String sql = "CREATE TABLE " + TABLE_WHITE + " (" + FIELD_id + " INTEGER primary key autoincrement, " 
  				+ " " + FIELD_NAME + " text, " + " " + FIELD_PHONE + " text);";
  		db.execSQL(sql);
  		//创建黑名单表
  		sql = "CREATE TABLE " + TABLE_BLACK + " (" + FIELD_id + " INTEGER primary key autoincrement, " 
  				+ " " + FIELD_NAME + " text, " + " " + FIELD_PHONE + " text);";
  		db.execSQL(sql);
  		//创建日志表
  		sql = "CREATE TABLE " + TABLE_LOG + " (" + FIELD_id + " INTEGER primary key autoincrement, " 
  				+ " " + FIELD_PHONE + " text, " + " " + FIELD_TIME + " text, " + " " + FIELD_PROCESS + " text);";
  		db.execSQL(sql);
  	}
  
  	@Override
  	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  	{
  		//删除白名单表
  		String sql = "DROP TABLE IF EXISTS " + TABLE_WHITE;
  		db.execSQL(sql);
  		//删除黑名单表
  		sql = "DROP TABLE IF EXISTS " + TABLE_BLACK;
  		db.execSQL(sql);
  		//删除日志表
  		sql = "DROP TABLE IF EXISTS " + TABLE_LOG;
  		db.execSQL(sql);
  		onCreate(db);
  	}

  	public Cursor select(String table)
  	{
  		SQLiteDatabase db = this.getReadableDatabase();
  		Cursor cursor = db.query(table, null, null, null, null, null, null);
  		return cursor;
  	}

  	public long insert(String name, String phone, String table)
  	{
  		SQLiteDatabase db = this.getWritableDatabase();
  		//将添加的值放入Content Values
  		ContentValues cv = new ContentValues();
  		cv.put(FIELD_NAME, name);
  		cv.put(FIELD_PHONE, phone);
  		long row = db.insert(table, null, cv);
  		return row;
  	}
  	
  	public long insertLog(String phone, String time, String process)
  	{
  		SQLiteDatabase db = this.getWritableDatabase();
  		//将添加的值放入Content Values
  		ContentValues cv = new ContentValues();
  		cv.put(FIELD_PHONE, phone);
  		cv.put(FIELD_TIME, time);
  		cv.put(FIELD_PROCESS, process);
  		long row = db.insert(TABLE_LOG, null, cv);
  		return row;
  	}

  	public void clearLog(){
  		SQLiteDatabase db = this.getWritableDatabase();

  		//删除表中的所有数据:
  		String sql = "delete from "+TABLE_LOG+" ;";
  		db.execSQL(sql);
  		//归零自增:
//  		sql = "update sqlite_sequence set seq=0 where name=’_id’;";
//  		db.execSQL(sql);
  	}
  	
  	public void delete(int id, String table)
  	{
  		SQLiteDatabase db = this.getWritableDatabase();
  		String where = FIELD_id + " = ?";
  		String[] whereValue =
  			{ Integer.toString(id) };
  		db.delete(table, where, whereValue);
  	}
}