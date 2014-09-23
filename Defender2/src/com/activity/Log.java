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
	 * Log日志界面
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
		
		//获取控件
		logs = (ListView)findViewById(R.id.log_listview);
		clear = (TextView)findViewById(R.id.log_clear);
		refresh = (TextView)findViewById(R.id.log_refresh);
		
		//定义适配器
		setAdapter();
		
		//添加监听
		clear.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				//清空数据库表，刷新listview
				DBProcess logdb = new DBProcess(Log.this);
				logdb.clearLog();
				logdb.close();
				setAdapter();
				Toast.makeText(Log.this, "清除成功", Toast.LENGTH_SHORT).show();
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
				Toast.makeText(Log.this, "刷新成功", Toast.LENGTH_SHORT).show();
			}
		});
	}
	private void setAdapter(){
		logconstruct = new LogConstruct(this);//实例化并添加数据项
		logAdapter = new LogAdapter(this,logconstruct.getList());//添加列表到适配器
		logs.setAdapter(logAdapter);//绑定适配器到listview
	}
}
