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
	 * ���������ã���ӵ���ɾ��
	 */
	private ListView whitelist;
	private Button add, imp, rtn;
	private DBProcess prodb = new DBProcess(this);//���ݿ����
	private Cursor myCursor;//�����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_whitelist);
		
		//��ȡ�ؼ�
		whitelist = (ListView)findViewById(R.id.setting_whitelist_listview);
		add = (Button)findViewById(R.id.setting_whitelist_add);
		imp = (Button)findViewById(R.id.setting_whitelist_import);
		rtn = (Button)findViewById(R.id.setting_whitelist_rtn);
		
		//��ʼ����ҳ
		IniList();
		
		//����
	    whitelist.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final int pztion = arg2;
				AlertDialog.Builder builder = new Builder(SetWhiteList.this);
				builder.setTitle("ɾ��");
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setMessage("ȷ���Ӱ�������ɾ����");
				builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){
        	 		public void onClick(DialogInterface dialoginterface, int i){
        			    myCursor = prodb.select("whitelist_table");//��ѯ�����
        			    int id = 0;
        		    	myCursor.moveToNext();
        			    for(; id<= pztion-1; id++)
        			    	myCursor.moveToNext();
        				prodb.delete(myCursor.getInt(0), "whitelist_table");
        				IniList();
        	 		}
        		});
				builder.setNegativeButton("ȡ��",null);
				builder.show();
			}});
	    
		add.setOnClickListener(new OnClickListener(){//�Ի�����Ӻ���
			@Override
			public void onClick(View v) {
				showAddDialog();
			}});
		imp.setOnClickListener(new OnClickListener(){//������ϵ��
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SetWhiteList.this,SetImportContact.class);
				intent.putExtra("source", "whitelist_table");
				startActivity(intent);
				prodb.close();
				finish();
			}});
		rtn.setOnClickListener(new OnClickListener() {//��������
			@Override
			public void onClick(View v) {
				prodb.close();
				finish();
			}
		});
	}
	
	//��ʾ��Ӱ�������Ŀ�ĶԻ���
	protected void showAddDialog() {
		//������ʾView
	    LayoutInflater inflater = LayoutInflater.from(SetWhiteList.this);
	    final View view = inflater.inflate(R.layout.setting_additem_dialog, null);
	    
	    //����View
	    AlertDialog.Builder builder = new Builder(SetWhiteList.this);
		builder.setTitle("��Ӱ�����");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(view);
		builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){
	 		public void onClick(DialogInterface dialoginterface, int i){
	 			class ViewHolder{
	 				EditText name;
	 				EditText phone;
	 			}//��ȡ�ؼ�
	 			ViewHolder holder = new ViewHolder();
	 			holder.name = (EditText)view.findViewById(R.id.add_item_name);
	 			holder.phone = (EditText)view.findViewById(R.id.add_item_phone);
	 			view.setTag(holder);
	 			
	 			//�����Ŀ
	 			String name = holder.name.getText().toString();
	 			String phone = holder.phone.getText().toString();
	 			if(name.equals("") || phone.equals(""))
	 				Toast.makeText(SetWhiteList.this, "��ӳɹ�ʧ�ܣ������绰����Ϊ��", Toast.LENGTH_SHORT).show();
	 			else{
	 				boolean name_flag = false;
	 				boolean phone_flag = false;
	 			    myCursor = prodb.select("whitelist_table");//��ѯ�����
	 			    while(myCursor.moveToNext()){
	 			    	if(name.equals(myCursor.getString(1)))
	 			    		name_flag = true;
	 			    	else if(phone.equals(myCursor.getString(2)))
	 			    		phone_flag = true;
	 			    }
	 			    if(name_flag)
	 					Toast.makeText(SetWhiteList.this, "���ʧ�ܣ�����ͬ����Ŀ", Toast.LENGTH_SHORT).show();
	 			    else if(phone_flag)
	 					Toast.makeText(SetWhiteList.this, "���ʧ�ܣ��������ڰ�������", Toast.LENGTH_SHORT).show();
	 			    else{
    	 				prodb.insert(name, phone, "whitelist_table");
	 					Toast.makeText(SetWhiteList.this, "��ӳɹ���", Toast.LENGTH_SHORT).show();
	 					IniList();
	 			    }
	 			}
	 		}
		});
		builder.setNegativeButton("ȡ��",null);
		builder.show();
	}

	//ˢ����ʾlistview
	private void IniList(){
	    myCursor = prodb.select("whitelist_table");//��ѯ�����
	    @SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.setting_black_white_list_item,
	        myCursor, 
	        new String[]{ DBProcess.FIELD_NAME }, 
	        new int[]{ R.id.black_white_list_item_textview });
	    whitelist.setAdapter(adapter);
	}

	//�������ذ�ť���ر����ݿ�
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
