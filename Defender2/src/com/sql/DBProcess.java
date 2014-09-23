package com.sql;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBProcess extends SQLiteOpenHelper
{
	/**
	 * ���ݿ������
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
  		//������������
  		String sql = "CREATE TABLE " + TABLE_WHITE + " (" + FIELD_id + " INTEGER primary key autoincrement, " 
  				+ " " + FIELD_NAME + " text, " + " " + FIELD_PHONE + " text);";
  		db.execSQL(sql);
  		//������������
  		sql = "CREATE TABLE " + TABLE_BLACK + " (" + FIELD_id + " INTEGER primary key autoincrement, " 
  				+ " " + FIELD_NAME + " text, " + " " + FIELD_PHONE + " text);";
  		db.execSQL(sql);
  		//������־��
  		sql = "CREATE TABLE " + TABLE_LOG + " (" + FIELD_id + " INTEGER primary key autoincrement, " 
  				+ " " + FIELD_PHONE + " text, " + " " + FIELD_TIME + " text, " + " " + FIELD_PROCESS + " text);";
  		db.execSQL(sql);
  	}
  
  	@Override
  	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
  	{
  		//ɾ����������
  		String sql = "DROP TABLE IF EXISTS " + TABLE_WHITE;
  		db.execSQL(sql);
  		//ɾ����������
  		sql = "DROP TABLE IF EXISTS " + TABLE_BLACK;
  		db.execSQL(sql);
  		//ɾ����־��
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
  		//����ӵ�ֵ����Content Values
  		ContentValues cv = new ContentValues();
  		cv.put(FIELD_NAME, name);
  		cv.put(FIELD_PHONE, phone);
  		long row = db.insert(table, null, cv);
  		return row;
  	}
  	
  	public long insertLog(String phone, String time, String process)
  	{
  		SQLiteDatabase db = this.getWritableDatabase();
  		//����ӵ�ֵ����Content Values
  		ContentValues cv = new ContentValues();
  		cv.put(FIELD_PHONE, phone);
  		cv.put(FIELD_TIME, time);
  		cv.put(FIELD_PROCESS, process);
  		long row = db.insert(TABLE_LOG, null, cv);
  		return row;
  	}

  	public void clearLog(){
  		SQLiteDatabase db = this.getWritableDatabase();

  		//ɾ�����е���������:
  		String sql = "delete from "+TABLE_LOG+" ;";
  		db.execSQL(sql);
  		//��������:
//  		sql = "update sqlite_sequence set seq=0 where name=��_id��;";
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