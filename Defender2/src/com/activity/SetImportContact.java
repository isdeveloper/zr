package com.activity;

import java.util.HashMap;
import java.util.List;

import com.example.defender2.R;
import com.listview.adapter.ImportAdapter;
import com.listview.consdata.ContactConstruct;
import com.listview.entity.Contact;
import com.sql.DBProcess;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

@SuppressLint("UseSparseArrays")
public class SetImportContact extends Activity {
	/**
	 * ��ȡͨѶ¼��������ϵ�˵��ڰ�������
	 */
	private ListView contactlist;//��ϵ���б�
	private Button finish;//��ɰ�ť
	private HashMap<Integer,Boolean> checkedMap = new HashMap<Integer, Boolean>();//ѡ�е���Ŀ
	private DBProcess prodb = new DBProcess(this);//���ݿ����
	private String source;//��Դ���ڰ���������Ϊ���ݿ�����ı�
    private List<Contact> datalist;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.setting_import_contact);
		
		//������ҳ����
		Intent intent = getIntent();
		source = intent.getStringExtra("source");
		
		//��ȡ�ؼ�
		contactlist = (ListView)findViewById(R.id.importcontact_list);
		finish = (Button)findViewById(R.id.importcontact_finish_button);
		
		//����������
		contactlist.setItemsCanFocus(false);
		contactlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		ContactConstruct contact = new ContactConstruct(this);
		datalist = contact.getList();//����list
		contactlist.setAdapter(new ImportAdapter(this,datalist,checkedMap));//list�󶨵����������������󶨵�listview

		//���ü���
		contactlist.setOnItemClickListener(new OnItemClickListener() {
			   @Override
			   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    //������positionָ������Ŀ���������Ƿ�ѡ�С�
			    //ֻ�е�ѡ��ģʽ�ѱ�����ΪCHOICE_MODE_SINGLE��CHOICE_MODE_MULTIPLEʱ ���������Ч��
			    checkedMap.put(position,contactlist.isItemChecked(position));
			   }
		});
		finish.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				addData();
				Toast.makeText(SetImportContact.this, "����ɹ���", Toast.LENGTH_SHORT).show();
				
				Intent intent;
				if(source.equals("whitelist_table"))
					intent= new Intent(SetImportContact.this,SetWhiteList.class);
				else if(source.equals("blacklist_table"))
					intent= new Intent(SetImportContact.this,SetBlackList.class);
				else
					intent= new Intent(SetImportContact.this,Setting.class);
				startActivity(intent);
				prodb.close();
				finish();
			}});
	}
	
	//�����ݿ��������
	private void addData(){
		Cursor myCursor;//�����
		myCursor = prodb.select(source);
		
		for(int posizion = 0; posizion <= datalist.size()-1; posizion++)
			if(checkedMap.containsKey(posizion))
				if(checkedMap.get(posizion) != null && true == checkedMap.get(posizion)){
					boolean flag = true;
					
					while(myCursor.moveToNext())
						if(myCursor.getString(1).equals(datalist.get(posizion).getName()))
							flag = false;
					
					if(flag)
						prodb.insert(datalist.get(posizion).getName(), datalist.get(posizion).getPhone(), source);
				}
	}
}
