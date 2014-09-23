package com.listview.consdata;

import java.util.ArrayList;
import java.util.List;

import com.listview.entity.Log;
import com.sql.DBProcess;

import android.content.Context;
import android.database.Cursor;

public class LogConstruct {
	/**
	 * 构造数据到防护日志list中
	 * 待修改
	 */
	Context c;
	private List<Log> list = new ArrayList<Log>();
	private DBProcess logdb = null;
	private Cursor myCursor;//结果集
	
	//为适配器添加数据项
	public LogConstruct(Context c){
		this.c = c;
		logdb = new DBProcess(c);
		
		myCursor = logdb.select("log_table");//查询结果集
		while(myCursor.moveToNext()){
			Log log = new Log();
			log.setPhoneNO("号码："+myCursor.getString(1));
			log.setTime("时间："+myCursor.getString(2));
			log.setProcess("类型："+myCursor.getString(3));
			
			list.add(log);
		}
		myCursor.close();
		logdb.close();
	}
	
	public List<Log> getList() {
		return list;
	}
}
