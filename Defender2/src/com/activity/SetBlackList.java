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
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class SetBlackList extends Activity {
	/**
	 * ���ú���������ӵ���ɾ��
	 */
	private Button add, imp, rtn;
	private ListView blacklist;
	private DBProcess prodb = new DBProcess(this);//���ݿ����
	private Cursor myCursor;//�����

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_blacklist);
		
		//��ȡ��ҳ�ؼ���
		blacklist = (ListView)findViewById(R.id.setting_blacklist_listview);
		add = (Button)findViewById(R.id.setting_blacklist_add);
		imp = (Button)findViewById(R.id.setting_blacklist_import);
		rtn = (Button)findViewById(R.id.setting_blacklist_rtn);
		
		//��ʼ����ҳ
		IniList();
		
		//����
		blacklist.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				final int pztion = arg2;
				AlertDialog.Builder builder = new Builder(SetBlackList.this);
				builder.setTitle("ɾ��");
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setMessage("ȷ���Ӻ�������ɾ����");
				builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){
        	 		public void onClick(DialogInterface dialoginterface, int i){
        			    myCursor = prodb.select("blacklist_table");//��ѯ�����
        			    int id = 0;
        		    	myCursor.moveToNext();
        			    for(; id<= pztion-1; id++)
        			    	myCursor.moveToNext();
        				prodb.delete(myCursor.getInt(0), "blacklist_table");
        				IniList();
        	 		}
        		});
				builder.setNegativeButton("ȡ��",null);
				builder.show();
			}});
		
		add.setOnClickListener(new OnClickListener(){//�Ի�����Ӻ�����
			@Override
			public void onClick(View v) {
				ShowAddDialog();
			}});
		imp.setOnClickListener(new OnClickListener(){//����ϵ�˵��������
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(SetBlackList.this,SetImportContact.class);
				intent.putExtra("source", "blacklist_table");
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
	
	//��ʾ��Ӻ������ĶԻ���
	protected void ShowAddDialog() {
		//������ʾView
	    LayoutInflater inflater = LayoutInflater.from(SetBlackList.this);
	    final View view = inflater.inflate(R.layout.setting_additem_dialog, null);
	    
	    //���öԻ���
	    AlertDialog.Builder builder = new Builder(SetBlackList.this);
		builder.setTitle("��Ӻ�����");
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setView(view);
		builder.setPositiveButton("ȷ��",new DialogInterface.OnClickListener(){
	 		public void onClick(DialogInterface dialoginterface, int i){
	 			class ViewHolder{
	 				EditText name;
	 				EditText phone;
	 			}//��ȡView������
	 			ViewHolder holder = new ViewHolder();
	 			holder.name = (EditText)view.findViewById(R.id.add_item_name);
	 			holder.phone = (EditText)view.findViewById(R.id.add_item_phone);
	 			view.setTag(holder);
	 			
	 			//�����Ŀ
	 			String name = holder.name.getText().toString();
	 			String phone = holder.phone.getText().toString();
	 			if(name.equals("") || phone.equals(""))
	 				Toast.makeText(SetBlackList.this, "��ӳɹ�ʧ�ܣ������绰����Ϊ��", Toast.LENGTH_SHORT).show();
	 			else{
	 				boolean name_flag = false;
	 				boolean phone_flag = false;
	 			    myCursor = prodb.select("blacklist_table");//��ѯ�����
	 			    while(myCursor.moveToNext()){
	 			    	if(name.equals(myCursor.getString(1)))
	 			    		name_flag = true;
	 			    	else if(phone.equals(myCursor.getString(2)))
	 			    		phone_flag = true;
	 			    }
	 			    if(name_flag)
	 					Toast.makeText(SetBlackList.this, "���ʧ�ܣ�����ͬ����Ŀ", Toast.LENGTH_SHORT).show();
	 			    else if(phone_flag)
	 					Toast.makeText(SetBlackList.this, "���ʧ�ܣ��������ں�������", Toast.LENGTH_SHORT).show();
	 			    else{
    	 				prodb.insert(name, phone, "blacklist_table");
	 					Toast.makeText(SetBlackList.this, "��ӳɹ���", Toast.LENGTH_SHORT).show();
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
	    myCursor = prodb.select("blacklist_table");//��ѯ�����
	    @SuppressWarnings("deprecation")
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.setting_black_white_list_item,
	        myCursor, 
	        new String[]{ DBProcess.FIELD_NAME }, 
	        new int[]{ R.id.black_white_list_item_textview });
	    blacklist.setAdapter(adapter);
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
