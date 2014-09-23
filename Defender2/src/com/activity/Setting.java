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
	 * ���ý��棬�������ĸ��Ӳ˵���
	 */
	private SharedPreferences sp = null;
	private Editor editor = null;
	private ListView setting = null;
	private String []menus = {"������", "������", "����ģʽ", "����"}; 
	
	@SuppressLint("CommitPrefEdits")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting);
		sp = getSharedPreferences("DEFENDER", MODE_PRIVATE);
		editor = sp.edit();
		
		//��ȡ��ҳ�ؼ�
		setting = (ListView)findViewById(R.id.setting);
        setting.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, menus));
        
        //�����ؼ�
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
					//ѡ��Ի���
					final String mode[] = {
							"�˹�ģʽ����ʾ���е绰",
							"ҹ��ģʽ���������е绰",
							"����ģʽ�������жϵ绰"};
					new AlertDialog.Builder(Setting.this)
					.setTitle("��ѡ�����ģʽ")
					.setIcon(android.R.drawable.ic_dialog_info)
					.setSingleChoiceItems(
							mode,//��ѡѡ��
							sp.getInt("MODE", 0),
							new DialogInterface.OnClickListener() {//����ѡ��
								public void onClick(DialogInterface dialog, int which) {
									editor.putInt("MODE", which);
									editor.commit();
									dialog.dismiss();
									Toast.makeText(Setting.this, "��ǰѡ��"+mode[which], Toast.LENGTH_SHORT).show();
								}
							}
					)
					.setNegativeButton("ȡ��", null)
					.show();
					break;
				case 3:
					new AlertDialog.Builder(Setting.this)
					                .setTitle("����")
					                .setMessage("AppName: YinDun\n" +
					                			"Version: v1.0\n" +
					                			"Developer:Bin\n\n" +
					                			"Copyright@Bupt.bin")
					                .setPositiveButton("ȷ��", null)
					                .show();
					break;
				}
			}});
    }
}
