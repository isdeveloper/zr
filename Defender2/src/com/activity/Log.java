package com.activity;

import com.example.defender2.R;
import com.listview.adapter.LogAdapter;
import com.listview.consdata.LogConstruct;
import com.sql.DBProcess;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Log extends Activity {
	/**
	 * Log��־����
	 */
	private ListView logs;
	private TextView clear;
	private TextView refresh;
	private SharedPreferences sp = null;
	private Editor editor = null;
	
	private LogConstruct logconstruct;
	private LogAdapter logAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.log);
		sp = getSharedPreferences("DEFENDER", MODE_PRIVATE);
		editor = sp.edit();
		
		//��ȡ�ؼ�
		logs = (ListView)findViewById(R.id.log_listview);
		clear = (TextView)findViewById(R.id.log_clear);
		refresh = (TextView)findViewById(R.id.log_refresh);
		
		//����������
		setAdapter();
		
		//��Ӽ���
		clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//������ݿ��ˢ��listview
				DBProcess logdb = new DBProcess(Log.this);
				logdb.clearLog();
				logdb.close();
				setAdapter();
				Toast.makeText(Log.this, "����ɹ�", Toast.LENGTH_SHORT).show();
			}
		});
		refresh.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				setAdapter();
				if(sp.getString("FLAG", "").equals("ans")){
					editor.putString("FLAG", "hang");
					System.out.println("Set Hang");
					editor.commit();
				}else if(sp.getString("FLAG", "").equals("hang")){
					editor.putString("FLAG", "ans");
					System.out.println("Set Ans");
					editor.commit();
				}
				Toast.makeText(Log.this, "ˢ�³ɹ�", Toast.LENGTH_SHORT).show();
			}
		});
	}
	private void setAdapter(){
		logconstruct = new LogConstruct(this);//ʵ���������������
		logAdapter = new LogAdapter(this,logconstruct.getList());//����б�������
		logs.setAdapter(logAdapter);//����������listview
	}
}
