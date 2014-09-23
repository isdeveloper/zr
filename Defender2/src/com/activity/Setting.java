package com.activity;

import com.example.defender2.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class Setting extends Activity {
	/**
	 * 设置界面，有以下四个子菜单项
	 */
	private SharedPreferences sp = null;
	private Editor editor = null;
	private ListView setting = null;
	private String []menus = {"白名单", "黑名单", "防护模式", "关于"}; 
	
	@SuppressLint("CommitPrefEdits")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		sp = getSharedPreferences("DEFENDER", MODE_PRIVATE);
		editor = sp.edit();
		
		//获取本页控件
		setting = (ListView)findViewById(R.id.setting);
        setting.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, menus));
        
        //监听控件
        setting.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3){
				Intent intent = null;
				
				switch(arg2){
				case 0:
					intent = new Intent(Setting.this,SetWhiteList.class);
					startActivity(intent);
					break;
				case 1:
					intent = new Intent(Setting.this,SetBlackList.class);
					startActivity(intent);
					break;
				case 2:
					//选择对话框
					final String mode[] = {
							"人工模式：提示所有电话",
							"夜间模式：屏蔽所有电话",
							"智能模式：智能判断电话"};
					new AlertDialog.Builder(Setting.this)
					.setTitle("请选择防护模式")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(
							mode,//单选选项
							sp.getInt("MODE", 0),
							new DialogInterface.OnClickListener() {//监听选项
								public void onClick(DialogInterface dialog, int which) {
									editor.putInt("MODE", which);
									editor.commit();
									dialog.dismiss();
									Toast.makeText(Setting.this, "当前选择"+mode[which], Toast.LENGTH_SHORT).show();
								}
							}
					)
					.setNegativeButton("取消", null)
					.show();
					break;
				case 3:
					new AlertDialog.Builder(Setting.this)
					                .setTitle("关于")
					                .setMessage("AppName: YinDun\n" +
					                			"Version: v1.0\n" +
					                			"Developer:Bin\n\n" +
					                			"Copyright@Bupt.bin")
					                .setPositiveButton("确定", null)
					                .show();
					break;
				}
			}});
    }
}
