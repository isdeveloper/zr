package com.activity;

import com.example.defender2.R;
import com.sql.DBProcess;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class SetWhiteList extends Activity {
	/**
	 * 白名单设置，添加导入删除
	 */
	private ListView whitelist;
	private Button add, imp, rtn;
	private DBProcess prodb = new DBProcess(this);//数据库操作
	private Cursor myCursor;//结果集

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_whitelist);
		
		//获取控件
		whitelist = (ListView)findViewById(R.id.setting_whitelist_listview);
		add = (Button)findViewById(R.id.setting_whitelist_add);
		imp = (Button)findViewById(R.id.setting_whitelist_import);
		rtn = (Button)findViewById(R.id.setting_whitelist_rtn);
		
		//初始化本页
		IniList();
		
		//监听
	    whitelist.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final int pztion = arg2;
				AlertDialog.Builder builder = new Builder(SetWhiteList.this);
				builder.setTitle("删除");
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setMessage("确定从白名单中删除？");
				builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
        	 		public void onClick(DialogInterface dialoginterface, int i){
        			    myCursor = prodb.select("whitelist_table");//查询结果集
        			    int id = 0;
        		    	myCursor.moveToNext();
        			    for(; id<= pztion-1; id++)
        			    	myCursor.moveToNext();
        				prodb.delete(myCursor.getInt(0), "whitelist_table");
        				IniList();
        	 		}
        		});
				builder.setNegativeButton("取消",null);
				builder.show();
			}});
	    
		add.setOnClickListener(new OnClickListener(){//对话框添加号码
			@Override
			public void onClick(View v) {
				showAddDialog();
			}});
		imp.setOnClickListener(new OnClickListener(){//导入联系人
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SetWhiteList.this,SetImportContact.class);
				intent.putExtra("source", "whitelist_table");
				startActivity(intent);
				prodb.close();
				finish();
			}});
		rtn.setOnClickListener(new OnClickListener() {//返回设置
			@Override
			public void onClick(View v) {
				prodb.close();
				finish();
			}
		});
	}
	
	//显示添加白名单条目的对话框
	protected void showAddDialog() {
		//设置显示View
	    LayoutInflater inflater = LayoutInflater.from(SetWhiteList.this);
	    final View view = inflater.inflate(R.layout.setting_additem_dialog, null);
	    
	    //设置View
	    AlertDialog.Builder builder = new Builder(SetWhiteList.this);
		builder.setTitle("添加白名单");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(view);
		builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
	 		public void onClick(DialogInterface dialoginterface, int i){
	 			class ViewHolder{
	 				EditText name;
	 				EditText phone;
	 			}//获取控件
	 			ViewHolder holder = new ViewHolder();
	 			holder.name = (EditText)view.findViewById(R.id.add_item_name);
	 			holder.phone = (EditText)view.findViewById(R.id.add_item_phone);
	 			view.setTag(holder);
	 			
	 			//添加条目
	 			String name = holder.name.getText().toString();
	 			String phone = holder.phone.getText().toString();
	 			if(name.equals("") || phone.equals(""))
	 				Toast.makeText(SetWhiteList.this, "添加成功失败！姓名电话不能为空", Toast.LENGTH_SHORT).show();
	 			else{
	 				boolean name_flag = false;
	 				boolean phone_flag = false;
	 			    myCursor = prodb.select("whitelist_table");//查询结果集
	 			    while(myCursor.moveToNext()){
	 			    	if(name.equals(myCursor.getString(1)))
	 			    		name_flag = true;
	 			    	else if(phone.equals(myCursor.getString(2)))
	 			    		phone_flag = true;
	 			    }
	 			    if(name_flag)
	 					Toast.makeText(SetWhiteList.this, "添加失败！已有同名条目", Toast.LENGTH_SHORT).show();
	 			    else if(phone_flag)
	 					Toast.makeText(SetWhiteList.this, "添加失败！号码已在白名单中", Toast.LENGTH_SHORT).show();
	 			    else{
    	 				prodb.insert(name, phone, "whitelist_table");
	 					Toast.makeText(SetWhiteList.this, "添加成功！", Toast.LENGTH_SHORT).show();
	 					IniList();
	 			    }
	 			}
	 		}
		});
		builder.setNegativeButton("取消",null);
		builder.show();
	}

	//刷新显示listview
	private void IniList(){
	    myCursor = prodb.select("whitelist_table");//查询结果集
	    @SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.setting_black_white_list_item,
	        myCursor, 
	        new String[]{ DBProcess.FIELD_NAME }, 
	        new int[]{ R.id.black_white_list_item_textview });
	    whitelist.setAdapter(adapter);
	}

	//监听返回按钮，关闭数据库
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK );{
			prodb.close();
			finish();
		}
		return true;
	}
}
