package com.listview.consdata;

import java.util.ArrayList;
import java.util.List;

import com.listview.entity.Log;
import com.sql.DBProcess;

import android.content.Context;
import android.database.Cursor;

public class LogConstruct {
	/**
	 * �������ݵ�������־list��
	 * ���޸�
	 */
	Context c;
	private List<Log> list = new ArrayList<Log>();
	private DBProcess logdb = null;
	private Cursor myCursor;//�����
	
	//Ϊ���������������
	public LogConstruct(Context c){
		this.c = c;
		logdb = new DBProcess(c);
		
		myCursor = logdb.select("log_table");//��ѯ�����
		while(myCursor.moveToNext()){
			Log log = new Log();
			log.setPhoneNO("���룺"+myCursor.getString(1));
			log.setTime("ʱ�䣺"+myCursor.getString(2));
			log.setProcess("���ͣ�"+myCursor.getString(3));
			
			list.add(log);
		}
		myCursor.close();
		logdb.close();
	}
	
	public List<Log> getList() {
		return list;
	}
}
